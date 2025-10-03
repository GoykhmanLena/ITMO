package ru.lenok.server.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.common.commands.AbstractCommand;
import ru.lenok.server.collection.LabWorkService;
import ru.lenok.server.utils.IdCounterService;

import static ru.lenok.common.commands.CommandDefinition.clear;


public class ClearCollectionCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public ClearCollectionCommand(LabWorkService labWorkService) {
        super(clear, "очистить коллекцию");
        this.labWorkService = labWorkService;
    }

    @Override
    public CommandResponse execute(String arg) {
        labWorkService.clear_collection();
        IdCounterService.setId(0);
        return new CommandResponse(EMPTY_RESULT);
    }
}
