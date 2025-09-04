package duke;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the in-memory list of tasks and coordinates persistence.
 * Provides operations to add, list, mark, unmark, delete, and find tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks = new ArrayList<>();
    private static final String LINE = "____________________________________________________________";
    private final Storage storage;

    // default (no auto-save)
    public TaskList() { this.storage = null; }

    // Level-7: preloaded tasks + storage to persist
    public TaskList(List<Task> initial, Storage storage) {
        this.tasks.addAll(initial);
        this.storage = storage;
    }

    /**
     * Adds a task to the end of the list and prints confirmation.
     *
     * @param t task to add
     */
    public void add(Task t) {
        tasks.add(t);
        System.out.println(LINE);
        System.out.println("Got it. I've added this task:");
        System.out.println(t);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(LINE);
        persist();
    }

    /**
     * Lists all tasks to standard output in a numbered format.
     */
    public void list() {
        System.out.println(LINE);
        System.out.println("Here are the tasks in your list: ");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
        System.out.println(LINE);
    }

    /**
     * Marks the given 1-based index as done.
     *
     * @param index 1-based task index
     * @throws BoshException if the index is out of range
     */
    public void mark(int index) throws BoshException {
        if (index < 1 || index > tasks.size()) {
            throw new IndexOutOfRangeException(index);
        }
        Task t = tasks.get(index - 1);
        t.markAsDone();
        Ui.box("Nice! Marked as done:", "  " + t);
        persist();
    }

    /**
     * Unmarks the given 1-based index as not done.
     *
     * @param index 1-based task index
     * @throws BoshException if the index is out of range
     */
    public void unmark(int index) throws BoshException {
        if (index < 1 || index > tasks.size()) {
            throw new IndexOutOfRangeException(index);
        }
        Task t = tasks.get(index - 1);
        t.markAsUndone();
        Ui.box("OK! Marked as not done:", "  " + t);
        persist();
    }

    /**
     * Deletes the task at the given 1-based index.
     *
     * @param oneBasedIndex 1-based task index
     * @throws BoshException if the index is out of range
     */
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
