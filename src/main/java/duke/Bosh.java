package duke;

import java.util.List;
import java.util.Scanner;


public class Bosh {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Ui.box("Hello! I'm Bosh", "What can I do for you?");

        // Level-7: load existing tasks, enable auto-save
        Storage storage = new Storage();
        TaskList tasks;
        try {
            List<Task> loaded = storage.load();          // [] if first run
            tasks = new TaskList(loaded, storage);       // auto-save enabled
        } catch (Exception e) {
            Ui.error("Starting with an empty list (load failed): " + e.getMessage());
            tasks = new TaskList(); // fallback: no auto-save
        }

        while (sc.hasNextLine()) {
            String input = sc.nextLine().trim();

            if (input.equals("bye")) {
                Ui.box("Bye. Hope to see you again soon!");
                break;
            }
            try {
                Parser.handle(input, tasks);
            } catch (BoshException e) {
                Ui.error(e.getMessage());
            } catch (Exception e) {
                Ui.error("Uh oh, something went wrong: " + e.getClass().getSimpleName());
            }
        }
    }
}
