package it.proiotti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Match extends Thread {

    private Socket p1;
    private Socket p2;

    private BufferedReader in1;
    private PrintWriter out1;

    private BufferedReader in2;
    private PrintWriter out2;

    private boolean p1_turn = true;

    private int board[] = {
            0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    public Match(Socket p1, Socket p2) throws IOException {
        this.p1 = p1;
        in1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
        out1 = new PrintWriter(p1.getOutputStream(), true);
        this.p2 = p2;
        in2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
        out2 = new PrintWriter(p2.getOutputStream(), true);
    }

    public int checkWin() {
        int[][] winConditions = {
                { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 },
                { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 },
                { 0, 4, 8 }, { 2, 4, 6 }
        };

        for (int[] condition : winConditions) {
            if (board[condition[0]] != 0 &&
                    board[condition[0]] == board[condition[1]] &&
                    board[condition[1]] == board[condition[2]]) {
                return board[condition[0]] == 1 ? 1 : 2;
            }
        }

        for (int cell : board) {
            if (cell == 0) {
                return 0; // Game continues
            }
        }
        return 3; // Draw
    }

    public void run() {
        while (true) {
            if (p1.isClosed() || p2.isClosed()) {
                try {
                    if (!p1.isClosed()) {
                        out1.println("DISCONNECTED");
                        p1.close();
                    }
                    if (!p2.isClosed()) {
                        out2.println("DISCONNECTED");
                        p2.close();
                    }
                } catch (Exception e) {

                }
            }
            while (p1_turn) {
                try {
                    String turn = in1.readLine();
                    int turn_parsed = Integer.parseInt(turn);
                    if (board[turn_parsed] == 0) {
                        board[turn_parsed] = 1;
                        out1.println("OK");
                        p1_turn = false;
                    } else {
                        out1.println("KO");
                    }
                } catch (Exception e) {
                    out1.println("KO");
                }
            }
            int result = checkWin();
            String boardState = "";
            for (int cell : board) {
                boardState += cell;
                boardState += ",";
            }
            switch (result) {
                case 0:
                    out2.println(boardState);
                    break;
                case 1:
                    // Player 1 wins
                    out1.println("W");
                    out2.println(boardState + "L");
                    try {
                        p1.close();
                        p2.close();
                    } catch (Exception e) {

                    }
                    return;

                case 2:
                    // Player 2 wins
                    out2.println("W");
                    out1.println(boardState + "L");
                    try {
                        p1.close();
                        p2.close();
                    } catch (Exception e) {

                    }
                    return;
                case 3:
                    // Draw
                    out1.println("P");
                    out2.println(boardState + "P");
                    try {
                        p1.close();
                        p2.close();
                    } catch (Exception e) {

                    }
                    return;
                default:
                    break;
            }
            while (!p1_turn) {
                try {
                    String turn = in2.readLine();
                    int turn_parsed = Integer.parseInt(turn);
                    if (board[turn_parsed] == 0) {
                        board[turn_parsed] = 2;
                        out2.println("OK");
                        p1_turn = true;
                    } else {
                        out2.println("KO");
                    }
                } catch (Exception e) {
                    out2.println("KO");
                }
            }

            result = checkWin();
            boardState = "";
            for (int cell : board) {
                boardState += cell;
                boardState += ",";
            }
            switch (result) {
                case 0:
                    out1.println(boardState);
                    break;
                case 1:
                    // Player 1 wins
                    out1.println("W");
                    out2.println(boardState + "L");
                    try {
                        p1.close();
                        p2.close();
                    } catch (Exception e) {

                    }
                    return;

                case 2:
                    // Player 2 wins
                    out2.println("W");
                    out1.println(boardState + "L");
                    try {
                        p1.close();
                        p2.close();
                    } catch (Exception e) {

                    }
                    return;
                case 3:
                    // Draw
                    out1.println("P");
                    out2.println(boardState + "P");
                    try {
                        p1.close();
                        p2.close();
                    } catch (Exception e) {

                    }
                    return;
                default:
                    break;
            }
        }
    }
}