import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Deadline extends Task {
    // Keep original input in case it isn't a valid date (for backward-compat)
    protected String byRaw;
    protected LocalDate date;          // if user typed yyyy-MM-dd
    protected LocalDateTime dateTime;  // if user typed yyyy-MM-dd HHmm

    private static final DateTimeFormatter IN_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter IN_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter OUT_DATE = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final DateTimeFormatter OUT_DATE_TIME = DateTimeFormatter.ofPattern("MMM d yyyy h:mma");

    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE); // or remove TaskType if you're not using enums
        this.byRaw = by == null ? "" : by.trim();
        parseIntoFields(this.byRaw);
    }

    // If you prefer to enforce format strictly, you could throw your BoshException here.
    private void parseIntoFields(String s) {
        if (s.isEmpty()) return;
        // Try datetime first (yyyy-MM-dd HHmm)
        try {
            dateTime = LocalDateTime.parse(s, IN_DATE_TIME);
            return;
        } catch (DateTimeParseException ignore) { /* fall through */ }

        // Then try date only (yyyy-MM-dd)
        try {
            date = LocalDate.parse(s, IN_DATE);
        } catch (DateTimeParseException ignore) {
            // leave both null; we’ll print the raw string as before
        }
    }

    // For Storage: always serialize to a canonical ISO string if we parsed it, else the raw input
    public String storageBy() {
        if (dateTime != null) return dateTime.format(IN_DATE_TIME); // yyyy-MM-dd HHmm
        if (date != null) return date.format(IN_DATE);              // yyyy-MM-dd
        return byRaw;                                               // fallback
    }

    @Override
    public String toString() {
        String pretty;
        if (dateTime != null) {
            pretty = dateTime.format(OUT_DATE_TIME);
        } else if (date != null) {
            pretty = date.format(OUT_DATE);
        } else {
            pretty = byRaw; // not a recognized date format ⇒ show original
        }
        return super.toString() + " (by: " + pretty + ")";
    }
}

