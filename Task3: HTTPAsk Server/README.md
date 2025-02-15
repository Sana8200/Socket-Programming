Task 3: HTTPAsk Server

In Task 3, I developed an HTTP server that acts as both a server and a client. It accepts HTTP requests with query parameters specifying the target server’s hostname and port. 
Upon receiving a request, the server uses a TCPClient to communicate with the specified server, retrieves the response, and sends it back as part of the HTTP response body.

This task involved integrating the TCP client from Task 1 into an HTTP server, effectively creating a web-based version of the TCP client.

