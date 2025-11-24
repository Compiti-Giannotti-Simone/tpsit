package it.proiotti;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(3000);

        
        do {
            Socket p1 = server.accept();
            BufferedReader in1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
            PrintWriter out1 = new PrintWriter(p1.getOutputStream(), true);
            out1.println("WAIT");
            Socket p2 = server.accept();
            BufferedReader in2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
            PrintWriter out2 = new PrintWriter(p2.getOutputStream(), true);
            out1.println("READY");
            out2.println("READY");

            Match m = new Match(p1,p2);
            m.start();
            
        } while(true);
    }
}