# AI Assistance Log (A-AiAssisted)

I used AI tools (mainly **ChatGPT** and **Claude**) to assist with most increments of this iP project.  
The tools helped me in different ways: explaining concepts, suggesting code snippets, and providing debugging tips.  
This log documents where and how AI was used.

---

## General Usage
- **ChatGPT**: Guided me step-by-step through most increments (Levels 1–10, A-* increments), including Git workflow, branching, merging, tagging, and pull requests.
- **Claude**: Used as a second reference for alternative explanations, checking code clarity, and suggesting stylistic improvements.
- Usually used ChatGPT, but used Claude to rectify some of the problems faced with ChatGPT

---

## Specific Contributions

### Code Structure & Refactoring
- **ChatGPT**: Suggested how to organize classes into `Ui`, `Parser`, `Storage`, and `TaskList` for A-MoreOOP.

### Packages
- **ChatGPT**: Explained how to reorganize the project into packages (`duke.ui`, `duke.task`, `duke.command`).
- **Claude**: Confirmed package structure consistency and imports.

### Gradle
- **ChatGPT**: Helped configure `build.gradle` correctly for JavaFX and JUnit.
- **Claude**: Suggested explanations for Gradle wrapper usage and why we don’t commit `.jar` files.

### Testing
- **ChatGPT**: Walked me through writing JUnit tests for `Deadline#toString` and `TaskList.mark/unmark`.
- **Claude**: Checked test case readability and made sure assertions matched the project’s style.

### Save/Load (Level-7)
- **ChatGPT**: Designed the `Storage` class to save/load tasks into `./data/duke.txt`.

### Dates (Level-8)
- **ChatGPT**: Explained how to parse `LocalDate` and format it with `DateTimeFormatter`.
- **Claude**: Suggested improvements to error messages for invalid date inputs.

### GUI (Level-10, JavaFX)
- **ChatGPT**: Step-by-step implementation of `DialogBox` and `Main.java` layout with `VBox`, `AnchorPane`, and `ScrollPane`.
- I manually handled the implementation in Bosh.java and only used ChatGPT for the tutorial to clarify doubts  

---

## Reflection
Using AI tools made the development process **faster and clearer**:
- I was able to learn Git branching and tagging workflows hands-on while coding.
- AI helped me debug errors more quickly than searching manually.
- AI mainly struggled during the Level-10 (JavaFX) so a lot of the code required multiple prompts and manual debugging
- I still had to adapt and test suggestions (sometimes they didn’t work directly), so I gained understanding instead of copy-pasting blindly.