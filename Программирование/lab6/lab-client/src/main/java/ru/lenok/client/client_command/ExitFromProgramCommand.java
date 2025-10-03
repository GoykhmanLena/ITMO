package ru.lenok.client.client_command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lenok.common.CommandResponse;
import ru.lenok.common.commands.AbstractCommand;

import static ru.lenok.common.commands.CommandDefinition.exit;

public class ExitFromProgramCommand extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(ExitFromProgramCommand.class);
    public ExitFromProgramCommand() {
        super(exit, "завершить программу клиента (без сохранения в файл)");
    }

    @Override
    public CommandResponse execute() {
        logger.info("Завершаю программу клиента");
        System.exit(0);
        return new CommandResponse(EMPTY_RESULT);
    }
}
