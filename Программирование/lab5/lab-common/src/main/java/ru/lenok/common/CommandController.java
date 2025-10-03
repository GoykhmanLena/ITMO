package ru.lenok.common;

import ru.lenok.common.commands.AbstractCommand;
import ru.lenok.common.commands.CommandWithElement;

public class CommandController {

    public CommandResponse handle(CommandRequest request) {
        CommandWithArgument commandWithArgument = request.getCommandWithArgument();
        AbstractCommand command = commandWithArgument.getCommand();
        CommandResponse commandResponse;
        try {
            String executionResult;
            if (command instanceof CommandWithElement) {
                CommandWithElement commandWithElement = (CommandWithElement) command;
                executionResult = commandWithElement.execute(commandWithArgument.getArgument(), request.getElement());
            } else {
                executionResult = command.execute(commandWithArgument.getArgument());
            }
            commandResponse = new CommandResponse(executionResult);
        } catch (Exception e) {
            commandResponse = new CommandResponse(e);
        }
        return commandResponse;
    }
}
