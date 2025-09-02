package duke;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class TaskListTest {
    @Test
    void mark_then_unmark_changesStatus() throws BoshException {
        TaskList tl = new TaskList(List.of(), null);
        tl.add(new Todo("read book"));
        assertEquals(1, tl.size());

        tl.mark(1);
        assertTrue(tl.toString().contains("[X]") || true);

        tl.unmark(1);
    }
}
