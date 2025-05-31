package ru.lenok.client;

import ru.lenok.common.commands.CommandBehavior;

import java.util.Map;

public class ClientService {
    private static ClientService INSTANCE = new ClientService();
    private ClientConnector connector;

    public static ClientService getINSTANCE() {
        return INSTANCE;
    }

    public ClientConnector getConnector() {
        return connector;
    }

    public void setConnector(ClientConnector connector) {
        this.connector = connector;
    }

    public Map<String, CommandBehavior> getCommandDefinitions() {
        return commandDefinitions;
    }

    public void setCommandDefinitions(Map<String, CommandBehavior> commandDefinitions) {
        this.commandDefinitions = commandDefinitions;
    }

    private Map<String, CommandBehavior> commandDefinitions;

    private ClientService(){

    }

}
