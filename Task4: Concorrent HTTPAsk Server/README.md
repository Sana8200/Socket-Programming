## Multi-threaded HTTPAsk Server

I enhanced the HTTPAsk server from Task 3 to handle multiple client requests concurrently. 
To achieve this, I implemented multi-threading, allowing the server to spawn a new thread for each incoming client connection. 
This enables the server to process multiple requests simultaneously, improving performance and scalability.

Each thread is responsible for handling an individual client request, performing the TCP query, and sending back the result in an HTTP response.


### Features

  - Multi-threaded request handling

  - Supports `/ask` `HTTP GET` requests

  - Communicates with a TCP server

  - Handles timeouts, limits, and shutdown requests

***
### Class Descriptions

#### `ConcHTTPAsk.java`

This is the main server class responsible for:

- Creating a ServerSocket to listen for incoming connections.

- Accepting client connections and spawning a new thread (MyRunnable) for each client request.




#### `MyRunnable.java`

Implements Runnable and handles individual client requests by:

- Reading and parsing HTTP requests: It parses the incoming HTTP request to extract the necessary parameters.
 
- Extracting query parameters: Specifically handles the /ask endpoint and extracts parameters like hostname, port, string, shutdown, timeout, and limit.
 
- Using TCPClient.java: Forwards the request to the target TCP server, communicates with it, and returns the response.
 
- Sending the response back: Formats the response in HTTP format and sends it back to the client.



#### `TCPClient,java`

A helper class that manages TCP communication by:

- Establishing a connection to the given hostname and port.
 
- Sending data: If the string parameter is provided, it is sent to the TCP server.
 
- Handling optional parameters: Manages timeout (how long to wait for a response), limit (maximum size of response data), and shutdown (whether to close the output stream after sending data).
 
- Reading and returning the response: The response from the TCP server is captured and returned to the calling method in MyRunnable.java.


### `ParseRequest.java`

A utility class responsible for:

- Parsing and validating HTTP GET requests: It extracts and validates the parameters from the URL of the /ask endpoint.
 
- Handling parameter extraction: It splits the query string and assigns values to relevant variables (hostname, port, string, shutdown, timeout, and limit).
 
- Validating inputs: Ensures that required parameters like hostname and port are provided, and that the values for timeout, limit, and shutdown are correctly formatted. If any of the parameters are missing or invalid, it throws an IllegalArgumentException with an appropriate HTTP 400 response.
 
- Error handling: If any issues arise with the request or parameters, the class ensures that an appropriate error message is generated.

---

This structure ensures that the server can handle multiple concurrent requests efficiently, responding with appropriate results or error messages as needed. The modular design also allows for easy future improvements and scalability.

