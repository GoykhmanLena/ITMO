package ru.lenok.common;

import lombok.Data;
import ru.lenok.common.commands.AbstractCommand;
import ru.lenok.common.commands.CommandName;
import ru.lenok.common.commands.CommandRegistry;
import ru.lenok.common.input.AbstractInput;
import ru.lenok.common.util.HistoryList;

import java.util.Stack;

@Data

public class InputProcessor {
    public static boolean debug = false;
    private LabWorkService labWorkService;
    private CommandRegistry commandRegistry;
    private HistoryList historyList = new HistoryList();
    private Stack<String> scriptExecutionContext;
    private CommandController commandController;

    public InputProcessor(LabWorkService labWorkService) {
        this.scriptExecutionContext = new Stack<>();
        this.labWorkService = labWorkService;
        init();
    }

    public void processInput(AbstractInput input, boolean interactive) throws Exception {
        String line;
        CommandWithArgument commandWithArgument = null;
        LabWorkItemAssembler labWorkItemAssembler = null;
        CommandRequest commandRequest = null;
        while ((line = input.readLine()) != null) {
            if (labWorkItemAssembler != null) {
                try {
                    labWorkItemAssembler.addNextLine(line);
                } catch (Exception e) {
                    handleException(interactive, e);
                    continue;
                }
                if (labWorkItemAssembler.isFinished()) {
                    commandRequest = new CommandRequest(
                            commandWithArgument,
                            labWorkItemAssembler.getLabWorkElement()
                    );
                    CommandResponse commandResponse = sendRequest(commandRequest);
                    processResponse(commandResponse);
                    labWorkItemAssembler = null;
                }
                continue;
            }
            try {
                commandWithArgument = parseLineAsCommand(line);
            } catch (Exception e) {
                handleException(interactive, e);
                continue;
            }
            if (commandWithArgument.getCommand().hasElement()) {
                labWorkItemAssembler = new LabWorkItemAssembler(interactive);
            } else {
                commandRequest = new CommandRequest(
                        commandWithArgument,
                        null
                );
                CommandResponse commandResponse = sendRequest(commandRequest);
                processResponse(commandResponse);
            }
        }
        if (labWorkItemAssembler != null) {
            if (labWorkItemAssembler.isFinished()) {
                commandRequest = new CommandRequest(
                        commandWithArgument,
                        labWorkItemAssembler.getLabWorkElement()
                );
                CommandResponse commandResponse = sendRequest(commandRequest);
                processResponse(commandResponse);
            } else {
                System.err.println("Внимание! У вас в файле есть невыполненная последняя команда - недостаточно полей введено");
            }
        }
    }

    private void handleException(boolean interactive, Exception e) throws Exception {
        if (!interactive) {
            throw e;
        }
        displayCommonError(e);
    }

    private CommandResponse sendRequest(CommandRequest commandRequest) {
        historyList.addCommand(commandRequest.getCommandWithArgument().getCommand().getName());
        return commandController.handle(commandRequest);
    }

    private void processResponse(CommandResponse commandResponse) {
        if (commandResponse.getError() == null) {
            System.out.println(commandResponse.getOutput());
        } else {
            displayCommonError(commandResponse.getError());
        }

    }

    private CommandWithArgument parseLineAsCommand(String line) {
        String[] splittedLine = line.trim().split("\\s+");
        CommandWithArgument result = null;
        CommandName commandName;
        try {
            commandName = CommandName.valueOf(splittedLine[0]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Такой команды НЕТ: " + splittedLine[0], e);
        }
        AbstractCommand command = commandRegistry.getCommand(commandName);
        if (!command.hasArg() && splittedLine.length >= 2) {
            throw new IllegalArgumentException("Слишком много аргументов, ожидалось 0: " + line);
        } else if (command.hasArg() && (splittedLine.length == 1 || splittedLine.length > 2)) {
            throw new IllegalArgumentException("Неправильное колличество аргументов, ожидался 1: " + line);
        }
        result = new CommandWithArgument(command, splittedLine.length == 2 ? splittedLine[1] : null);
        return result;
    }

    public void setScriptExecutionContext(String path) {
        scriptExecutionContext.push(path);
    }

    public void exitContext() {
        scriptExecutionContext.pop();
    }

    public boolean checkContext(String currentFile) {
        return scriptExecutionContext.contains(currentFile);
    }

    private void displayCommonError(Exception e) {
        if (debug) {
            e.printStackTrace();
        } else {
            System.err.println(e.getMessage());
        }
    }

    void init() {
        this.commandRegistry = new CommandRegistry(labWorkService, this);
        this.commandController = new CommandController();
    }

    /*
    все команды создаются при инициализации - только один раз, при этом в конструктор передается LabWorkService (сервисный слой, который работает со storage - моя коллекция + crud)
    view - InputProcessor должен сформировать CommandRequest, отправить на контроллер CommandController
    CommandRequest состоит из имени команды, ее аргумента и полностью собранного LabWork элемента
    контроллер должен вызвать метод команды Command.execute и передать туда аргумент и LabWork
    Результат работы команды оборачивается в CommandResponse - строка или ошибка, и возвращается в InputProcessor

     */
}
