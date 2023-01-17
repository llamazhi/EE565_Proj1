import java.net.*;
import java.io.*;
import java.lang.Thread;

// ThreadedHTTPWorker class is responsible for all the
// actual string & data transfer
public class ThreadedHTTPWorker extends Thread {
    private Socket client;

    public ThreadedHTTPWorker(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            String fileName = "src/testVideo.mp4";
            File file = new File(fileName);
            outputStream = client.getOutputStream();
            inputStream = new FileInputStream(file);
            DataOutputStream outToClient = new DataOutputStream(client.getOutputStream());
            int numOfBytes = (int) file.length();

            final String CRLF = "\r\n";
            String response = "HTTP/1.1 200 OK" + CRLF +
                    "Content-Type: video/mp4" + CRLF +
                    "Transfer-Encoding: chunked" + CRLF +
                    "Content-Length: " + numOfBytes + CRLF +
                    CRLF;

            outToClient.writeBytes(response);
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
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
