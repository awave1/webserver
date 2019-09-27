package cpsc441.a2.server;

import cpsc441.a2.util.Utils;
import cpsc441.a2.WebServer;
import cpsc441.a2.util.HeaderParser;
import cpsc441.a2.util.URLParser;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Server class
 *
 * Starts local server in working directory and serves any files that are located in the directory
 * Server will return following response codes:
 * 1. 200 if file is found
 * 2. 404 if file not found
 * 3. 400 if request header or request line is malformed
 *
 * Server handles multiple connections
 *
 * @author Artem Golovin
 */
public class Server extends BaseServer {
    private HeaderParser header;

    private URLParser url;

    public Server(WebServer server, Socket client, HeaderParser header, int id) {
        super(server, client, id);
        this.header = header;
    }

    @Override
    protected void serve() {
        int code = 200;
        File file = new File(url.getPath(false));

        if (!file.exists()) {
            code = 404;
        } else if (!header.isCorrect()) {
            code = 400;
        }

        try {
            String responseHeader = buildResponseHeader(code, file, header);

            System.out.println();
            System.out.println("Response: #" + getServerId());
            System.out.println(responseHeader);

            String method = header.getMethod();
            OutputStream toClient = getClient().getOutputStream();
            byte[] headerBytes = responseHeader.getBytes(StandardCharsets.US_ASCII);

            if (method.equals("GET")) {
                byte[] fileBytes = Files.readAllBytes(file.toPath());

                String range = header.getRange();
                toClient.write(headerBytes);
                if (range != null) {
                    String[] rangeSize = range.split("-");

                    int from = Integer.parseInt(rangeSize[0]);
                    int to = Integer.parseInt(rangeSize[1]);
                    int size = (to - from) + 1;
                    byte[] part = new byte[size];

                    System.arraycopy(fileBytes, from, part, 0, size);
                    toClient.write(part);
                } else {
                    toClient.write(fileBytes);
                }

            } else if (method.equals("HEAD")) {
                toClient.write(headerBytes);
            }

            toClient.flush();
            toClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect(URLParser url) {
        this.url = url;

        new Thread(this).start();
    }

    private String buildResponseHeader(int code, File file, HeaderParser header) throws IOException {
        String head = String.format(
                "HTTP/1.1 %d\r\n" +
                        "Date: %s\r\n" +
                        "Server: %s\r\n" +
                        "Connection: close\r\n",
                code, Utils.getCurrentDate(), getServer().getServerName()
        );

        if (code == 200) {
            head += String.format(
                    "Last-modified: %s\r\n" +
                            "Content-Length: %d\r\n" +
                            "Content-Type: %s\r\n" +
                            "Accept-Ranges: bytes\r\n",
                    Utils.getLastModified(file),
                    file.length(),
                    Utils.getContentType(file)
            );


            if (header.getRange() != null) {
                head += String.format(
                        "Content-Range: bytes %s/%d\r\n",
                        header.getRange(), file.length()
                );

                head = head.replaceAll("HTTP/1.1 200", "HTTP/1.1 206 Partial Content");
            }
        }

        head += "\r\n";

        return head;
    }
}
