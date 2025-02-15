package tcpclient;
import java.io.IOException;
import java.io.*;
import java.net.*;


public class TCPClient {

    public TCPClient() {
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        Socket s = new Socket(hostname, port);
        OutputStream outputStream = s.getOutputStream();
        InputStream inputStream = s.getInputStream();
        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();

        outputStream.write(toServerBytes);
        outputStream.flush();


        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            responseBuffer.write(buffer, 0, bytesRead);
        }

        byte[] response = responseBuffer.toByteArray();

        inputStream.close();
        outputStream.close();
        s.close();

        return response;
    }

    public byte[] askServer(String hostname, int port) throws IOException {
        Socket s = new Socket(hostname, port);
        InputStream inputStream = s.getInputStream();
        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();


        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            responseBuffer.write(buffer, 0, bytesRead);
        }

        byte[] response = responseBuffer.toByteArray();

        inputStream.close();
        s.close();

        return response;
    }
}
