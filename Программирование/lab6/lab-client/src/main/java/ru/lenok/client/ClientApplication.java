package ru.lenok.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lenok.common.commands.CommandDefinition;
import ru.lenok.client.input.AbstractInput;
import ru.lenok.client.input.ConsoleInput;

import java.net.InetAddress;
import java.util.Collection;
import java.util.UUID;

public class ClientApplication {
    private static final Logger logger = LoggerFactory.getLogger(ClientApplication.class);
    private final InetAddress ip;
    private final int port;
    public static final UUID CLIENT_ID = UUID.randomUUID();
    private Collection<CommandDefinition> commandDefinitions;

    public ClientApplication(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start() {
        ClientConnector clientConnector = new ClientConnector(ip, port);
        commandDefinitions = clientConnector.sendHello();

        try (AbstractInput input = new ConsoleInput()) {
            ClientInputProcessor inputProcessor = new ClientInputProcessor(commandDefinitions, clientConnector);
            inputProcessor.processInput(input, true);
        } catch (Exception e) {
            logger.error("Ошибка: ", e);
        }
    }
}
