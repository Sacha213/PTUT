package com.example.myapplication;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;


public class Information extends AppCompatActivity {

    /******************* Attribut *******************/
    private FirebaseFirestore db; //Base de donnée Firestore
    private StorageReference storageReference;

    private LinearLayout layout;// afficheur scroll
    private ProgressBar progressBar;
    private int tailleProgresseBar;
    private ImageView imageParametre;

    private Menu menu;

    private Button annonce;

    private FirebaseAuth mAuth;

    private boolean annoncePubliee;
    private AlertDialog.Builder confirmationPubliee;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        /******************* Initialisation des variables *******************/
        this.layout = findViewById(R.id.dynamiqueLayout); // liaison avec le scroll layout
        this.menu = new Menu(this);
        this.imageParametre = findViewById(R.id.imageParametre);

        db = FirebaseFirestore.getInstance(); // Acces à la base de donnée cloud firestore
        storageReference = FirebaseStorage.getInstance().getReference();

        this.annonce = findViewById(R.id.boutonAnnonces);
        this.progressBar = findViewById(R.id.progressBarActu);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();//On récupaire les données transmise venant de l'ancienne activité
        annoncePubliee = intent.getBooleanExtra("AnnoncePubliee",false);

        confirmationPubliee = new AlertDialog.Builder(this); //Création de la boîte de dialogue


        /******************* On affiche la boite de dialogue si l'utilisazteur à publiée une annonce *******************/
        if (annoncePubliee){
            confirmationPubliee.setTitle("Super..."); //Titre
            confirmationPubliee.setMessage("Votre annonce à bien été publiée");
            confirmationPubliee.setIcon(R.drawable.approval); //Ajout de l'icone valider
            confirmationPubliee.show(); //Affichage de la boîte de dialogue

        }

        /******************* Acces aux articles *******************/
        getAllDocs();


        /******************* Mise en place d'écouteur *******************/
        annonce.setOnClickListener(new View.OnClickListener() { //Lors q'un clic sur le bouton annonce
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Annonce.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }
        });

        imageParametre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Parametres.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }
        });




        }

    /******************* Fonction qui nous permet de générer les articles *******************/
    public void getAllDocs() {
        db.collection("articles")
                .orderBy("Date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            tailleProgresseBar = task.getResult().size();
                            progressBar.setMax(tailleProgresseBar);//On initialise la progresse bar avec un max de la progression

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                afficherImage(document);
                            }

                        }
                    }
                });
    }

    public void afficherImage(QueryDocumentSnapshot document){

        final StorageReference monImage = storageReference.child("Images/"+document.getString("Image"));

        File localFile = null;

        try {

            //On récupère le nom de l'image et son extension
            String nomImage = document.getString("Image").split("\\.")[0];
            String extension = document.getString("Image").split("\\.")[1];
            localFile = File.createTempFile(nomImage,extension);
        }
        catch (IOException e){
            System.out.println(e);
        }


        monImage.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        //On créer un layout pour ajouter l'image et le titre de l'annonce
                        LinearLayout layoutInformation = new LinearLayout(getApplicationContext());
                        layoutInformation.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 0, 0);
                        layoutInformation.setLayoutParams(params);
                        layoutInformation.setPadding(0,0,0,0);
                        //layoutInformation.setBackgroundResource(R.drawable.background_info);

                        monImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {


                                //Ajout de l'image de l'article
                                ImageView image = new ImageView(getApplicationContext());
                                LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,layout.getWidth()); // Dimenssion de l'image
                                image.setLayoutParams(paramsImage);
                                Picasso.get().load(uri).into(image);
                                layoutInformation.addView(image);

                                //Ajout du titre de l'article
                                TextView titre = new TextView(getApplicationContext());
                                titre.setText(document.getString("Titre"));
                                titre.setTypeface(null, Typeface.BOLD);//Gras
                                titre.setTextSize(20);//Taille du titre
                                titre.setTextColor(getResources().getColor(R.color.gris_fonce));
                                layoutInformation.addView(titre);

                                //Ajout du diviseur
                                View diviseur = new View(getApplicationContext());
                                diviseur.setBackgroundColor(getResources().getColor(R.color.beige_claire));
                                LinearLayout.LayoutParams paramsDiviseur = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                                paramsDiviseur.setMargins(0, 40, 0, 0);
                                diviseur.setLayoutParams(paramsDiviseur);
                                layoutInformation.addView(diviseur);

                                layout.addView(layoutInformation);




                                /******************* Mise en place d'écouteur *******************/
                                layoutInformation.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        /******************* Changement de page *******************/
                                        Intent otherActivity = new Intent(getApplicationContext(), Article.class); //Ouverture d'une nouvelle activité
                                        otherActivity.putExtra("Identifiant",document.getId()); //Envoie de donner dans la nouvelle activité (information de l'annonce)
                                        startActivity(otherActivity);

                                        finish();//Fermeture de l'ancienne activité
                                        overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité
                                    }
                                });
                            }



                        }
                        ).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                //On augmente le chargement de la bar de 1
                                progressBar.setProgress(progressBar.getProgress()+1);

                                //Supression de la progressbar
                                if(progressBar.getProgress()==tailleProgresseBar)progressBar.setVisibility(View.INVISIBLE);

                            }
                        });
                    }
                });


    }

    /******************* Gestion du retour en arrière *******************/
    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


}


