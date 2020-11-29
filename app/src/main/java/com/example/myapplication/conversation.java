package com.example.myapplication;

public class conversation {
    private String pseudo;
    private String[] message;

    public conversation(String pseudo, String[] message) {
        this.pseudo = pseudo;
        this.message = message;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String[] getMessage() {
        return message;
    }
}
