package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;
import ru.lenok.common.models.LabWork;

public class InfoAboutCollectionCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public InfoAboutCollectionCommand(LabWorkService labWorkService) {
        super("info", "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        this.labWorkService = labWorkService;
    }

    @Override
    public String execute(String arg) {
        return ("Это LabWorkCollection, текущий размер: " + labWorkService.getCollectionSize() + ", состоит из элементов типа: " + LabWork.class + "\n");
    }
}
