package duke;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DeadlineTest {
    @Test
    void toString_prettyDate_onlyDate() {
        Deadline d = new Deadline("return book", "2019-10-15");
        String s = d.toString();
        assertTrue(s.contains("(by: Oct 15 2019)"));
    }

    @Test
    void toString_prettyDate_withTime() {
        Deadline d = new Deadline("pay bills", "2019-10-15 1800");
        String s = d.toString();
        assertTrue(
                s.contains("(by: Oct 15 2019 6:00PM)") ||
                        s.contains("(by: Oct 15 2019 6:00 PM)")
        );
    }
}