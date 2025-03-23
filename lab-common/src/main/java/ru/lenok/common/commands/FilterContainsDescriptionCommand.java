package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;


public class FilterContainsDescriptionCommand extends AbstractCommand {
    LabWorkService collection;

    public FilterContainsDescriptionCommand(LabWorkService collection) {
        super("filter_contains_description description", "вывести элементы, значение поля description которых содержит заданную подстроку");
        this.collection = collection;
    }

    @Override
    public String execute(String arg) {
        return collection.filterWithDescription(arg);
    }

    @Override
    public boolean hasArg() {
        return true;
    }
}
