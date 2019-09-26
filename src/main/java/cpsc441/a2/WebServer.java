package cpsc441.a2;

import cpsc441.a2.server.ServerWorker;
import cpsc441.a2.util.Constants;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * cpsc441.a2.WebServer Class
 * 
 * CPSC 441
 * Assignment 2
 * 
 * @author 	Majid Ghaderi
 */

public class WebServer extends BasicWebServer {
    private ServerSocket serverSocket;
    int connectionCounter = 0;
    private String SERVER_NAME = "pump_it_up";

	private boolean shutdown = false;
	
	// Call the parent constructor
	public WebServer(int port) {
		super(port);

        try {
            // Initialize server socket on localhost
            this.serverSocket = new ServerSocket(port, 0, InetAddress.getByName(null));
            System.out.format("Server started: %s:%d\n",  serverSocket.getInetAddress().getHostAddress(), port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
	public void run() {
        ExecutorService executor = Executors.newFixedThreadPool(Constants.CONNECTION_THREAD_POOL_SIZE);
        Socket client = null;

        try {
            serverSocket.setSoTimeout(1000);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while (!shutdown) {
		    try {
		        client = serverSocket.accept();
                System.out.println("> connected: " + client.toString());
                ServerWorker serverWorker = new ServerWorker(client, this, ++connectionCounter);
                executor.execute(serverWorker);
            } catch (IOException ignored) { }
		}

        try {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }

            if (client != null) {
                client.close();
            }

            serverSocket.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
            executor.shutdownNow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
    @Override
	public void shutdown() {
		shutdown = true;
	}

    /**
     * Get current host name
     *
     * @return host name
     */
    public String getHost() {
        String hostname = serverSocket.getInetAddress().getHostName();
        return hostname + ":" + Integer.toString(serverPort);
    }

    public String getServerName() {
	    return SERVER_NAME;
    }
}
