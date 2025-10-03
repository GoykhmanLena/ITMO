package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;


public class FilterStartsWithNameCommand extends AbstractCommand {
    LabWorkService collection;

    public FilterStartsWithNameCommand(LabWorkService collection) {
        super("filter_starts_with_name name ", "вывести элементы, значение поля name которых начинается с заданной подстроки");
        this.collection = collection;
    }

    @Override
    public String execute(String arg) {
        return collection.filterWithName(arg);
    }

    @Override
    public boolean hasArg() {
        return true;
    }
}
