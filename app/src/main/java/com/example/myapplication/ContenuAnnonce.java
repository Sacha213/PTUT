package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ContenuAnnonce extends AppCompatActivity {


    /******************* Attribut *******************/
    private Menu menu; //Menu
    private String identifiant;
    private TextView titre;
    private TextView contenu;
    private TextView auteurDate;
    private FirebaseFirestore db; //Base de donnée Firestore


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenuannonce);

        /******************* Initialisation des variables *******************/
        this.menu = new Menu(this);


        db = FirebaseFirestore.getInstance(); // Acces à la base de donnée cloud firestore

        Intent intent = getIntent();//On récupaire les données transmise venant de l'ancienne activité
        identifiant = intent.getStringExtra("Identifiant");
        this.titre = findViewById(R.id.textAnnonce);
        this.contenu = findViewById(R.id.contenuAnnonce);
        this.auteurDate = findViewById(R.id.textAuteurDate);
        getInfoAnnonce();
    }

    public void getInfoAnnonce(){
        db.collection("annonces")
                .document(identifiant)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                    titre.setText(document.getString("Titre"));
                    contenu.setText(document.getString("Contenu"));
                    String auteurString = document.getString("Auteur");
                    Date dateA = document.getTimestamp("Date").toDate();
                    SimpleDateFormat formateur = new SimpleDateFormat("dd/MM/yyyy");
                    String strDate= formateur.format(dateA);
                    auteurDate.setText(Html.fromHtml("Publié par <strong>"+auteurString+"</strong> le "+strDate));
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

