package duke;

public class EmptyDescriptionException extends BoshException {
    public EmptyDescriptionException(String type) {
        super("A " + type + " needs a description. Try again!");
    }
}
