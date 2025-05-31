package ru.lenok.server.connectivity;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lenok.common.util.SerializationUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
public class ServerResponseSender{
    private static final Logger logger = LoggerFactory.getLogger(ServerResponseSender.class);
    private final SerializationUtils serializer = SerializationUtils.INSTANCE;
    private final DatagramSocket socket;
    private final static int THREAD_COUNT = 5;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);

    public ServerResponseSender(DatagramSocket datagramSocket) {
        this.socket = datagramSocket;
    }

    public void sendMessageToClient(Object response, InetAddress clientIp, int clientPort) {
        try {
            SerializationUtils.ChunksWithCRC chunksWithCRC = serializer.serializeAndSplitToChunks(response);
            List<byte[]> chunks = chunksWithCRC.getChunks();
            int chunkCount = chunks.size();
            SerializationUtils.ChunksCountWithCRC chunksCountWithCRC = new SerializationUtils.ChunksCountWithCRC(chunkCount, chunksWithCRC.getCrc());
            byte[] chunksCountWithCRCSerialized = serializer.serialize(chunksCountWithCRC);

            DatagramPacket responsePacket = new DatagramPacket(chunksCountWithCRCSerialized, chunksCountWithCRCSerialized.length, clientIp, clientPort);
            socket.send(responsePacket);
            logger.info("Отправлено количество чанков и чексумма: " + chunksCountWithCRC + " clientIp = " + clientIp + " clientPort = " + clientPort);
            int i = 0;
            for (byte[] responseDataChunk : chunks) {
                responsePacket = new DatagramPacket(responseDataChunk, responseDataChunk.length, clientIp, clientPort);
                socket.send(responsePacket);
                i++;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    //ignored
                }
                logger.debug("Отправлен чанк " + i + " из " + chunkCount);
            }
            if (chunkCount == 1) {
                logger.debug("Отправлены данные: " + response);
            }
        } catch (IOException e){
            logger.error("Ошибка отправки: " + e);
        }
    }
}
