import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private final Path dir = Paths.get("data");
    private final Path file = dir.resolve("bosh.txt");

    public List<Task> load() throws IOException {
        List<Task> out = new ArrayList<>();
        if (Files.notExists(dir)) Files.createDirectories(dir);
        if (Files.notExists(file)) return out; // first run

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        for (String raw : lines) {
            if (raw.trim().isEmpty()) continue;
            // Formats:
            // T | 1 | desc
            // D | 0 | desc | yyyy-MM-dd  OR  yyyy-MM-dd HHmm  OR legacy string
            // E | 1 | desc | from | to   (left as string for L8 minimal)
            try {
                String[] p = raw.split("\\s*\\|\\s*");
                String type = p[0];
                boolean done = "1".equals(p[1]);

                Task t;
                switch (type) {
                    case "T":
                        t = new Todo(p[2]);
                        break;
                    case "D":
                        t = new Deadline(p[2], p[3]); // constructor handles both ISO and legacy
                        break;
                    case "E":
                        t = new Event(p[2], p[3], p[4]);
                        break;
                    default:
                        continue; // skip unknown lines
                }
                if (done) t.markAsDone();
                out.add(t);
            } catch (Exception ignore) {
                // Stretch: log/box an error for corrupted lines
            }
        }
        return out;
    }

    public void save(List<Task> tasks) throws IOException {
        if (Files.notExists(dir)) Files.createDirectories(dir);
        List<String> lines = new ArrayList<>();
        for (Task t : tasks) {
            lines.add(serialize(t));
        }
        Files.write(file, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

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
        return String.join(" | ", "T", done, t.description);
    }
}