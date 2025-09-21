# Bosh User Guide

![Ui Screenshot](Ui.png)

Bosh is your friendly desktop task management companion that helps you organize your daily tasks, deadlines, and events. Designed for users who prefer a fast, keyboard-driven interface, Bosh combines the efficiency of a command-line tool with the convenience of a graphical interface.

## Quick Start

1. Ensure you have Java 17 or above installed in your computer.
2. Download the latest `bosh.jar` from [here](https://github.com/Bosh1707/ip).
3. Copy the file to the folder you want to use as the home folder for your Bosh application.
4. Open a command terminal, navigate to the folder containing the jar file, and run the command `java -jar bosh.jar` to start the application. The GUI similar to the one shown above should appear in a few seconds.
5. Type commands in the command box and press Enter to execute them. Try typing `help` to see all available commands!
6. Refer to the Features section below for details of each command.

---

## Features

**Notes about command format:**

- Words in `<angle_brackets>` are parameters to be supplied by you.
    - e.g., in `todo <description>`, `<description>` is a parameter which can be used as `todo buy groceries`.
- Items in square brackets are optional.
    - e.g., `sort [criteria]` can be used as `sort` or `sort type`.

### Adding Tasks

#### Adding a todo task: `todo`

Adds a simple todo task to your task list.

**Format:** `todo <description>`

**Examples:**
todo read book
todo buy groceries for dinner

#### Adding a deadline task: `deadline`

Adds a task with a deadline to your task list.

**Format:** `deadline <description> /by <time>`

**Examples:**
```
deadline return book /by Sunday
deadline submit assignment /by 2024-02-15
deadline pay bills /by 2024-02-15 1800
```
**Date formats supported:**
- Free text: Sunday, next week, end of month
- Date only: `2024-02-15` (displays as "Feb 15 2024")
- Date with time: `2024-02-15 1800` (displays as "Feb 15 2024 6:00PM")

#### Adding an event task: `event`

Adds an event with start and end times to your task list.

**Format:** `event <description> /from <start_time> /to <end_time>`

**Examples:**
```
event team meeting /from Mon 2pm /to 4pm
event conference /from Dec 3 /to Dec 5
```
---

### Managing Tasks

#### Viewing all tasks: `list`

Shows all tasks in your task list with their index numbers.

**Format:** `list`

#### Marking a task as done: `mark`

Marks the specified task as completed.

**Format:** `mark <task_number>`

**Example:**
`mark 2`
*marks the 2nd task as done*

#### Unmarking a task: `unmark`

Marks the specified task as not completed.

**Format:** `unmark <task_number>`

**Example:**
`unmark 1`
*marks the 1st task as not done*

#### Deleting a task: `delete`

Removes the specified task from your task list.

**Format:** `delete <task_number>`

**Example:**
`delete 3`
*removes the 3rd task*

---

### Organizing Tasks

#### Finding tasks: `find`

Finds tasks whose descriptions contain the given keyword.

**Format:** `find <keyword>`

**Examples:**

`find book`

`find meeting`

💡 **Search tips:**
- Search is case-insensitive
- Partial matches are supported

#### Sorting tasks: `sort`

Sorts all tasks by the specified criteria.

**Format:** `sort [criteria]`

**Available criteria:**
- `description` (or `desc`) - sorts alphabetically by task description
- `type` - groups by task type (Todo → Deadline → Event)
- `date` (or `deadline`) - sorts by deadline date (earliest first)
- `status` (or `done`) - shows incomplete tasks first

**Examples:**
- `sort`               sorts by description (default)
- `sort type`          groups tasks by type
- `sort date`          sorts deadlines chronologically
- `sort status`        shows incomplete tasks first

---

### Other Commands

#### Getting help: `help`

Shows all available commands and their usage.

**Format:** `help`

#### Exiting the application: `bye`

Closes the Bosh application.

**Format:** `bye`

---

## Data Storage

Bosh automatically saves your tasks to a data file (`data/bosh.txt`) after every change. There's no need to save manually!

### Data File Location

- The data file is created in a `data` folder in the same directory as the `bosh.jar` file
- If the data file is missing or corrupted, Bosh will start with an empty task list
- You can transfer your data by copying the entire `data` folder

### Backup Your Data

We recommend backing up the `data` folder regularly to prevent data loss.

---

## Troubleshooting

### Common Issues

**Q: The application won't start**

A:
- Make sure you have Java 17 or higher installed
- Try running `java -version` in your command terminal to check

**Q: My tasks disappeared**

A:
- Check if the `data` folder and `bosh.txt` file exist in the same directory as the jar file
- If the data file is corrupted, Bosh will start with an empty list

**Q: I can't find a task I added**

A:
- Use the `list` command to see all tasks
- Use the `find` command to search for specific keywords

**Q: Date formats aren't working**

A:
- For precise date parsing, use the format `yyyy-MM-dd` (e.g., `2024-02-15`)
- For date with time, use `yyyy-MM-dd HHmm` (e.g., `2024-02-15 1800`)
- Free text dates (like "Sunday") will be stored as-is

### Error Messages

Bosh provides helpful error messages when something goes wrong:

- `"A [task_type] needs a description"` - You forgot to add a description for your task
- `"There isn't a task #[number]"` - The task number doesn't exist, use `list` to check
- `"Missing /by"` - Deadline tasks need the `/by` keyword
- `"Event needs both /from and /to"` - Event tasks need both time markers

---

## Command Summary

| Action | Format | Example |
|--------|--------|---------|
| Add todo | `todo <description>` | `todo read book` |
| Add deadline | `deadline <description> /by <time>` | `deadline submit report /by Sunday` |
| Add event | `event <description> /from <start> /to <end>` | `event meeting /from 2pm /to 4pm` |
| List tasks | `list` | `list` |
| Mark done | `mark <number>` | `mark 1` |
| Mark undone | `unmark <number>` | `unmark 1` |
| Delete | `delete <number>` | `delete 2` |
| Find | `find <keyword>` | `find book` |
| Sort | `sort [criteria]` | `sort date` |
| Help | `help` | `help` |
| Exit | `bye` | `bye` |

---

**Happy task managing with Bosh! 🚀** 
