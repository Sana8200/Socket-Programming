Task 2: HTTPEcho Server

For this task, I built an HTTP server that listens for client connections and echoes back any data it receives, formatted as a valid HTTP response. 
The server processes incoming HTTP requests and sends the received content back in the response body with a “200 OK” status.

I implemented the server to handle incoming HTTP requests, parse the data, and send it back in a properly structured HTTP response. 

`TCPAsk.java` – An example Java program that uses the `tcpclient.tcpclient.TCPClient` class.


tcpclient.tcpclient.TCPClient - Java TCP Client with Timeout, Shutdown, and Limit Features
This Java class implements a TCP client that connects to a specified server and port, sends data, and receives a response. It supports:
- Timeout handling – Stops reading if the server takes too long to respond.
- Shutdown option – Allows closing the output stream after sending data.
- Data limit enforcement – Restricts the amount of data received from the server.

The client reads data from the server into a buffer, ensuring it respects the timeout and data limit settings before closing the connection.
