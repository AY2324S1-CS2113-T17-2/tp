package seedu.duke.ui;

/**
 * Enumerates messages to be displayed to users.
 */
public enum UserOutput {
    WELCOME_MESSAGE("Hello! Welcome to CafeCTRL!"),
    GOODBYE_MESSAGE("Goodbye <3 Have a great day ahead!");

    public final String message;
    UserOutput(String message) {
        this.message = message;
    }
}
