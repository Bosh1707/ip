import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private final Path dir = Paths.get("data");
    private final Path file = dir.resolve("bosh.txt");

    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (Files.notExists(dir)) {
            Files.createDirectories(dir); // ensure folder exists for future saves
        }
        if (Files.notExists(file)) {
            return tasks;
        }
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        for (String raw : lines) {
            if (raw.trim().isEmpty()) continue;
            // Format:
            // T | 1 | desc
            // D | 0 | desc | by
            // E | 1 | desc | from | to
            try {
                String[] p = raw.split("\\s*\\|\\s*");
                String type = p[0];
                boolean done = "1".equals(p[1]);
                if ("T".equals(type)) {
                    Task t = new Todo(p[2]);
                    if (done) t.markAsDone();
                    tasks.add(t);
                } else if ("D".equals(type)) {
                    Task t = new Deadline(p[2], p[3]);
                    if (done) t.markAsDone();
                    tasks.add(t);
                } else if ("E".equals(type)) {
                    Task t = new Event(p[2], p[3], p[4]);
                    if (done) t.markAsDone();
                    tasks.add(t);
                }
            } catch (Exception ignore) {
                // report corrupted line
            }
        }
        return tasks;
    }

    public void save(List<Task> tasks) throws IOException {
        if (Files.notExists(dir)) {
            Files.createDirectories(dir);
        }
        List<String> out = new ArrayList<>();
        for (Task t : tasks) {
            out.add(serialize(t));
        }
        Files.write(file, out, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private String serialize(Task t) {
        String done = t.isDone ? "1" : "0";
        if (t instanceof Todo) {
            return String.join(" | ", "T", done, t.description);
        } else if (t instanceof Deadline) {
            Deadline d = (Deadline) t;
            return String.join(" | ", "D", done, d.description, d.by);
        } else if (t instanceof Event) {
            Event e = (Event) t;
            return String.join(" | ", "E", done, e.description, e.from, e.to);
        }
        return String.join(" | ", "T", done, t.description);
    }
}