import java.net.*;
import java.io.*;

public class ConcHTTPAsk {


    static String hostname = "";
    static String string = "";
    static int port;
    static Integer servicePort = null;
    static boolean shutdown = false;
    static Integer limit = null;
    static Integer timeout = null;


    private static final String HTTP400 = "HTTP/1.1 400 Bad Request\r\n";
    private static final String HTTP404 = "HTTP/1.1 404 Not Found\r\n";
    private static final String HTTP408 = "HTTP/1.1 408 Request Timeout\r\n";


    public static void main(String[] args) throws IOException {
        port = Integer.parseInt(args[0]);  //Server port, main method takes one argument, the port number
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is running on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected");
            //Connecetion socket which is Client socket is argument for Thread
            Thread clientThread = new Thread(new MyRunnable(clientSocket));
            clientThread.setName("ClientHandler-" + clientSocket.getPort());
            clientThread.start();
        }
    }
}
