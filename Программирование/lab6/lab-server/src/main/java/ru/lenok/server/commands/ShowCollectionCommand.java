package ru.lenok.server.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.server.collection.LabWorkService;
import ru.lenok.common.commands.AbstractCommand;

import static ru.lenok.server.collection.LabWorkService.sortMapAndStringify;
import static ru.lenok.common.commands.CommandDefinition.show;

public class ShowCollectionCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public ShowCollectionCommand(LabWorkService labWorkService) {
        super(show, "вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        this.labWorkService = labWorkService;
    }

    @Override
    public CommandResponse execute() {
        String sortCollectionAndStringifyResult = sortMapAndStringify(labWorkService.getWholeMap());
        return new CommandResponse(labWorkService.getCollectionSize() == 0 ? "ПУСТАЯ КОЛЛЕКЦИЯ" : sortCollectionAndStringifyResult);
    }
}
