public class Parser {
    public static void handle(String input, TaskList tasks) {
        if (input.equals("list")) {
            tasks.list();
            return;
        }
        if (input.startsWith("mark ")) {
            int idx = Integer.parseInt(input.substring(5).trim());
            tasks.mark(idx);
            return;
        }
        if (input.startsWith("unmark ")) {
            int idx = Integer.parseInt(input.substring(7).trim());
            tasks.unmark(idx);
            return;
        }
        if (input.startsWith("todo ")) {
            String desc = input.substring(5).trim();
            tasks.add(new Todo(desc));
            return;
        }
        if (input.startsWith("deadline ")) {
            String rest = input.substring(9).trim();
            int byIdx = rest.indexOf("/by");
            if (byIdx == -1) {
                System.out.println("____________________________________________________________");
                System.out.println("OOPS: deadline needs '/by <time>'");
                System.out.println("____________________________________________________________");
                return;
            }
            String desc = rest.substring(0, byIdx).trim();
            String by = rest.substring(byIdx + 3).trim(); // after "/by"
            tasks.add(new Deadline(desc, by));
            return;
        }
        if (input.startsWith("event ")) {
            String rest = input.substring(6).trim();
            int fromIdx = rest.indexOf("/from");
            int toIdx = rest.indexOf("/to");
            if (fromIdx == -1 || toIdx == -1 || toIdx < fromIdx) {
                System.out.println("____________________________________________________________");
                System.out.println("OOPS: event needs '/from <start> /to <end>'");
                System.out.println("____________________________________________________________");
                return;
            }
            String desc = rest.substring(0, fromIdx).trim();
            String from = rest.substring(fromIdx + 5, toIdx).trim();
            String to = rest.substring(toIdx + 3).trim();
            tasks.add(new Event(desc, from, to));
            return;
        }

        // Fallback: treat as a plain task add (Level-2 behavior)
        tasks.add(new Todo(input));
    }
}
