package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Note extends AppCompatActivity {

    /******************* Attribut *******************/
    private ImageView calendrier; //Icônes du menu
    private ImageView notes;
    private ImageView informations;
    private ImageView drive;
    private ImageView messagerie;

    private LinearLayout layoutVertical;

    //Base de données
    private DatabaseManager databaseManager;

    private String url = "https://tomuss.univ-lyon1.fr/S/2020/Automne/rss/a06907256c4369f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        /******************* Initialisation des variables *******************/
        this.calendrier = findViewById(R.id.calendrier);
        this.notes = findViewById(R.id.notes);
        this.informations = findViewById(R.id.informations);
        this.drive = findViewById(R.id.drive);
        this.messagerie = findViewById(R.id.messagerie);

        this.layoutVertical = findViewById(R.id.dynamiqueLayoutNotes);

        databaseManager = new DatabaseManager(this);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE); //Gestionnaire connexion réseau
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo(); //Information du réseau

        /******************* Téléchargement du fichier rss *******************/

        if (networkInfo.isAvailable()) { //On vérifie qu'il y a une connexion à internet

            TelechargementNotes note = new TelechargementNotes();
            note.execute();
        }

        else {
            affichageNotes();//On affiche les notes de la bd sans les actualisé
        }

        /******************* Gestion des évènements du menu *******************/

        calendrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Calendrier.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité


            }
        });

        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Note.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        informations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Information.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Drive.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        messagerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Messagerie.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }
        });
    }

    public class TelechargementNotes extends AsyncTask<Void, Void, Void>{


        @Override
        protected void onPreExecute() {
            //On réinitialise les bd NOTES et MATIERES
            databaseManager.deleteAllMatieres();
            databaseManager.deleteAllNotes();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String codeRss = codeSource(); //On récupère le code rss

            decodage(codeRss);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //On affiche les notes
            affichageNotes();
            
        }
    }

    public String codeSource(){

        String codeRss = "";
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.connect();

            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());

            byte[] bytes = new byte[1024];
            int tmp ;
            while( (tmp = bis.read(bytes) ) != -1 ) {
                String chaine = new String(bytes,0,tmp);
                codeRss += chaine;
            }

            conn.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return codeRss;
    }

    public void decodage(String codeRss){

        String[] codeRssItem = codeRss.split("<item>"); //On sépare le texte par balise <item>

        for (int i=1; i < codeRssItem.length; i++){
            String [] codeRssTitle = codeRssItem[i].split("<title>"); //On sépare le texte par balise <title>

            String description = codeRssTitle[1].split(":")[1]; //On récupère la description de la note
            String note = codeRssTitle[1].split(":")[2].split("</title>")[0]; //On récupère la note

            String[] codeRssDescription = codeRssItem[i].split("<description>"); //On sépare le texte par balise <description>

            String matiere = codeRssDescription[1].split(",")[0]; //On récupère la matière de la note

            //On enlève la partie M12.. qui n'est pas interresente et on garde juste l'intitulé de la matière
            int nbCaractere = matiere.split(" ")[0].length();//On compte le nombre de caractère à enlever grace au split
            matiere = matiere.substring(nbCaractere+1);//On fait +1 pour enlever l'espace

            stockageNote(matiere,description,note);
        }
    }

    public void stockageNote(String matiere, String description, String note){

        //On récupère les matières de la BD
        String[] tabMatiere = databaseManager.getMatieres().split("/");

        //On parcours les matière pour vérifié son existence
        boolean trouver = false;
        for(String mat : tabMatiere){
            if (mat.equals(matiere))trouver=true;
        }

        if(!trouver)databaseManager.insertMatiere(matiere);//Si la matière n'existe pas dans la BD on l'ajoute

        //On ajoute la note dans la BD
        databaseManager.insertNote(note, description, matiere);
    }

    public void affichageNotes(){
        //Etape 1 : On récupère les matières puis on les stockes dans un tableau
        String[] tabMatiere = databaseManager.getMatieres().split("/");

        //Etape 2 : On parcours les matières
        for (String mat : tabMatiere){
            //Etape 3 : On affiche la matière

            TextView titreMatiere = new TextView(getApplicationContext());
            titreMatiere.setText(mat);
            titreMatiere.setTextSize(20);//Taille de la matiere
            titreMatiere.setPadding(10,10,10,10);
            //titreMatiere.setGravity(Gravity.CENTER);
            titreMatiere.setBackgroundColor(getResources().getColor(R.color.matiere));
            LinearLayout.LayoutParams paramsM = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsM.setMargins(0,50,0,20);
            titreMatiere.setLayoutParams(paramsM);
            layoutVertical.addView(titreMatiere);//On l'ajoute au layout


            //Etape 4 : On récupère toutes les notes de la matière
            String[] tabNote = databaseManager.getNotes(mat).split("---");

            //Etape 5 : On parcours les notes

            //On créer un layout horizontale pour pouvoir y ajouter les notes
            LinearLayout layoutHorizontale = new LinearLayout(getApplicationContext());
            layoutHorizontale.setOrientation(LinearLayout.HORIZONTAL);

            for(int i=0; i< tabNote.length;i++ ){

                if(i%3==0 && i!=0){ //On va créer un nouveau linearlayout horizontale toutes les 3 notes
                    //On ajoute le layout Horizontale au layout vertical
                    layoutVertical.addView(layoutHorizontale);

                    layoutHorizontale = new LinearLayout(getApplicationContext()); //On crée un nouveau layout
                    layoutHorizontale.setOrientation(LinearLayout.HORIZONTAL);
                }

                //Etape 6 : On affiche la note et sa description
                TextView textNote = new TextView(getApplicationContext());
                textNote.setText(tabNote[i]);
                textNote.setTextSize(15);//Taille de la matiere
                textNote.setBackgroundColor(getResources().getColor(R.color.note));
                textNote.setPadding(10,10,10,10);
                LinearLayout.LayoutParams paramsN = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsN.setMargins(10,10,10,10);
                textNote.setLayoutParams(paramsN);
                layoutHorizontale.addView(textNote);//On l'ajoute au layout

            }

            //On ajoute le layout Horizontale au layout vertical
            layoutVertical.addView(layoutHorizontale);

        }
    }



}