package ru.lenok.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.lenok.common.models.LabWork;
import ru.lenok.common.util.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Data
public class LabWorkService {
    public static long idCounter;
    private Storage storage;

    public LabWorkService(Hashtable<String, LabWork> initialState) {
        this.storage = new Storage(initialState);
    }

    public String put(String key, LabWork lab) {
        String warning = null;
        if (storage.getMap().containsKey(key)) {
            warning = "ПРЕДУПРЕЖДЕНИЕ: элемент с таким ключом уже существовал, он будет перезаписан, ключ = " + key;
        }
        storage.put(key, lab);
        return warning;
    }

    public void remove(String key) {
        storage.remove(key);
    }

    public int getCollectionSize() {
        return storage.length();
    }

    public void clear_collection() {
        storage.clear();
    }

    public String getCollectionAsJson() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        String json = gson.toJson(getStorage().getMap());
        return json;
    }

    public String filterWithDescription(String descript_part) {
        StringBuilder answer = new StringBuilder("");
        for (String key : storage.getMap().keySet()) {
            LabWork labWork = storage.getMap().get(key);
            if (labWork.getDescription().contains(descript_part)) {
                answer.append(key + " = " + labWork + "\n");
            }
        }
        return (answer.toString());
    }

    public String filterWithName(String name_part) {
        StringBuilder answer = new StringBuilder("");
        List keys = new ArrayList<>(storage.getMap().keySet());
        for (int i = 0; i < keys.size(); i++) {
            if (storage.getMap().get(keys.get(i)).getName().startsWith(name_part)) {
                answer.append(keys.get(i) + " = " + storage.getMap().get(keys.get(i)) + "\n");
            }
        }
        return (answer.toString());
    }

    public void removeGreater(LabWork elem) {
        List keys = new ArrayList<>(storage.getMap().keySet());
        //       System.out.println(keys);
        for (int i = 0; i < keys.size(); i++) {
            //           System.out.println(storage.getMap().get(keys.get(i)).compareTo(elem));
            if (storage.getMap().get(keys.get(i)).compareTo(elem) > 0) {
                storage.remove((String) keys.get(i));
            }
        }
        idCounter--;
    }

    public void replaceIfGreater(String key, LabWork elem) {
        if (storage.getMap().get(key).compareTo(elem) < 0) {
            storage.put(key, elem);
        }
    }

    public void updateByLabWorkId(Long id, LabWork labWork) {
        String key = getKeyByLabWorkId(id);
        labWork.setId(id);
        storage.put(key, labWork);
    }

    public String toString() {
        return storage.toString();
    }

    public String getKeyByLabWorkId(Long id) {
        for (String key : storage.getMap().keySet()) {
            if (storage.getMap().get(key).getId() == id) {
                return key;
            }

        }
        throw new IllegalArgumentException("Нет элемента с таким id");
    }

    public String sortedByNameCollection(String arg) {
        List<LabWorkEntry> entryList = new ArrayList<>();
        for (Map.Entry<String, LabWork> mapEntry : getStorage().getMap().entrySet()) {
            entryList.add(new LabWorkEntry(mapEntry.getKey(), mapEntry.getValue()));

        }
        Collections.sort(entryList);
        StringBuilder result = new StringBuilder();
        for (LabWorkEntry labWorkEntry : entryList) {
            String key = labWorkEntry.key;
            result.append(key).append(" = ").append(labWorkEntry.labWork).append("\n");
        }
        return result.toString();
    }

    @AllArgsConstructor
    private static class LabWorkEntry implements Comparable<LabWorkEntry> {
        String key;
        LabWork labWork;

        @Override
        public int compareTo(LabWorkEntry labWorkEntry) {
            return this.labWork.getName().compareTo(labWorkEntry.labWork.getName());
        }
    }
}
