package duke;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manages the in-memory list of tasks and coordinates persistence.
 * Provides operations to add, list, mark, unmark, delete, and find tasks.
 * Enhanced with sorting functionality and improved separation of concerns.
 */
public class TaskList {
    private final ArrayList<Task> tasks = new ArrayList<>();
    private static final String DIVIDER_LINE = "____________________________________________________________";
    private static final int FIRST_TASK_INDEX = 1;
    private final Storage storage;

    // Default constructor (no auto-save)
    public TaskList() {
        this.storage = null;
    }

    // Constructor with preloaded tasks and storage for persistence
    public TaskList(List<Task> initial, Storage storage) {
        this.tasks.addAll(initial);
        this.storage = storage;
    }

    // ========== Public Task Management Methods ==========

    /**
     * Adds a task to the end of the list and prints confirmation.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
        displayAddConfirmation(task);
        saveToStorage();
    }

    /**
     * Lists all tasks to standard output in a numbered format.
     */
    public void list() {
        displayTaskList();
    }

    /**
     * Marks the given 1-based index as done.
     *
     * @param index 1-based task index
     * @throws BoshException if the index is out of range
     */
    public void mark(int index) throws BoshException {
        Task task = getValidatedTask(index);
        task.markAsDone();
        displayMarkConfirmation(task, true);
        saveToStorage();
    }

    /**
     * Unmarks the given 1-based index as not done.
     *
     * @param index 1-based task index
     * @throws BoshException if the index is out of range
     */
    public void unmark(int index) throws BoshException {
        Task task = getValidatedTask(index);
        task.markAsUndone();
        displayMarkConfirmation(task, false);
        saveToStorage();
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
        displayDeleteConfirmation(removed);
        saveToStorage();
    }

    /**
     * Finds and displays tasks containing the given keyword.
     *
     * @param keyword search keyword
     * @throws BoshException if keyword is null or empty
     */
    public void find(String keyword) throws BoshException {
        validateKeyword(keyword);
        List<Task> matchingTasks = findMatchingTasks(keyword);
        displaySearchResults(matchingTasks);
    }

    // ========== Sorting Methods ==========

    /**
     * Sorts tasks alphabetically by description.
     */
    public void sortByDescription() {
        tasks.sort(createDescriptionComparator());
        displaySortConfirmation("description");
        saveToStorage();
    }

    /**
     * Sorts tasks by type (Todo, Deadline, Event).
     */
    public void sortByType() {
        tasks.sort(createTypeComparator());
        displaySortConfirmation("type");
        saveToStorage();
    }

    /**
     * Sorts tasks by deadline date. Tasks without dates come last.
     */
    public void sortByDeadline() {
        tasks.sort(createDeadlineComparator());
        displaySortConfirmation("deadline");
        saveToStorage();
    }

    /**
     * Sorts tasks by completion status (incomplete tasks first).
     */
    public void sortByStatus() {
        tasks.sort(createStatusComparator());
        displaySortConfirmation("status");
        saveToStorage();
    }

    /**
     * Returns the number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    // ========== Private Validation Methods ==========

    /**
     * Validates and retrieves a task at the given index.
     */
    private Task getValidatedTask(int index) throws BoshException {
        validateTaskIndex(index);
        return getTaskAtIndex(index);
    }

    /**
     * Validates that the given index is within valid range.
     */
    private void validateTaskIndex(int index) throws BoshException {
        if (isInvalidIndex(index)) {
            throw new IndexOutOfRangeException(index);
        }
    }

    /**
     * Checks if an index is out of valid range.
     */
    private boolean isInvalidIndex(int index) {
        return index < FIRST_TASK_INDEX || index > tasks.size();
    }

    /**
     * Retrieves task at the given 1-based index.
     */
    private Task getTaskAtIndex(int oneBasedIndex) {
        return tasks.get(oneBasedIndex - FIRST_TASK_INDEX);
    }

