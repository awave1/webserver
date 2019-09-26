package cpsc441.a2.util;

public class URLParser {
    private static URLParser parser;

    private int port = -1;
    private String url;
    private String host;
    private String path;
    private String file;

    public static URLParser parse(String url) {
        if (parser == null) {
            parser = new URLParser();
        }

        parser.url = url;
        parser.parseUrl();
        return parser;
    }

    private void parseUrl() {
        if (url != null) {
            String[] components = url.split("//");
            String urlPart = components.length > 1 ? components[1] : components[0];

            String[] hostComponents = urlPart.split(":");

            if (hostComponents.length > 1) {
                host = hostComponents[0].isEmpty() ? null : hostComponents[0];
                path = hostComponents[1].substring(hostComponents[1].indexOf('/'));
                port = hostComponents[1].substring(0, hostComponents[1].indexOf('/')).isEmpty()
                        ? -1
                        : Integer.parseInt(hostComponents[1].substring(0, hostComponents[1].indexOf('/')));
            } else {
                host = hostComponents[0].substring(0, hostComponents[0].indexOf('/'));

                if (host.isEmpty()) {
                    host = null;
                }

                if (host != null) {
                    path = hostComponents[0].substring(host.length());
                    port = 80;
                }
            }

            this.file = path.substring(path.lastIndexOf('/')).replaceFirst("/",  "");
        }
    }

    public String getUrl() {
        return url;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPath(boolean withTrailingSlash) {
        return withTrailingSlash ? path : path.substring(1);
    }

    public String getPath() {
        return getPath(true);
    }

    public String getFile() {
        return file;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("URL: \n")
                .append("host: " + this.getHost())
                .append("\n")
                .append("port: " + this.getPort())
                .append("\n")
                .append("path: " + this.getPath())
                .append("\n");

        return sb.toString();
    }
}
