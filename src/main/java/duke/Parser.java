package duke;

/**
 * Parses user input lines and dispatches to task operations.
 * Throws BoshException for invalid or incomplete commands.
 */
public class Parser {

    /**
     * Handles a single input line by mutating the given TaskList and printing output.
     * Applies Single Level of Abstraction Principle (SLAP) for better readability.
     *
     * @param line raw user input
     * @param tasks task list to operate on
     * @throws BoshException for invalid inputs
     */
    public static void handle(String line, TaskList tasks) throws BoshException {
        validateInput(line, tasks);

        if (line.isEmpty()) {
            throw new UnknownCommandException("(empty line)");
        }

        // Handle single-word commands first
        if (handleSingleWordCommands(line, tasks)) {
            return;
        }

        // Handle commands with arguments
        if (handleCommandsWithArguments(line, tasks)) {
            return;
        }

        // Fallback: treat as todo task (Level-2 behavior)
        tasks.add(new Todo(line));
    }

    /**
     * Validates input parameters according to preconditions.
     */
    private static void validateInput(String line, TaskList tasks) {
        assert line != null : "Input line cannot be null";
        assert tasks != null : "TaskList cannot be null";
    }

    /**
     * Handles commands that don't require arguments.
     *
     * @return true if command was handled, false otherwise
     */
    private static boolean handleSingleWordCommands(String line, TaskList tasks) throws BoshException {
        switch (line) {
            case "list":
                tasks.list();
                return true;
            case "sort":
                tasks.sortByDescription();
                return true;
            case "help":
                showHelp();
                return true;
            case "delete":
                throw new BoshException("Usage: delete <task-number>");
            case "todo":
                throw new EmptyDescriptionException("todo");
            case "deadline":
                throw new MissingArgumentException("Usage: deadline <desc> /by <time>");
            case "event":
                throw new MissingArgumentException("Usage: event <desc> /from <start> /to <end>");
            case "find":
                throw new BoshException("Usage: find <keyword>");
            default:
                return false;
        }
    }

    /**
     * Handles commands that require arguments.
     *
     * @return true if command was handled, false otherwise
     */
    private static boolean handleCommandsWithArguments(String line, TaskList tasks) throws BoshException {
        if (handleTaskManagementCommands(line, tasks)) return true;
        if (handleTaskCreationCommands(line, tasks)) return true;
        if (handleUtilityCommands(line, tasks)) return true;

        return false;
    }

    /**
     * Handles task management commands (mark, unmark, delete).
     */
    private static boolean handleTaskManagementCommands(String line, TaskList tasks) throws BoshException {
        if (line.startsWith("mark ")) {
            int idx = parsePositiveIndex(line.substring(5).trim());
            tasks.mark(idx);
            return true;
        }

        if (line.startsWith("unmark ")) {
            int idx = parsePositiveIndex(line.substring(7).trim());
            tasks.unmark(idx);
            return true;
        }

        if (line.startsWith("delete ")) {
            int idx = parsePositiveIndex(line.substring(7).trim());
            validateTaskExists(idx, tasks);
            tasks.delete(idx);
            return true;
        }

        return false;
    }

    /**
     * Handles task creation commands (todo, deadline, event).
     */
    private static boolean handleTaskCreationCommands(String line, TaskList tasks) throws BoshException {
        if (line.startsWith("todo ")) {
            handleTodoCommand(line, tasks);
            return true;
        }

        if (line.startsWith("deadline ")) {
            handleDeadlineCommand(line, tasks);
            return true;
        }

        if (line.startsWith("event ")) {
            handleEventCommand(line, tasks);
            return true;
        }

        return false;
    }

    /**
     * Handles utility commands (sort, find).
     */
    private static boolean handleUtilityCommands(String line, TaskList tasks) throws BoshException {
        if (line.startsWith("sort ")) {
            String sortBy = line.substring(5).trim();
            handleSortCommand(sortBy, tasks);
            return true;
        }

        if (line.startsWith("find ")) {
            String keyword = line.substring(5).trim();
            validateKeyword(keyword);
            tasks.find(keyword);
            return true;
        }

        return false;
    }

    /**
     * Handles todo command creation with validation.
     */
    private static void handleTodoCommand(String line, TaskList tasks) throws BoshException {
        String desc = line.substring(5).trim();
        if (desc.isEmpty()) {
            throw new EmptyDescriptionException("todo");
        }
        tasks.add(new Todo(desc));
    }

