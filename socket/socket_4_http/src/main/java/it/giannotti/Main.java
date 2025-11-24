package it.giannotti;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(3000);
        while(true) {
            Socket socket = ss.accept();
            ServerThread t = new ServerThread(socket);
            t.start();
            
        }
    }
}