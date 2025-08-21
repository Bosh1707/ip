public class UnknownCommandException extends BoshException {
    public UnknownCommandException(String cmd) {
        super("I don’t recognize the command: \"" + cmd + "\".");
    }
}
