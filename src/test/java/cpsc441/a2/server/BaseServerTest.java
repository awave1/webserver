package cpsc441.a2.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseServerTest {
    @Test
    public void shouldCreateInstaceOfProxyServer() {
        BaseServer proxy = new ProxyServer(null, null);
        assertTrue(proxy instanceof ProxyServer);
    }

    @Test
    public void shouldCreateInstanceOfServer() {
        BaseServer server = new Server(null, null, null, -1);
        assertTrue(server instanceof Server);
    }
}