import java.util.Scanner;

public class Bosh {
    private static final String line = "____________________________________________________________";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TaskList tasks = new TaskList();

        System.out.println(line);
        System.out.println(" Hello! I'm Bosh");
        System.out.println(" What can I do for you?");
        System.out.println(line);

        while (sc.hasNextLine()) {
            String input = sc.nextLine().trim();

            if (input.equals("bye")) {
                System.out.println(line);
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            } else {
                Parser.handle(input, tasks);
            }
        }
    }
}
