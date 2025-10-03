package ru.lenok.common.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class Coordinates implements Serializable, Comparable<Coordinates> {
    private double x;
    private Float y;

    public boolean validate() {
        return y != null;
    }

    @Override
    public int compareTo(Coordinates o) {
        if (o == null) {
            throw new NullPointerException("Сравниваемый объект не может быть null");
        }
        if (this.y == null || o.y == null) {
            throw new IllegalStateException("Поле y не может быть null при сравнении");
        }

        double thisLength = Math.sqrt(this.x * this.x + this.y * this.y);
        double otherLength = Math.sqrt(o.x * o.x + o.y * o.y);

        return Double.compare(thisLength, otherLength);
    }
}