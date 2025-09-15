import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    private Label text;
    private ImageView displayPicture;

    /**
     * Creates a DialogBox with the specified text and image.
     *
     * @param text The text to display in the dialog box
     * @param img The image to display alongside the text (can be null)
     */
    public DialogBox(String text, Image img) {
        this.text = new Label(text);
        this.text.setWrapText(true);
        this.text.setPadding(new Insets(10));
        this.text.setStyle("-fx-background-color: #e1e1e1; -fx-background-radius: 10;");
        this.text.setMaxWidth(300);

        // Create ImageView with proper sizing
        this.displayPicture = new ImageView();
        this.displayPicture.setFitWidth(50.0);
        this.displayPicture.setFitHeight(50.0);
        this.displayPicture.setPreserveRatio(false);

        // Handle case where image might be null or failed to load
        if (img != null && !img.isError()) {
            this.displayPicture.setImage(img);
            // Make the image circular
            Circle clip = new Circle(25, 25, 25);
            this.displayPicture.setClip(clip);
        } else {
            // Create a simple colored circle as placeholder
            this.displayPicture.setStyle(
                    "-fx-background-color: #cccccc; " +
                            "-fx-background-radius: 25; " +
                            "-fx-border-color: #999999; " +
                            "-fx-border-radius: 25; " +
                            "-fx-border-width: 2;"
            );
        }

        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(10);
        this.setPadding(new Insets(5));

        // Add a spacer to push content to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.getChildren().addAll(spacer, this.text, this.displayPicture);
    }

    /**
     * Creates a dialog box for user messages.
     *
     * @param text The user's message
     * @param img The user's avatar image (can be null)
     * @return A DialogBox styled for user messages
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.text.setStyle("-fx-background-color: #a8d8ea; -fx-background-radius: 10;");

        // If no image, make user placeholder blue
        if (img == null || img.isError()) {
            db.displayPicture.setStyle(
                    "-fx-background-color: #4a90e2; " +
                            "-fx-background-radius: 25; " +
                            "-fx-border-color: #2e5c8a; " +
                            "-fx-border-radius: 25; " +
                            "-fx-border-width: 2;"
            );
        }
        return db;
    }

    /**
     * Creates a dialog box for Bosh's responses.
     *
     * @param text Bosh's response
     * @param img Bosh's avatar image (can be null)
     * @return A DialogBox styled for Bosh's messages
     */
    public static DialogBox getBoshDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.setAlignment(Pos.TOP_LEFT);

        // Clear existing children and rebuild for left alignment
        db.getChildren().clear();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        db.getChildren().addAll(db.displayPicture, db.text, spacer);
        db.text.setStyle("-fx-background-color: #ffaa8a; -fx-background-radius: 10;");

        // If no image, make Bosh placeholder orange
        if (img == null || img.isError()) {
            db.displayPicture.setStyle(
                    "-fx-background-color: #ff6b35; " +
                            "-fx-background-radius: 25; " +
                            "-fx-border-color: #cc5429; " +
                            "-fx-border-radius: 25; " +
                            "-fx-border-width: 2;"
            );
        }
        return db;
    }
}
