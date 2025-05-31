package ru.lenok.client;

import javafx.application.Application;
import javafx.stage.Stage;

public class ClientUI extends Application {
    private static String host;
    private static int port;

    @Override
    public void start(Stage primaryStage) {
        new LoginForm(host, port).start(primaryStage);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            String[] hostPort = args[0].split(":");
            if (hostPort.length == 2) {
                host = hostPort[0];
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

        launch(args);
    }
}