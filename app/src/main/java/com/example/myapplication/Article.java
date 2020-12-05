package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class Article extends AppCompatActivity {

    /******************* Attribut *******************/
    private Menu menu; //Menu

    private String identifiant;
    private FirebaseFirestore db; //Base de donnée Firestore

    private TextView titre;
    private TextView contenu;
    private TextView auteurDate;
    private ImageView image;
    private LinearLayout layout;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        /******************* Initialisation des variables *******************/
        this.menu = new Menu(this);

        this.titre = findViewById(R.id.textTitre);
        this.contenu = findViewById(R.id.textContenu);
        this.auteurDate = findViewById(R.id.textAuteurDate);
        this.image = findViewById(R.id.imageArticle);
        this.layout = findViewById(R.id.dynamiqueLayoutArticle);



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

                                titre.setText(document.getString("Titre"));
                                contenu.setText(Html.fromHtml(document.getString("Contenu")));

                                String auteur = document.getString("Auteur");
                                Date date = document.getTimestamp("Date").toDate();
                                SimpleDateFormat formateur = new SimpleDateFormat("dd/MM/yyyy");
                                String strDate= formateur.format(date);
                                auteurDate.setText(Html.fromHtml("Publié par <strong>"+auteur+"</strong> le "+strDate));


                                afficherImage(document);
                    }
                });
    }

    public void afficherImage(DocumentSnapshot document) {

        final StorageReference monImage = storageReference.child("Images/" + document.getString("Image"));

        File localFile = null;

        try {
            //On récupère le nom de l'image et son extension
            String nomImage = document.getString("Image").split("\\.")[0];
            String extension = document.getString("Image").split("\\.")[1];
            localFile = File.createTempFile(nomImage,extension);
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
                                LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,layout.getWidth()); // Dimenssion de l'image
                                image.setLayoutParams(paramsImage);
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