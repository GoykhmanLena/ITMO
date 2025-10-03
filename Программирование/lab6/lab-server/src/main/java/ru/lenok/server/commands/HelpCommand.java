package ru.lenok.server.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.common.commands.AbstractCommand;

import java.util.stream.Collectors;

import static ru.lenok.common.commands.CommandDefinition.help;

public class HelpCommand extends AbstractCommand {
    private final CommandRegistry commandRegistry;

    public HelpCommand(CommandRegistry inv) {
        super(help, "вывести справку по доступным командам");
        this.commandRegistry = inv;
    }

    @Override
    public CommandResponse execute() {
        String result = commandRegistry.getClientCommandDefinitions().stream()
                .map(item -> commandRegistry.getCommandDescription(item))
                .collect(Collectors.joining("\n"));
        return new CommandResponse(result);
    }
}
