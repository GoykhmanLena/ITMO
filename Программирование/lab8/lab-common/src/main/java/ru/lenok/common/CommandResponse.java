package ru.lenok.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommandResponse implements Serializable {
    private final String output;
    private final Exception error;
    private final Object outputObject;

    public CommandResponse(String result, Object objResult) {
        this.output = result;
        this.outputObject = objResult;
        this.error = null;
    }

    public CommandResponse(Exception error) {
        this.error = error;
        this.outputObject = null;
        this.output = null;
    }
}
