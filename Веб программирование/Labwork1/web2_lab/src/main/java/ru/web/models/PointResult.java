package ru.web.models;

import java.io.Serializable;
import java.util.Date;

public class PointResult implements Serializable {
    private double x;
    private double y;
    private double r;
    private boolean isHit;
    private Date timestamp;

    public PointResult(double x, double y, double r, boolean isHit) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.isHit = isHit;
        this.timestamp = new Date();
    }

    // Getters for JSP
    public double getX() { return x; }
    public double getY() { return y; }
    public double getR() { return r; }
    public boolean getIsHit() {return isHit;}
    public Date getTimestamp() {return timestamp;}

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setR(double r) { this.r = r; }
    public void setHit(boolean hit) { isHit = hit; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "PointResult{" +
                "x=" + x +
                ", y=" + y +
                ", r=" + r +
                ", isHit=" + isHit +
                ", timestamp=" + timestamp +
                '}';
    }
}