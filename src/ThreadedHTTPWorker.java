import java.net.*;
import java.io.*;
import java.lang.Thread;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// ThreadedHTTPWorker class is responsible for all the
// actual string & data transfer
public class ThreadedHTTPWorker extends Thread {
    private Socket client;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private final String CRLF = "\r\n";
    public ThreadedHTTPWorker(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            System.out.println("Worker Thread starts running ... ");
            this.outputStream = new DataOutputStream(this.client.getOutputStream());
            this.inputStream = new DataInputStream(this.client.getInputStream());

            // retrieve request header as String
            BufferedReader in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            String inputLine;
            String req = "";
            while ((inputLine = in.readLine()) != null) {
//                System.out.println (inputLine);
                req += inputLine;
                req += "\r\n";
                if (inputLine.length() == 0) {
                    break;
                }
            }
            System.out.println("Converted request to String ...");
            System.out.println(req);

            // TODO: Parse the request
            parseRequest(req, this.outputStream);
        }
        catch (IOException e) {
            System.out.println("Something wrong with connection");
            e.printStackTrace();
        }
        finally {
            if (this.outputStream != null) {
                try {
                    this.outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (this.inputStream != null) {
                try {
                    this.inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (this.client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void parseRequest(String req, DataOutputStream out) {
        System.out.println("Begin to parse request ... ");
        try {
            int pageURLIndex = 0;
            pageURLIndex = req.indexOf("HTTP");
            String header = req.substring(0, pageURLIndex); // remove HTTP/1.1 or HTTP/1.0
            String relativeURL = header.substring(4); // remove GET at this version
            System.out.println("relativeURL: " + relativeURL);

            if (!relativeURL.equals("/ ")) {
                String errorResponse = "HTTP/1.1 404 Bad Request" + CRLF
                        + CRLF;
                out.writeBytes(errorResponse);
                System.out.println("Error Page Found");
            }
            else {
                String path = "src/testVideo.mp4";
                File f = new File(path);

                String MIMEType = categorizeFile(path);
//                System.out.println(MIMEType);

                // check if it is a partial content request
                if(req.contains("Range: ")) {
                    String[] lines =  req.split("\r\n");
                    int start = 0;
                    int end = 0;
                    for (String l : lines) {
                        // check if the line contains "Range: " field
                        if (l.contains("Range: bytes=")) {
                            int len = "Range: bytes=".length();
                            String range = l.substring(len);
                            String startNum = range.split("-")[0];
                            String endNum = range.split("-")[1];
                            start = Integer.parseInt(startNum);
                            end = Integer.parseInt(endNum);
                        }
                    }
                    sendParitialContent(MIMEType, start, end, f);
                    System.out.println("Partial content request detected");
                }
                else {
                    sendFullContent(MIMEType, path);
                    System.out.println("Full content request detected");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String categorizeFile (String path) {
        try {
            // convert the file name into string
            String MIMEType = "";
            Path p = Paths.get(path);
            MIMEType = Files.probeContentType(p);
            return MIMEType;
        } catch (IOException e) {
            e.printStackTrace();
            return "Unacceptable file found";
        }
    }

    private void sendParitialContent(String MIMEType, int rangeStart, int rangeEnd, File f) {
        try {
            String date = getDateInfo();
            long fileSize = f.length();
            int actualLength = rangeEnd - rangeStart + 1;
            String partialResonse = "HTTP/1.1 206 Partial Content" + this.CRLF +
                                "Content-Type: " + MIMEType + this.CRLF +
                                "Content-Length: " + actualLength + this.CRLF +
                                "Date: " + date + this.CRLF +
                                "Content-Range: bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize + this.CRLF +
                                this.CRLF;
            this.outputStream.writeBytes(partialResonse);
            sendPartialFile(f, rangeStart, rangeEnd);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendPartialFile(File f, int rangeStart, int readLen) {
        try {
            FileInputStream fileInputStream
                    = new FileInputStream(f);
            byte[] buffer = new byte[readLen];
            fileInputStream.read(buffer, rangeStart, readLen);
            this.outputStream.write(buffer, 0, readLen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFullContent(String MIMEType, String path) {
        try {
            String date = getDateInfo();
            String dateInfo = getDateInfo();
            String response = "HTTP/1.1 200 OK" + this.CRLF +
                    "Content-Type: " + MIMEType + this.CRLF +
                    "Transfer-Encoding: chunked" + this.CRLF +
                    "Date: " + date + this.CRLF +
                    "Last-Modified: " + dateInfo + " GMT" + this.CRLF +
                    this.CRLF;

            this.outputStream.writeBytes(response);
            System.out.println("Response header sent ... ");
            sendFileInChunk(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDateInfo() {
        // produce day of the week
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss");
        return formatter.format(cal.getTime());
    }

    private void sendFileInChunk(String path) {
        try {
            int bytes = 0;
            // Open the File where located in your pc
            File file = new File(path);
            FileInputStream fileInputStream
                    = new FileInputStream(file);
            System.out.println("Begin to send file ... ");

            // Here we  break file into chunks
            byte[] buffer = new byte[1024];
            while((bytes = fileInputStream.read(buffer)) != -1) {
//                System.out.println(bytes);
                String chunkSize =  Integer.toHexString(bytes); // get Hex string of chunk size
//                System.out.println(chunkSize);
                this.outputStream.writeBytes(chunkSize + this.CRLF); // chunk size\r\n

                // Send the file
                this.outputStream.write(buffer, 0, bytes); // file content
                this.outputStream.flush(); // flush all the contents into stream
                this.outputStream.writeBytes(this.CRLF);
            }
            this.outputStream.writeBytes("0" + this.CRLF);
            this.outputStream.writeBytes(this.CRLF);

            // close the file here
            System.out.println("File sent");
            fileInputStream.close();
        } catch (IOException e) {
            System.out.println("File transfer issue");
            e.printStackTrace();
        }
    }
}
