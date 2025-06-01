package ru.lenok.client;

import lombok.Data;
import ru.lenok.common.CommandRequest;
import ru.lenok.common.CommandResponse;
import ru.lenok.common.CommandWithArgument;
import ru.lenok.common.auth.User;
import ru.lenok.common.commands.CommandBehavior;
import ru.lenok.common.models.LabWorkWithKey;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
@Data

public class ClientService {
    private static ClientService INSTANCE = new ClientService();
    private ClientConnector connector;
    private Consumer<List<LabWorkWithKey>> notificationListener;
    private int serverNotificationPort;
    private User user;

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
    public void registerNotificationListener(Consumer<List<LabWorkWithKey>> notificationListener){
        this.notificationListener = notificationListener;
    }

    public Consumer<List<LabWorkWithKey>> getNotificationListener() {
        return notificationListener;
    }

    public void insertLabWork(LabWorkWithKey labWorkWithKey){
        CommandBehavior behavior = commandDefinitions.get("insert");
        CommandRequest showRequest = new CommandRequest(new CommandWithArgument("insert", behavior , labWorkWithKey.getKey(), null), labWorkWithKey, user, getServerNotificationPort());
        CommandResponse commandResponse = getConnector().sendCommand(showRequest);
    }
}
