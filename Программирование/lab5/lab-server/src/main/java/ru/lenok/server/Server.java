package ru.lenok.server;

import ru.lenok.common.Application;

import java.io.IOException;


public final class Server {

    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) throws IOException {
        Application app = new Application(args);
        app.start();
    }
}

