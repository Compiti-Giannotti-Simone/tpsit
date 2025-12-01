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
import java.util.stream.Collectors;

public class ServerThread extends Thread {

    private final BufferedReader in;
    private final PrintWriter out;
    private final DataOutputStream outBinary;

    private String headers;

    public ServerThread(Socket socket) throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        outBinary = new DataOutputStream(socket.getOutputStream());
    }

    public void print405() {
        headers = """
                HTTP/1.1 405 Method Not Allowed
                Content-Length: 0
                Allow: GET
                """;
        out.println(headers);

    }

    public void print404() {
        try {
            File file = new File("htdocs/notfound.html");
            headers = """
                    HTTP/1.1 404 Not Found
                    Content-Type: text/html; charset=UTF-8
                    Content-Length: """ + file.length() + "\n";
            out.println(headers);
            InputStream input = new FileInputStream(file);
            byte[] buf = new byte[8193];
            int n;
            while ((n = input.read(buf)) != 0) {
                outBinary.write(buf, 0, n);
            }
            input.close();
        } catch (Exception e) {
        }
    }

    public void printFile(File file) {
        try {
            headers = """
                    HTTP/1.1 200 OK
                    Content-Type: """ + getContentType(file) +
                    """
                    ; charset=UTF-8
                    Content-Length: """ + file.length() + "\n";
            out.println(headers);

            InputStream input = new FileInputStream(file);
            byte[] buf = new byte[8193];
            int n;
            while ((n = input.read(buf)) != 0) {
                outBinary.write(buf, 0, n);
            }
            input.close();
        } catch (Exception e) {
        }
    }

    public void redirectTo(String path) {
        System.out.println(path);
        headers = """
                HTTP/1.1 301 Moved Permanently
                Content-Length: 0
                Location: """ + path;
        out.println(headers + "\n");
    }

    private String getContentType(File file) {
        String name = file.getName();
        String extension = name.split("[.]", 2)[1];
        switch (extension) {
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "json":
                return "application/json";
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
            System.out.println(request[0] + " " + request[1] + " " + request[2]); 
            String line = "";
            do {
                line = in.readLine();
            } while (!line.equals(""));

            if (request[0].equals("GET")) {
                if (request[1].endsWith("/")) {
                    request[1] += "index.html";
                }
                    File file = new File("htdocs" + request[1]);
                    if (file.isDirectory()) {
                        redirectTo(request[1] + "/");
                    }
                    else if(file.isFile()) {
                        printFile(file);
                    }
                    else {
                        print404();
                    }
            } else {
                print405();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
