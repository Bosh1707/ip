import java.util.ArrayList;

public class TaskList {
    private final ArrayList<Task> tasks = new ArrayList<>();
    private static final String line = "____________________________________________________________";

    public void add(Task t) {
        tasks.add(t);
        System.out.println(line);
        System.out.println(" added: " + t.description);
        System.out.println(line);
    }

    public void list() {
        System.out.println(line);
        System.out.println("Here are the tasks in your list: ");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
        System.out.println(line);
    }

    public void mark(int index) {
        Task t = tasks.get(index - 1);
        t.markAsDone();
        System.out.println(line);
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(t);
        System.out.println(line);
    }

    public void unmark(int index) {
        Task t = tasks.get(index - 1);
        t.markAsUndone();
        System.out.println(line);
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(t);
        System.out.println(line);
    }


}
