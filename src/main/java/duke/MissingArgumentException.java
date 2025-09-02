package duke;

public class MissingArgumentException extends BoshException {
    public MissingArgumentException(String hint) {
        super("This seems incomplete. " + hint);
    }
}
