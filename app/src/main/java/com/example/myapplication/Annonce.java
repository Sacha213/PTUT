package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Annonce extends AppCompatActivity {

    /******************* Attribut *******************/
    private Button creerAnnonce;
    private Menu menu;

    private FirebaseFirestore db; //Base de donnée Firestore

    private LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce);



        /******************* Initialisation des variables *******************/
        this.menu = new Menu(this);

        this.creerAnnonce = findViewById(R.id.boutonCreerAnnonces);

        db = FirebaseFirestore.getInstance();

        this.layout = findViewById(R.id.dynamiqueLayoutAnnonce);

        /******************* Acces aux annonces *******************/
        getAllAnnonce();


        /******************* Gestion du bouton de creation d'annonce *******************/
        creerAnnonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), NouvelleAnnonce.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité
            }
        });

    }

    public void getAllAnnonce(){
        db.collection("annonces")
                .orderBy("Date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                String auteur = document.getString("Auteur");

                                String titre = document.getString("Titre");

                                String contenu = document.getString("Contenu");

                                Date date = document.getDate("Date");
                                SimpleDateFormat annee = new SimpleDateFormat(("yyyy"));
                                SimpleDateFormat mois = new SimpleDateFormat(("MM"));
                                SimpleDateFormat jour = new SimpleDateFormat(("dd"));
                                String s1 = annee.format(date);
                                String s2 = mois.format(date);
                                String s3 = jour.format(date);
                                String dateAnnonce = s3+"/"+s2+"/"+s1;

                                int type = document.getDouble("Type").intValue();

                                AfficheAnnonce(id,auteur, type, titre, contenu, dateAnnonce);


                            }
                        } else {
                            System.out.println("Pas de document trouvé ");
                        }
                    }
                });

    }

    public void AfficheAnnonce(String id,String auteur, int type, String titre, String contenu, String dateAnnonce){


        //ajout de cadre annonce
        View blockAnnonce = new View(getApplicationContext());
        LinearLayout Lannonce = new LinearLayout(getApplicationContext());
        Lannonce.setOrientation(LinearLayout.HORIZONTAL);
        Lannonce.setGravity(Gravity.CENTER_VERTICAL);
        Lannonce.setWeightSum(1);
        LinearLayout.LayoutParams paramslayoutCal = new LinearLayout.LayoutParams(layout.getWidth()-60, 150);
        paramslayoutCal.setMargins(30, 0, 30, 0);
        Lannonce.setLayoutParams(paramslayoutCal);
        Lannonce.setBackgroundColor(getResources().getColor(R.color.gris_claire));
        Lannonce.setDividerPadding(20);

        //Ajout de l'image de l'article
        ImageView image = new ImageView(getApplicationContext());
        ViewGroup.LayoutParams params = new ActionBar.LayoutParams(100,100); // Dimensions de l'image
        image.setLayoutParams(params);
        //image.setX(175);//Centrage de l'image a modifier
        if(type == 1){
            image.setImageResource(R.drawable.detective);
        }
        else if(type == 2){
            image.setImageResource(R.drawable.information);
        }
        else if (type == 3){
            image.setImageResource(R.drawable.micro);
        }
        else {
            image.setImageResource(R.drawable.stranger_things);
        }
        Lannonce.addView(image);

        //Ajout du titre de l'annonce
        TextView titreAnnonce = new TextView(getApplicationContext());
        titreAnnonce.setText(titre);
        titreAnnonce.setGravity(Gravity.CENTER);//Centrage du titre
        titreAnnonce.setTextSize(25);//Taille du titre
        LinearLayout.LayoutParams paramsTitre = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1f);
        titreAnnonce.setLayoutParams(paramsTitre);
        Lannonce.addView(titreAnnonce);

       // ajout du chevron
        ImageView chevron = new ImageView(getApplicationContext());
        chevron.setLayoutParams(params);
        chevron.setImageResource(R.drawable.fleche_droite);
        LinearLayout.LayoutParams paramsChevron = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1f);
        titreAnnonce.setLayoutParams(paramsChevron);
        Lannonce.addView(chevron);

        layout.addView(Lannonce);



        //Ajout d'un espace pour séparer les articles
        TextView espace = new TextView(getApplicationContext());
        espace.setText("");
        espace.setTextSize(5);
        layout.addView(espace);



        /******************* Mise en place d'écouteur *******************/
        Lannonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), ContenuAnnonce.class); //Ouverture d'une nouvelle activité
                otherActivity.putExtra("Identifiant",id); //Envoie de donner dans la nouvelle activité (information de l'annonce)
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité
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
        overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

    }
}