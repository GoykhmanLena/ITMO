package ru.lenok.server.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.server.collection.LabWorkService;
import ru.lenok.common.commands.AbstractCommand;
import ru.lenok.common.models.LabWork;

import static ru.lenok.common.commands.CommandDefinition.replace_if_greater;


public class ReplaceIfGreaterInCollectionCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public ReplaceIfGreaterInCollectionCommand(LabWorkService labWorkService) {
        super(replace_if_greater, "Аргумент - ключ. Элемент. Заменить значение по ключу, если новое значение больше старого");
        this.labWorkService = labWorkService;
    }


    @Override
    public CommandResponse execute(String key, LabWork element) {
        labWorkService.replaceIfGreater(key, element);
        return new CommandResponse(EMPTY_RESULT);
    }
}
