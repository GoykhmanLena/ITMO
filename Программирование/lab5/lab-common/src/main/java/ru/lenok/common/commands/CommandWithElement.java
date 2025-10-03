package ru.lenok.common.commands;


import ru.lenok.common.models.LabWork;

import java.io.IOException;

public abstract class CommandWithElement extends AbstractCommand {

    public CommandWithElement(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean hasElement() {
        return true;
    }

    public abstract String execute(String argument, LabWork element);

    @Override
    public String execute(String arg) throws IOException {
        throw new UnsupportedOperationException();
    }
}
