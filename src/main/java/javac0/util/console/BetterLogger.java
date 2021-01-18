package javac0.util.console;

public class BetterLogger
{
    public static void log(String msg)
    {
        System.out.println(msg);
    }

    public static void log(int tagColor, String tag, int msgColor, String msg)
    {
        log(String.format("[ %s ]: %s", appendColor(tagColor, tag), appendColor(msgColor, msg)));
    }

    public static String appendColor(int color, String msg)
    {
        return String.format("\033[%dm%s\033[0m", color, msg);
    }

    public static void log(String tag, String msg)
    {
        log(37, tag, 37, msg);
    }

    public static void error(String tag, String msg)
    {
        log(31, tag, 37, msg);
    }

    public static void error(String msg)
    {
        error("Error", msg);
    }

    public static void fatal(String tag, String msg)
    {
        log(31, tag, 31, msg);
    }

    public static void notify(String tag, String msg)
    {
        log(34, tag, 37, msg);
    }

    public static void notify(String msg)
    {
        notify("Notice", msg);
    }

    public static void success(String tag, String msg)
    {
        log(32, tag, 37, msg);
    }

    public static void success(String msg)
    {
        success("Success", msg);
    }
}
