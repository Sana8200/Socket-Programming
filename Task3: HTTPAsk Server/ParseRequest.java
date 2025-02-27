

public class ParseRequest {

    static String hostname = "";
    static String string = "";
    static Integer servicePort = null;
    static boolean shutdown = false;
    static Integer limit = null;
    static Integer timeout = null;


    static final String HTTP400 = "HTTP/1.1 400 Bad Request\r\n";

    public static void parseRequests(String request) throws IllegalArgumentException {

        // GET /ask?hostname=time.nist.gov&limit=1200&port=13 HTTP/1.1

        // ["GET", "/ask?hostname=time.nist.gov&limit=1200&port=13", "HTTP/1.1"]     requestLine, splitting by " "
        //      [0]  "GET"
        //		[1]  "/ask?hostname=time.nist.gov&limit=1200&port=13"
        //		[2]  "HTTP/1.1"
        String[] requestLines = request.split("\r\n");

        if (requestLines.length == 0) {
            throw new IllegalArgumentException(HTTP400 + "Invalid request: " + request);
        }


        // ["/ask", "hostname=time.nist.gov&limit=1200&port=13"]                    requestParts, splitting at ?
        //      [0]  "/ask"
        //      [1]  "hostname=time.nist.gov&limit=1200&port=13"
        String[] requestParts = requestLines[0].split(" ");
        if (!"GET".equals(requestParts[0])) {
            throw new IllegalArgumentException(HTTP400 + "Only GET requests are accepted.");
        }

        if (!requestParts[2].equals("HTTP/1.1")) {
            throw new IllegalArgumentException("505 HTTP Version Not Supported");
        }

        if (!requestParts[1].startsWith("/ask?")) {
            throw new IllegalArgumentException(HTTP400 + "Invalid endpoint. Use /ask?");
        }
        // ["hostname=time.nist.gov", "limit=1200", "port=13"]                     parameters, splitting by &
        //     	[0]  "hostname=time.nist.gov"
        //		[1]  "limit=1200"
        //		[2]  "port=13"     client port
        String url = requestParts[1].substring(5);
        String[] parameters = url.split("&");

        // ["hostname=time.nist.gov"]                                             key value, splitting by =, looping
        //      [0]  "hostname"
        //      [1]  "time.nist.gov"

        // ["limit=1200"]
        //      [0]  "limit"
        //      [1]  "1200"

        // ["port=13"]    client port
        //      [0]  "port"
        //      [1]  "13"


        // Reset / clean the previous request state
        hostname = "";
        servicePort = null;
        string = "";
        shutdown = false;
        limit = null;
        timeout = null;

        for (String param : parameters) {
            String[] keyValue = param.split("=");
            if (keyValue.length != 2) {
                throw new IllegalArgumentException(HTTP400 + "Invalid parameter: " + param);
            }
            String key = keyValue[0];
            String value = keyValue[1];

            System.out.println(key + "=" + value);

            switch (key) {
                case "hostname":
                    if (value.isEmpty()) {
                        throw new IllegalArgumentException(HTTP400 + " Missing or empty 'hostname' value.");
                    }
                    hostname = value;
                    break;
                case "port":
                    try {
                        servicePort = Integer.parseInt(value);  // Client port from request parameter
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(HTTP400 + "'port' parameter value (" + value + ") is not an integer!");
                    }
                    break;
                case "string":
                    string = value + "\n";
                    break;
                case "shutdown":
                    try {
                        shutdown = Boolean.parseBoolean(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(HTTP400 + "'shutdown' parameter value (" + value + ") is not a boolean!");
                    }
                    break;
                case "timeout":
                    try {
                        timeout = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(HTTP400 + "'timeout' parameter value (" + value + ") is not an integer!");
                    }
                    break;
                case "limit":
                    try {
                        limit = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(HTTP400 + "'limit' parameter value (" + value + ") is not an integer!");
                    }
                    break;
                default:
                    throw new IllegalArgumentException(HTTP400 + "Invalid parameter key: " + key);
            }
        }

        System.out.println("Parsed hostname: " + hostname);
        System.out.println("Parsed service port: " + servicePort);
        System.out.println("Attempting to connect to " + hostname + " on port " + servicePort);

        if (hostname.isEmpty()) {
            throw new IllegalArgumentException(HTTP400 + " Service hostname should be provided");
        }

        if (servicePort == null) {
            throw new IllegalArgumentException(HTTP400 + " Service port should be provided");
        }
    }
}

