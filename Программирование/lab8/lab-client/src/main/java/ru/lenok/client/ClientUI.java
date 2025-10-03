package ru.lenok.client;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.lenok.common.auth.User;

import java.io.IOException;

import static ru.lenok.client.Client.validateArgsAndCreateApp;

public class ClientUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        new LoginForm().start(primaryStage);
    }

    public static void main(String[] args) throws IOException {
        ClientApplication clientApplication = validateArgsAndCreateApp(args, true);
        ClientConnector clientConnector = new ClientConnector(clientApplication.getIp(), clientApplication.getPort());
        ClientService clientService = ClientService.getInstance();
        clientService.setConnector(clientConnector);
        User user = clientApplication.getUser();
        if (user != null) {
            clientService.setUser(user);
            clientService.setRegistration(clientApplication.isRegister());
        }
        launch(args);
    }
}