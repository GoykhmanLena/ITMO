package ru.lenok.common.commands;


import ru.lenok.common.InputProcessor;
import ru.lenok.common.LabWorkService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.lenok.common.commands.CommandName.*;

public class CommandRegistry {
    private final LabWorkService labWorkService;
    public Map<CommandName, AbstractCommand> commands = new HashMap<>();

    public CommandRegistry(LabWorkService labWorkService, InputProcessor inputProcessor) {
        this.labWorkService = labWorkService;
        commands.put(insert, new InsertToCollectionCommand(labWorkService));
        commands.put(exit, new ExitFromProgramCommand());
        commands.put(show, new ShowCollectionCommand(labWorkService));
        commands.put(save, new SaveToFileCommand(labWorkService));
        commands.put(remove_key, new RemoveByKeyFromCollectionCommand(labWorkService));
        commands.put(update_id, new UpdateByIdInCollectionCommand(labWorkService));
        commands.put(print_ascending, new PrintAscendingCommand(labWorkService));
        commands.put(remove_greater, new RemoveGreaterFromCollectionCommand(labWorkService));
        commands.put(replace_if_greater, new ReplaceIfGreaterInCollectionCommand(labWorkService));
        commands.put(filter_contains_description, new FilterContainsDescriptionCommand(labWorkService));
        commands.put(filter_starts_with_name, new FilterStartsWithNameCommand(labWorkService));
        commands.put(help, new HelpCommand(this));
        commands.put(info, new InfoAboutCollectionCommand(labWorkService));
        commands.put(clear, new ClearCollectionCommand(labWorkService));
        commands.put(execute_script, new ExecuteScriptCommand(labWorkService, inputProcessor));
        commands.put(history, new HistoryCommand(inputProcessor));
    }

    public AbstractCommand getCommand(CommandName commandName) throws IllegalArgumentException {
        return commands.get(commandName);
    }

    public String getCommandDescription(CommandName commandName) {
        AbstractCommand command = getCommand(commandName);
        return command.getName() + ": " + command.getDescription();
    }

    public Collection<CommandName> getCommandNames() {
        return commands.keySet();
    }
}
