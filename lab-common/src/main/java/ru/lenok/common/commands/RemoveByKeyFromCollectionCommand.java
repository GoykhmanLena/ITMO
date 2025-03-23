package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;

public class RemoveByKeyFromCollectionCommand extends AbstractCommand {
    LabWorkService collection;

    public RemoveByKeyFromCollectionCommand(LabWorkService collection) {
        super("remove_key null", "удалить элемент из коллекции по его ключу");
        this.collection = collection;
    }

    @Override
    public boolean hasArg() {
        return true;
    }

    @Override
    public String execute(String key) {
        collection.remove(key);
        return EMPTY_RESULT;
    }
}
