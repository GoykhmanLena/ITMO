package ru.lenok.server.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.server.collection.LabWorkService;
import ru.lenok.common.commands.AbstractCommand;
import ru.lenok.common.models.LabWork;

import static ru.lenok.common.commands.CommandDefinition.remove_greater;

public class RemoveGreaterFromCollectionCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public RemoveGreaterFromCollectionCommand(LabWorkService labWorkService) {
        super(remove_greater, "Элемент. Удалить из коллекции все элементы, превышающие заданный");
        this.labWorkService = labWorkService;
    }

    @Override
    public CommandResponse execute(LabWork element) {
        labWorkService.removeGreater(element);
        return new CommandResponse(EMPTY_RESULT);
    }
}
