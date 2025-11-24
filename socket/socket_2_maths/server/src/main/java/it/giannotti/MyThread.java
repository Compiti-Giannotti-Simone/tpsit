package it.giannotti;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MyThread extends Thread {

    private Socket socket;

    public MyThread(Socket s) {
        this.socket = s;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("v1.0.0");
            boolean running = true;

            do {
                String result = "";
                String operator = in.readLine();
                if (!operator.equals("0")) {
                    String n1 = in.readLine();
                    String n2 = in.readLine();
                    try {
                        switch (operator) {
                            case "1":
                            result = "ok:" + ( Float.parseFloat(n1) + Float.parseFloat(n2) );
                                break;
                            case "2":
                            result = "ok:" + ( Float.parseFloat(n1) - Float.parseFloat(n2) );
                                break;
                            case "3":
                            result = "ok:" + ( Float.parseFloat(n1) * Float.parseFloat(n2) );
                                break;
                            case "4":
                            if(!n2.equals("0"))  {
                                result = "ok:" + ( Float.parseFloat(n1) / Float.parseFloat(n2) );
                            }else {
                                result = "ko:div_by_zero";
                            }
                                break;
                            default:
                            result = "ko:invalid_operator";
                                break;
                        }
                    } catch (Exception e) {
                        if(e.getClass() == java.lang.NumberFormatException.class) {
                            result = "ko:invalid_data";
                        }
                    }
                    out.println(result);
                }
                else {
                    running = false;

                }
            } while (running);
            socket.close();
        } catch (Exception e) {
        }
    }
}
