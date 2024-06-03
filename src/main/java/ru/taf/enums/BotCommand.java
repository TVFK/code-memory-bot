package ru.taf.enums;

public enum BotCommand {
    HELP("/help"),
    START("/start"),
    ALL_MEMORY_PAGES("/my_pages"),
    NEW_MEMORY_PAGE("/new_page");

    private final String command;

    BotCommand(String command) {
        this.command = command;
    }

    @Override
    public String toString(){
        return command;
    }

    public static BotCommand fromValue(String v) {
        for (BotCommand c: BotCommand.values()) {
            if (c.command.equals(v)) {
                return c;
            }
        }
        return null;
    }
}
