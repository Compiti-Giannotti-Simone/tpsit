package it.giannotti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.stream.Collectors;

public class ServerThread extends Thread {

    private final BufferedReader in;
    private final PrintWriter out;

    private String headers;

    public ServerThread(Socket socket) throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void print404() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("./notfound.html");
        String result = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
        headers = """
                HTTP/1.1 404 Not Found
                Content-Type: text/html; charset=UTF-8
                Server: ECAcc (nyd/D10E)
                Content-Length: """ + result.length() + "\n";
        out.println(headers);
        out.println(result);
    }

    public void printIndex() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("./index.html");
        String result = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
        headers = """
                HTTP/1.1 200 OK
                Content-Type: text/html; charset=UTF-8
                Server: ECAcc (nyd/D10E)
                Content-Length: """ + result.length() + "\n";
        out.println(headers);
        out.println(result);
    }

    @Override
    public void run() {
        try {
            String line = in.readLine();
            String request = line.split(" ", 3)[1];
            System.out.println(request);
            do {
                System.out.println(line);
                line = in.readLine();
            } while (!line.equals(""));
            switch (request) {
                case "/":
                    printIndex();
                    break;
                case "/favicon.ico":
                    break;

                default:
                    print404();
                    break;
            }

        } catch (Exception e) {
        }
    }

}
