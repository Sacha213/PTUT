package com.example.myapplication;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.regex.Pattern;

public class ChatRecherche extends AppCompatActivity {

    /******************* Attribut *******************/

    private static final String TAG = "Recherche";
    private FirebaseFirestore db; //Base de donnée Firestore


    private String prenom;
    private String nom;
    private String rslt;

    private Menu menu;
    private ImageView button;
    private EditText recherche;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_recherche);

        /******************* Initialisation des variables *******************/

        this.menu = new Menu(this);
        this.button = findViewById(R.id.boutonRecherche);
        this.recherche = findViewById(R.id.recherche);
        this.layout = findViewById(R.id.scrollRechercheChat);

        db = FirebaseFirestore.getInstance();

        System.out.println(getIntent().getStringExtra("mode"));

        /******************* Gestion de l'affichage des utilisateurs *******************/

        //Si le mode d'affichage est "mainlist" on affcihe tout les utilisateurs
        if(getIntent().getStringExtra("mode").equals("mainlist")) {
            db.collection("Token")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String pseudo = document.getId();

                                //On créer un layout horizontale pour pouvoir y ajouter les bulles
                                LinearLayout layoutHorizontale = new LinearLayout(getApplicationContext());
                                layoutHorizontale.setOrientation(LinearLayout.HORIZONTAL);

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
                                layoutMessage.setGravity(Gravity.CENTER_VERTICAL);



                                //Ajout du pseudo
                                TextView pseudoView = new TextView(getApplicationContext());
                                pseudoView.setText(pseudo);
                                pseudoView.setTypeface(null, Typeface.BOLD);//Gras
                                pseudoView.setTextSize(30);//Taille du titre
                                pseudoView.setTextColor(getResources().getColor(R.color.gris_fonce));
                                layoutMessage.addView(pseudoView);

                                layoutHorizontale.addView(layoutMessage);

                                /******************* Mise en place d'écouteur *******************/
                                layoutHorizontale.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        System.out.println("zeubiiii");

                                        /******************* Changement de page *******************/
                                        Intent intent = new Intent(getApplicationContext(), ChatLecture.class);
                                        intent.putExtra("users", pseudo);
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

                        }
                    });
        }

        //Si le mode d'affichage est "this" on affcihe les utilisateur recherchés
        if(getIntent().getStringExtra("mode").equals("this")) {
            String temp = getIntent().getStringExtra("recherche");

            db.collection("Token")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String pseudo = document.getId();
                                if (Pattern.matches(".*"+temp.toLowerCase()+".*", pseudo.toLowerCase()))
                                {
                                    //On créer un layout horizontale pour pouvoir y ajouter les bulles
                                    LinearLayout layoutHorizontale = new LinearLayout(getApplicationContext());
                                    layoutHorizontale.setOrientation(LinearLayout.HORIZONTAL);

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
                                    layoutMessage.setGravity(Gravity.CENTER_VERTICAL);



                                    //Ajout du pseudo
                                    TextView pseudoView = new TextView(getApplicationContext());
                                    pseudoView.setText(pseudo);
                                    pseudoView.setTypeface(null, Typeface.BOLD);//Gras
                                    pseudoView.setTextSize(30);//Taille du titre
                                    pseudoView.setTextColor(getResources().getColor(R.color.gris_fonce));
                                    layoutMessage.addView(pseudoView);

                                    layoutHorizontale.addView(layoutMessage);

                                    /******************* Mise en place d'écouteur *******************/
                                    layoutHorizontale.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            System.out.println("zeubiiii");

                                            /******************* Changement de page *******************/
                                            Intent intent = new Intent(getApplicationContext(), ChatLecture.class);
                                            intent.putExtra("users", pseudo);
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
                            }
                        }
                    });
        }










        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = recherche.getText().toString();
                System.out.println(temp);
                Intent intent = new Intent(getApplicationContext(), ChatRecherche.class);
                intent.putExtra("mode", "this");
                intent.putExtra("recherche",temp);//Ouverture d'une nouvelle activité
                startActivity(intent);
                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0, 0);
            }
        });

    }

    /******************* Gestion du retour en arrière *******************/
    @Override
    public void onBackPressed() {

        /******************* Changement de page *******************/
        Intent otherActivity = new Intent(getApplicationContext(), ChatReception.class); //Ouverture d'une nouvelle activité
        startActivity(otherActivity);


        finish();//Fermeture de l'ancienne activité
        overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

    }



}




