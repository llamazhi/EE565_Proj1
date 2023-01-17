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
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (server != null) {
                try {
                    server.close();
                }
                catch (IOException e) {}
            }
        }
    }
}
