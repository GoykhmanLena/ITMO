package ru.lenok.server.commands;


import ru.lenok.common.CommandResponse;
import ru.lenok.server.collection.LabWorkService;
import ru.lenok.common.commands.AbstractCommand;
import ru.lenok.common.commands.CommandDefinition;
import ru.lenok.common.models.LabWork;
import ru.lenok.server.utils.IdCounterService;

import static ru.lenok.common.commands.CommandDefinition.insert;


public class InsertToCollectionCommand extends AbstractCommand {
    LabWorkService labWorkService;
    CommandDefinition commandDefinition;


    public InsertToCollectionCommand(LabWorkService labWorkService, CommandDefinition commandDefinition) {
        super(insert, "Аргумент - ключ; Элемент; Добавить новый элемент с заданным ключом");
        this.labWorkService = labWorkService;
        this.commandDefinition = commandDefinition;
    }

    @Override
    public CommandResponse execute(String key, LabWork element) {
        element.setId(IdCounterService.getNextId());
        String warning = labWorkService.put(key, element);
        return new CommandResponse (warning == null ? EMPTY_RESULT : warning);
    }
}
