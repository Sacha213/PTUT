package com.example.myapplication;

import java.util.Date;

public class Message {
    public String message;
    public int messageType;
    public Date messageDate;

    // Constructor
    public Message(String message, int messageType, long date) {
        this.message = message;
        this.messageType = messageType;
        this.messageDate = new Date(date);

    }

    public String getMessage()
    {
        return this.message;
    }
}
