package ru.lenok.server.collection;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lenok.common.models.LabWork;
import ru.lenok.common.models.LabWorkWithKey;
import ru.lenok.server.daos.DBConnector;
import ru.lenok.server.daos.LabWorkDAO;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Data
public class LabWorkService {
    private static final Logger logger = LoggerFactory.getLogger(LabWorkService.class);

    private MemoryStorage memoryStorage;
    private final LabWorkDAO labWorkDAO;
    private final Object monitor;
    private final DataSource ds;
    Consumer<List<LabWorkWithKey>> clientNotifier;

    public LabWorkService(LabWorkDAO labWorkDAO, DBConnector dbConnector, Consumer<List<LabWorkWithKey>> clientNotifier) throws SQLException {
        this.labWorkDAO = labWorkDAO;
        ds = dbConnector.getDatasource();
        this.memoryStorage = new MemoryStorage(new Hashtable<>(labWorkDAO.selectAll()));
        monitor = memoryStorage.getMonitor();
        this.clientNotifier = clientNotifier;
    }

    public String getMapAsString() {
        synchronized (monitor) {
            return memoryStorage.getCollectionAsString();
        }
    }

    public List<LabWorkWithKey> getLabWorkList() {
        synchronized (monitor) {
            Map<String, LabWork> mapSafe = memoryStorage.getMapSafe();
            return mapSafe.entrySet().stream()
                    .map(e -> new LabWorkWithKey(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
        }
    }

    public String put(String key, LabWork labWork) throws SQLException {
        synchronized (monitor) {
            Long id = memoryStorage.getId(key);
            if (id != null) {
                logger.warn("Элемент с таким ключом уже существует, будет обновлено содержимое, id остается прежним, key = " + key  + ", id = " + id);
                checkAccess(labWork.getOwnerId(), key);
                labWork.setId(id);
                updateByLabWorkId(id, labWork);
            }
            else {
                Long elemId = labWorkDAO.insert(key, labWork);
                labWork.setId(elemId);
            }
            memoryStorage.put(key, labWork);
            clientNotifier.accept(getLabWorkList());
            return "";
        }
    }

    public void remove(String key) throws SQLException {
        synchronized (monitor) {
            labWorkDAO.delete(key);
            memoryStorage.remove(key);
            clientNotifier.accept(getLabWorkList());
        }
    }

    public int getCollectionSize() {
        synchronized (monitor) {
            return memoryStorage.length();
        }
    }

    public void clearCollection(long ownerId) throws SQLException {
        synchronized (monitor) {
            labWorkDAO.deleteForUser(ownerId);
            memoryStorage.deleteForUser(ownerId);
            clientNotifier.accept(getLabWorkList());
        }
    }

    /*   public String getCollectionAsJson() throws IOException {
           Gson gson = new GsonBuilder()
                   .setPrettyPrinting()
                   .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                   .create();
           String json = gson.toJson(getMemoryStorage().getMap());
           return json;
       }
   */
    public String filterWithDescription(String descriptPart) {
        synchronized (monitor) {
            return memoryStorage.filterWithDescription(descriptPart);
        }
    }

    public String filterWithName(String namePart) {
        synchronized (monitor) {
            return memoryStorage.filterWithName(namePart);
        }
    }

    public void removeGreater(LabWork elem, long userId) throws SQLException {
        synchronized (monitor) {
            List<String> keysForRemoving = memoryStorage.keysOfGreater(elem, userId);
            labWorkDAO.deleteByKeys(keysForRemoving);
            keysForRemoving.forEach(key -> memoryStorage.remove(key));
            clientNotifier.accept(getLabWorkList());
        }
    }


    public void replaceIfGreater(String key, LabWork newLabWork) throws SQLException {
        synchronized (monitor) {
            try (Connection connection = ds.getConnection()) {
                if (memoryStorage.comparing(key, newLabWork)) {
                    checkAccess(newLabWork.getOwnerId(), key);
                    newLabWork.setId(memoryStorage.getId(key));
                    labWorkDAO.updateById(key, newLabWork, connection);
                    memoryStorage.put(key, newLabWork);
                    clientNotifier.accept(getLabWorkList());
                }
            }
        }
    }

    public void updateByLabWorkId(Long id, LabWork labWork) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            updateByLabWorkIdWithConnection(id, labWork, connection);
        }
    }

    public void updateByLabWorkIdWithConnection(Long id, LabWork labWork, Connection connection) throws SQLException {
        synchronized (monitor) {
            String key = memoryStorage.getKeyByLabWorkId(id);
            checkAccess(labWork.getOwnerId(), key);
            labWork.setId(id);
            labWorkDAO.updateById(key, labWork, connection);
            memoryStorage.put(key, labWork);
            clientNotifier.accept(getLabWorkList());
        }
    }

    public void checkAccess(Long currentUserId, String key) {
        synchronized (monitor) {
            memoryStorage.checkAccess(currentUserId, key);
        }
    }

    public LabWork getLabWorkById(Long labWorkId) {
        synchronized (monitor) {
            return memoryStorage.getLabWorkById(labWorkId);
        }
    }
}
