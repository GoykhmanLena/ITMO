package ru.lenok.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.lenok.common.models.LabWork;

@Data
@AllArgsConstructor
public class CommandRequest {
    private CommandWithArgument commandWithArgument;
    private LabWork element;
}
