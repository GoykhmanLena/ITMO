package ru.lenok.common.commands;

public class ExitFromProgramCommand extends AbstractCommand {
    public ExitFromProgramCommand() {
        super("exit", "завершить программу (без сохранения в файл)");
    }

    @Override
    public String execute(String arg) {
        System.out.println("Exiting the application.");
        System.exit(0);
        return EMPTY_RESULT;
    }
}
