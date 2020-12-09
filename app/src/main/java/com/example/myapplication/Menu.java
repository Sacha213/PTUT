package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import java.net.HttpURLConnection;

import javax.mail.Folder;
import javax.mail.Store;


public class Menu {

    /******************* Attribut *******************/
    private ImageView calendrier; //Icônes du menu
    private ImageView notes;
    private ImageView informations;
    private ImageView drive;
    private ImageView messagerie;
    private Activity activity;


    public Menu(Activity activite){


        /******************* Initialisation des variables *******************/
        this.activity = activite;//On conecte à l'activité

        this.calendrier = this.activity.findViewById(R.id.calendrier);
        this.notes = this.activity.findViewById(R.id.notes);
        this.informations = this.activity.findViewById(R.id.informations);
        this.drive = this.activity.findViewById(R.id.drive);
        this.messagerie = this.activity.findViewById(R.id.messagerie);

        /******************* Gestion des évènements du menu *******************/

        calendrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(activity.getApplicationContext(), Calendrier.class); //Ouverture d'une nouvelle activité
                activity.startActivity(otherActivity);

                activity.finish();//Fermeture de l'ancienne activité
                activity.overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(activity.getApplicationContext(), Note.class); //Ouverture d'une nouvelle activité
                activity.startActivity(otherActivity);

                activity.finish();//Fermeture de l'ancienne activité
                activity.overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        informations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(activity.getApplicationContext(), Information.class); //Ouverture d'une nouvelle activité
                activity.startActivity(otherActivity);

                activity.finish();//Fermeture de l'ancienne activité
                activity.overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(activity.getApplicationContext(), Drive.class); //Ouverture d'une nouvelle activité
                activity.startActivity(otherActivity);

                activity.finish();//Fermeture de l'ancienne activité
                activity.overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        messagerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(activity.getApplicationContext(), Messagerie.class); //Ouverture d'une nouvelle activité
                activity.startActivity(otherActivity);

                activity.finish();//Fermeture de l'ancienne activité
                activity.overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }
        });

    }

    public Menu(Activity activite, DatabaseManager databaseManager){


        /******************* Initialisation des variables *******************/
        this.activity = activite;//On conecte à l'activité

        this.calendrier = this.activity.findViewById(R.id.calendrier);
        this.notes = this.activity.findViewById(R.id.notes);
        this.informations = this.activity.findViewById(R.id.informations);
        this.drive = this.activity.findViewById(R.id.drive);
        this.messagerie = this.activity.findViewById(R.id.messagerie);

        /******************* Gestion des évènements du menu *******************/

        calendrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(activity.getApplicationContext(), Calendrier.class); //Ouverture d'une nouvelle activité
                activity.startActivity(otherActivity);

                //On ferme la database
                databaseManager.close();

                activity.finish();//Fermeture de l'ancienne activité
                activity.overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(activity.getApplicationContext(), Note.class); //Ouverture d'une nouvelle activité
                activity.startActivity(otherActivity);

                //On ferme la database
                databaseManager.close();

                activity.finish();//Fermeture de l'ancienne activité
                activity.overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        informations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(activity.getApplicationContext(), Information.class); //Ouverture d'une nouvelle activité
                activity.startActivity(otherActivity);

                //On ferme la database
                databaseManager.close();

                activity.finish();//Fermeture de l'ancienne activité
                activity.overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(activity.getApplicationContext(), Drive.class); //Ouverture d'une nouvelle activité
                activity.startActivity(otherActivity);

                //On ferme la database
                databaseManager.close();

                activity.finish();//Fermeture de l'ancienne activité
                activity.overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        messagerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(activity.getApplicationContext(), Messagerie.class); //Ouverture d'une nouvelle activité
                activity.startActivity(otherActivity);

                //On ferme la database
                databaseManager.close();

                activity.finish();//Fermeture de l'ancienne activité
                activity.overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }
        });

    }


}
