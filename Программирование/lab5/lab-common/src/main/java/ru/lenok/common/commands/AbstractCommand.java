package ru.lenok.common.commands;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public abstract class AbstractCommand {
    public static final String EMPTY_RESULT = "";
    private final String name;
    private final String description;

    public abstract String execute(String arg) throws IOException;

    public boolean hasElement() {
        return false;
    }

    public boolean hasArg() {
        return false;
    }

}
