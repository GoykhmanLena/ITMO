package ru.lenok.server.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.server.collection.LabWorkService;
import ru.lenok.common.commands.AbstractCommand;
import ru.lenok.common.commands.CommandDefinition;
import ru.lenok.common.models.LabWork;

import java.util.Map;

import static ru.lenok.server.collection.LabWorkService.sortMapAndStringify;


public class FilterStartsWithNameCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public FilterStartsWithNameCommand(LabWorkService labWorkService) {
        super(CommandDefinition.filter_starts_with_name, "Аргумент - name. Вывести элементы, значение поля name которых начинается с заданной подстроки");
        this.labWorkService = labWorkService;
    }

    @Override
    public CommandResponse execute(String arg) {
        Map<String, LabWork> filteredMap = labWorkService.filterWithDescription(arg);
        return new CommandResponse(sortMapAndStringify(filteredMap));
    }
}
