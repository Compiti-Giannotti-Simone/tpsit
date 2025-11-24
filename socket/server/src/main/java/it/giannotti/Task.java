package it.giannotti;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data
public class Task {

    private int id;
    private String machine;
    private Priority priority;

    //Renamed to Status from State for conflicting class names
    private Status status;

    private String desc;
    private String author;

    //List of users who claimed the Task, author of the task is automatically added in
    private List<String> responsibles;

    public Task(int id, String machine, Priority priority, String desc, String author) {
        this.id = id;
        this.machine = machine;
        this.priority = priority;
        this.status = Status.TODO;
        this.desc = desc;
        this.author = author;
        this.responsibles = new ArrayList<>();
        this.responsibles = Collections.synchronizedList(responsibles);
        responsibles.add(author);
    }

    @Override
    public String toString() {
        return "[" + id + "] " + machine + " " + priority + " " + status +  " " + author + " - " + desc;
    }

    
}
