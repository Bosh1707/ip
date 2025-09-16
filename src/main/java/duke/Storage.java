package duke;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles loading tasks from disk and saving tasks to disk using a simple line format.
 * Ensures the data directory exists and tolerates a missing file on first run.
 */
public class Storage {
    private final Path dir = Paths.get("data");
    private final Path file = dir.resolve("bosh.txt");

    /**
     * Loads tasks from the data file.
     *
     * @return list of tasks loaded (possibly empty)
     * @throws IOException if the file cannot be read or created
     */
    public List<Task> load() throws IOException {
        if (Files.notExists(dir)) {
            Files.createDirectories(dir);
        }
        if (Files.notExists(file)) {
            return new ArrayList<>(); // first run
        }

        return Files.readAllLines(file, StandardCharsets.UTF_8)
                .stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(this::parseTaskFromLine)
                .filter(task -> task != null) // Filter out failed parses
                .collect(Collectors.toList());
    }

    /**
     * Persists the given list of tasks to the data file.
     *
     * @param tasks tasks to persist
     * @throws IOException if the file cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        if (Files.notExists(dir)) {
            Files.createDirectories(dir);
        }

        List<String> lines = tasks.stream()
                .map(this::serialize)
                .collect(Collectors.toList());

        Files.write(file, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Parses a single line into a Task object.
     *
     * @param line the line to parse
     * @return parsed Task or null if parsing fails
     */
    private Task parseTaskFromLine(String line) {
        try {
            String[] parts = line.split("\\s*\\|\\s*");
            if (parts.length < 3) {
                return null; // Invalid format
            }

            String type = parts[0];
            boolean isDone = "1".equals(parts[1]);
            String description = parts[2];

            Task task = createTaskFromParts(type, description, parts);
            if (task != null && isDone) {
                task.markAsDone();
            }
            return task;
        } catch (Exception e) {
            // Log error in a real application
            return null; // Skip corrupted lines
        }
    }

    /**
     * Creates a task based on the parsed parts.
     */
    private Task createTaskFromParts(String type, String description, String[] parts) {
        switch (type) {
            case "T":
                return new Todo(description);
            case "D":
                return parts.length >= 4 ? new Deadline(description, parts[3]) : null;
            case "E":
                return parts.length >= 5 ? new Event(description, parts[3], parts[4]) : null;
            default:
                return null; // Unknown task type
        }
    }

    /**
     * String builder based on task t
     *
     * @param t tasks to serialize
     * @return serialized tasks
     */
    private String serialize(Task t) {
        String done = t.isDone ? "1" : "0";
        if (t instanceof Todo) {
            return String.join(" | ", "T", done, t.description);
        } else if (t instanceof Deadline) {
            Deadline d = (Deadline) t;
            return String.join(" | ", "D", done, d.description, d.storageBy());
        } else if (t instanceof Event) {
            Event e = (Event) t;
            return String.join(" | ", "E", done, e.description, e.from, e.to);
        }

        // Fallback for unknown task types
        return String.join(" | ", "T", done, t.description);
    }
}