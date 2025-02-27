import java.net.*;
import java.io.*;


public class HTTPAsk {
  static int port;

  public static void main(String[] args) throws IOException {
    port = Integer.parseInt(args[0]);
    ServerSocket serverSocket = new ServerSocket(port);
    System.out.println("Server is running on port " + port);

    while (true) {
      Socket clientSocket = serverSocket.accept();
      System.out.println("Client connected");
      AcceptClient.acceptingClient(clientSocket);
    }
  }
}
