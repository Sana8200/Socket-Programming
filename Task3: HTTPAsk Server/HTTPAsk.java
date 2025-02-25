import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class HTTPAsk {

  // Query parameters
  static String hostname;      // Domain name for the host
  static String string;        // Data to send to server
  static int port;             // Port number
  static int clientPort;
  static boolean shutdown = false;     // Shutdown flag
  static Integer limit = null;        // data limits in bytes
  static Integer timeout = null;      // Maximum iwth time in ms


  private static final String HTTP400 = "HTTP/1.1 400 Bad Request\r\n";
  private static final String HTTP404 = "HTTP/1.1 404 Not Found\r\n";
  private static final String HTTP408 = "HTTP/1.1 408 Request Timeout\r\n";


  public static void main(String[] args) throws IOException {
    port = Integer.parseInt(args[0]);
    ServerSocket serverSocket = new ServerSocket(port);
    System.out.println("Server is running on port " + port);

    while (true) {
      Socket clientSocket = serverSocket.accept();
      System.out.println("Client connected");
      acceptingClient(clientSocket);
    }
  }

  //Invalid HTTP Request (Bad Request) 400 Bad Request
  //BadRequestException
          /* If the HTTP request has invalid parameters,
             such as an incorrect URI (e.g., not starting with “/ask”),
             or any other malformed HTTP request that does not comply with the HTTP standards.*/

  //Invalid Host or Network Issues:
  //IOException, UnknownHostException, ConnectException
          /* If the server cannot connect to the requested host (e.g., due to a non-existent host,
          unreachable server, or closed port). */

  //Page Not Found (404 Error)
  //PageNotFoundException (or a similar custom exception)
          /*If the URI in the HTTP request is valid but the requested resource does not exist
          (e.g., if the URI doesn’t map to any recognized endpoint or service on your server). */

  //General Server Error (500 Error):
  //ServerErrorException (or similar)
          /*  If there’s a general internal error in the server, such as database issues,
          or errors that don’t fall into the “bad request” or “page not found” categories. */

  //Socket Timeouts or Connection Issues:
  //SocketTimeoutException, IOException
  /*If the client attempts to connect to a server, but the connection times out or the server is unresponsive.*/

  //General Exception Handling:
  //Exception
  /*Any unexpected exceptions not handled by the specific error cases.*/



  // 1. Handling IOException:
  //IOException is the most common exception you will encounter while working with sockets in Java.
  // It can be thrown if there are network issues or problems with reading/writing data to a socket.

  //2. Handling UnknownHostException:
  //This exception occurs when the host you are trying to connect to cannot be resolved, i.e.,
  // the server address doesn’t exist or is invalid.

  //3. Handling SocketTimeoutException:
  //This exception occurs when a socket connection times out while waiting for a response from the server.

  //4. Handling ConnectException:
  //This exception occurs when a connection to a server fails.
  // It can happen if the server is down, the port is closed, or the server is unreachable.

  //5. Handling NullPointerException:
  //In case an unexpected null value is passed around (such as a null socket object),
  // you should catch this exception and handle it accordingly.


  /** Accepting new client */
  public static void acceptingClient(Socket clientSocket) throws IOException {
    String responseBody = "";
    String responseStatus = "HTTP/1.1 200 OK\r\n";

    try (
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream(), true)
    ) {
      System.out.println("Hello client, It's Sana's server");

      StringBuilder builder = new StringBuilder();
      String line;
      while ((line = inFromClient.readLine()) != null && !line.isEmpty()) {
        builder.append(line).append("\r\n");
      }

      String request = builder.toString();

      System.out.println("Request Of the Client: " + request);

      if (request.isEmpty()) {
        System.out.println(HTTP400);
        outToClient.println(HTTP400);
        return;
      }


      try {
        parseRequests(request, port);
        System.out.println("Parsed request successfully");

        TCPClient client = new TCPClient(shutdown, timeout, limit);

        System.out.println("Is the client processing ?");

        byte[] toServerBytes;
        if (string != null) {
          toServerBytes = string.getBytes(StandardCharsets.UTF_8);
        } else {
          toServerBytes = null;
        }

        byte[] responseBytes = client.askServer(hostname, clientPort, toServerBytes);
        responseBody = new String(responseBytes, StandardCharsets.UTF_8);

        System.out.println("Successfully communicated with the target server");
        System.out.println("| Port: " + port + " | Hostname: " + hostname);
        System.out.println("| Timeout: " + timeout + " | Shutdown: " + "| Limit: " + limit + "\n");

      } catch (IllegalArgumentException e) {
        responseStatus = HTTP400;
        responseBody = "Invalid request parameters: " + e.getMessage();
      } catch (SocketTimeoutException e) {
        responseStatus = HTTP408;
        responseBody = "Request Timeout: " + e.getMessage();
      } catch (UnknownHostException e) {
        responseStatus = HTTP404;
        responseBody = "Unknown Host: " + e.getMessage();
      } catch (IOException e) {
        responseStatus = HTTP400;
        System.out.println("Why internel server error every time ?!");
        responseBody = "Internal Server Error: " + e.getMessage();
      } catch (Exception e) {
        responseStatus = HTTP400;
        System.out.println("What happend here ?!");
        responseBody = "Unexpected Exception: " + e.getMessage();
      }

      System.out.println("Is Client has any problems, whay client sending wrong ones ");

      String httpDate = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now());
      System.out.println("Response Status: " + responseStatus);

      String responseHeader = responseStatus +
              "Server: SanaServer\r\n" +
              "Content-Type: text/plain; charset=UTF-8\r\n" +
              "Date: " + httpDate + "\r\n" +
              "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n"
              + responseBody;

      outToClient.print(responseHeader);
      System.out.println(responseHeader);
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
  public static void parseRequests(String request, int port) throws IllegalArgumentException {

    // GET /ask?hostname=time.nist.gov&limit=1200&port=13 HTTP/1.1


    // ["GET", "/ask?hostname=time.nist.gov&limit=1200&port=13", "HTTP/1.1"]     requestLine, splitting by " "
    //      [0]  "GET"
    //		[1]  "/ask?hostname=time.nist.gov&limit=1200&port=13"
    //		[2]  "HTTP/1.1"
    String[] requestLines = request.split("\r\n");


    // ["/ask", "hostname=time.nist.gov&limit=1200&port=13"]                    requestParts, splitting at ?
    //      [0]  "/ask"
    //      [1]  "hostname=time.nist.gov&limit=1200&port=13"
    String[] requestParts = requestLines[0].split(" ");


    // ["hostname=time.nist.gov", "limit=1200", "port=13"]                     parameters, splitting by &
    //     	[0]  "hostname=time.nist.gov"
    //		[1]  "limit=1200"
    //		[2]  "port=13"     client port
    String url = requestParts[1];
    String[] parameters = url.substring(5).split("&");

    System.out.println("Parsed parameters:     Parse the correct ones come on");
    System.out.println("Hostname: " + hostname);
    System.out.println("Port: " + clientPort);


    // ["hostname=time.nist.gov"]                                             key value, splitting by =, looping
    //      [0]  "hostname"
    //      [1]  "time.nist.gov"

    // ["limit=1200"]
    //      [0]  "limit"
    //      [1]  "1200"

    // ["port=13"]    client port
    //      [0]  "port"
    //      [1]  "13"

    if (requestParts.length < 3 && requestLines.length == 0) {
      throw new IllegalArgumentException( HTTP400 + " Malformed HTTP request.");
    }

    if (!"GET".equals(requestParts[0])) {
      throw new IllegalArgumentException( HTTP400 + "GET requests are accepted.");
    }

    if (!requestParts[2].equals("HTTP/1.1")) {
      throw new IllegalArgumentException("505 HTTP Version Not Supported");
    }

    System.out.println("URI being processed:  " + requestParts[1] + "\r\n" + requestParts[2] + "Is it Prcoessing with Sana's server right?");
    if (!requestParts[1].startsWith("/ask?")) {
      throw new IllegalArgumentException(HTTP400 + "Invalid endpoint. Use /ask?");
    }
    System.out.println("Ok ask is fine");

    boolean hostnamePresent = false;

    for (String param : parameters) {
      String[] keyValue = param.split("=");
      String value = keyValue[1];
      if ("hostname".equals(keyValue[0])){
        hostname = value;
        hostnamePresent = true;
      }
      try {
        switch (keyValue[0]) {
          case "hostname":
            hostname = value;
            break;
          case "port":
            clientPort = Integer.parseInt(value);
            break;
          case "string":
            string = value + "\n";
            break;
          case "shutdown":
            shutdown = Boolean.parseBoolean(value);
            break;
          case "timeout":
            timeout = Integer.parseInt(value);
            break;
          case "limit":
            limit = Integer.parseInt(value);
            break;
        }
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException( HTTP400 + "Invalid parameter value: " + keyValue[0]);
      }
    }

    if (!hostnamePresent || hostname == null || hostname.isEmpty()) {
      throw new IllegalArgumentException( HTTP400 + " Missing or empty 'hostname' parameter.");
    }
    System.out.println("Parsed hostname: " + hostname);
    System.out.println("Parsed port: " + clientPort);
    System.out.println("Attempting to connect to " + hostname + " on port " + clientPort);
  }



  // TCP Client
  public static class TCPClient {
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

      if (shutdown) {
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
}
