package ru.lenok.client;

import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientUI extends Application {
    private static final Logger logger = LoggerFactory.getLogger(ClientUI.class);

    @Override
    public void start(Stage primaryStage) {
        new LoginForm().start(primaryStage);
    }

    public static void main(String[] args) {
        String host;
        int port = 0;
        InetAddress ip = null;
        if (args.length > 0) {
            String[] hostPort = args[0].split(":");
            if (hostPort.length == 2) {
                host = hostPort[0];
                try {
                    ip = InetAddress.getByName(host);
                } catch (UnknownHostException e) {
                    logger.error("Ошибка: ", e);
                    System.exit(-1);
                }
                try {
                    port = Integer.parseInt(hostPort[1]);
                } catch (NumberFormatException e) {
                    System.err.println("Неверный формат порта");
                    System.exit(1);
                }
            }
        } else {
            System.err.println("Необходимо указать хост и порт в формате host:port");
            System.exit(1);
        }

        ClientConnector clientConnector = new ClientConnector(ip, port);
        ClientService.getINSTANCE().setConnector(clientConnector);
        launch(args);
    }
}