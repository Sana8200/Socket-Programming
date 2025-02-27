import java.net.*;
import java.io.*;

public class ConcHTTPAsk {

    static int port;

    public static void main(String[] args) throws IOException {
        port = Integer.parseInt(args[0]);  //Server port, main method takes one argument, the port number
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is running on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected");
            // Connection socket which is Client socket is argument for Thread
            Thread clientThread = new Thread(new MyRunnable(clientSocket));
            clientThread.setName("ClientHandler-" + clientSocket.getPort());
            clientThread.start();
        }
    }
}
