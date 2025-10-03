package ru.lenok.server.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.common.commands.AbstractCommand;

import static ru.lenok.common.commands.CommandDefinition.history;

public class HistoryCommand extends AbstractCommand {
    private final IHistoryProvider historyProvider;

    public HistoryCommand(IHistoryProvider historyProvider) {
        super(history, "вывести последние 15 команд (без их аргументов)");
        this.historyProvider = historyProvider;
    }

    @Override
    public CommandResponse execute(String clientID) {
        String lastNCommands = historyProvider.getHistoryByClientID(clientID).getLastNCommands(15);
        return new CommandResponse("История клиента с ID: " + clientID + "\n" + lastNCommands);
    }
}
