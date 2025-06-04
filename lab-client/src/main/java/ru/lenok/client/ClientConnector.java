package ru.lenok.client;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lenok.common.CommandRequest;
import ru.lenok.common.CommandResponse;
import ru.lenok.common.auth.LoginRequest;
import ru.lenok.common.auth.LoginResponse;
import ru.lenok.common.auth.User;
import ru.lenok.common.commands.CommandBehavior;
import ru.lenok.common.models.LabWorkWithKey;
import ru.lenok.common.util.SerializationUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static ru.lenok.common.util.SerializationUtils.BUFFER_SIZE;
import static ru.lenok.common.util.SerializationUtils.INSTANCE;

@AllArgsConstructor
public class ClientConnector {
    private static final Logger logger = LoggerFactory.getLogger(ClientConnector.class);
    public static final int INTER_WAIT_SLEEP_TIMEOUT = 50;
    private static final int RETRY_COUNT = 1;
    private static final int WAIT_TIMEOUT = 1000 * 10;
    private final DatagramSocket notificationListeningSocket;

    private final InetSocketAddress serverAddress;

    public ClientConnector(InetAddress ip, int port) throws IOException {
        serverAddress = new InetSocketAddress(ip, port);
        this.notificationListeningSocket = new DatagramSocket(0);
        ClientService.getInstance().setServerNotificationPort(getServerNotificationPort());
        new Thread(() -> listenForServerNotifications()).start();
    }

    public int getServerNotificationPort() {
        return notificationListeningSocket.getLocalPort();
    }

