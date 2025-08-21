import java.util.ArrayList;

public class TaskList {
    private final ArrayList<Task> tasks = new ArrayList<>();
    private static final String line = "____________________________________________________________";

    public void add(Task t) {
        tasks.add(t);
        System.out.println(line);
        System.out.println("Got it. I've added this task:");
        System.out.println(t);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
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

    public void mark(int index) throws BoshException {
        if (index < 1 || index > tasks.size()) {
            throw new IndexOutOfRangeException(index);
        }
        Task t = tasks.get(index - 1);
        t.markAsDone();
        Ui.box("Nice! Marked as done:", "  " + t);
    }

    public void unmark(int index) throws BoshException {
        if (index < 1 || index > tasks.size()) {
            throw new IndexOutOfRangeException(index);
        }
        Task t = tasks.get(index - 1);
        t.markAsUndone();
        Ui.box("OK! Marked as not done:", "  " + t);
    }


}
