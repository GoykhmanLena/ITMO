package ru.lenok.server.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.server.collection.LabWorkService;
import ru.lenok.common.commands.AbstractCommand;
import ru.lenok.common.commands.CommandDefinition;
import ru.lenok.common.models.LabWork;

public class UpdateByIdInCollectionCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public UpdateByIdInCollectionCommand(LabWorkService labWorkService) {
        super(CommandDefinition.update_id, "Аргумент - id. Элемент. Обновить значение элемента коллекции, id которого равен заданному");
        this.labWorkService = labWorkService;
    }

    @Override
    public CommandResponse execute(String id_str, LabWork element) {
        Long id;
        try {
            id = Long.parseLong(id_str);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("id имеет формат Long, попробуйте ввести еще раз");
        }
        labWorkService.updateByLabWorkId(id, element);
        return new CommandResponse(EMPTY_RESULT);
    }
}