package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;
import ru.lenok.common.models.LabWork;

public class UpdateByIdInCollectionCommand extends CommandWithElement {
    LabWorkService labWorkService;

    public UpdateByIdInCollectionCommand(LabWorkService labWorkService) {
        super("update id {element}", "обновить значение элемента коллекции, id которого равен заданному");
        this.labWorkService = labWorkService;
    }

    @Override
    public String execute(String key, LabWork element) {
        Long id;
        try {
            id = Long.parseLong(key);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("id имеет формат Long, попробуйте ввести еще раз");
        }
        labWorkService.updateByLabWorkId(id, element);
        return EMPTY_RESULT;
    }

    @Override
    public boolean hasArg() {
        return true;
    }
}