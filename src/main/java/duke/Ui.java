package duke;

public class Ui {

    /**
     * Displays the given lines in a formatted box.
     *
     * @param lines lines to display in the box
     */
    public static void box(String... lines) {
        if (lines == null || lines.length == 0) {
            return; // Nothing to display
        }

        System.out.println("____________________________________________________________");
        for (String s : lines) {
            System.out.println(s);
        }
        System.out.println("____________________________________________________________");
    }

    /**
     * Displays error messages in a formatted box.
     *
     * @param lines error message lines to display
     */
    public static void error(String... lines) {
        box(lines);
    }

    /**
     * Displays a welcome message.
     *
     * @param appName name of the application
     * @param welcomeMessage welcome message to display
     */
    public static void showWelcome(String appName, String welcomeMessage) {
        box("Hello! I'm " + appName, welcomeMessage);
    }

    /**
     * Displays a goodbye message.
     */
    public static void showGoodbye() {
        box("Bye. Hope to see you again soon!");
    }
}
