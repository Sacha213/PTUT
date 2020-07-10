package com.example.myapplication;

import android.app.ActionBar;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class Menu {

    /******************* Attribut *******************/
    private ImageView calendrier;
    private ImageView notes;
    private ImageView informations;
    private ImageView drive;
    private ImageView messagerie;

    private ImageView page;

    public Menu(ImageView cal, ImageView note, ImageView info, ImageView dri, ImageView message){
        calendrier = cal;
        notes = note;
        informations = info;
        drive = dri;
        messagerie = message;
    }

    /******************* Gestion des évènements du menu *******************/
    public ImageView menu(){

        calendrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 page = calendrier;

            }
        });

        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                page = notes;

            }
        });

        informations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                page = informations;

            }
        });

        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                page = drive;

            }
        });

        messagerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                page = messagerie;

            }
        });

        return page;
    }



}
