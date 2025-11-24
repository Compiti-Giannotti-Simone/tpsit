package it.perini;
import java.io.*;
import java.util.*;

public class GameHandler {
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    private boolean isPlayer1 = false;

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";

    public GameHandler(BufferedReader in, PrintWriter out, Scanner scanner) {
        this.in = in;
        this.out = out;
        this.scanner = scanner;
    }

    public void runGame() throws IOException {
        while (true) {
            String msg = in.readLine();
            if (msg == null) break;

            switch (msg) {
                case "WAIT":
                    System.out.println("Waiting for opponent...");
                    isPlayer1 = true;
                    break;
                case "READY":
                    System.out.println("Game started!");
                    if(isPlayer1) out.println(getMove());
                    else System.out.println("Waiting for opponent's move...");
                    break;
                case "OK":
                    System.out.println("Move accepted. Waiting for opponent...");
                    break;
                case "KO":
                    System.out.println("Invalid move. Try again:");
                    out.println(getMove());
                    break;
                case "W":
                    System.out.println("You win!");
                    return;
                case "P":
                    System.out.println("Draw.");
                    return;
                case "DISCONNECTED":
                    System.out.println("Opponent disconnected. You win!");
                    return;
                default:
                    if (msg.contains(",")) {
                        showBoard(msg);
                        if (msg.endsWith(",L")) {
                            System.out.println("You lose.");
                            return;
                        } else if (msg.endsWith(",P")) {
                            System.out.println("Draw.");
                            return;
                        } else {
                            out.println(getMove());
                        }
                    } else {
                        System.out.println("Unknown message: " + msg);
                    }
                    break;
            }
        }
    }

    private int getMove() {
        int move;
        while (true) {
            System.out.print("Enter your move (0-8): ");
            try {
                move = Integer.parseInt(scanner.nextLine());
                if (move >= 0 && move <= 8) break;
                System.out.println("Number must be 0-8.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
        return move;
    }

    private void showBoard(String msg) {
        String[] cells = msg.split(",");
        System.out.println("Board:");
        for (int i = 0; i < 9; i++) {
            String symbol = switch (cells[i]) {
                case "1" -> ANSI_BLUE + "X" + ANSI_RESET;
                case "2" -> ANSI_RED + "O" + ANSI_RESET;
                default -> "" + i;
            };
            System.out.print(" " + symbol + " ");
            if ((i + 1) % 3 == 0) {
                if (i < 8) System.out.println("\n---|---|---");
                else System.out.println();
            } else {
                System.out.print("|");
            }
        }
        System.out.println("-----------");
    }
}