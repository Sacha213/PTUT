package com.example.myapplication;

public class Affichage {
    private String pseudo;
    private String lastMessage;



    public Affichage(String pseudo, String lastMessage) {
        this.pseudo = pseudo;
        this.lastMessage = lastMessage;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