    /**
     * Handles deadline command creation with validation.
     */
    private static void handleDeadlineCommand(String line, TaskList tasks) throws BoshException {
        String rest = line.substring(9).trim();
        int byIdx = rest.indexOf("/by");

        if (byIdx == -1) {
            throw new MissingArgumentException("Missing \"/by\". Example: deadline return book /by Sunday");
        }

        String desc = rest.substring(0, byIdx).trim();
        String by = rest.substring(byIdx + 3).trim();

        validateDeadlineInputs(desc, by);
        tasks.add(new Deadline(desc, by));
    }

    /**
     * Handles event command creation with validation.
     */
    private static void handleEventCommand(String line, TaskList tasks) throws BoshException {
        String rest = line.substring(6).trim();
        int fromIdx = rest.indexOf("/from");
        int toIdx = rest.indexOf("/to");

        if (fromIdx == -1 || toIdx == -1 || toIdx < fromIdx) {
            throw new MissingArgumentException("Event needs both /from and /to. Example: event meeting /from Mon 2pm /to 4pm");
        }

        String desc = rest.substring(0, fromIdx).trim();
        String from = rest.substring(fromIdx + 5, toIdx).trim();
        String to = rest.substring(toIdx + 3).trim();

        validateEventInputs(desc, from, to);
        tasks.add(new Event(desc, from, to));
    }

    /**
     * Validates deadline command inputs.
     */
    private static void validateDeadlineInputs(String desc, String by) throws BoshException {
        if (desc.isEmpty()) {
            throw new EmptyDescriptionException("deadline");
        }
        if (by.isEmpty()) {
            throw new MissingArgumentException("Please specify a time after /by.");
        }
    }

    /**
     * Validates event command inputs.
     */
    private static void validateEventInputs(String desc, String from, String to) throws BoshException {
        if (desc.isEmpty()) {
            throw new EmptyDescriptionException("event");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new MissingArgumentException("Both start and end times are required.");
        }

        assert !desc.isEmpty() : "Event description should not be empty";
        assert !from.isEmpty() : "Event 'from' time should not be empty";
        assert !to.isEmpty() : "Event 'to' time should not be empty";
    }

    /**
     * Validates that a keyword is not empty.
     */
    private static void validateKeyword(String keyword) throws BoshException {
        if (keyword.isEmpty()) {
            throw new BoshException("Usage: find <keyword>");
        }
        assert !keyword.isEmpty() : "Keyword should not be empty at this point";
    }

    /**
     * Validates that a task exists at the given index.
     */
    private static void validateTaskExists(int idx, TaskList tasks) throws BoshException {
        if (idx < 1 || idx > tasks.size()) {
            throw new BoshException("No task #" + idx + " exists.");
        }
    }

    /**
     * Displays help information about available commands.
     */
    private static void showHelp() {
        Ui.box(
                "Available commands:",
                "",
                "Task Management:",
                "  todo <description> - Add a todo task",
                "  deadline <desc> /by <time> - Add a deadline task",
                "  event <desc> /from <start> /to <end> - Add an event task",
                "  list - Show all tasks",
                "  mark <task-number> - Mark task as done",
                "  unmark <task-number> - Mark task as not done",
                "  delete <task-number> - Delete a task",
                "",
                "Search & Organization:",
                "  find <keyword> - Find tasks containing keyword",
                "  sort - Sort tasks by description",
                "  sort <criteria> - Sort by: description, type, date, status",
                "",
                "Other:",
                "  help - Show this help message",
                "  bye  - Exit the application"
        );
    }

    /**
     * Handles sort command with different sort criteria.
     *
     * @param sortBy the sorting criteria
     * @param tasks the task list to sort
     * @throws BoshException if invalid sort criteria is provided
     */
    private static void handleSortCommand(String sortBy, TaskList tasks) throws BoshException {
        switch (sortBy.toLowerCase()) {
            case "description":
            case "desc":
                tasks.sortByDescription();
                break;
            case "type":
                tasks.sortByType();
                break;
            case "date":
            case "deadline":
                tasks.sortByDeadline();
                break;
            case "status":
            case "done":
                tasks.sortByStatus();
                break;
            default:
                throw new BoshException("Invalid sort criteria. Available options: description, type, date, status");
        }
    }

    /**
     * Parses a string to a positive integer for task indexing.
     *
     * @param s string to parse
     * @return positive integer
     * @throws BoshException if string is not a valid positive integer
     */
    private static int parsePositiveIndex(String s) throws BoshException {
        assert s != null : "Index string cannot be null";

        try {
            int idx = Integer.parseInt(s);
            if (idx <= 0) throw new NumberFormatException();
            assert idx > 0 : "Parsed index must be positive";
            return idx;
        } catch (NumberFormatException e) {
            throw new BoshException("Please give a valid positive task number.");
        }
    }
}

