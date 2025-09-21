import duke.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * GUI wrapper for the Bosh chatbot that captures console output.
 */
public class BoshGui {
    private Storage storage;
    private TaskList tasks;

    public BoshGui() {
        // Initialize storage and task list (similar to Bosh.main)
        storage = new Storage();
        try {
            List<Task> loaded = storage.load();
            tasks = new TaskList(loaded, storage);
        } catch (Exception e) {
            tasks = new TaskList(); // fallback: no auto-save
        }
    }

    /**
     * Gets the welcome message for the chatbot.
     */
    public String getWelcomeMessage() {
        return "Hello! I'm Bosh\nWhat can I do for you?";
    }

    /**
     * Processes user input and returns the response by capturing console output.
     */
    public String getResponse(String input) {
        if (input.trim().equalsIgnoreCase("bye")) {
            return "Bye. Hope to see you again soon!";
        }

        // Capture console output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        try {
            System.setOut(new PrintStream(baos));
            System.setErr(new PrintStream(baos));

            Parser.handle(input.trim(), tasks);

            String output = baos.toString();

            // Clean up the output by removing the box lines

            return cleanOutput(output);

        } catch (BoshException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Uh oh, something went wrong: " + e.getClass().getSimpleName();
        } finally {
            // Restore original streams
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    /**
     * Cleans the console output by removing box lines and formatting.
     */
    private String cleanOutput(String output) {
        if (output.trim().isEmpty()) {
            return "✅ Done!";
        }

        // Remove the box lines and clean up formatting
        String cleaned = output.replace("____________________________________________________________", "")
                .replaceAll("\\n{2,}", "\n")
                .trim();

        if (cleaned.isEmpty()) {
            return "✅ Task completed!";
        }

        return cleaned;
    }
}
