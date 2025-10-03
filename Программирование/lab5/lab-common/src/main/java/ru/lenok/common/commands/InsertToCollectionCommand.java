package ru.lenok.common.commands;


import ru.lenok.common.LabWorkService;
import ru.lenok.common.models.LabWork;


public class InsertToCollectionCommand extends CommandWithElement {
    LabWorkService labWorkService;


    public InsertToCollectionCommand(LabWorkService labWorkService) {
        super("insert null {element}", "добавить новый элемент с заданным ключом");
        this.labWorkService = labWorkService;
    }

    @Override
    public String execute(String key, LabWork element) {
        String warning = labWorkService.put(key, element);
        return warning == null ? EMPTY_RESULT : warning;
    }

    @Override
    public boolean hasArg() {
        return true;
    }
}
