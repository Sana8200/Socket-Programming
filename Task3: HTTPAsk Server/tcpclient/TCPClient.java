package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    boolean shutdown;
    Integer timeout;
    Integer limit;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        Socket socket = new Socket(hostname, port);

        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();


        if (timeout != null) {
            socket.setSoTimeout(timeout);
        }

        if (toServerBytes != null && toServerBytes.length > 0){
            outputStream.write(toServerBytes);
            outputStream.flush();
        }

        if(shutdown){
            socket.shutdownOutput();
        }

        byte[] buffer = new byte[1024];
        int bytesRead;

        long lastReadTime = System.currentTimeMillis();

        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                if (timeout != null && System.currentTimeMillis() - lastReadTime >= timeout) {
                    System.out.println("Timeout occurred, breaking loop.");
                    break;
                }
                if (limit != null && responseBuffer.size() + bytesRead > limit) {
                    System.out.println("Data limit reached, breaking loop.");
                    responseBuffer.write(buffer, 0, limit - responseBuffer.size());
                    break;
                } else {
                    responseBuffer.write(buffer, 0, bytesRead);
                }
                lastReadTime = System.currentTimeMillis();
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Socket timed out.");
            return responseBuffer.toByteArray();
        } finally {
            inputStream.close();
            outputStream.close();
            socket.close();
        }

        return responseBuffer.toByteArray();
    }
}
