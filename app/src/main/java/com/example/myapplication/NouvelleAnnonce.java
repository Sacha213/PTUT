package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NouvelleAnnonce extends AppCompatActivity {

    /******************* Attribut *******************/
    private ImageView calendrier; //Icônes du menu
    private ImageView notes;
    private ImageView informations;
    private ImageView drive;
    private ImageView messagerie;

    private EditText titre;
    private RadioButton perte;
    private RadioButton info;
    private RadioButton enquete;
    private RadioButton autres;
    private EditText contenu;
    private Button publier;

    private FirebaseFirestore db; //Base de donnée Firestore
    private DatabaseManager databaseManager;//Base de données local


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nouvelle_annonce);

        /******************* Initialisation des variables *******************/
        this.calendrier = findViewById(R.id.calendrier);
        this.notes = findViewById(R.id.notes);
        this.informations = findViewById(R.id.informations);
        this.drive = findViewById(R.id.drive);
        this.messagerie = findViewById(R.id.messagerie);

        this.titre = findViewById(R.id.titreNewAnnonce);
        this.perte = findViewById(R.id.checkBoxPerte);
        this.info = findViewById(R.id.checkBoxInfo);
        this.enquete = findViewById(R.id.checkBoxEnquete);
        this.autres = findViewById(R.id.checkBoxAutres);
        this.contenu = findViewById(R.id.ContenuAnnonce);
        this.publier = findViewById(R.id.boutonPublierAnnonce);

        db = FirebaseFirestore.getInstance(); // Acces à la base de donnée cloud firestore
        databaseManager = new DatabaseManager(this);



        //On coche par default la checkbox autres
        autres.setChecked(true);



        /******************* Gestion publication annonce *******************/
        publier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //On récupère le choix de la checkbox
                int choixCheckbox;
                if (perte.isChecked())choixCheckbox=1;
                else if (info.isChecked())choixCheckbox=2;
                else if (enquete.isChecked())choixCheckbox=3;
                else choixCheckbox = 4;


                //Création data pour la BD Firebase
                Map<String, Object> data = new HashMap<>();
                data.put("Titre",titre.getText().toString());
                data.put("Auteur",databaseManager.getIdentifiant());
                data.put("Date",new Date());
                data.put("Contenu",contenu.getText().toString());
                data.put("Type",choixCheckbox);

                db.collection("annonces").add(data);
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

    public void onCheckBoxClicked(View view){

    }
}