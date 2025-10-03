package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static ru.lenok.common.Application.filename;

public class SaveToFileCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public SaveToFileCommand(LabWorkService labWorkService) {
        super("save", "сохранить коллекцию в файл");
        this.labWorkService = labWorkService;
    }

    @Override
    public String execute(String arg) throws IOException {
        String json = labWorkService.getCollectionAsJson();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(json);
        }
        return EMPTY_RESULT;
    }
}
