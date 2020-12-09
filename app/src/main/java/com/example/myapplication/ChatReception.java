package com.example.myapplication;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatReception extends AppCompatActivity {


    /******************* Attribut *******************/

    private static final String TAG = "MainList";
    private DatabaseManager databaseManager;
    private List<Affichage> listeSender = new ArrayList<>();


    private Menu menu; //Menu
    private ImageView write; //Bouton écrire

    private LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_reception);

        /******************* Initialisation des variables *******************/
        this.write = findViewById((R.id.ecrireChat));
        this.layout = findViewById(R.id.scrollReceptionChat);

        databaseManager = new DatabaseManager(this); //initialisation de la base de donnée local pour récupérer les messages
        menu = new Menu(this, databaseManager);


        listeSender = databaseManager.getSender();
        System.out.println(listeSender);




        /******************* Affichage des messages *******************/


        //On récupère la liste des pseudo et des dernier message
        List<Affichage> listeSender = databaseManager.getSender();
        //On la parcours
        for (Affichage affichage : listeSender){

            //On créer un layout horizontale pour pouvoir y ajouter les bulles
            LinearLayout layoutHorizontale = new LinearLayout(getApplicationContext());
            layoutHorizontale.setOrientation(LinearLayout.HORIZONTAL);
            //layoutHorizontale.setGravity(Gravity.CENTER_VERTICAL);

            //On affiche une rond devant les informations
            ImageView rond = new ImageView(getApplicationContext());
            rond.setImageResource(R.drawable.pastille_violette);
            //Parametre de l'image
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(200, 200); // Dimenssion de l'image
            lp.setMargins(0,0,20,0); //margin
            rond.setLayoutParams(lp);

            layoutHorizontale.addView(rond); //On ajoute le rond au layout horizontale


            //Création d'un layout pour mettre l'utilisateur et le dernier message reçu
            LinearLayout layoutMessage = new LinearLayout(getApplicationContext());
            layoutMessage.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 40);
            layoutMessage.setLayoutParams(params);
            layoutMessage.setPadding(10,10,10,10);

            //Ajout du pseudo
            TextView pseudo = new TextView(getApplicationContext());
            pseudo.setText(affichage.getPseudo());
            pseudo.setTypeface(null, Typeface.BOLD);//Gras
            pseudo.setTextSize(30);//Taille du titre
            pseudo.setTextColor(getResources().getColor(R.color.gris_fonce));
            layoutMessage.addView(pseudo);

            //Ajout du dernier message
            TextView message = new TextView(getApplicationContext());
            message.setText(affichage.getLastMessage());
            message.setTextSize(20);//Taille du titre
            message.setTextColor(getResources().getColor(R.color.gris));
            layoutMessage.addView(message);

            layoutHorizontale.addView(layoutMessage);


            /******************* Mise en place d'écouteur *******************/
            layoutHorizontale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("zeubiiii");

                    /******************* Changement de page *******************/
                    Intent intent = new Intent(getApplicationContext(), ChatLecture.class);
                    intent.putExtra("users", affichage.getPseudo());
                    startActivity(intent);//Ouverture d'une nouvelle activité

                    finish();//Fermeture de l'ancienne activité
                    overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité


                }
            });

            //On ajout le layout au scroll
            layout.addView(layoutHorizontale);

            //Ajout du diviseur
            View diviseur = new View(getApplicationContext());
            diviseur.setBackgroundColor(getResources().getColor(R.color.gris_claire));
            LinearLayout.LayoutParams paramsDiviseur = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            paramsDiviseur.setMargins(0, 20, 0, 20);
            diviseur.setLayoutParams(paramsDiviseur);
            layout.addView(diviseur);

        }




        /******************* Mise en place d'écouteur *******************/

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent intent = new Intent(getApplicationContext(), ChatRecherche.class);
                intent.putExtra("mode", "mainlist");
                startActivity(intent);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité


            }
        });



    }


    /******************* Gestion du retour en arrière *******************/
    @Override
    public void onBackPressed() {

        /******************* Changement de page *******************/
        Intent otherActivity = new Intent(getApplicationContext(), Messagerie.class); //Ouverture d'une nouvelle activité
        startActivity(otherActivity);

        //On ferme la database
        databaseManager.close();

        finish();//Fermeture de l'ancienne activité
        overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

    }

}
