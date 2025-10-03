package ru.lenok.common.commands;


import static ru.lenok.common.commands.ArgType.*;

public enum CommandDefinition {
    insert(STRING, true),
    exit(false, true),
    show(),
    save(),
    remove_key(STRING),
    update_id(LONG, true),
    print_ascending(),
    remove_greater(true, false),
    replace_if_greater(STRING, true),
    filter_contains_description(STRING),
    filter_starts_with_name(STRING),
    help(),
    info(),
    clear(),
    history(),
    execute_script(STRING, false, true);
    private final ArgType argType;
    private final boolean hasElement;
    private final boolean isClient;
    CommandDefinition(ArgType argType, boolean hasElement, boolean isClient){
        this.argType = argType;
        this.hasElement = hasElement;
        this.isClient = isClient;
    }
    CommandDefinition(){
        this(null, false, false);
    }
    CommandDefinition(ArgType argType){
        this(argType, false, false);
    }
    CommandDefinition(ArgType argType, boolean hasElement){
        this(argType, hasElement, false);
    }
    CommandDefinition(boolean hasElement, boolean isClient){
        this(null, hasElement, isClient);
    }
    public boolean hasElement(){
        return this.hasElement;
    }
    public ArgType getArgType(){
        return this.argType;
    }
    public boolean hasArg(){return this.argType != null;}
    public boolean isClient(){
        return this.isClient;
    }
}
