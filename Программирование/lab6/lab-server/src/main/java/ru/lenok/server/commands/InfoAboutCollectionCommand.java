package ru.lenok.server.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.server.collection.LabWorkService;
import ru.lenok.common.commands.AbstractCommand;
import ru.lenok.common.models.LabWork;

import static ru.lenok.common.commands.CommandDefinition.info;

public class InfoAboutCollectionCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public InfoAboutCollectionCommand(LabWorkService labWorkService) {
        super(info, "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        this.labWorkService = labWorkService;
    }

    @Override
    public CommandResponse execute() {
        return new CommandResponse("Это LabWorkCollection, текущий размер: " + labWorkService.getCollectionSize() + ", состоит из элементов типа: " + LabWork.class + "\n");
    }
}
