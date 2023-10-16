package seedu.duke;

import seedu.duke.ui.Ui;

import java.util.Scanner;

/**
 * Main entry-point for the java.duke.CafeCtrl application.
 */
public class CafeCtrl {
    private static Ui ui; // Declare ui as a static variable

    public static void main(String[] args) {
        ui = new Ui();

        ui.showWelcome();
        ui.showLogo();

        Scanner in = new Scanner(System.in);
        System.out.println(in.nextLine());
        ui.showGoodbye();
    }
}




