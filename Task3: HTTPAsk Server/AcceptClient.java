import tcpclient.TCPClient;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;



public class AcceptClient {

    // Query parameters
    static String hostname = "";      // Domain name for the host
    static String string = "";        // Data to send to server
    static int port;             // Port number
    static Integer servicePort = null;
    static boolean shutdown = false;     // Shutdown flag
    static Integer limit = null;        // data limits in bytes
    static Integer timeout = null;      // Maximum iwth time in ms


    private static final String HTTP400 = "HTTP/1.1 400 Bad Request\r\n";
    private static final String HTTP404 = "HTTP/1.1 404 Not Found\r\n";
    private static final String HTTP408 = "HTTP/1.1 408 Request Timeout\r\n";

    public static void acceptingClient(Socket clientSocket) throws IOException {
        String serviceResponseBody = "";
        String responseStatus = "HTTP/1.1 200 OK\r\n";

        StringBuilder requestBuilder = new StringBuilder();
        String line;

        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream(), true);

            while ((line = inFromClient.readLine()) != null && !line.isEmpty()) {
                requestBuilder.append(line).append("\r\n");
            }


            String request = requestBuilder.toString();
            if (request.isEmpty()) {
                outToClient.print(HTTP400 + "Empty request!");
                return;
            }

            System.out.println("Request Of the Client: " + request);

            try {
                ParseRequest.parseRequests(request);
                System.out.println("Parsed request successfully");

                TCPClient service = new TCPClient(shutdown, timeout, limit);

                byte[] toServiceBytes = null;
                if (!string.isEmpty()) {
                    toServiceBytes = string.getBytes(StandardCharsets.UTF_8);
                }

                byte[] serviceResponseBytes = service.askServer(hostname, servicePort, toServiceBytes);
                serviceResponseBody = new String(serviceResponseBytes, StandardCharsets.UTF_8);

                System.out.println("Successfully communicated with the target server");
                System.out.println("| Port: " + port + " | Hostname: " + hostname);
                System.out.println("| Timeout: " + timeout + " | Shutdown: " + "| Limit: " + limit + "\n");

            } catch (IllegalArgumentException e) {
                responseStatus = HTTP400;
                serviceResponseBody = "Invalid request parameters: " + e.getMessage();
            } catch (SocketTimeoutException e) {
                responseStatus = HTTP408;
                serviceResponseBody = "Request Timeout: " + e.getMessage();
            } catch (UnknownHostException e) {
                responseStatus = HTTP404;
                serviceResponseBody = "Unknown Host: " + e.getMessage();
            } catch (IOException e) {
                responseStatus = HTTP400;
                serviceResponseBody = "Internal Server Error: " + e.getMessage();
            } catch (Exception e) {
                responseStatus = HTTP400;
                serviceResponseBody = "Unexpected Exception: " + e.getMessage();
            }

            String httpDate = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now());

            String clientResponse = responseStatus +
                    "Server: SanaServer\r\n" +
                    "Content-Type: text/plain; charset=UTF-8\r\n" +
                    "Date: " + httpDate + "\r\n" +
                    "Content-Length: " + serviceResponseBody.length() + "\r\n\r\n"
                    + serviceResponseBody;

            outToClient.print(clientResponse);
            System.out.println(clientResponse);
            outToClient.flush();

            System.out.println("Response sent to the client.");

        } catch (IOException e) {
            System.err.println("Error processing client request: " + e.getMessage());
        } finally {
            clientSocket.close();
            System.out.println("Client socket closed.");
        }
    }
}


