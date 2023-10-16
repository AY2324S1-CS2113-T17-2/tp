package seedu.duke.ui;

/**
 * Enumerates messages to be displayed to users.
 */
public enum UserOutput {
    WELCOME_MESSAGE("Hello! Welcome to CafeCTRL!"),
    GOODBYE_MESSAGE("Goodbye <3 Have a great day ahead!"),
    LOGO("  _____        __     _____ _        _ \n"
            + "/  __ \\      / _|   /  __ \\ |      | |\n"
            + "| /  \\/ __ _| |_ ___| /  \\/ |_ _ __| |\n"
            + "| |    / _` |  _/ _ \\ |   | __| '__| |\n"
            + "| \\__/\\ (_| | ||  __/ \\__/\\ |_| |  | |\n"
            + " \\____/\\__,_|_| \\___|\\____/\\__|_|  |_|\n");

    public final String message;
    UserOutput(String message) {
        this.message = message;
    }
}
