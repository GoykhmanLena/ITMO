package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;
import ru.lenok.common.models.LabWork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PrintAscendingCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public PrintAscendingCommand(LabWorkService labWorkService) {
        super("print_ascending", "вывести элементы коллекции в порядке возрастания");
        this.labWorkService = labWorkService;
    }

    //    @Override
    public void execute2(String arg) {
        List<LabWork> col = new ArrayList<>(labWorkService.getStorage().getMap().values());
        Collections.sort(col);
        for (LabWork labWork : col) {
            System.out.println(labWork);
        }
    }

    public String execute(String arg) {
        return labWorkService.sortedByNameCollection(arg);
    }
}
