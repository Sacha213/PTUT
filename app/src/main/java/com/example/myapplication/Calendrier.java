package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Calendrier extends AppCompatActivity {

    /******************* Attribut *******************/
    private ImageView calendrier; //Icônes du menu
    private ImageView notes;
    private ImageView informations;
    private ImageView drive;
    private ImageView messagerie;

    private Button deconnexion;
    private TextView datecourante;
    private LinearLayout layoutFront;
    private LinearLayout layoutBack;
    private ImageView flecheGauche;
    private ImageView flecheDroite;

    //Base de données
    private DatabaseManager databaseManager;

    private FirebaseAuth mAuth;



    private String url = "https://adelb.univ-lyon1.fr/jsp/custom/modules/plannings/anonymous_cal.jsp?resources=40699&projectId=2&calType=ical&firstDate=2020-11-23&lastDate=2020-11-28";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendrier);

        /******************* Initialisation des variables *******************/
        this.calendrier = findViewById(R.id.calendrier);
        this.notes = findViewById(R.id.notes);
        this.informations = findViewById(R.id.informations);
        this.drive = findViewById(R.id.drive);
        this.messagerie = findViewById(R.id.messagerie);

        this.deconnexion = findViewById(R.id.boutonDeconnexion);

        this.layoutFront = findViewById(R.id.layoutCalendrierFront);
        this.layoutBack = findViewById(R.id.layoutCalendrierBack);
        this.datecourante = findViewById(R.id.textjour);
        this.flecheDroite = findViewById(R.id.flechedroite);
        this.flecheGauche = findViewById(R.id.flechegauche);

        databaseManager = new DatabaseManager(this);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE); //Gestionnaire connexion réseau
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo(); //Information du réseau

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        if (networkInfo.isAvailable()) { //On vérifie qu'il y a une connexion à internet

            TelechargementCalendrier cal = new TelechargementCalendrier();
            cal.execute();
        } else {
            affichageCalendrier();//On affiche le calendrier de la bd sans les actualisé
        }

        /******************* Mise en place d'écouteur *******************/
        flecheGauche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //code pour changer date et appeller affichage
            }
        });

        /******************* Mise en place d'écouteur *******************/
        flecheDroite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //code pour changer date et appeller affichage
            }
        });


        /******************* Mise en place d'écouteur *******************/
        deconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /******************* Changement de page *******************/
                databaseManager.deleteAllUsers(); // Supression des données de la table USERS

                mAuth.signOut();//On se déconnecte

                finish();//Fermeture de l'ancienne activité
            }
        });

        /******************* Gestion des évènements du menu *******************/

        calendrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Calendrier.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0, 0);//Suprimmer l'animation lors du changement d'activité


            }
        });

        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Note.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0, 0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        informations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Information.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0, 0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Drive.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0, 0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        messagerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Messagerie.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0, 0);//Suprimmer l'animation lors du changement d'activité

            }
        });
    }

    /******************* Gestion du retour en arrière *******************/
    @Override
    public void onBackPressed() {

        /******************* Changement de page *******************/
        Intent otherActivity = new Intent(getApplicationContext(), Information.class); //Ouverture d'une nouvelle activité
        startActivity(otherActivity);


        finish();//Fermeture de l'ancienne activité
        overridePendingTransition(0, 0);//Suprimmer l'animation lors du changement d'activité

    }


    public class TelechargementCalendrier extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            //On réinitialise la table calendrier de la base de données
            databaseManager.deleteAllCours();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            String codeCal = codeSource(); //On récupère le code du fichier calendrier

            decodage(codeCal); //On va selectionner les informations dans le text qui nous interressent

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //On affiche les notes
            affichageCalendrier();

        }
    }

    public String codeSource() {

        String codeCal = "";
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.connect();

            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());

            byte[] bytes = new byte[1024];
            int tmp;
            while ((tmp = bis.read(bytes)) != -1) {
                String chaine = new String(bytes, 0, tmp);
                codeCal += chaine;
            }

            conn.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return codeCal;
    }

    public void decodage(String codeCal){
            //Parcer le code pour récupérer les informations importantes/utiles

            String[] event = codeCal.split("BEGIN:VEVENT");
            for(int i=1;i< event.length; i++){
                // on va parser chaque partie de notre event pour recuperer seulement les informations necessaires
                int debutCours = Integer.parseInt(event[i].split("DTSTART:")[1].substring(9,13));
                int finCours = Integer.parseInt(event[i].split("DTEND:")[1].substring(9,13));
                String nomCours = event[i].split("SUMMARY:")[1].split("LOCATION:")[0];
                nomCours = nomCours.substring(nomCours.split(" ")[0].length()+1);
                String salle = event[i].split("LOCATION:")[1].split("DESCRIPTION:")[0];
                String prof = event[i].split("DESCRIPTION:")[1].split(String.valueOf("n"))[3];
                String idCours = event[i].split("DTSTART:")[1].split("DTEND:")[0];
                String date = event[i].split("DTSTART:")[1].substring(0,8);

                System.out.println("Prof"+prof);
                System.out.println("salle"+salle);
                System.out.println("debutCours"+debutCours);
                System.out.println("fin"+finCours);
                System.out.println("nom"+nomCours);
                System.out.println("id"+idCours);
                System.out.println("date"+date);



                //on ajoute 100 equivalent a 1h a chauqe horaire car basé sur le fuseau horaire anglais
                debutCours +=100;
                finCours +=100;

                //on modifie l'affichage de la date en separant mois jour et année par des /
                String annee = date.substring(0,4);
                String mois = date.substring(4,6);
                String jour = date.substring(6,8);
                date = jour+"/"+mois+"/"+annee;

                stockageCalendrier(debutCours,finCours,nomCours,salle,prof,idCours,date);
            }


    }

    public void stockageCalendrier(int debutC,int finC, String nomC, String salleC, String profC,String idC, String dateC){

        //Faire les insertion dans la base de donnée

        databaseManager.insertCours(idC,debutC,finC,dateC,nomC,salleC,profC);
    }

    public void affichageCalendrier() {
        //Gérer l'affichage avec le linéare layout

        /******************* affichage date *******************/
        Date d = new Date();
        SimpleDateFormat annee = new SimpleDateFormat("yyyy");
        SimpleDateFormat mois = new SimpleDateFormat("MM");
        SimpleDateFormat jour = new SimpleDateFormat("dd");
        String s1 = annee.format(d);
        String s2 = mois.format(d);
        String s3 = jour.format(d);
        datecourante.setText(s3 + " " + s2 + " " + s1);


        String[] tabId = databaseManager.getCours("27/11/2020");


        //Parcourir toutes le minutes de la journée de 8h à 20h
        //Gérer l'affichage des traits, des cours et des heures (easy peasy)

        for (int i = 800; i <=2000; i++){
            LinearLayout lCalHori = new LinearLayout(getApplicationContext());
            lCalHori.setOrientation(LinearLayout.HORIZONTAL);

            //Etape 1 : On regarde si c une heure
            if(i%100==0){
                //On affiche l'heure tt les deux Heure
                if(i%200==0){
                    TextView heure = new TextView(getApplicationContext());
                    if(i<1000){
                        heure.setText("0"+String.valueOf(i).substring(0,1)+":00");
                    }else{
                        heure.setText(String.valueOf(i).substring(0,2)+":00");
                    }
                    heure.setTextSize(20);
                    lCalHori.addView(heure);
                }

                //Ajout du trait
                View trait = new View(getApplicationContext());
                trait.setBackgroundColor(Color.GRAY);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(lCalHori.getWidth(), 3);
                //params.setMargins(0, 20, 0, 20);
                trait.setLayoutParams(params);
                lCalHori.addView(trait);

                layoutBack.addView(lCalHori);

            }
            //Etape 2 : affichage des cours
            for(String id : tabId){
                if(databaseManager.getHDEB(id)==i){
                    int duree = databaseManager.getHFIN(id)-databaseManager.getHDEB(id);
                    TextView blockCours = new TextView(getApplicationContext());
                    blockCours.setText(databaseManager.getNomCours(id));
                    blockCours.setBackgroundColor(getResources().getColor(R.color.vert_claire));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(lCalHori.getWidth(), duree);
                    blockCours.setLayoutParams(params);
                    layoutFront.addView(blockCours);
                }
            }


        }

    }
}