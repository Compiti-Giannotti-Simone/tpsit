package it.giannotti;

import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(3000);
        do {
            Socket socket = server.accept();
            MyThread t = new MyThread(socket);
            t.start();
        } while(true);

    }
}