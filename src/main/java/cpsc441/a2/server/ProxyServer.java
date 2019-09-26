package cpsc441.a2.server;

import cpsc441.a2.util.HeaderParser;
import cpsc441.a2.util.URLParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * ProxyServer class
 *
 * Handles proxy connections, when Host value in header differs from localhost.
 * ProxyServer reads the response from remote Host and forwards it to localhost
 *
 * ProxyServer can handle multiple connections
 *
 * @author Artem Golovin
 */
public class ProxyServer implements Runnable {
    private Socket client;
    private HeaderParser header;

    private InputStream fromClient;
    private OutputStream toClient;
    private InputStream fromServer;
    private OutputStream toServer;

    private byte[] chunk;

    public ProxyServer(Socket client, HeaderParser header) {
        this.client = client;
        this.header = header;

        // 8KB chunk
        int chunkSize = (int) Math.pow(2, 13);
        this.chunk = new byte[chunkSize];
    }

    /**
     * Connect to given url and start new thread to handle connection
     *
     * method performs the following:
     * 1. opens Socket connection to url host on specified port
     * 2. reads 8kb chunks from the socket
     * 3. forwards each chunk to client
     *
     * @param url target url
     */
    public void connect(URLParser url) {
        try (Socket server = new Socket(url.getHost(), url.getPort())) {
            fromClient = client.getInputStream();
            toClient = client.getOutputStream();
            fromServer = server.getInputStream();
            toServer = server.getOutputStream();

            new Thread(this).start();

            int bytesRead;
            while ((bytesRead = fromServer.read(chunk)) != -1) {
                toClient.write(chunk, 0, bytesRead);
                toClient.flush();
            }

            toClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            toServer.write(header.getHeaderBytes());
            toServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
