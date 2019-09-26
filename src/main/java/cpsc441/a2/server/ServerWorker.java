package cpsc441.a2.server;

import cpsc441.a2.WebServer;
import cpsc441.a2.util.HeaderParser;
import cpsc441.a2.util.URLParser;

import java.io.*;
import java.net.Socket;

public class ServerWorker implements Runnable {
    private int id;
    private Socket client;
    private WebServer server;

    private BufferedReader buffIn;
    private InputStream fromClient;
    private OutputStream toClient;

    public ServerWorker(Socket client, WebServer server, int id) {
        this.client = client;
        this.server = server;
        this.id = id;

        try {
            this.fromClient = client.getInputStream();
            this.toClient = client.getOutputStream();
            this.buffIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String s;
            StringBuilder headerString = new StringBuilder();
            while ((s = buffIn.readLine()) != null) {
                if (s.isEmpty()) {
                    break;
                }

                headerString.append(s).append("\r\n");
            }

            if (!headerString.toString().isEmpty()) {
                if (!headerString.toString().contains("Connection: close")) {
                    headerString.append("Connection: close").append("\r\n");
                }

                headerString.append("\r\n");


                HeaderParser header = new HeaderParser(headerString.toString());
                String host = header.getHost();

                URLParser url = URLParser.parse(host + header.getRequestPath());

                System.out.println();
                System.out.println("Request: #" + id);
                System.out.println(headerString);

                if (host != null) {
                    if (!host.equals(server.getHost())) {
                        System.out.println("Starting proxy");
                        ProxyServer proxyServer = new ProxyServer(client, header);
                        proxyServer.connect(url);
                    } else {
                        System.out.println("Starting server");
                        Server server = new Server(this.server, client, header, id);
                        server.connect(url);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
