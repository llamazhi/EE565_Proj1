import java.net.*; 
import java.io.*; 
import java.lang.Thread;

public class EchoHandler extends Thread{
    Socket clientSocket;
    EchoHandler (Socket clientSocket){
        this.clientSocket=clientSocket;
    }
    @Override
    public void run(){
        try{
            System.out.println ("Connection successful");
            System.out.println ("Waiting for input.....");

            // TODO: Read the picture file into bytes
            DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
            String fileName = "C://study/ee565/project1/Project1_565/src/Husky.jpg";
            File file = new File(fileName);
            int numOfBytes = (int) file.length();

            FileInputStream inFile  = new FileInputStream(fileName);

            byte[] fileInBytes = new byte[numOfBytes];
            inFile.read(fileInBytes);

            outToClient.writeBytes("HTTP/1.1 200 Document Follows\r\n");

            if (fileName.endsWith(".jpg"))
                outToClient.writeBytes("Content-Type: image/jpeg\r\n");
            if (fileName.endsWith(".gif"))
                outToClient.writeBytes("Content-Type: image/gif\r\n");

            outToClient.writeBytes("Content-Length: " + numOfBytes + "\r\n");
            outToClient.writeBytes("\r\n");

            outToClient.write(fileInBytes, 0, numOfBytes);


            // original starter code starts here
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
                                      true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader( clientSocket.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
            System.out.println ("Server: " + inputLine);
            out.println(inputLine);

            if (inputLine.equals("Bye."))
                break;
            }
            out.close();
            in.close();

            clientSocket.close();
        }
        catch (Exception e){
            System.err.println("Exception caught: Client Disconnected.");
        }
        finally{
            try{clientSocket.close();}
            catch(Exception e){;}
        }
    }
}

