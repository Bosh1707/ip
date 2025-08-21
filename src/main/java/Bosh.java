import java.util.Scanner;

public class Bosh {
    private static final String line = "____________________________________________________________";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TaskList tasks = new TaskList();

        Ui.box("Hello! I'm Duke", "What can I do for you?");

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
                // Safety net: unexpected errors
                Ui.error("Uh oh, something went wrong: " + e.getClass().getSimpleName());
            }
        }
    }
}
