package ru.lenok.server.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.server.collection.LabWorkService;
import ru.lenok.common.commands.AbstractCommand;
import ru.lenok.common.commands.CommandDefinition;


public class PrintAscendingCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public PrintAscendingCommand(LabWorkService labWorkService) {
        super(CommandDefinition.print_ascending, "вывести элементы коллекции в порядке возрастания");
        this.labWorkService = labWorkService;
    }
    @Override
    public CommandResponse execute(String arg) {
        return new CommandResponse(labWorkService.sortedByNameCollection());
    }
}
