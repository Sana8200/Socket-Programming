import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HTTPAsk {

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


  public static void main(String[] args) throws IOException {
    port = Integer.parseInt(args[0]);  // Server port
    ServerSocket serverSocket = new ServerSocket(port);
    System.out.println("Server is running on port " + port);

    while (true) {
      Socket clientSocket = serverSocket.accept();
      System.out.println("Client connected");
      acceptingClient(clientSocket);
    }
  }


  /** Accepting new client */
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
        parseRequests(request);
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


  /** Parsing the request */
  public static void parseRequests(String request) throws IllegalArgumentException {

    // GET /ask?hostname=time.nist.gov&limit=1200&port=13 HTTP/1.1


    // ["GET", "/ask?hostname=time.nist.gov&limit=1200&port=13", "HTTP/1.1"]     requestLine, splitting by " "
    //      [0]  "GET"
    //		[1]  "/ask?hostname=time.nist.gov&limit=1200&port=13"
    //		[2]  "HTTP/1.1"
    String[] requestLines = request.split("\r\n");

    if (requestLines.length == 0) {
      throw new IllegalArgumentException(HTTP400 + "Invalid request: " + request);
    }


    // ["/ask", "hostname=time.nist.gov&limit=1200&port=13"]                    requestParts, splitting at ?
    //      [0]  "/ask"
    //      [1]  "hostname=time.nist.gov&limit=1200&port=13"
    String[] requestParts = requestLines[0].split(" ");
    if (!"GET".equals(requestParts[0])) {
      throw new IllegalArgumentException(HTTP400 + "Only GET requests are accepted.");
    }

    if (!requestParts[2].equals("HTTP/1.1")) {
      throw new IllegalArgumentException("505 HTTP Version Not Supported");
    }

    if (!requestParts[1].startsWith("/ask?")) {
      throw new IllegalArgumentException(HTTP400 + "Invalid endpoint. Use /ask?");
    }
    // ["hostname=time.nist.gov", "limit=1200", "port=13"]                     parameters, splitting by &
    //     	[0]  "hostname=time.nist.gov"
    //		[1]  "limit=1200"
    //		[2]  "port=13"     client port
    String url = requestParts[1].substring(5);
    String[] parameters = url.split("&");

    // ["hostname=time.nist.gov"]                                             key value, splitting by =, looping
    //      [0]  "hostname"
    //      [1]  "time.nist.gov"

    // ["limit=1200"]
    //      [0]  "limit"
    //      [1]  "1200"

    // ["port=13"]    client port
    //      [0]  "port"
    //      [1]  "13"


    // Reset / clean the previous request state
    hostname = "";
    servicePort = null;
    string = "";
    shutdown = false;
    limit = null;
    timeout = null;

    for (String param : parameters) {
      String[] keyValue = param.split("=");
      if (keyValue.length != 2) {
        throw new IllegalArgumentException(HTTP400 + "Invalid parameter: " + param);
      }
      String key = keyValue[0];
      String value = keyValue[1];

      System.out.println(key + "=" + value);

      switch (key) {
        case "hostname":
          if (value.isEmpty()) {
            throw new IllegalArgumentException(HTTP400 + " Missing or empty 'hostname' value.");
          }
          hostname = value;
          break;
        case "port":
          try {
            servicePort = Integer.parseInt(value);  // Client port from request parameter
          } catch (NumberFormatException e) {
            throw new IllegalArgumentException(HTTP400 + "'port' parameter value (" + value + ") is not an integer!");
          }
          break;
        case "string":
          string = value + "\n";
          break;
        case "shutdown":
          try {
            shutdown = Boolean.parseBoolean(value);
          } catch (NumberFormatException e) {
            throw new IllegalArgumentException(HTTP400 + "'shutdown' parameter value (" + value + ") is not a boolean!");
          }
          break;
        case "timeout":
          try {
            timeout = Integer.parseInt(value);
          } catch (NumberFormatException e) {
            throw new IllegalArgumentException(HTTP400 + "'timeout' parameter value (" + value + ") is not an integer!");
          }
          break;
        case "limit":
          try {
            limit = Integer.parseInt(value);
          } catch (NumberFormatException e) {
            throw new IllegalArgumentException(HTTP400 + "'limit' parameter value (" + value + ") is not an integer!");
          }
          break;
        default:
          throw new IllegalArgumentException(HTTP400 + "Invalid parameter key: " + key);
      }
    }

    System.out.println("Parsed hostname: " + hostname);
    System.out.println("Parsed service port: " + servicePort);
    System.out.println("Attempting to connect to " + hostname + " on port " + servicePort);

    if (hostname.isEmpty()) {
      throw new IllegalArgumentException(HTTP400 + " Service hostname should be provided");
    }

    if (servicePort == null) {
      throw new IllegalArgumentException(HTTP400 + " Service port should be provided");
    }
  }
}
