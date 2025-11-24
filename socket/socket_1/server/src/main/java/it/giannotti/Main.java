package it.giannotti;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello world!");
        ServerSocket server = new ServerSocket(55555);
        Socket client = server.accept();
        System.out.println("connesso");

        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);

        out.println("Welcome | v1.0.0");

        boolean running = true;

        do {
            String string = in.readLine();
            if(!string.equals("!exit")) {
                string = string.toUpperCase();
                out.println(string);
            } 
            else {
                running = false;
            }
        } while(running);
        client.close();
        server.close();
    }
}