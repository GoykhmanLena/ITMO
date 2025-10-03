package ru.lenok.common.commands;


import lombok.AllArgsConstructor;
import lombok.Data;
import ru.lenok.common.CommandResponse;
import ru.lenok.common.models.LabWork;

import java.io.IOException;

@Data
@AllArgsConstructor
public abstract class AbstractCommand implements Executable {
    public static final String EMPTY_RESULT = "";
    private final CommandDefinition commandDefinition;
    private final String description;

    public CommandResponse execute(String arg) throws IOException{
        throw new UnsupportedOperationException();
    }
    public CommandResponse execute(String argument, LabWork element) throws IOException{
        throw new UnsupportedOperationException();
    }
    public CommandResponse execute() throws IOException{
        throw new UnsupportedOperationException();
    }
    public CommandResponse execute(LabWork element) throws IOException{
        throw new UnsupportedOperationException();
    }

    public boolean hasElement() {
        return commandDefinition.hasElement();
    }
    public boolean hasArg() {
        return commandDefinition.hasArg();
    }
    public boolean isClientCommand() {
        return commandDefinition.isClient();
    }
    public CommandDefinition getCommandDefinition(){
        return commandDefinition;
    }

}
