package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;
import ru.lenok.common.models.LabWork;

public class RemoveGreaterFromCollectionCommand extends CommandWithElement {
    LabWorkService collection;

    public RemoveGreaterFromCollectionCommand(LabWorkService collection) {
        super("remove_greater {element}", "удалить из коллекции все элементы, превышающие заданный");
        this.collection = collection;
    }

    @Override
    public String execute(String arg, LabWork element) {
        collection.removeGreater(element);
        return EMPTY_RESULT;
    }
}
