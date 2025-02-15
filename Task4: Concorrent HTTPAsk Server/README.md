Task 4: Multi-threaded HTTPAsk Server

In the final task, I enhanced the HTTPAsk server from Task 3 to handle multiple client requests concurrently. 
To achieve this, I implemented multi-threading, allowing the server to spawn a new thread for each incoming client connection. 
This enables the server to process multiple requests simultaneously, improving performance and scalability.

Each thread is responsible for handling an individual client request, performing the TCP query, and sending back the result in an HTTP response.
