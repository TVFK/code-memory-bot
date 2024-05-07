package ru.taf.enums;

public enum BotCommands {
    HELP("/help"),
    START("/start"),
    CANCEL("/cancel"),
    ALL_MEMORY_PAGES("/my_pages"),
    NEW_MEMORY_PAGE("/new_page");

    private final String command;

    BotCommands(String command) {
        this.command = command;
    }

    @Override
    public String toString(){
        return command;
    }

    public static BotCommands fromValue(String v) {
        for (BotCommands c: BotCommands.values()) {
            if (c.command.equals(v)) {
                return c;
            }
        }
        return null;
    }
}
