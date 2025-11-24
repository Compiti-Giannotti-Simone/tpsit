package it.giannotti;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws IOException {
        AtomicInteger nextId = new AtomicInteger(1);
        List<Task> tasks = new ArrayList<>();
        tasks = Collections.synchronizedList(tasks);
        ServerSocket ss = new ServerSocket(55555);
        while(true) {
            Socket socket = ss.accept();
            ServerThread t = new ServerThread(socket,nextId,tasks);
            t.start();
        }
        //ss.close();
    }
}