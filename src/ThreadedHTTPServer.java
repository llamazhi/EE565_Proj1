import java.net.*;
import java.io.*;
import java.lang.Thread;

// ThreadedHTTPServer class allows the server to simultaneously handle
// multiple connections by creating threads
public class ThreadedHTTPServer extends Thread {
    private ServerSocket server;
    public ThreadedHTTPServer(int port) throws IOException {
        this.server = new ServerSocket(port);
    }

    @Override
    public void run() {
        try {
            while(server.isBound() && !server.isClosed()) {
                Socket client = server.accept();
                System.out.println("Connection accepted");

                ThreadedHTTPWorker workerThread = new ThreadedHTTPWorker(client);
                workerThread.start();
                System.out.println("New worker thread built");
            }
        }
        catch (IOException e) {
            System.out.println("Failed to connect");
            e.printStackTrace();
        }
        finally {
            if (server != null) {
                try {
                    server.close();
                }
                catch (IOException e) {
                    System.out.println("Server closed accidentally");
                }
            }
        }
    }
}
