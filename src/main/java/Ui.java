public class Ui {
    public static void box(String... lines) {
        System.out.println("    ____________________________________________________________");
        for (String s : lines) {
            System.out.println("     " + s);
        }
        System.out.println("    ____________________________________________________________");
    }
    public static void error(String... lines) {
        box(lines);
    }
}
