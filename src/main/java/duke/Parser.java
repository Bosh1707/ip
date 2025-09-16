package duke;

/**
 * Parses user input lines and dispatches to task operations.
 * Throws BoshException for invalid or incomplete commands.
 */
public class Parser {

    /**
     * Handles a single input line by mutating the given TaskList and printing output.
     *
     * @param line raw user input
     * @param tasks task list to operate on
     * @throws BoshException for invalid inputs
     */
    public static void handle(String line, TaskList tasks) throws BoshException {
        assert line != null : "Input line cannot be null";
        assert tasks != null : "TaskList cannot be null";

        if (line.isEmpty()) {
            throw new UnknownCommandException("(empty line)");
        }
        if (line.equals("list")) {
            tasks.list();
            return;
        }
        if (line.equals("sort")) {
            tasks.sortByDescription();
            return;
        }
        if (line.startsWith("sort ")) {
            String sortBy = line.substring(5).trim();
            handleSortCommand(sortBy, tasks);
            return;
        }
        if (line.startsWith("mark ")) {
            int idx = parsePositiveIndex(line.substring(5).trim());
            assert idx > 0 : "Index must be positive after parsing";
            tasks.mark(idx);
            return;
        }
        if (line.startsWith("unmark ")) {
            int idx = parsePositiveIndex(line.substring(7).trim());
            assert idx > 0 : "Index must be positive after parsing";
            tasks.unmark(idx);
            return;
        }
        if (line.equals("delete")) {
            throw new BoshException("Usage: delete <task-number>");
        }
        if (line.startsWith("delete ")) {
            int idx = parsePositiveIndex(line.substring(7).trim());
            assert idx > 0 : "Index must be positive after parsing";
            if (idx < 1 || idx > tasks.size()) {
                throw new BoshException("No task #" + idx + " exists.");
            }
            tasks.delete(idx);
            return;
        }

        if (line.equals("todo")) {
            throw new EmptyDescriptionException("todo");
        }
        if (line.startsWith("todo ")) {
            String desc = line.substring(5).trim();
            if (desc.isEmpty()) throw new EmptyDescriptionException("todo");
            tasks.add(new Todo(desc));
            return;
        }
        if (line.equals("deadline")) {
            throw new MissingArgumentException("Usage: deadline <desc> /by <time>");
        }
        if (line.startsWith("deadline ")) {
            String rest = line.substring(9).trim();
            int byIdx = rest.indexOf("/by");
            if (byIdx == -1) {
                throw new MissingArgumentException("Missing \"/by\". Example: deadline return book /by Sunday");
            }
            String desc = rest.substring(0, byIdx).trim();
            String by = rest.substring(byIdx + 3).trim();
            if (desc.isEmpty()) throw new EmptyDescriptionException("deadline");
            if (by.isEmpty()) throw new MissingArgumentException("Please specify a time after /by.");
            tasks.add(new Deadline(desc, by));
            return;
        }
        if (line.equals("event")) {
            throw new MissingArgumentException("Usage: event <desc> /from <start> /to <end>");
        }
        if (line.startsWith("event ")) {
            String rest = line.substring(6).trim();
            int fromIdx = rest.indexOf("/from");
            int toIdx = rest.indexOf("/to");
            if (fromIdx == -1 || toIdx == -1 || toIdx < fromIdx) {
                throw new MissingArgumentException("Event needs both /from and /to. Example: event meeting /from Mon 2pm /to 4pm");
            }
            String desc = rest.substring(0, fromIdx).trim();
            String from = rest.substring(fromIdx + 5, toIdx).trim();
            String to = rest.substring(toIdx + 3).trim();
            if (desc.isEmpty()) throw new EmptyDescriptionException("event");
            if (from.isEmpty() || to.isEmpty()) {
                throw new MissingArgumentException("Both start and end times are required.");
            }

            assert !desc.isEmpty() : "Event description should not be empty";
            assert !from.isEmpty() : "Event 'from' time should not be empty";
            assert !to.isEmpty() : "Event 'to' time should not be empty";
            tasks.add(new Event(desc, from, to));
            return;
        }
        if (line.equals("find")) {
            throw new BoshException("Usage: find <keyword>");
        }
        if (line.startsWith("find ")) {
            String keyword = line.substring(5).trim();
            if (keyword.isEmpty()) throw new BoshException("Usage: find <keyword>");
            assert !keyword.isEmpty() : "Keyword should not be empty at this point";
            tasks.find(keyword);
            return;
        }
        // Help command to show available commands
        if (line.equals("help")) {
            showHelp();
            return;
        }

        // Fallback: treat as a plain task add (Level-2 behavior)
        tasks.add(new Todo(line));
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

