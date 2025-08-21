public class IndexOutOfRangeException extends BoshException {
    public IndexOutOfRangeException(int idx) {
        super("There isn’t a task #" + idx + " in your list.");
    }
}
