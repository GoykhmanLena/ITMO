package ru.web.utils;

public class AreaChecker {
    public static boolean check(double x, double y, double r){
        if (x >= 0 && y >= 0)
            return y <= ((r/2) - (x/2));
        if (x >= 0 && y <= 0 && x <= r)
            return y >= (-r/2);
        if (x <= 0 && y <= 0)
            return x*x + y*y <= r*r;
        return false;
    }
}
