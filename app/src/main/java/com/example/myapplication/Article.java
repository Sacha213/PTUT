package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class Article extends AppCompatActivity {

    /******************* Attribut *******************/
    private Menu menu; //Menu

    private String identifiant;
    private FirebaseFirestore db; //Base de donnée Firestore

    private TextView titre;
    private TextView contenu;
    private TextView auteur;
    private TextView date;
    private ImageView image;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        /******************* Initialisation des variables *******************/
        this.menu = new Menu(this);

        this.titre = findViewById(R.id.textTitre);
        this.contenu = findViewById(R.id.textContenu);
        this.auteur = findViewById(R.id.textAuteur);
        this.date = findViewById(R.id.textDate);
        this.image = findViewById(R.id.imageArticle);



        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance(); // Acces à la base de donnée cloud firestore

        Intent intent = getIntent();//On récupaire les données transmise venant de l'ancienne activité
        identifiant = intent.getStringExtra("Identifiant");

        System.out.println(identifiant); // Provisoire

        /******************* Génération de l'article *******************/

        getArticle();


    }

    /******************* Fonction qui nous permet de générer les articles *******************/
    public void getArticle() {
        db.collection("articles")
                .document(identifiant)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {

                                titre.setText(document.getString("Nom"));
                                contenu.setText(document.getString("Contenu"));
                                auteur.setText(document.getString("Auteur"));
                               // date.setText(document.getTimestamp("Date").toString());

                                afficherImage(document);
                    }
                });
    }

    public void afficherImage(DocumentSnapshot document) {

        final StorageReference monImage = storageReference.child("Images/" + document.getString("Image") + ".jpg");

        File localFile = null;

        try {
            localFile = File.createTempFile(document.getString("Image"), "jpg");
        } catch (IOException e) {
            System.out.println(e);
        }

        monImage.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        monImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                //Ajout de l'image de l'article
                                Picasso.get().load(uri).into(image);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
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