    private Object sendData(Object obj) {
        int retryCount = RETRY_COUNT;
        while (retryCount > 0) {
            retryCount--;
            try (DatagramChannel clientChannel = DatagramChannel.open()) {
                clientChannel.configureBlocking(false);
                //clientChannel.bind(clientAddress);

                byte[] data = INSTANCE.serialize(obj);
                ByteBuffer buffer = ByteBuffer.wrap(data);

                clientChannel.send(buffer, serverAddress);
                logger.debug("Данные отправлены серверу: " + obj);

                ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                InetSocketAddress sourceAddress = waitForResponse(clientChannel, receiveBuffer);
                if (sourceAddress != null) {
                    //receiveBuffer.flip();
                    Object response = INSTANCE.deserialize(receiveBuffer.array());
                    long expectedChunksSize;
                    List<byte[]> chunks = new ArrayList<>();
                    if (response instanceof SerializationUtils.ChunksCountWithCRC) {
                        SerializationUtils.ChunksCountWithCRC chunksCountWithCRC = (SerializationUtils.ChunksCountWithCRC) response;
                        expectedChunksSize = chunksCountWithCRC.getChunksCount();
                        logger.debug("Ответ от сервера, ожидается количество чанков: " + expectedChunksSize);
                        for (int i = 1; i <= expectedChunksSize; i++) {
                            receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                            receiveBuffer.clear();
                            sourceAddress = waitForResponse(clientChannel, receiveBuffer);
                            if (sourceAddress != null) {
                                chunks.add(INSTANCE.copy(receiveBuffer.array(), receiveBuffer.position()));
                                logger.debug("Ответ от сервера, получен чанк: " + i);
                            }
                        }
                        if (expectedChunksSize == chunks.size()) {
                            response = INSTANCE.deserializeFromChunks(chunks, chunksCountWithCRC.getCrc());
                            if (expectedChunksSize == 1) {
                                logger.debug("Ответ от сервера: " + response);
                            } else {
                                logger.debug("Ответ от сервера: итого получено чанков " + expectedChunksSize);
                            }
                            return response;
                        } else {
                            logger.error("Ожидалось " + expectedChunksSize + " чанков, а пришло: " + chunks.size());
                            continue;
                        }
                    } else {
                        logger.error("Ожидалось количество чанков, а пришло другое: " + response);
                    }
                    return response;
                } else {
                    if (retryCount > 0) {
                        logger.warn("Сервер не ответил, повторяю попытку отправить, попытка: " + (RETRY_COUNT - retryCount + 1) + " из " + RETRY_COUNT);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new IllegalArgumentException("Сервер недоступен");
    }

    private static InetSocketAddress waitForResponse(DatagramChannel clientChannel, ByteBuffer receiveBuffer) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        InetSocketAddress sourceAddress = null;

        while ((System.currentTimeMillis() - startTime) < WAIT_TIMEOUT) {
            receiveBuffer.clear();
            sourceAddress = (InetSocketAddress) clientChannel.receive(receiveBuffer);

            if (sourceAddress != null) {
                break;
            }

            Thread.sleep(INTER_WAIT_SLEEP_TIMEOUT);
        }
        return sourceAddress;
    }

    public Map<String, CommandBehavior> sendHello(boolean isRegister, User user) throws Exception {
        LoginRequest loginRequest = new LoginRequest(user, isRegister, getServerNotificationPort());
        Object response = sendData(loginRequest);
        if (response instanceof LoginResponse) {
            LoginResponse loginResponse = (LoginResponse) response;
            if (loginResponse.getError() != null) {
                throw loginResponse.getError();
            }
            return loginResponse.getClientCommandDefinitions();
        }
        throw new IllegalArgumentException("Ошибка логина/регистрации: " + response);
    }

    public CommandResponse sendCommand(CommandRequest commandRequest) {
        Object commandResponse = sendData(commandRequest);
        if (commandResponse instanceof CommandResponse) {
            return (CommandResponse) commandResponse;
        }
        throw new IllegalArgumentException("Неверный ответ от сервера на команду: " + commandResponse);
    }

    public void listenForServerNotifications() {
        while (true) {
            try {
                byte[] buffer = new byte[SerializationUtils.BUFFER_SIZE];
                DatagramPacket packetFromClient = new DatagramPacket(buffer, buffer.length);

                logger.info("Жду оповещения от сервера на порту " + getServerNotificationPort());
                notificationListeningSocket.receive(packetFromClient);

                byte[] actualData = Arrays.copyOfRange(buffer, 0, packetFromClient.getLength());
                Object serverNotification = SerializationUtils.INSTANCE.deserialize(actualData);
                long expectedChunksSize;
                List<byte[]> chunks = new ArrayList<>();
                if (serverNotification instanceof SerializationUtils.ChunksCountWithCRC) {
                    SerializationUtils.ChunksCountWithCRC chunksCountWithCRC = (SerializationUtils.ChunksCountWithCRC) serverNotification;
                    expectedChunksSize = chunksCountWithCRC.getChunksCount();
                    logger.debug("Ответ от сервера, ожидается количество чанков: " + expectedChunksSize + " crc = " + chunksCountWithCRC.getCrc());
                    for (int i = 1; i <= expectedChunksSize; i++) {
                        notificationListeningSocket.receive(packetFromClient);
                        actualData = Arrays.copyOfRange(buffer, 0, packetFromClient.getLength());
                        chunks.add(actualData);
                        logger.debug("Ответ от сервера, получен чанк: " + i);
                        logger.info("размер чанка " + actualData.length);
                    }
                    if (expectedChunksSize == chunks.size()) {
                        serverNotification = INSTANCE.deserializeFromChunks(chunks, chunksCountWithCRC.getCrc());
                        if (expectedChunksSize == 1) {
                            logger.debug("Ответ от сервера: " + serverNotification);
                        } else {
                            logger.debug("Ответ от сервера: итого получено чанков " + expectedChunksSize);
                        }
                        if (serverNotification instanceof CommandResponse){
                            CommandResponse commandResponse = (CommandResponse) serverNotification;
                            List<LabWorkWithKey> labWorkWithKeyList = (List<LabWorkWithKey>) commandResponse.getOutputObject();
                            Consumer<List<LabWorkWithKey>> serverNotifier = ClientService.getInstance().getNotificationListener();
                            if (serverNotifier != null) {
                                serverNotifier.accept(labWorkWithKeyList);
                            }
                        }
                    } else {
                        logger.error("Ожидалось " + expectedChunksSize + " чанков, а пришло: " + chunks.size());
                    }
                } else {
                    logger.error("Ожидалось количество чанков, а пришло другое: " + serverNotification);
                }

            } catch (Exception e) {
                logger.error("Ошибка: " + e);
            }
        }
    }

}
