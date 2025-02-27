# HTTPAsk Server

In Task 3, I developed an HTTP server that acts as both a server and a client. It accepts HTTP requests with query parameters specifying the target server’s hostname and port. 
Upon receiving a request, the server uses a tcpclient.tcpclient.TCPClient to communicate with the specified server, retrieves the response, and sends it back as part of the HTTP response body.

This task involved integrating the TCP client from Task 1 into an HTTP server, effectively creating a web-based version of the TCP client.


## Features
- Parses HTTP GET requests with query parameters.
- Communicates with a TCP server based on the extracted parameters.
- Handles errors such as bad requests, timeouts, and unknown hosts.
- Supports optional parameters like timeout, limit, and shutdown.


## How It Works
1. The server listens on a specified port.
2. Clients send GET requests in the format:
- GET /ask?hostname=&port=&string=&timeout=&limit=&shutdown=<true/false> HTTP/1.1
3. The server extracts parameters and forwards the request to the target TCP server.
4. The response from the TCP server is returned as an HTTP response.



## Classes

### `HTTPAsk.java`
The main class that starts the HTTP server.

- *`main(String[] args)`*
Starts the server and listens for incoming connections.

### `AcceptClient.java`
Processes client requests, extracts query parameters, and communicates with the TCP server.

### `ParseRequest.java`
Parses the HTTP GET request and extracts required parameters.



### `TCPClient`
A helper class for communicating with a TCP server.

- *`TCPClient(boolean shutdown, Integer timeout, Integer limit)`*
Constructor to initialize TCP client settings.

- *`askServer(String hostname, int port, byte[] toServerBytes)`* 
Connects to the TCP server, sends data, and retrieves the response.
