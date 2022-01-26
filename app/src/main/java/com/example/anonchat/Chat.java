package com.example.anonchat;

import java.util.ArrayList;

public class Chat {
    public Chat() { }

    public Chat( String name, String id, String description, ArrayList<Message> messages) {
        this.description = description;
        this.name = name;
        this.id = id;
        this.messages = messages;
    }

    public String description;
    public String name;
    public String id;
    public ArrayList<Message> messages;


}
