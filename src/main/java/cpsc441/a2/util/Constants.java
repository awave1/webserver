package cpsc441.a2.util;

/**
 * Useful constants
 *
 * @author Artem Golovin
 */
public class Constants {
    public static byte CR = "\r".getBytes()[0];
    public static byte LF = "\n".getBytes()[0];

    public static final String HEADER_DELIMETER = ": ";
    public static final String HEADER_NEWLINE = "\r\n";

    public static int CONNECTION_THREAD_POOL_SIZE = 8;
}
