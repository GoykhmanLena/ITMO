package ru.lenok.common;

import ru.lenok.common.input.AbstractInput;
import ru.lenok.common.input.ConsoleInput;
import ru.lenok.common.models.LabWork;
import ru.lenok.common.util.JsonReader;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Hashtable;

import static java.lang.Math.max;
import static ru.lenok.common.LabWorkService.idCounter;

public class Application {
    public static String filename;
    String[] args;
    private LabWorkService labWorkService;

    public Application(String[] args) {
        this.args = args;
        init();
    }

    public void start() throws IOException {
        PrintStream ps = new PrintStream(System.out, true);
        System.setErr(ps);
        JsonReader jsonReader = new JsonReader();
        Hashtable<String, LabWork> map = new Hashtable<>();
        HashSet<Long> setOfId = new HashSet<>();
        if (args.length > 0) {
            filename = args[0];
            try {
                map = jsonReader.loadFromJson(filename);
                System.out.println("Файл успешно загружен");
            } catch (IOException e) {
                System.err.println("Ошибка при чтении файла: " + e.getMessage());
                System.err.println("Программа завершается");
                System.exit(0);
            }
            for (LabWork labWork : map.values()) {
                idCounter = max(labWork.getId(), idCounter);
                setOfId.add(labWork.getId());
                if (!labWork.validate()) {
                    System.err.println("В файле еть данные, которые не соответствуют необходимым критериям, поэтому коллекция будет очищена");
                    map.clear();
                    idCounter = 0;
                    break;
                }
            }
            if (setOfId.size() < map.size()) {
                System.err.println("В файле есть повторяющиеся id, содержимое коллекции будет перезаписано");
                map.clear();
                idCounter = 0;
            }
            labWorkService = new LabWorkService(map);
        } else {
            System.out.println("Не был введен файл для чтения и записи");
            System.err.println("Программа завершается");
            System.exit(0);
        }
        try (AbstractInput input = new ConsoleInput()) {
            InputProcessor inputProcess = new InputProcessor(labWorkService);
            inputProcess.processInput(input, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(collection);
    }

    void init() {
    }
}
