import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The main GUI application for Bosh chatbot.
 */
public class Main extends Application {
    private BoshGui bosh;
    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;

    // Avatar images
    private Image userImage;
    private Image boshImage;

    @Override
    public void start(Stage stage) {
        // Initialize the Bosh chatbot
        bosh = new BoshGui();

        // Load avatar images inside the start method
        try {
            userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
            boshImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

            // Check if images loaded successfully
            if (userImage.isError()) {
                System.err.println("Failed to load user image: " + userImage.getException().getMessage());
                userImage = null;
            }
            if (boshImage.isError()) {
                System.err.println("Failed to load bosh image: " + boshImage.getException().getMessage());
                boshImage = null;
            }

            if (userImage != null && boshImage != null) {
                System.out.println("✅ Avatar images loaded successfully!");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not load avatar images: " + e.getMessage());
            System.err.println("Using colored placeholders instead.");
            userImage = null;
            boshImage = null;
        }

        // Step 1: Create the main layout components
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        dialogContainer.setSpacing(10);
        dialogContainer.setPadding(new Insets(10));

        scrollPane.setContent(dialogContainer);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);

        userInput = new TextField();
        userInput.setPromptText("Type your command here...");
        sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        // Step 2: Set up the layout constraints
        AnchorPane.setTopAnchor(scrollPane, 1.0);
        AnchorPane.setBottomAnchor(scrollPane, 60.0);
        AnchorPane.setLeftAnchor(scrollPane, 1.0);
        AnchorPane.setRightAnchor(scrollPane, 1.0);

        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);
        sendButton.setPrefWidth(55.0);
        sendButton.setPrefHeight(55.0);

        AnchorPane.setLeftAnchor(userInput, 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);
        AnchorPane.setRightAnchor(userInput, 60.0);
        userInput.setPrefHeight(55.0);

        // Step 3: Set up event handling
        sendButton.setOnMouseClicked((event) -> {
            handleUserInput();
        });

        userInput.setOnAction((event) -> {
            handleUserInput();
        });

        // Step 4: Create scene and show stage
        scene = new Scene(mainLayout, 450, 700);
        stage.setScene(scene);
        stage.setTitle("Bosh Chatbot - Your Personal Task Manager");
        stage.setResizable(false);
        stage.show();

        // Step 5: Display welcome message
        String welcomeMessage = bosh.getWelcomeMessage();
        dialogContainer.getChildren().add(
                DialogBox.getBoshDialog(welcomeMessage, boshImage)
        );

        // Add some helpful instructions
        String instructions = "Try these commands:\n" +
                "todo <description>\n" +
                "deadline <description> /by <time>\n" +
                "event <description> /from <start> /to <end>\n" +
                "list\n" +
                "mark <number>\n" +
                "delete <number>\n" +
                "find <keyword>\n" +
                "help\n" +
                "bye\n";


        dialogContainer.getChildren().add(
                DialogBox.getBoshDialog(instructions, boshImage)
        );
    }

    /**
     * Handles user input by processing it through Bosh and displaying the response.
     */
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.trim().isEmpty()) {
            return;
        }

        String response = bosh.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBoshDialog(response, boshImage)
        );

        userInput.clear();

        // Auto-scroll to bottom
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

        // Check if user wants to exit
        if (input.trim().equalsIgnoreCase("bye")) {
            // Close the application after a brief delay
            javafx.concurrent.Task<Void> sleeper = new javafx.concurrent.Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // Handle interruption
                    }
                    return null;
                }
            };
            sleeper.setOnSucceeded(e -> javafx.application.Platform.exit());
            new Thread(sleeper).start();
        }
    }
}
