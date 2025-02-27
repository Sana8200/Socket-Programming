package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    boolean shutdown;
    Integer timeout;
    Integer limit;
    
    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }
