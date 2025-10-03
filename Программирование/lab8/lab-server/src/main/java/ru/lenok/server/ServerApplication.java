package ru.lenok.server;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lenok.common.CommandResponse;
import ru.lenok.common.models.LabWork;
import ru.lenok.common.models.LabWorkWithKey;
import ru.lenok.server.collection.LabWorkService;
import ru.lenok.server.commands.CommandRegistry;
import ru.lenok.server.commands.IHistoryProvider;
import ru.lenok.server.connectivity.ClientAddress;
import ru.lenok.server.connectivity.IncomingMessage;
import ru.lenok.server.connectivity.ServerConnectionListener;
import ru.lenok.server.connectivity.ServerResponseSender;
import ru.lenok.server.daos.*;
import ru.lenok.server.request_processing.RequestHandler;
import ru.lenok.server.services.OfferService;
import ru.lenok.server.services.ProductService;
import ru.lenok.server.services.UserService;
import ru.lenok.server.utils.HistoryList;
import ru.lenok.server.utils.JsonReader;

import java.io.IOException;
import java.net.DatagramSocket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

public class ServerApplication implements IHistoryProvider {

    private static final Logger logger = LoggerFactory.getLogger(ServerApplication.class);
    private LabWorkService labWorkService;
    private CommandRegistry commandRegistry;
    private RequestHandler reqHandler;
    private int port;
    private final Properties properties;
    private ServerConnectionListener serverConListener;
    private ServerResponseSender serverRespSender;
    private UserService userService;
    private ProductService productService;
    private OfferService offerService;
    private Cache<ClientAddress, ClientAddress> clientsCache;
    private Object notificationMonitor;

    public ServerApplication(Properties properties) {
        this.properties = properties;
        init();
    }

    public void start() {
        ForkJoinPool listeningPool = new ForkJoinPool();
        ForkJoinPool processingPool = new ForkJoinPool();
        ExecutorService sendingExecutor = Executors.newFixedThreadPool(4);


        logger.info("Сервер работает");
        try {
            while (true) {
                IncomingMessage incomingMessage = serverConListener.listenAndReceiveMessage();
                CompletableFuture
                        .supplyAsync(
                                () -> reqHandler.handleIncomingMessage(incomingMessage)
                                , processingPool)
                        .thenAcceptAsync(
                                response -> serverRespSender.sendMessageToClient(response.getResponse(), response.getClientIp(), response.getClientPort())
                                , sendingExecutor);
            }

        } catch (Exception e) {
            logger.error("Ошибка, ", e);
        }
    }

    private void init() {
        //Thread collectionNotification = new Thread(() -> sendNotificationsToClients());
        //collectionNotification.start();
        try {
            port = Integer.parseInt(properties.getProperty("listenPort"));
        } catch (NumberFormatException e) {
            logger.error("Ошибка, не распознан порт: ", e);
            System.exit(1);
        }

        try {
            initServices();
            clientsCache = CacheBuilder.newBuilder()
                    .expireAfterWrite(5, TimeUnit.HOURS)
                    .build();
            this.commandRegistry = new CommandRegistry(labWorkService, productService, offerService, this);

            reqHandler = new RequestHandler(commandRegistry, userService, clientsCache);

            serverConListener = new ServerConnectionListener(port);

            serverRespSender = new ServerResponseSender(serverConListener.getSocket());
            handleSaveOnTerminate();
        } catch (Exception e) {
            logger.error("Ошибка, ", e);
            System.exit(1);
        }
    }

    private void sendNotificationsToClients() {
        while (true) {
            try {
                synchronized (notificationMonitor) {
                    notificationMonitor.wait();
                    ConcurrentMap<ClientAddress, ClientAddress> clientsCacheMap = clientsCache.asMap();
                    for (ClientAddress clientAddress : clientsCacheMap.keySet()) {

                    }
                }
            } catch (Exception e) {
                logger.error("Ошибка при отправке обновления клиентам", e);
            }
        }
    }

    private void handleSaveOnTerminate() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Сервер завершает работу. Обрабатываем событие Ctrl + C.");

            DatagramSocket socket = serverConListener.getSocket();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }));
    }

    private void initServices() {
        String dbPort = properties.getProperty("dbPort");
        String dbUser = properties.getProperty("dbUser");
        String dbPassword = properties.getProperty("dbPassword");
        String dbHost = properties.getProperty("dbHost");
        String dbSchema = properties.getProperty("dbSchema");
        String dbReinit = properties.getProperty("dbReinit");
        String filename = properties.getProperty("initialCollectionPath");

        boolean reinitDB = Boolean.parseBoolean(dbReinit);

        Hashtable<String, LabWork> initialState = new Hashtable<>();
        if (reinitDB && filename != null && !filename.isEmpty()) {

            JsonReader jsonReader = new JsonReader();
            HashSet<Long> setOfId = new HashSet<>();
            try {
                initialState = jsonReader.loadFromJson(filename);
                logger.info("Файл успешно загружен: {}", filename);
            } catch (IOException e) {
                logger.error("Ошибка при чтении файла: {}", e.getMessage());
                logger.error("Программа завершается");
                System.exit(1);
            }
            for (LabWork labWork : initialState.values()) {
                setOfId.add(labWork.getId());
            }
            if (setOfId.size() < initialState.size()) {
                logger.warn("В файле есть повторяющиеся id — коллекция будет очищена");
                initialState.clear();
            }
        }


        try {
            DBConnector dbConnector = new DBConnector(dbHost, dbPort, dbUser, dbPassword, dbSchema);
            Set<Long> userIdsFromLabWorks = getUserIdsFromLabWorks(initialState);
            UserDAO userDAO = new UserDAO(userIdsFromLabWorks, dbConnector, reinitDB);
            ProductDAO productDAO = new ProductDAO(userIdsFromLabWorks, dbConnector, reinitDB);
            LabWorkDAO labWorkDAO = new LabWorkDAO(initialState, dbConnector, reinitDB);
            OfferDAO offerDAO = new OfferDAO(dbConnector, reinitDB);
            userService = new UserService(userDAO, dbConnector);
            labWorkService = new LabWorkService(labWorkDAO, dbConnector, (l) -> notifyClients(l));
            productService = new ProductService(productDAO, dbConnector);
            offerService = new OfferService(labWorkDAO, productDAO, offerDAO, labWorkService, dbConnector);
        } catch (SQLException | NoSuchAlgorithmException e) {
            logger.error("Ошибка при инициализации сервисов: {} {}", e.getMessage());
            e.printStackTrace();
            logger.error("Программа завершается");
            System.exit(1);
        }
    }

    private void notifyClients(List<LabWorkWithKey> labWorkWithKeys) {
        ConcurrentMap<ClientAddress, ClientAddress> clientsCacheMap = clientsCache.asMap();
        logger.info("Будут оповещены все клиенты, количество: " + clientsCacheMap.size());
        for (ClientAddress clientAddress : clientsCacheMap.keySet()) {
            CompletableFuture
                    .runAsync(
                            () -> {
                                CommandResponse response = new CommandResponse("", labWorkWithKeys);
                                serverRespSender.sendMessageToClient(response, clientAddress.getClientIp(), clientAddress.getClientPort());
                            });
        }
    }

    private Set<Long> getUserIdsFromLabWorks(Map<String, LabWork> initialState) {
        Set<Long> result = new HashSet<>();
        for (LabWork labWork : initialState.values()) {
            result.add(labWork.getOwnerId());
        }
        return result;
    }

    @Override
    public HistoryList getHistoryByClientID(Long clientID) {
        return reqHandler.getHistoryByClientID(clientID);
    }
}
