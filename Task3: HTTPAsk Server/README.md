## HTTPAsk Server

In Task 3, I developed an HTTP server that listens for incoming HTTP requests. The server expects GET requests with query parameters specifying the target server’s hostname, port, and optional parameters such as timeout, data limit, and shutdown flag. Upon receiving a request, the server extracts and validates these parameters using ParseRequest.

It then creates a TCPClient instance using `tcpclient.TCPClient` class to establish a TCP connection with the specified hostname and port, optionally sending a message if provided. The TCPClient retrieves the response from the remote server while respecting the configured timeout, limit, and shutdown settings. Finally, the HTTP server formats the TCP server’s response into a valid HTTP response and sends it back to the client.

This implementation bridges HTTP and raw TCP communication, allowing users to interact with TCP servers via HTTP requests.


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

