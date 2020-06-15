package com.example.myapplication;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;






public class accueil extends AppCompatActivity {

    /******************* Attribut *******************/
    private AlertDialog.Builder bienvenueDialogue; //Boite de dialogue pour un message de bienvenu
    private DatabaseManager databaseManager;//Base de données
    private ScrollView layout;// afficheur scroll


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        /******************* Initialisation des variables *******************/
        bienvenueDialogue = new AlertDialog.Builder(this); //Création de la boîte de dialogue
        databaseManager = new DatabaseManager(this);
        this.layout = findViewById(R.id.scrollActu); // liaison avec le layout


        /******************* Affichage de la boîte de dialogue de bienvenue *******************/
        bienvenueDialogue.setTitle("Bienvenue"); //Titre
        bienvenueDialogue.setMessage("Bravo, tu as réussi à te connecter "); //Message
        bienvenueDialogue.setIcon(R.drawable.valider); //Ajout de l'icone valider
        bienvenueDialogue.show(); //Affichage de la boîte de dialogue

        System.out.println(databaseManager.getIdentifiant());//Affichage des identifiants enregistré dans la base de données (provisoire)

        /******************* Test *******************/

        ImageView image = new ImageView(this);
        ViewGroup.LayoutParams params = new ActionBar.LayoutParams(100,100);
        image.setLayoutParams(params);
        image.setBackgroundResource(R.drawable.article);
        layout.addView(image);

        /******************* Affichage des articles *******************/

        AccesBD test = new AccesBD();
        test.execute();




        }

    private class AccesBD extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try{

                DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                Class.forName("oracle.jdbc.driver.OracleDriver");
              //  Class.forName("oracle.jdbc.driver.")

                System.out.println("co");

            } catch (ClassNotFoundException e) {

                System.out.println("Where is your Oracle JDBC Driver?");

            }


            Connection connexionBd;
            Statement stmt = null;
            try {
                connexionBd = DriverManager.getConnection("jdbc:oracle:thin:@iutdoua-oracle.univ-lyon1.fr:1521:orcl", "p1915095", "461541");

                stmt = connexionBd.createStatement();
                ResultSet resultat = stmt.executeQuery("select * from ANNONCES where IDANNONCE = 1 ");

                while(resultat.next()) {
                    System.out.println("ID : " + resultat.getInt(1) + " Nom : " + resultat.getString(2) + "Comtenu : " + resultat.getString(3));

                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }

            return null;
        }
    }

}
