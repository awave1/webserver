package utils;

import cpsc441.a2.util.Constants;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class TestUtils {
    public static String buildHeaders(boolean isProxy) {
        ArrayList<String> headers = new ArrayList<String>(){{
            add("Connection: close");
            add(String.format("Date: %s", getCurrentDate()));
        }};

        if (isProxy) {
            // TODO: make more dynamic?
            headers.add("Host: httpbin.org");
        }

        StringBuilder output = new StringBuilder();
        for (String s : headers) {
            output.append(s);
            output.append(Constants.HEADER_NEWLINE);
        }
        output.append(Constants.HEADER_NEWLINE);
        return output.toString();
    }

    public static String buildProxyHeaders() {
        return buildHeaders(true);
    }

    public static String getCurrentDate() {
        LocalTime time = LocalTime.now();
        return time.toString();
    }
}