    /**
     * Validates that a keyword is not null or empty.
     */
    private void validateKeyword(String keyword) throws BoshException {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BoshException("Usage: find <keyword>");
        }
    }

    // ========== Private Search Methods ==========

    /**
     * Finds all tasks that match the given keyword.
     */
    private List<Task> findMatchingTasks(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();
        List<Task> matchingTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (taskContainsKeyword(task, lowerCaseKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Checks if a task's description contains the keyword.
     */
    private boolean taskContainsKeyword(Task task, String lowerCaseKeyword) {
        return task.getDescription().toLowerCase().contains(lowerCaseKeyword);
    }

    // ========== Private Comparator Factory Methods ==========

    /**
     * Creates a comparator for sorting by description.
     */
    private Comparator<Task> createDescriptionComparator() {
        return Comparator.comparing(task -> task.getDescription().toLowerCase());
    }

    /**
     * Creates a comparator for sorting by task type.
     */
    private Comparator<Task> createTypeComparator() {
        return (t1, t2) -> {
            int priority1 = getTypePriority(t1);
            int priority2 = getTypePriority(t2);

            if (priority1 != priority2) {
                return Integer.compare(priority1, priority2);
            }
            // If same type, sort by description
            return t1.getDescription().compareToIgnoreCase(t2.getDescription());
        };
    }

    /**
     * Creates a comparator for sorting by deadline date.
     */
    private Comparator<Task> createDeadlineComparator() {
        return (t1, t2) -> {
            boolean t1HasDate = hasDeadlineDate(t1);
            boolean t2HasDate = hasDeadlineDate(t2);

            // Tasks with dates come first
            if (t1HasDate && !t2HasDate) return -1;
            if (!t1HasDate && t2HasDate) return 1;

            if (!t1HasDate && !t2HasDate) {
                return t1.getDescription().compareToIgnoreCase(t2.getDescription());
            }

            return compareDeadlineDates(t1, t2);
        };
    }

    /**
     * Creates a comparator for sorting by completion status.
     */
    private Comparator<Task> createStatusComparator() {
        return (t1, t2) -> {
            if (t1.isDone != t2.isDone) {
                // Incomplete tasks (false) come first
                return Boolean.compare(t1.isDone, t2.isDone);
            }
            return t1.getDescription().compareToIgnoreCase(t2.getDescription());
        };
    }

    // ========== Private Sorting Helper Methods ==========

    /**
     * Gets the priority value for task type ordering.
     */
    private int getTypePriority(Task task) {
        if (task instanceof Todo) return 1;
        if (task instanceof Deadline) return 2;
        if (task instanceof Event) return 3;
        return 4; // Unknown types come last
    }

    /**
     * Checks if a task is a deadline with a valid date.
     */
    private boolean hasDeadlineDate(Task task) {
        if (!(task instanceof Deadline)) return false;
        Deadline deadline = (Deadline) task;
        return deadline.date != null || deadline.dateTime != null;
    }

    /**
     * Compares two deadline tasks by their dates.
     */
    private int compareDeadlineDates(Task t1, Task t2) {
        Deadline d1 = (Deadline) t1;
        Deadline d2 = (Deadline) t2;

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

        return d1.getDescription().compareToIgnoreCase(d2.getDescription());
    }

    // ========== Private Display Methods ==========

    /**
     * Displays the complete task list with numbering.
     */
    private void displayTaskList() {
        printDivider();
        System.out.println("Here are the tasks in your list: ");

        for (int i = 0; i < tasks.size(); i++) {
            int displayIndex = i + FIRST_TASK_INDEX;
            System.out.println(displayIndex + "." + tasks.get(i));
        }
        printDivider();
    }

    /**
     * Displays confirmation message after adding a task.
     */
    private void displayAddConfirmation(Task task) {
        printDivider();
        System.out.println("Got it. I've added this task:");
        System.out.println(task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        printDivider();
    }

    /**
     * Displays confirmation message after marking/unmarking a task.
     */
    private void displayMarkConfirmation(Task task, boolean isMarked) {
        String message = isMarked ? "Nice! Marked as done:" : "OK! Marked as not done:";
        Ui.box(message, "  " + task);
    }

    /**
     * Displays confirmation message after deleting a task.
     */
    private void displayDeleteConfirmation(Task removedTask) {
        Ui.box(
                "Noted. I've removed this task:",
                "  " + removedTask,
                "Now you have " + tasks.size() + " tasks in the list."
        );
    }

    /**
     * Displays search results with numbering.
     */
    private void displaySearchResults(List<Task> matchingTasks) {
        printDivider();
        System.out.println("Here are the matching tasks in your list:");

        for (int i = 0; i < matchingTasks.size(); i++) {
            int displayIndex = i + FIRST_TASK_INDEX;
            System.out.println(displayIndex + "." + matchingTasks.get(i));
        }
        printDivider();
    }

    /**
     * Displays confirmation message after sorting and shows the sorted list.
     */
    private void displaySortConfirmation(String sortBy) {
        Ui.box("Tasks have been sorted by " + sortBy + "!");
        displayTaskList();
    }

    /**
     * Prints the divider line for formatting.
     */
    private void printDivider() {
        System.out.println(DIVIDER_LINE);
    }

    // ========== Private Storage Methods ==========

    /**
     * Persists the current task list to storage if available.
     */
    private void saveToStorage() {
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
