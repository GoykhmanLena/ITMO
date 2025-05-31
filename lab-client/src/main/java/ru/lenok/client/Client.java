package ru.lenok.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public final class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private static final Client INSTANCE = new Client();


    private Client() {
    }

    public static Client getINSTANCE() {
        return INSTANCE;
    }

    public void startClient(String host, int port, String username, String password, Boolean isRegistration) {
        if (host == null || username == null || password == null || port == -1) {
            printUsageAndExit();
        }

        if (!isValidHost(host)) {
            logger.info("Ошибка: Неверный формат хоста: " + host);
            System.exit(1);
        }

        if (!isValidPort(port)) {
            logger.info("Ошибка: Неверный формат порта: " + port);
            System.exit(1);
        }

        logger.info("Хост: " + host);
        logger.info("Порт: " + port);
        logger.info("Имя пользователя: " + username);
        logger.info("Пароль: " + password);
        logger.info("Режим регистрации: " + (isRegistration ? "Да" : "Нет"));
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            logger.error("Ошибка: ", e);
            System.exit(-1);
        }

        ClientApplication app = new ClientApplication(ip, port, isRegistration, username, password);
        app.start();
    }

    private static boolean isValidHost(String host) {
        String ipPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        String domainPattern = "^[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
        return Pattern.matches(ipPattern, host) || Pattern.matches(domainPattern, host) || "localhost".equals(host);
    }

    private static boolean isValidPort(int port) {
        return port >= 1 && port <= 65535;
    }

    private static void printUsageAndExit() {
        logger.info("Использование: <host>:<port> -u <username> -p <password> [-r]");
        System.exit(1);
    }
}
