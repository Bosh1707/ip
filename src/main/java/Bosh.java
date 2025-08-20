import java.util.Scanner;

public class Bosh {
    private static final String line = "____________________________________________________________";
    private static final int MAX = 100;
    private static final String[] tasks = new String[MAX];
    private static int n = 0;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println(line);
        System.out.println(" Hello! I'm Bosh");
        System.out.println(" What can I do for you?");
        System.out.println(line);

        while (true) {
            String input = sc.nextLine().trim();

            if (input.equals("bye")) {
                System.out.println(line);
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            } else if (input.equals("list")) {
                System.out.println(line);
                for (int i = 0; i < n; i++) {
                    System.out.println(" " + (i + 1) + ". " + tasks[i]);
                }
                System.out.println(line);
            } else if (!input.isEmpty()) {
                if (n < MAX) {
                    tasks[n++] = input;
                    System.out.println(line);
                    System.out.println(" added: " + input);
                    System.out.println(line);
                } else {
                    System.out.println(line);
                    System.out.println(" Sorry, the list is full (100 items).");
                    System.out.println(line);
                }
            } else {
                // Ignore empty lines
            }
        }
    }
}
