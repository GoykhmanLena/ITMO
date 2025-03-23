package ru.lenok.common.commands;

public class HelpCommand extends AbstractCommand {
    private final CommandRegistry commandRegistry;

    public HelpCommand(CommandRegistry inv) {
        super("help", "вывести справку по доступным командам");
        this.commandRegistry = inv;
    }

    @Override
    public String execute(String arg) {
        StringBuffer sb = new StringBuffer();
        for (CommandName commandName : commandRegistry.getCommandNames()) {
            sb.append(commandRegistry.getCommandDescription(commandName) + "\n");
        }
        return sb.toString();
    }
}
