import java.io.IOException;

import tcpclient.TCPClient;

public class TCPAsk {
    /*
     * Usage: explain how to use the program, then exit with failure status
     */
    private static void usage() {
        System.err.println("Usage: TCPAsk host port <data to server>");
        System.exit(1);
    }

    /*
     * Main program. Parse arguments on command line and call TCPClient
     */
    public static void main( String[] args) {
        String hostname = null;
        int port = 0;
        //byte[] userInputBytes = new byte[0];
		byte[] userInputBytes = null;		
        
        try {
            // Get mandatory command line arguments: hostname and port number
            int argindex = 0;
            hostname = args[argindex++];
            port = Integer.parseInt(args[argindex++]);

            // Remaining arguments, if any, are string to send to server
            if (argindex < args.length) {
                // Collect remaining arguments into a string with single space as separator
                StringBuilder builder = new StringBuilder();
                boolean first = true;
                while (argindex < args.length) {
                    if (first)
                        first = false;
                    else
                        builder.append(" ");
                    builder.append(args[argindex++]);
                }
                builder.append("\n");
                userInputBytes = builder.toString().getBytes();
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            // Exceeded array while parsing command line, or could
            // not convert port number argument to integer -- tell user
            // how to use the program
            usage();
        }

        try {
            TCPClient tcpClient = new tcpclient.TCPClient();
            byte[] serverBytes;
			if (userInputBytes != null) {
				serverBytes = tcpClient.askServer(hostname, port, userInputBytes);
			}
			else {
				serverBytes = tcpClient.askServer(hostname, port);
			}
            String serverOutput = new String(serverBytes);
            System.out.printf("%s:%d says:\n%s", hostname, port, serverOutput);
        } catch(IOException ex) {
            System.err.println(ex);
            System.exit(1);
        }
    }
}

