import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private final ArrayList<Task> tasks = new ArrayList<>();
    private static final String line = "____________________________________________________________";
    private final Storage storage;

    // default (no auto-save)
    public TaskList() { this.storage = null; }

    // Level-7: preloaded tasks + storage to persist
    public TaskList(List<Task> initial, Storage storage) {
        this.tasks.addAll(initial);
        this.storage = storage;
    }

    public void add(Task t) {
        tasks.add(t);
        System.out.println(line);
        System.out.println("Got it. I've added this task:");
        System.out.println(t);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(line);
        persist();
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
        persist();
    }

    public void unmark(int index) throws BoshException {
        if (index < 1 || index > tasks.size()) {
            throw new IndexOutOfRangeException(index);
        }
        Task t = tasks.get(index - 1);
        t.markAsUndone();
        Ui.box("OK! Marked as not done:", "  " + t);
        persist();
    }

    public void delete(int oneBasedIndex) throws BoshException {
        if (oneBasedIndex < 1 || oneBasedIndex > tasks.size()) {
            throw new IndexOutOfRangeException(oneBasedIndex);
        }
        Task removed = tasks.remove(oneBasedIndex - 1);
        Ui.box(
                "Noted. I've removed this task:",
                "  " + removed,
                "Now you have " + tasks.size() + " tasks in the list."
        );
        persist();
    }

    public int size() {
        return tasks.size();
    }

    private void persist() {
        if (storage == null) return;
        try {
            storage.save(tasks);
        } catch (IOException e) {
            Ui.error("Could not save tasks: " + e.getMessage());
        }
    }
}
