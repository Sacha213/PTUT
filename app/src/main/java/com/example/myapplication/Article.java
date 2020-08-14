package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Article extends AppCompatActivity {

    /******************* Attribut *******************/
    private ImageView calendrier; //Icônes du menu
    private ImageView notes;
    private ImageView informations;
    private ImageView drive;
    private ImageView messagerie;

    private String identifiant;
    private FirebaseFirestore db; //Base de donnée Firestore

    private TextView titre;
    private TextView contenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        /******************* Initialisation des variables *******************/
        this.calendrier = findViewById(R.id.calendrier);
        this.notes = findViewById(R.id.notes);
        this.informations = findViewById(R.id.informations);
        this.drive = findViewById(R.id.drive);
        this.messagerie = findViewById(R.id.messagerie);

        this.titre = findViewById(R.id.textTitre);
        this.contenu = findViewById(R.id.textContenu);

        db = FirebaseFirestore.getInstance(); // Acces à la base de donnée cloud firestore

        Intent intent = getIntent();
        identifiant = intent.getStringExtra("Identifiant");

        System.out.println(identifiant); // Provisoire

        /******************* Génération de l'article *******************/

        getArticle();


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

    /******************* Fonction qui nous permet de générer les articles *******************/
    public void getArticle() {
        db.collection("articles")
                .limit(Integer.valueOf(identifiant)+1)// Bricolage : On choisit le dernier article qui sera par conséquent afficher grâce à l'identifiant de l'article
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                titre.setText(document.getString("Nom"));
                                contenu.setText(document.getString("Contenu"));

                            }
                        } else {
                            System.out.println("Pas de document trouvé ");
                        }
                    }
                });
    }

}