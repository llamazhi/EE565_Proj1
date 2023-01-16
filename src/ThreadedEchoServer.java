import java.net.*;
import java.io.*;
import java.lang.Thread;

class ThreadedEchoHandler extends Thread {
    Socket clientSocket;

    ThreadedEchoHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            System.out.println("Connection successful");
            System.out.println("Waiting for input.....");
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
                    true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Server: " + inputLine);
                out.println(inputLine);

                if (inputLine.equals("Bye."))
                    break;
            }
            out.close();
            in.close();
            clientSocket.close();
        } catch (Exception e) {
            System.err.println("Exception caught: Client Disconnected.");
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                ;
            }
        }
    }
}

public class ThreadedEchoServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        int PortNumber = 5;
        if (args.length > 0) {
            PortNumber = Integer.parseInt(args[0]);
        } else {
            PortNumber = 8080;
        }
        try {
            serverSocket = new ServerSocket(PortNumber);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 10007.");
            System.exit(1);
        }

        System.out.println("Waiting for connection.....");

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ThreadedEchoHandler handler = new ThreadedEchoHandler(clientSocket);
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        serverSocket.close();
    }
}