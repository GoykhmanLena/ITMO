package ru.lenok.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.lenok.common.commands.CommandDefinition;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CommandWithArgument implements Serializable {
    private final CommandDefinition commandDefinition;
    private final String argument;
}