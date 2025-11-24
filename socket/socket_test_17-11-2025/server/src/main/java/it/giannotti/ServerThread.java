package it.giannotti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerThread extends Thread {

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    private AtomicInteger nextId;
    private List<Task> tasks;

    private boolean logged_in = false;

    private String username = "";
    private String response = "";
    //command sent by client
    private String cmd = "";
    //command divided into 4 params, where params[0] is always the main command (e.g. LIST, NEW, MINE etc..)
    private String[] params;

    public ServerThread(Socket socket, AtomicInteger nextId, List<Task> tasks) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        this.nextId = nextId;
        this.tasks = tasks;
    }

    private void listAllTasks() {
        out.println("TASKS:");
        for (Task task : tasks) {
            out.println(task.toString());
        }
    }

    private void listUserTasks() {
        out.println("TASKS:");
        for (Task task : tasks) {
            if (task.getAuthor().equals(username) || task.getResponsibles().contains(username)) {
                out.println(task.toString());
            }
        }
    }

    private void listTasksByStatus(Status status) {
        out.println("TASKS:");
        for (Task task : tasks) {
            if (task.getStatus() == status) {
                out.println(task.toString());
            }
        }
    }

    /**
     *
     * @param id Id of task to be found
     *
     * @return index of task, -1 if not found
     */
    private int findTaskById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return tasks.indexOf(task);
            }
        }
        return -1;
    }

    @Override
    public void run() {
        try {
            out.println("WELCOME");

            while (!logged_in) {
                cmd = in.readLine();
                params = cmd.split(" ", 4);
                //allow quitting while not logged in
                if (cmd.equals("QUIT")) {
                    socket.close();
                    return;
                }
                //if command is a login command with no additional parameters, allow the login
                if (params[0].equals("LOGIN") && params.length == 2) {
                    logged_in = true;
                    username = params[1];
                } else {
                    out.println("ERR LOGINREQUIRED");
                }
            }
            out.println("OK");
            while (!cmd.equals("QUIT")) {
                cmd = in.readLine();
                params = cmd.split(" ", 4);
                switch (params[0]) {
                    case "LOGIN":
                    // LOGIN <username> ; 2 param
                    // useless while already logged in
                        response = "ERR LOGINREQUIRED";
                        break;
                    case "NEW":
                        // NEW <name> <priority> <description> ; 4 param
                        if (params.length == 4) {
                            try {
                                tasks.add(new Task(nextId.get(), params[1], Priority.valueOf(params[2]), params[3], username));
                                response = "OK CREATED " + nextId;
                                nextId.incrementAndGet();
                            } catch (IllegalArgumentException e) {
                                response = "ERR PRIORITY";
                            }
                        } else {
                            response = "ERR SYNTAX";
                        }
                        break;
                    case "LIST":
                        // LIST ; 1 param
                        if (params.length == 1) {
                            listAllTasks();
                            response = "END";
                        } else {
                            response = "ERR SYNTAX";
                        }
                        break;
                    case "FILTER":
                        // FILTER <status> ; 2 params
                        if (params.length == 2) {
                            try {
                                listTasksByStatus(Status.valueOf(params[1].toUpperCase()));
                                response = "END";
                            } catch (IllegalArgumentException e) {
                                response = "ERR STATE";
                            }
                        } else {
                            response = "ERR SYNTAX";
                        }
                        break;
                    case "MINE":
                        // MINE ; 1 param
                        if (params.length == 1) {
                            listUserTasks();
                            response = "END";
                        } else {
                            response = "ERR SYNTAX";
                        }
                        break;
                    case "SET":
                        // SET <id> <status> ; 3 params
                        if (params.length == 3) {
                            int index = findTaskById(Integer.parseInt(params[1]));
                            if (index == -1) {
                                response = "ERR NOTFOUND";
                            } else {
                                Task task = tasks.get(index);
                                if (!task.getAuthor().equals(username) && !task.getResponsibles().contains(username)) {
                                    response = "ERR PERMISSION";
                                } else {
                                    try {
                                        task.setStatus(Status.valueOf(params[2]));
                                    } catch (IllegalArgumentException e) {
                                        response = "ERR STATE";
                                    }
                                    response = "OK UPDATED";
                                }
                            }
                        } else {
                            response = "ERR SYNTAX";
                        }
                        break;

                    case "DEL":
                        // DEL <id> ; 2 params
                        if (params.length == 2) {
                            int index = findTaskById(Integer.parseInt(params[1]));
                            if (index == -1) {
                                response = "ERR NOTFOUND";
                            } else {
                                Task task = tasks.get(index);
                                if (task.getStatus() == Status.DONE) {
                                    response = "ERR STATUSLOCK";
                                } else {
                                    if (!task.getAuthor().equals(username) && !task.getResponsibles().contains(username)) {
                                        response = "ERR PERMISSION";
                                    } else {
                                        tasks.remove(task);
                                        response = "OK DELETED";
                                    }
                                }
                            }
                        } else {
                            response = "ERR SYNTAX";
                        }
                        break;
                    case "QUIT":
                        // QUIT ; 1 param
                        if (params.length == 1) {
                            response = "BYE";
                        } else {
                            response = "ERR SYNTAX";
                        }
                        break;
                    case "CLAIM":
                        // CLAIM <id> ; 2 params
                        if (params.length == 2) {
                            int index = findTaskById(Integer.parseInt(params[1]));
                            if (index == -1) {
                                response = "ERR NOTFOUND";
                            } else {
                                Task task = tasks.get(index);
                                if (task.getStatus() == Status.DONE) {
                                    response = "ERR STATUSLOCK";
                                } else {
                                    if (task.getResponsibles().contains(username)) {
                                        //new error msg for claiming system
                                        response = "ERR ALREADYCLAIMED";
                                    } else {
                                        tasks.remove(task);
                                        //new ok message for claiming system
                                        response = "OK CLAIMED";
                                    }
                                }
                            }
                        } else {
                            response = "ERR SYNTAX";
                        }
                        break;
                    default:
                        response = "ERR UNKNOWNCMD";
                        break;
                }
                out.println(response);
            }
            socket.close();
        } catch (IOException e) {

        }
    }

}
