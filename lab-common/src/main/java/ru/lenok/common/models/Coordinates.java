package ru.lenok.common.models;

import lombok.Data;

@Data
public class Coordinates {
    private double x;
    private Float y; //Поле не может быть null

    public boolean validate() {
        return y != null;
    }
}