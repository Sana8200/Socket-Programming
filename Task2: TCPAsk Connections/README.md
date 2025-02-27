# TCPAsk Connections 

A lightweight Java TCP client for communicating with a server over a TCP connection. It supports configurable timeouts, response size limits, and optional output shutdown for precise communication control.

## Features
- Customizable Timeout – Set a timeout to prevent indefinite waiting for a server response.
- Data Limit Handling – Restrict the maximum amount of data received from the server.
- Output Stream Shutdown – Optionally close the output stream after sending data.
- Robust Error Handling – Handles timeouts and ensures proper resource management.

---

### `TCPClient`
communicating with the TCP server.

- *`TCPClient(boolean shutdown, Integer timeout, Integer limit)`*
Constructor to initialize TCP client settings.

Methods:
*`askServer(String hostname, int port, byte[] toServerBytes)`*
- Establishes a TCP connection to the given hostname and port.
- Sends the provided byte data to the server.
- Reads the response, adhering to the specified limit and timeout settings.
- Shuts down the connection if the shutdown flag is enabled.
- Returns the response as a byte array.
