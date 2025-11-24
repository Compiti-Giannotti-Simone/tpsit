package it.giannotti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws UnknownHostException, IOException {
        Scanner scanner = new Scanner(System.in);
        String ip = "";
        int port = 0;
        System.out.println("Inserisci indirizzo ip: ");
        ip = scanner.nextLine();
        System.out.println("Inserisci porta: ");
        port = Integer.parseInt(scanner.nextLine());
        Socket socket = new Socket(ip, port);
        
        System.out.println("connesso");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String version = in.readLine();
        boolean running = true;

        do {
            System.out.println("Inserisci stringa: ");
            String string = scanner.nextLine();
            out.println(string);
            if(!string.equals("!exit")) {
                String transformed = in.readLine();
                System.out.println("Stringa trasformata: " + transformed); 
            } else {
                running = false;
            }
        } while (running);
        socket.close();
        scanner.close();
    }
}