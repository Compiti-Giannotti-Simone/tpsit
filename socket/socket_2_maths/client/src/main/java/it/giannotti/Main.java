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
            String result = "";
            String operator = "";
            System.out.println("Seleziona l'operatore: \n1. Addizione\n2. Sottrazione\n3. Moltiplicazione\n4. Divisione\n0. Esci");
            operator = scanner.nextLine();
            operator.toLowerCase();
            switch (operator) {
                case "addizione":
                operator = "1";
                break;
                case "+":
                operator = "1";
                break;
                case "sottrazione":
                operator = "2";
                break;
                case "-":
                operator = "2";
                break;
                case "moltiplicazione": 
                operator = "3";
                break;
                case "*": 
                operator = "3";
                break;
                case "divisione":
                operator = "4";
                break;
                case "/":
                operator = "4";
                break;
                case "esci":
                operator = "0";
                break;
                default:
                break;
            }
            out.println(operator);
            if(operator.equals("0")) break;
            System.out.println("Inserisci il primo numero: ");
            out.println(scanner.nextLine());
            System.out.println("Inserisci il secondo numero: ");
            out.println(scanner.nextLine());
            result = in.readLine();
            if(result.charAt(0) == 'k') {
                System.out.println("Errore: " + result.substring(3));
            }
            else {
                System.out.println("Risultato: " + result.substring(3));
            }
        } while (running);
        socket.close();
        scanner.close();
    }
}