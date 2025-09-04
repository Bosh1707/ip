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
        if (line.isEmpty()) {
            throw new UnknownCommandException("(empty line)");
        }
        if (line.equals("list")) {
            tasks.list();
            return;
        }
        if (line.startsWith("mark ")) {
            int idx = parsePositiveIndex(line.substring(5).trim());
            tasks.mark(idx);
            return;
        }
        if (line.startsWith("unmark ")) {
            int idx = parsePositiveIndex(line.substring(7).trim());
            tasks.unmark(idx);
            return;
        }
        if (line.equals("delete")) {
            throw new BoshException("Usage: delete <task-number>");
        }
        if (line.startsWith("delete ")) {
            int idx = parsePositiveIndex(line.substring(7).trim());
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
            tasks.add(new Event(desc, from, to));
            return;
        }
        if (line.equals("find")) {
            throw new BoshException("Usage: find <keyword>");
        }
        if (line.startsWith("find ")) {
            String keyword = line.substring(5).trim();
            if (keyword.isEmpty()) throw new BoshException("Usage: find <keyword>");
            tasks.find(keyword);
            return;
        }

        // Fallback: treat as a plain task add (Level-2 behavior)
        tasks.add(new Todo(line));
    }

    private static int parsePositiveIndex(String s) throws BoshException {
        try {
            int idx = Integer.parseInt(s);
            if (idx <= 0) throw new NumberFormatException();
            return idx;
        } catch (NumberFormatException e) {
            throw new BoshException("Please give a valid positive task number.");
        }
    }
}

