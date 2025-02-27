## Task 4: Multi-threaded HTTPAsk Server

In the final task, I enhanced the HTTPAsk server from Task 3 to handle multiple client requests concurrently. 
To achieve this, I implemented multi-threading, allowing the server to spawn a new thread for each incoming client connection. 
This enables the server to process multiple requests simultaneously, improving performance and scalability.

Each thread is responsible for handling an individual client request, performing the TCP query, and sending back the result in an HTTP response.

***
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

- Reading and parsing HTTP requests.

- Extracting query parameters from `/ask` endpoint.

- Using `TCPClient,java` to forward the request to the target TCP server.

- Sending the response back to the HTTP client.


#### `TCPClient,java`

A helper class that manages TCP communication by:

- Establishing a connection to the given hostname and port.

- Sending data if provided.

- Handling optional parameters like timeout, limit, and shutdown.

- Reading and returning the response from the TCP server.
