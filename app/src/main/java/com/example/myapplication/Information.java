package com.example.myapplication;


import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class Information extends AppCompatActivity {

    /******************* Attribut *******************/
    private DatabaseManager databaseManager;//Base de données local
    private FirebaseFirestore db; //Base de donnée Firestore
    private LinearLayout layout;// afficheur scroll

    private ImageView calendrier; //Icônes du menu
    private ImageView notes;
    private ImageView informations;
    private ImageView drive;
    private ImageView messagerie;

    private Button annonce;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        /******************* Initialisation des variables *******************/
        databaseManager = new DatabaseManager(this);
        this.layout = findViewById(R.id.dynamiqueLayout); // liaison avec le scroll layout

        this.calendrier = findViewById(R.id.calendrier);
        this.notes = findViewById(R.id.notes);
        this.informations = findViewById(R.id.informations);
        this.drive = findViewById(R.id.drive);
        this.messagerie = findViewById(R.id.messagerie);

        db = FirebaseFirestore.getInstance(); // Acces à la base de donnée cloud firestore

        this.annonce = findViewById(R.id.boutonAnnonces);

        System.out.println(databaseManager.getIdentifiant());//Affichage des identifiants enregistré dans la base de données (provisoire)




        /******************* Acces aux articles *******************/

        getAllDocs();


        /******************* Mise en place d'écouteur *******************/
        annonce.setOnClickListener(new View.OnClickListener() { //Lors q'un clic sur le bouton connexion
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Annonce.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

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

    /******************* Fonction qui nous permet de générer les articles *******************/
    public void getAllDocs() {
        db.collection("articles")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                System.out.println(document.getId() + " => " + document.getData()); //A enlever

                                //Ajout de l'image de l'article
                                ImageView image = new ImageView(getApplicationContext());
                                ViewGroup.LayoutParams params = new ActionBar.LayoutParams(600,600); // Dimenssion de l'image
                                image.setLayoutParams(params);
                                image.setBackgroundResource(R.drawable.article_test);
                                image.setX(250);//Centrage de l'image
                                layout.addView(image);

                                //Ajout du titre de l'article
                                TextView titre = new TextView(getApplicationContext());
                                titre.setText(document.getString("Nom"));
                                titre.setGravity(Gravity.CENTER);//Centrage du titre
                                titre.setTextSize(25);//Taille du titre
                                layout.addView(titre);

                                //Ajout d'un espace pour séparer les articles
                                TextView espace = new TextView(getApplicationContext());
                                espace.setText("   ");
                                layout.addView(espace);

                                /******************* Mise en place d'écouteur *******************/
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        /******************* Changement de page *******************/
                                        Intent otherActivity = new Intent(getApplicationContext(), Article.class); //Ouverture d'une nouvelle activité
                                        otherActivity.putExtra("Identifiant",document.getId());
                                        startActivity(otherActivity);

                                        finish();//Fermeture de l'ancienne activité
                                        overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité
                                    }
                                });


                            }
                        } else {
                            System.out.println("Pas de document trouvé ");
                        }
                    }
                });
    }



}


