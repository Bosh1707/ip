package duke;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manages the in-memory list of tasks and coordinates persistence.
 * Provides operations to add, list, mark, unmark, delete, and find tasks.
 * Enhanced with sorting functionality.
 */
public class TaskList {
    private final ArrayList<Task> tasks = new ArrayList<>();
    private static final String LINE = "____________________________________________________________";
    private static final int FIRST_TASK_INDEX = 1;
    private final Storage storage;

    // default (no auto-save)
    public TaskList() {
        this.storage = null;
    }

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
        printAddConfirmation(t);
        persist();
    }

    /**
     * Lists all tasks to standard output in a numbered format.
     */
    public void list() {
        printDivider();
        System.out.println("Here are the tasks in your list: ");

        for (int i = 0; i < tasks.size(); i++) {
            int displayIndex = i + FIRST_TASK_INDEX;
            System.out.println(displayIndex + "." + tasks.get(i));
        }
        printDivider();
    }

    /**
     * Marks the given 1-based index as done.
     *
     * @param index 1-based task index
     * @throws BoshException if the index is out of range
     */
    public void mark(int index) throws BoshException {
        validateTaskIndex(index);

        Task task = getTaskAtIndex(index);
        task.markAsDone();
        Ui.box("Nice! Marked as done:", "  " + task);
        persist();
    }

    /**
     * Unmarks the given 1-based index as not done.
     *
     * @param index 1-based task index
     * @throws BoshException if the index is out of range
     */
    public void unmark(int index) throws BoshException {
        validateTaskIndex(index);

        Task task = getTaskAtIndex(index);
        task.markAsUndone();
        Ui.box("OK! Marked as not done:", "  " + task);
        persist();
    }

    /**
     * Deletes the task at the given 1-based index.
     *
     * @param oneBasedIndex 1-based task index
     * @throws BoshException if the index is out of range
     */
    public void delete(int oneBasedIndex) throws BoshException {
        validateTaskIndex(oneBasedIndex);

        Task removed = tasks.remove(oneBasedIndex - FIRST_TASK_INDEX);
        printDeleteConfirmation(removed);
        persist();
    }

    /**
     * Finds and displays tasks containing the given keyword.
     *
     * @param keyword search keyword
     * @throws BoshException if keyword is null or empty
     */
    public void find(String keyword) throws BoshException {
        if (isInvalidKeyword(keyword)) {
            throw new BoshException("Usage: find <keyword>");
        }

        List<Task> matchingTasks = findMatchingTasks(keyword);
        printSearchResults(matchingTasks);
    }

    /**
     * Sorts tasks alphabetically by description.
     */
    public void sortByDescription() {
        tasks.sort(Comparator.comparing(task -> task.getDescription().toLowerCase()));
        printSortConfirmation("description");
        persist();
    }

    /**
     * Sorts tasks by type (Todo, Deadline, Event).
     */
    public void sortByType() {
        tasks.sort((t1, t2) -> {
            // Define order: Todo < Deadline < Event
            int priority1 = getTypePriority(t1);
            int priority2 = getTypePriority(t2);

            if (priority1 != priority2) {
                return Integer.compare(priority1, priority2);
            }
            // If same type, sort by description
            return t1.getDescription().compareToIgnoreCase(t2.getDescription());
        });
        printSortConfirmation("type");
        persist();
    }

    /**
     * Sorts tasks by deadline date. Tasks without dates come last.
     */
    public void sortByDeadline() {
        tasks.sort((t1, t2) -> {
            boolean t1HasDate = hasDeadlineDate(t1);
            boolean t2HasDate = hasDeadlineDate(t2);

            // Tasks with dates come first
            if (t1HasDate && !t2HasDate) return -1;
            if (!t1HasDate && t2HasDate) return 1;
            if (!t1HasDate && !t2HasDate) {
                // Both have no dates, sort by description
                return t1.getDescription().compareToIgnoreCase(t2.getDescription());
            }

            // Both have dates, compare them
            return compareDeadlineDates(t1, t2);
        });
        printSortConfirmation("deadline");
        persist();
    }

    /**
     * Sorts tasks by completion status (incomplete tasks first).
     */
    public void sortByStatus() {
        tasks.sort((t1, t2) -> {
            if (t1.isDone != t2.isDone) {
                // Incomplete tasks (false) come first
                return Boolean.compare(t1.isDone, t2.isDone);
            }
            // Same status, sort by description
            return t1.getDescription().compareToIgnoreCase(t2.getDescription());
        });
        printSortConfirmation("status");
        persist();
    }

    // Private Helper Methods

    private void validateTaskIndex(int index) throws BoshException {
        if (isInvalidIndex(index)) {
            throw new IndexOutOfRangeException(index);
        }
    }

    private boolean isInvalidIndex(int index) {
        return index < FIRST_TASK_INDEX || index > tasks.size();
    }

    private Task getTaskAtIndex(int oneBasedIndex) {
        return tasks.get(oneBasedIndex - FIRST_TASK_INDEX);
    }

    private boolean isInvalidKeyword(String keyword) {
        return keyword == null || keyword.trim().isEmpty();
    }

    private List<Task> findMatchingTasks(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();
        List<Task> matchingTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (taskMatchesKeyword(task, lowerCaseKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    private boolean taskMatchesKeyword(Task task, String lowerCaseKeyword) {
        return task.getDescription().toLowerCase().contains(lowerCaseKeyword);
    }

    // Sorting helper methods

    private int getTypePriority(Task task) {
        if (task instanceof Todo) return 1;
        if (task instanceof Deadline) return 2;
        if (task instanceof Event) return 3;
        return 4; // Unknown types come last
    }

    private boolean hasDeadlineDate(Task task) {
        if (!(task instanceof Deadline)) return false;
        Deadline deadline = (Deadline) task;
        return deadline.date != null || deadline.dateTime != null;
    }

    private int compareDeadlineDates(Task t1, Task t2) {
        Deadline d1 = (Deadline) t1;
        Deadline d2 = (Deadline) t2;

        // Compare LocalDateTime first, then LocalDate
        if (d1.dateTime != null && d2.dateTime != null) {
            return d1.dateTime.compareTo(d2.dateTime);
        }
        if (d1.dateTime != null && d2.date != null) {
            return d1.dateTime.toLocalDate().compareTo(d2.date);
        }
        if (d1.date != null && d2.dateTime != null) {
            return d1.date.compareTo(d2.dateTime.toLocalDate());
        }
        if (d1.date != null && d2.date != null) {
            return d1.date.compareTo(d2.date);
        }

        // Fallback to description comparison
        return d1.getDescription().compareToIgnoreCase(d2.getDescription());
    }

    // Printing methods

    private void printAddConfirmation(Task task) {
        printDivider();
        System.out.println("Got it. I've added this task:");
        System.out.println(task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        printDivider();
    }

    private void printDeleteConfirmation(Task removedTask) {
        Ui.box(
                "Noted. I've removed this task:",
                "  " + removedTask,
                "Now you have " + tasks.size() + " tasks in the list."
        );
    }

    private void printSearchResults(List<Task> matchingTasks) {
        printDivider();
        System.out.println("Here are the matching tasks in your list:");

        for (int i = 0; i < matchingTasks.size(); i++) {
            int displayIndex = i + FIRST_TASK_INDEX;
            System.out.println(displayIndex + "." + matchingTasks.get(i));
        }
        printDivider();
    }

    private void printSortConfirmation(String sortBy) {
        Ui.box("Tasks have been sorted by " + sortBy + "!");
        list(); // Show the sorted list
    }

    public int size() {
        return tasks.size();
    }

    private void printDivider() {
        System.out.println(LINE);
    }


    private void persist() {
        if (storage == null) {
            return;
        }

        try {
            storage.save(tasks);
        } catch (IOException e) {
            Ui.error("Could not save tasks: " + e.getMessage());
        }
    }
}
