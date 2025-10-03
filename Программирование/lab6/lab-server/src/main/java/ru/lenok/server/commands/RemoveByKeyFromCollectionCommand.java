package ru.lenok.server.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.server.collection.LabWorkService;
import ru.lenok.common.commands.AbstractCommand;

import static ru.lenok.common.commands.CommandDefinition.remove_key;

public class RemoveByKeyFromCollectionCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public RemoveByKeyFromCollectionCommand(LabWorkService labWorkService) {
        super(remove_key, "Аргумент - ключ. Удалить элемент из коллекции по его ключу");
        this.labWorkService = labWorkService;
    }
    @Override
    public CommandResponse execute(String key) {
        labWorkService.remove(key);
        return new CommandResponse(EMPTY_RESULT);
    }
}
