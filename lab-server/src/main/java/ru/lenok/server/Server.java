package ru.lenok.server;

import ru.lenok.common.Application;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public final class Server {

    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) throws IOException {
        String lena = "Лена";
        byte[] bytes = lena.getBytes();
        byte[] bytes1 = lena.getBytes(StandardCharsets.UTF_8);
        byte[] bytes2 = lena.getBytes("cp1251");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        int read = inputStream.read();

        Application app = new Application(args);
        app.start();
    }
}

