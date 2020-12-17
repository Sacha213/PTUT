package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Parametres extends AppCompatActivity {

    /******************* Attribut *******************/

    private DatabaseManager databaseManager;
    private Menu menu; //Menu

    private EditText textMail;
    private EditText textTomuss;
    private EditText textCalendrier;
    private Button buttonValider;
    private Button buttonDeconnexion;
    private Button buttonSupprimerCompte;

    private FirebaseAuth mAuth;

    private AlertDialog.Builder dialogConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametres);

        /******************* Initialisation des variables *******************/

        databaseManager = new DatabaseManager(this);
        this.menu = new Menu(this,databaseManager);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        dialogConfirmation = new AlertDialog.Builder(this);

        this.textMail = findViewById(R.id.adresseMail);
        this.textTomuss = findViewById(R.id.lienTomuss);
        this.textCalendrier = findViewById(R.id.lienCalendrier);
        this.buttonValider = findViewById(R.id.boutonParametres);
        this.buttonDeconnexion = findViewById(R.id.boutonDeconnexion);
        this.buttonSupprimerCompte = findViewById(R.id.boutonSupprimerCompte);

        //On affiche les informations
        textMail.setText(databaseManager.getMail());
        textTomuss.setText(databaseManager.getLienTomuss());
        textCalendrier.setText(databaseManager.getLienCalendrier());

        /******************* Mise en place d'écouteur *******************/

        //Si l'utilisateur clique sur le bouton valider alors on va enregistrer les nouvelles informations
        buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textCal = textCalendrier.getText().toString();
                if (!textCal.contains("https")){
                    textCal = textCal.replace("http","https");
                }

                databaseManager.setInformationsUser(textMail.getText().toString(),textTomuss.getText().toString(),textCal);

                //On affiche une fenetre de dialogue pour précisé à l'utilisateur que ses informations ont bien été modifiées
                dialogConfirmation.setTitle("Super..."); //Titre
                dialogConfirmation.setMessage("Vos données on bien été modifiées"); //Message
                dialogConfirmation.setIcon(R.drawable.approval); //Ajout de l'icone valider
                dialogConfirmation.show(); //Affichage de la boîte de dialogue


            }
        });

        buttonDeconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /******************* Changement de page *******************/
                databaseManager.deleteAllUsers(); // Supression des données de la table USERS

                mAuth.signOut();//On se déconnecte

                finish();//Fermeture de l'ancienne activité
            }
        });

        buttonSupprimerCompte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseManager.deleteAllUsers(); // Supression des données de la table USERS

                //On supprime l'utilisateur
                FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        /******************* Changement de page *******************/
                        Intent otherActivity = new Intent(getApplicationContext(), Connexion.class); //Ouverture d'une nouvelle activité
                        startActivity(otherActivity);

                        //On ferme la database
                        databaseManager.close();

                        finish();//Fermeture de l'ancienne activité
                        overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité
                    }
                });



            }
        });




    }

    /******************* Gestion du retour en arrière *******************/
    @Override
    public void onBackPressed() {

        /******************* Changement de page *******************/
        Intent otherActivity = new Intent(getApplicationContext(), Annonce.class); //Ouverture d'une nouvelle activité
        startActivity(otherActivity);

        //On ferme la database
        databaseManager.close();

        finish();//Fermeture de l'ancienne activité
        overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

    }
}