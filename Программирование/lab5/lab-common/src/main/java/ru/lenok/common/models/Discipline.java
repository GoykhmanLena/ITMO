package ru.lenok.common.models;

import lombok.Data;

@Data
public class Discipline {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private long practiceHours;

    public boolean validate() {
        return name != null && !name.equals("");
    }
}