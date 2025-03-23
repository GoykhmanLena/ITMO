package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;

public class ShowCollectionCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public ShowCollectionCommand(LabWorkService labWorkService) {
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        this.labWorkService = labWorkService;
    }

    @Override
    public String execute(String arg) {
        return labWorkService.getCollectionSize() == 0 ? "ПУСТАЯ КОЛЛЕКЦИЯ" : labWorkService.toString();
    }

}
