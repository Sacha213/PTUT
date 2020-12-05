package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
    private Menu menu;

    private EditText titre;
    private RadioButton perte;
    private RadioButton info;
    private RadioButton enquete;
    private RadioButton autres;
    private EditText contenu;
    private Button publier;

    private FirebaseFirestore db; //Base de donnée Firestore
    private DatabaseManager databaseManager;//Base de données local

    private AlertDialog.Builder erreurAnnonceDialogue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nouvelle_annonce);

        /******************* Initialisation des variables *******************/
        this.menu = new Menu(this);

        this.titre = findViewById(R.id.titreNewAnnonce);
        this.perte = findViewById(R.id.checkBoxPerte);
        this.info = findViewById(R.id.checkBoxInfo);
        this.enquete = findViewById(R.id.checkBoxEnquete);
        this.autres = findViewById(R.id.checkBoxAutres);
        this.contenu = findViewById(R.id.ContenuAnnonce);
        this.publier = findViewById(R.id.boutonPublierAnnonce);

        erreurAnnonceDialogue = new AlertDialog.Builder(this);
        db = FirebaseFirestore.getInstance(); // Acces à la base de donnée cloud firestore
        databaseManager = new DatabaseManager(this);



        //On coche par default la checkbox autres
        autres.setChecked(true);



        /******************* Gestion publication annonce *******************/
        publier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //verification du contenu et du titre
                String titreAnnonce = titre.getText().toString();
                String contenuAnnonce = contenu.getText().toString();
                //on vérifie qu'il existe un titre et un contenu d'au moins 20 caracteres.
                if ( !titreAnnonce.equals("") && contenuAnnonce.length()>20 ){
                    //On récupère le choix de la checkbox
                    int choixCheckbox;
                    if (perte.isChecked())choixCheckbox=1;
                    else if (info.isChecked())choixCheckbox=2;
                    else if (enquete.isChecked())choixCheckbox=3;
                    else choixCheckbox = 4;


                    //Création data pour la BD Firebase
                    Map<String, Object> data = new HashMap<>();
                    data.put("Titre",titreAnnonce);
                    data.put("Auteur",databaseManager.getIdentifiant());
                    data.put("Date",new Date());
                    data.put("Contenu",contenuAnnonce);
                    data.put("Type",choixCheckbox);

                    db.collection("annonces").add(data);

                    /******************* Redirection page informations *******************/
                    Intent otherActivity = new Intent(getApplicationContext(), Information.class); //Ouverture d'une nouvelle activité
                    otherActivity.putExtra("AnnoncePubliee",true); //Envoie de donner dans la nouvelle activité (Annonce publiée)
                    startActivity(otherActivity);

                    finish();//Fermeture de l'ancienne activité
                    overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité




                }
                else{

                    /******************* Affichage de la boîte de dialogue de bienvenue *******************/
                    erreurAnnonceDialogue.setTitle("Oups..."); //Titre
                    erreurAnnonceDialogue.setMessage("Votre annonce n'a pas pu être publié, vous devez choisir un titre et écrire du contenu"); //Message
                    erreurAnnonceDialogue.setIcon(R.drawable.road_closure); //Ajout de l'icone erreur
                    erreurAnnonceDialogue.show(); //Affichage de la boîte de dialogue
                }
            }
        });


    }

    public void onCheckBoxClicked(View view){

    }
    /******************* Gestion du retour en arrière *******************/
    @Override
    public void onBackPressed() {

        /******************* Changement de page *******************/
        Intent otherActivity = new Intent(getApplicationContext(), Annonce.class); //Ouverture d'une nouvelle activité
        startActivity(otherActivity);


        finish();//Fermeture de l'ancienne activité
        overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

    }
}