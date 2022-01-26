package com.example.anonchat;

import android.graphics.Bitmap;

public class Message {
    public String message;
    public User user;
    public Bitmap image;

    public Message(String message, User user, Bitmap image) {
        this.message = message;
        this.user = user;
        this.image = image;
    }

    public Message() {
    }
}
