package ru.lenok.common.models;

import java.time.LocalDateTime;

public class LabWorkWithKey extends LabWork{
    private final String key;

    public LabWorkWithKey(String key, LabWork labWork) {
        super(labWork.getId(), labWork.getName(), labWork.getCoordinates(), labWork.getMinimalPoint(), labWork.getDescription(), labWork.getDifficulty(), labWork.getDiscipline(), labWork.getCreationDate(), labWork.getOwnerId());
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
