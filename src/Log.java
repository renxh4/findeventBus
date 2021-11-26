public class Log {
    public static boolean DEBUFG = false;

    public static void d(String msg) {
        if (DEBUFG) {
            System.out.println(msg);
        }
    }
}
