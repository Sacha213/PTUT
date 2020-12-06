package com.example.myapplication;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    public String message;
    public int messageType;
    public Date messageDate;

    // Constructor
    public Message(String message, int messageType, Date date) {
        this.message = message;
        this.messageType = messageType;
        this.messageDate = date;

    }

    public String getMessage()
    {
        return this.message;
    }
}
