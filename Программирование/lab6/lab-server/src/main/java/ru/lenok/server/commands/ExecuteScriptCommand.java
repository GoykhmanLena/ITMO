package ru.lenok.server.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.server.collection.LabWorkService;
import ru.lenok.common.commands.AbstractCommand;

import java.io.IOException;

import static ru.lenok.common.commands.CommandDefinition.execute_script;


public class ExecuteScriptCommand extends AbstractCommand {

    public ExecuteScriptCommand(LabWorkService labWorkService) {
        super(execute_script, "Аргумент - filename, считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
    }

    public CommandResponse execute(String arg) throws IOException {
        return new CommandResponse("execute_script добавлен в историю");
    }
}
