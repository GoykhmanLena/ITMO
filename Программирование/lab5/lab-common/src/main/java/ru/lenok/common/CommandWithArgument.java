package ru.lenok.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.lenok.common.commands.AbstractCommand;

@Data
@AllArgsConstructor
public class CommandWithArgument {
    private final AbstractCommand command;
    private final String argument;
}