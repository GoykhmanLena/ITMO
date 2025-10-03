package ru.lenok.server.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.common.commands.AbstractCommand;
import ru.lenok.common.commands.CommandDefinition;

public class ExitFromProgramCommand extends AbstractCommand {
    public ExitFromProgramCommand() {
        super(CommandDefinition.exit, "завершить программу (без сохранения в файл)");
    }

    @Override
    public CommandResponse execute() {
        return new CommandResponse(EMPTY_RESULT);
    }
}
