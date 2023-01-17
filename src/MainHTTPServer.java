import java.io.*;

// This is the main driver class for the project
public class MainHTTPServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;

        try {
            ThreadedHTTPServer serverThread = new ThreadedHTTPServer(port);
            serverThread.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}