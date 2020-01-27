package cpsc441.a2.server;

import cpsc441.a2.WebServer;
import cpsc441.a2.util.HeaderParser;
import cpsc441.a2.util.URLParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import utils.TestUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProxyServerTest {
    private static final String HEADERS = TestUtils.buildProxyHeaders();
    private static final String TEST_URL = "https://httpbin.org/base64/SFRUUEJJTiBpcyBhd2Vzb21l";

    @Captor
    private ArgumentCaptor<byte[]> valueCapture;

    @Test
    public void shouldCreateProxyServer() throws IOException {
        HeaderParser header = new HeaderParser(HEADERS);
        URLParser url = URLParser.parse(TEST_URL);

        ServerSocket serverSocket = mock(ServerSocket.class);
        Socket clientSocket = mock(Socket.class);
        OutputStream out = mock(OutputStream.class);
        InputStream in = mock(InputStream.class);

        when(serverSocket.accept()).thenReturn(clientSocket);
        when(clientSocket.getInputStream()).thenReturn(in);
        when(clientSocket.getOutputStream()).thenReturn(out);

        ProxyServer server = new ProxyServer(clientSocket, header);
        server.connect(url);
    }
}