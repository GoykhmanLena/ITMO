package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;

import static ru.lenok.common.LabWorkService.idCounter;

public class ClearCollectionCommand extends AbstractCommand {
    LabWorkService collection;

    public ClearCollectionCommand(LabWorkService collection) {
        super("clear", "очистить коллекцию");
        this.collection = collection;
    }

    @Override
    public String execute(String arg) {
        collection.clear_collection();
        idCounter = 0;
        return EMPTY_RESULT;
    }
}
