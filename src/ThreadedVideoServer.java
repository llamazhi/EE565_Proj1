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
            DataOutputStream outToClient = new DataOutputStream(this.clientSocket.getOutputStream());
            String fileName = "src/testVideo.mp4";
            File file = new File(fileName);
            int numOfBytes = (int) file.length();
            InputStream inputStream = new FileInputStream(file);

            outToClient.writeBytes("HTTP/1.1 200 OK\r\n");
            outToClient.writeBytes("Content-Type: video/mp4\r\n");
            outToClient.writeBytes("Transfer-Encoding: chunked");
            outToClient.writeBytes("Content-Length: " + numOfBytes + "\r\n");
            outToClient.writeBytes("\r\n");

            System.out.println("reached before transferring");
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            int count = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outToClient.write(buffer, 0, bytesRead);
                count++;
            }
            System.out.println("Transfer completed");
            System.out.println(numOfBytes);
            System.out.println(count + "chunks transferred");
            inputStream.close();
//            clientSocket.close();
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

public class ThreadedVideoServer {

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