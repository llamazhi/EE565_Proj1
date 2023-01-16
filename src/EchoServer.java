import java.net.*;
import java.io.*;

public class EchoServer {

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
            System.err.println("Could not listen on port: 8080.");
            System.exit(1);
        }

        System.out.println("Waiting for connection.....");

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                EchoHandler handler = new EchoHandler(clientSocket);
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.out.println(e);
            System.exit(1);
        }

        serverSocket.close();
    }

    // private void sendPage(Socket client) throws Exception {
    // System.out.println("Page writter called");
    //
    // File index = new File("index.html");
    //
    // PrintWriter printWriter = new PrintWriter(client.getOutputStream());// Make a
    // writer for the output stream to
    // // the client
    // BufferedReader reader = new BufferedReader(new FileReader(index));// grab a
    // file and put it into the buffer
    // // print HTTP headers
    // printWriter.println("HTTP/1.1 200 OK");
    // printWriter.println("Content-Type: text/html");
    // printWriter.println("Content-Length: " + index.length());
    // printWriter.println("\r\n");
    // String line = reader.readLine();// line to go line by line from file
    // while (line != null)// repeat till the file is read
    // {
    // printWriter.println(line);// print current line
    //
    // line = reader.readLine();// read next line
    // }
    // reader.close();// close the reader
    // printWriter.close();
    // }
}
