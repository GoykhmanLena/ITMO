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
    private boolean isRegister;
    private ClientInputProcessor clientInputProcessor;

    public static ClientService getInstance() {
        return INSTANCE;
    }

    public void setConnector(ClientConnector connector) {
        this.connector = connector;
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

    public CommandResponse createLabWork(LabWorkWithKey labWorkWithKey){
        CommandBehavior behavior = commandDefinitions.get("insert");
        CommandRequest request = new CommandRequest(new CommandWithArgument("insert", behavior , labWorkWithKey.getKey(), null), labWorkWithKey, user, getServerNotificationPort());
        return connector.sendCommand(request);
    }
    public CommandResponse updateLabWork(LabWorkWithKey labWorkWithKey){
        CommandBehavior behavior = commandDefinitions.get("update_id");
        CommandRequest request = new CommandRequest(new CommandWithArgument("update_id", behavior , labWorkWithKey.getId().toString(), null), labWorkWithKey, user, getServerNotificationPort());
        return connector.sendCommand(request);
    }

    public void login(String login, String password, boolean register) throws Exception {
        User user = new User(login, password);
        Map<String, CommandBehavior> commandDefinitions = connector.sendHello(register, user);
        setUser(user);
        setCommandDefinitions(commandDefinitions);
    }

    public CommandResponse getAllLabWorks() throws Exception {
        CommandBehavior commandBehavior = commandDefinitions.get("show");
        CommandRequest request = new CommandRequest(new CommandWithArgument("show", commandBehavior, null, null), null, user, getServerNotificationPort());
        return connector.sendCommand(request);
    }

    public Exception deleteLabWork(String key) {
        CommandBehavior commandBehavior = commandDefinitions.get("remove_key");
        CommandRequest request = new CommandRequest(new CommandWithArgument("remove_key", commandBehavior, key, null), null, user, getServerNotificationPort());
        CommandResponse commandResponse = connector.sendCommand(request);
        return commandResponse.getError();
    }

    public Exception clearLabWorks() {
        CommandBehavior commandBehavior = commandDefinitions.get("clear");
        CommandRequest showRequest = new CommandRequest(new CommandWithArgument("clear", commandBehavior, null, null), null, user, getServerNotificationPort());
        CommandResponse commandResponse = connector.sendCommand(showRequest);
        return commandResponse.getError();
    }

    public CommandResponse getHelp()  {
        CommandBehavior commandBehavior = commandDefinitions.get("help");
        CommandRequest request = new CommandRequest(new CommandWithArgument("help", commandBehavior, null, null), null, user, getServerNotificationPort());
        return connector.sendCommand(request);
    }

    public CommandResponse getHistory()  {
        CommandBehavior commandBehavior = commandDefinitions.get("history");
        CommandRequest request = new CommandRequest(new CommandWithArgument("history", commandBehavior, null, null), null, user, getServerNotificationPort());
        return connector.sendCommand(request);
    }

    public CommandResponse getInfo()  {
        CommandBehavior commandBehavior = commandDefinitions.get("info");
        CommandRequest request = new CommandRequest(new CommandWithArgument("info", commandBehavior, null, null), null, user, getServerNotificationPort());
        return connector.sendCommand(request);
    }

    public void executeScript(String path) throws RuntimeException {
        if (clientInputProcessor == null) {
            clientInputProcessor = new ClientInputProcessor(commandDefinitions, connector, user);
        }
        CommandBehavior commandBehavior = commandDefinitions.get("execute_script");
        CommandWithArgument commandWithArgument = new CommandWithArgument("execute_script", commandBehavior, path, null);
        CommandRequest commandRequest = new CommandRequest(commandWithArgument, null, user, serverNotificationPort);
        clientInputProcessor.runExecuteScript(commandRequest);
    }

    public void setRegistration(boolean register) {
        isRegister  = register;
    }
}
