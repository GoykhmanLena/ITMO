package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;
import ru.lenok.common.models.LabWork;


public class ReplaceIfGreaterInCollectionCommand extends CommandWithElement {
    LabWorkService collection;

    public ReplaceIfGreaterInCollectionCommand(LabWorkService collection) {
        super("replace_if_greater null {element}", "заменить значение по ключу, если новое значение больше старого");
        this.collection = collection;
    }


    @Override
    public String execute(String key, LabWork element) {
        collection.replaceIfGreater(key, element);
        return EMPTY_RESULT;
    }

    @Override
    public boolean hasArg() {
        return true;
    }
}
