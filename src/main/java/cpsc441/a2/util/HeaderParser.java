package cpsc441.a2.util;

import java.util.HashMap;

/**
 * Simple Header parser
 *
 * @author Artem Golovin
 */
public class HeaderParser {

    private String header;
    private String method;
    private HashMap<String, String> head = new HashMap<>();

    /**
     * Header lines are mapped to HashMap for easier access within the class
     * @param header - header string
     * @return an instance of HeaderParser
     */
    public HeaderParser(String header) {
        this.header = header;
        parse();
    }

    public HeaderParser(byte[] bytes) {
        this.header = new String(bytes);
        parse();
    }

    private void parse() {
        String[] lines = header.split(Constants.HEADER_NEWLINE);

        for (String line : lines) {
            if (line.split(Constants.HEADER_DELIMETER).length > 1) {
                String[] content = line.split(Constants.HEADER_DELIMETER);
                if (content.length > 1) {
                    head.put(content[0], content[1]);
                }
            } else {
                String[] request = line.split(" ");
                if (request.length > 1) {
                    head.put(request[0], request[1]);
                    this.method = request[0];
                }
            }
        }
    }

    /**
     * Check if header lines contains "Accept-Ranges" header
     * @return true, if ranges are supported
     */
    public boolean supportsRangeRequest() {
        return head.keySet().contains("Accept-Ranges") && !get("Accept-Ranges").equals("none");
    }

    public String getHost() {
        return get("Host");
    }

    /**
     * Check if header contains "Connection: close"
     * @return true, if header contains "Connection: close"
     */
    public boolean isConnectionClose() {
        return get("Connection").equals("close");
    }

    /**
     * Get "Date" header
     *
     * @return date string
     */
    public String getDate() {
        return get("Date");
    }

    public String getRequestPath(String method) {
        return get(method);
    }

    public String getRequestPath() {
        return get(method);
    }

    private String get(String key) {
        return head.getOrDefault(key, null);
    }

    public byte[] getHeaderBytes() {
        return header.getBytes();
    }

    public String getMethod() {
        return method;
    }

    public String getRange() {
        String range = get("Range");
        if (range != null) {
            return range.split("=")[1];
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "HeaderParser {\n\n" +
                header +
                "}";
    }

    /**
     * Simple check if header is formatted correctly
     *
     * @return true, if header is ASCII encoded and contains '\r\n' separator at the end
     */
    public boolean isCorrect() {
        return Utils.isAscii(header) && header.charAt(header.length() - 2) == Constants.CR
                && header.charAt(header.length() - 1) == Constants.LF;
    }
}
