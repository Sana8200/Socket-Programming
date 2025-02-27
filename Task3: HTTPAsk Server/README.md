## HTTPAsk Server

In Task 3, I developed an HTTP server that acts as both a server and a client. It accepts HTTP requests with query parameters specifying the target server’s hostname and port. 
Upon receiving a request, the server uses a tcpclient.tcpclient.TCPClient to communicate with the specified server, retrieves the response, and sends it back as part of the HTTP response body.

This task involved integrating the TCP client from Task 1 into an HTTP server, effectively creating a web-based version of the TCP client.


### Features
- Parses HTTP GET requests with query parameters.
- Communicates with a specified TCP server based on extracted parameters.
- Handles errors such as bad requests, timeouts, and unknown hosts.
- Supports optional parameters like timeout, limit, and shutdown flags.
- Returns server responses back as HTTP responses.


### How It Works
1. The server listens on a specified port.
2. Clients send GET requests in the format:
- GET /ask?hostname=&port=&string=&timeout=&limit=&shutdown=<true/false> HTTP/1.1
3. The server extracts parameters and forwards the request to the target TCP server.
4. The response from the TCP server is returned as an HTTP response.




### `HTTPAsk.java`
The entry point to the HTTP server.
- *`main(String[] args):`* Starts the server and listens for incoming connections.

### `AcceptClient.java`
Handles incoming client requests, extracts query parameters, and communicates with the TCP server.

Methods:
*`acceptingClient(Socket clientSocket)`*
- Reads and parses the incoming HTTP request.
- Extracts the query parameters and validates them.
- Calls ParseRequest.parseRequests() to extract hostname, port, and optional parameters.
- Uses TCPClient to forward the request to the target TCP server.
- Handles various errors (e.g., bad requests, timeouts, unknown hosts).
- Sends the response back to the HTTP client.

### `ParseRequest.java`
Parses the HTTP GET requests and extracts the necessary parameters for communication with the target server.

Extracts query parameters from the HTTP GET request and validates them.

Methods:
*`parseRequests(String request)`*
- Splits and processes the request string.
- Validates required fields (hostname, port).
- Extracts optional parameters (string, timeout, limit, shutdown).
- Throws exceptions for malformed or missing required parameters.


### `TCPClient`
A helper class for communicating with the TCP server.

- *`TCPClient(boolean shutdown, Integer timeout, Integer limit)`*
Constructor to initialize TCP client settings.

Methods:
*`askServer(String hostname, int port, byte[] toServerBytes)`*
- Establishes a TCP connection to the given hostname and port.
- Sends the provided byte data to the server.
- Reads the response, adhering to the specified limit and timeout settings.
- Shuts down the connection if the shutdown flag is enabled.
- Returns the response as a byte array.

