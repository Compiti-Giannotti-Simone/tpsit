package it.perini;

import java.io.*;
import java.net.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String host = "10.22.9.23";
        int port = 3000;

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            GameHandler handler = new GameHandler(in, out, scanner);
            handler.runGame();

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}