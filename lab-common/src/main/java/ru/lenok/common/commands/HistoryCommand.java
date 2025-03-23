package ru.lenok.common.commands;

import ru.lenok.common.InputProcessor;

public class HistoryCommand extends AbstractCommand {
    private final InputProcessor inputProcessor;

    public HistoryCommand(InputProcessor inputProcessor) {
        super("history", "вывести последние 15 команд (без их аргументов)");
        this.inputProcessor = inputProcessor;
    }

    @Override
    public String execute(String arg) {
        return inputProcessor.getHistoryList().getLastNCommands(15);
    }
}
