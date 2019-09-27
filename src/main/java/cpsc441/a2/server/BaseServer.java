package cpsc441.a2.server;

import cpsc441.a2.WebServer;
import cpsc441.a2.util.HeaderParser;
import cpsc441.a2.util.URLParser;

import java.net.Socket;

abstract class BaseServer implements Runnable {
    private WebServer server;
    private Socket client;
    private HeaderParser headerParser;
    private int serverId;

    public BaseServer(WebServer server, Socket client, int id) {
        this.server = server;
        this.client = client;
        this.serverId = id;
    }

    public BaseServer(Socket client) {
        this.client = client;
    }

    /**
     * serve() handles main server logic. it processes requests and serves files
     */
    protected abstract void serve();

    /**
     * connect sets the url (localhost url) and starts server thread
     * @param url
     */
    public abstract void connect(URLParser url);

    @Override
    public void run() {
        this.serve();
    }


    public HeaderParser getHeaderParser() {
        return headerParser;
    }

    public void setHeaderParser(HeaderParser headerParser) {
        this.headerParser = headerParser;
    }

    public int getServerId() {
        return serverId;
    }

    public WebServer getServer() {
        return server;
    }

    public Socket getClient() {
        return client;
    }
}
