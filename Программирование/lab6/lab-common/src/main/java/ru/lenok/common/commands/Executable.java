package ru.lenok.common.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.common.models.LabWork;

import java.io.IOException;

public interface Executable {
    CommandResponse execute(String arg)throws IOException;
    CommandResponse execute(String argument, LabWork element) throws IOException;
    CommandResponse execute() throws IOException;
    CommandResponse execute(LabWork element) throws IOException;
}
