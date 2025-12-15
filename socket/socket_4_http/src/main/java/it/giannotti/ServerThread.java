package it.giannotti;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerThread extends Thread {

    private final BufferedReader in;
    private final PrintWriter out;
    private final DataOutputStream outBinary;

    private String headers;
    private File file;

    private String request_method;
    private String request_resource;
    private String request_httpver;
    private HashMap<String,String> request_headers;
    private String request_body;

    public ServerThread(Socket socket) throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        outBinary = new DataOutputStream(socket.getOutputStream());
        request_headers = new HashMap<>();
    }

    private int checkRequestLine() {
        boolean valid_method = false;
        boolean valid_resource = false;
        boolean valid_httpver = false;
        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"};
        for (String string : methods) {
            if(string.equals(request_method)) {
                valid_method = true;
            }
        }
        valid_resource = request_resource != null && request_resource.startsWith("/");
        String versionPattern = "^HTTP/\\d\\.\\d$"; // Matches HTTP/x.x where x is a digit
        Pattern pattern = Pattern.compile(versionPattern);
        Matcher matcher = pattern.matcher(request_httpver);
        valid_httpver = matcher.matches();

        if(valid_httpver && valid_method && valid_resource) {
            return 0;
        }
        return -1;
    }

    private String readBody(BufferedReader in, int contentLength) throws IOException {
        if (contentLength <= 0) {
            return "";
        }
        char[] buf = new char[contentLength];
        int read = 0;
        while (read < contentLength) {
            int n = in.read(buf, read, contentLength - read);
            if (n == -1) {
                break;
            }
            read += n;
        }
        return new String(buf, 0, read);
    }

    private void sendFile(File file, DataOutputStream outBinary) throws IOException {
        try (InputStream input = new FileInputStream(file)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = input.read(buf)) != -1) {
                outBinary.write(buf, 0, n);
            }
        }
    }

    private void notAllowed() {
        headers = """
                HTTP/1.1 405 Method Not Allowed
                Content-Length: 0
                Allow: GET, POST, HEAD
                """ + "\n";
        out.println(headers);

    }

    private void badRequest() {
        headers = """
                HTTP/1.1 400 Bad Request
                Content-Length: 0
                """ + "\n";
        out.println(headers);

    }

    private void notFound() {
        try {
            File file = new File("htdocs/notfound.html");
            headers = """
                    HTTP/1.1 404 Not Found
                    Content-Type: text/html; charset=UTF-8
                    Content-Length: """ + file.length() + "\n";
            out.println(headers);
            sendFile(file, outBinary);
        } catch (Exception e) {
        }
    }

    private void found(String path) {
        System.out.println(path);
        headers = """
                HTTP/1.1 302 Found
                Content-Length: 0
                Location: """ + path + "\n";
        out.println(headers);
    }

    private void printHeader(File file) {
        try {
            headers = "HTTP/1.1 200 OK\nContent-Type: " + getContentType(file) +"\nContent-Length: " + file.length() + "\r\n";
            out.println(headers);
        } catch (Exception e) {
        }
    }

    private void echoBody() {
        try {
            headers = """
                    HTTP/1.1 200 OK
                    Content-Type: """ + getContentType(file) +
                    """
                            Content-Length: """ + request_headers.get("Content-Length") + "\n";
            out.println(headers);
            out.println(request_body);
        } catch (Exception e) {
        }
    }

    private int findFile(String resource) {
        if (resource.endsWith("/")) {
            resource += "index.html";
        }
        File file = new File("htdocs" + resource);
        if (file.isDirectory()) {
            return 302;
        } else if (file.isFile()) {
            return 0;
        } else {
            return 404;
        }
    }

    private String getContentType(File file) {
        String name = file.getName();
        String extension = name.split("[.]", 2)[1];
        switch (extension) {
            case "html":
                return "text/html; charset=UTF-8";
            case "css":
                return "text/css; charset=UTF-8";
            case "js":
                return "application/javascript; charset=UTF-8";
            case "json":
                return "application/json; charset=UTF-8";
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "gif":
                return "image/gif";
            case "svg":
                return "image/svg+xml";
            case "ico":
                return "image/x-icon";
            case "pdf":
                return "application/pdf";
            case "zip":
                return "application/zip";
            case "txt":
                return "text/plain";
            default:
                return "application/octet-stream";
        }
    }

    @Override
    public void run() {
        try {
            String[] request = in.readLine().split(" ", 3);
            in.readLine(); // skip \n line
            request_method = request[0];
            request_resource = request[1];
            request_httpver = request[2];
            System.out.println(request_method + " " + request_resource + " " + request_httpver);
            if(checkRequestLine() != 0) {
                badRequest();
                return;
            }

            String line = "";
            do {
                line = in.readLine();
                System.out.println(line);
                if(!line.equals("")){
                    request_headers.put(line.split("[:]",2)[0],line.split(" ",2)[1]);    
                } 
            } while (!line.equals(""));
            if(request_method.equals("POST")) {
                request_body = readBody(in,Integer.parseInt(request_headers.get("Content-Length")));
            }

            switch(findFile(request_resource)) {
                case 0:
                    if (request_resource.endsWith("/")) {
                        request_resource += "index.html";
                    }
                    file = new File("htdocs" + request_resource);
                    break;
                case 302:
                    found(request_resource + "/");
                    return;
                case 404:
                    notFound();
                    return;
            }

            switch (request_method) {
                case "GET":
                    printHeader(file);
                    sendFile(file, outBinary);
                    return;
                case "HEAD":
                    printHeader(file);
                    return;
                case "POST":
                    echoBody();
                    return;
                case "OPTIONS":
                case "PUT":
                case "TRACE":
                case "DELETE":
                case "PATCH":
                case "CONNECT":
                    notAllowed();
                    return;
                default:
                    badRequest();
                    return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
