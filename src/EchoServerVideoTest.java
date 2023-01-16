import java.net.*;
import java.io.*;

public class EchoServerVideoTest {
    public static void main(String args[]) throws IOException {
        ServerSocket server = new ServerSocket(8080);
        System.out.println("Listening for connection on port 8080 ....");
        try {
            while (true) {
                Socket socket = server.accept();

                // TODO: read the video and transfer to the client
                DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
                String fileName = "testVideo.mp4";
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
                try {
                    inputStream.close();
                } catch (IOException ioex) {
                    System.err.println("Close stream failed.");
                    System.err.println(ioex);
                }
            }

        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.err.println(e);
        }
        server.close();
    }
}
