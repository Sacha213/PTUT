package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import com.google.firebase.firestore.FirebaseFirestore;




public class Connexion extends AppCompatActivity { //Classe pricipale : page de connexion des utilisateur pour vérifier que leurs identifiants Lyon1 sont correctes

    /******************* Attribut *******************/
    //Interface utilisateur
    private Button connexion;
    private EditText identifiant;
    private EditText motDePasse;
    FirebaseFirestore db;
    private FindToken findToken;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);





        /******************* Initialisation des variables *******************/
        this.connexion = findViewById(R.id.connexion);
        this.identifiant = findViewById(R.id.identifiant);
        this.motDePasse = findViewById(R.id.motdepasse);

        findToken = new FindToken();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();



        if (mAuth.getCurrentUser() != null) { //On vérifie si l'utilisateur est déja connecté

            findToken.FindToken(this);

            /******************* Changement de page *******************/
            Intent otherActivity = new Intent(getApplicationContext(), Information.class); //Ouverture d'une nouvelle activité
            startActivity(otherActivity);

            finish();//Fermeture de l'ancienne activité
            overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

        }


        /******************* Mise en place d'écouteur *******************/
        connexion.setOnClickListener(new View.OnClickListener() { //Lors q'un clic sur le bouton connexion
            @Override
            public void onClick(View v) {

                RequetteHttp requette = new RequetteHttp(); // On instanci l'objet requette de la classe RequetteHttp qui est dans une AsyncTask
                requette.execute(identifiant.getText().toString(),motDePasse.getText().toString()); //On lance la requette http avec comme paramètre l'identifiant et le mot de passe


            }
        });
    }

    public class RequetteHttp extends AsyncTask<String, Void, Void> {

        /******************* Attribut *******************/
        private AlertDialog.Builder erreur; //Boite de dialogue pour un message d'erreur
        private String result =""; //Conteuneur de la réponse à la requette http
        private String idUtilisateur; //identifiant de l'utilisateur
        private String passwordUtilisateur; //mot de passe de l'utilisateur

        /******************* Méthode appeler automatiquement avant l'exécution de la méthode principale (doInBackground) *******************/
        protected void onPreExecute() {

            /******************* Initialisation des variables *******************/
            erreur = new AlertDialog.Builder(Connexion.this); //création de la boîte de dialogue pour un usage future
        }

        /******************* Méthode principale *******************/
        @Override
        protected Void doInBackground(String... strings) {

            /******************* Initialisation des variables *******************/
            idUtilisateur = strings[0]; //L'identifiant et le mot de passe sont tout les deux dans le paramètre strings --> on effectue donc une séparation des paramètre
            passwordUtilisateur = strings[1];

            OutputStreamWriter writer = null;
            BufferedReader reader = null;
            try {
                //Encodage des paramètres de la requête
                String data="username="+idUtilisateur+"&password="+passwordUtilisateur+"&lt=&execution=1daceeec-018c-49a3-8168-ea7c136fd31a_ZXlKaGJHY2lPaUpJVXpVeE1pSjkuVG1nckwwSkRNQ3N2UlRoUWVWSXpOVGd2WTBocVRIVldhRVpJZUc1TGNrUmpRV2xFZVRGTlNrNURSVUZsUVU0ck9WRTRPV2g0TVUxcGRtWXdRM2sxUkhSSlRUSjNSSFJPZURSWlMwaEpWMHhTUkcxNlZFbFdNV0Z0THpkcFRWRnlhV2MyVERGRFUyUXpaMG92U2paVE1tcDZXSGd3WjJkQ2VHczFTM1pIVTNoRGMzTk5UVEJ3VlZNMVYwa3ZZMnhKYWpobE16VkZUak50WW5GNE1UQkdiSEJCT0c1NVMxYzNXRlpHVW04d2JtNHZSbXhrSzFOS1Uzb3ZWMWxqWW1aNlRWRm1XVkZGYkZweGMwNTZkRTFOU1hWMFlUVlRNVmt4VFdWMmFsSlNZV1U1YVZWTmQyZGFTSGh6VldoblJYRlpkMlpvY1hOa2FIbDZjV3RyVWprNFMzQmhPSGd6YW5Ca2VqWkhhQzlXU205U1dXbE5SRmhSUlRrelNFaDFOSGRrTVRFdldIZFJRamx0Y25SWlVXNVdaRXRtU0RaRlJTdFFRMVZ4VTNRclMwbE5jRXB2WTBWVlRUWktSbFlyWjFsa1drdEVkR0phUWtFMFdqRnBiRWQ0YURWMGEwNXZRV3hMZDBKVkswaDVWSGh1VldWTFEwSkdVRTF1U25sMmFsWXdOak5sUlROdVpTdHJRbUpHTW14WVZteDFibnBSV1d3eVVYWkNUV00wU1VVclptMUNjbWx1V1V0clpTdDNMM2xxZFhwRVdVaENSRFF3Vmk5SksxUTVlRkJETVhKNmIyVlpZbGh3TkRCRVZqTnRjVXN3UW5kV1JsVk9TMDlIT0V4YU1UbHpSMEpSWldkSVprbExSbGMyVFVaa2NGbFdVazFIVERsc2VqUkRlRkJHVm5OdU1XeFpNbTlqVEdFM1ptc3lOMWcyVFRoMGRXRmpSelpxV1dSWk5sQk5aMnAyUWtKalUzWlJVa2d5VlZaUWFGbzJSaTlOVDJObE5sRlVaR2szZFROR1prMXpibVJpYkVNNVlYTmxiazB3WVZwbFoxWlZURmwxWnk5RmJFTmhNRTlEUkRjMVNFY3pkM0V5VVdjNWJtVmpjR1pYV1hSQmEwcFdNV0ZoTDBWbk9HRkhOMlJRU0U5dFdVdDZWWEJhTTJaRFlVbDNWMjFyYjNKd04wNXlSbFZ4VFU4d2JGbFNaaXRMZUd0eVRXeFdhbXgxTDFKQlNtWkxSbVF3ZVhOV1RqQjJTR1EzUkRSeVpIbEtaRmQ1VDJZck5uUkhhUzkxYmtoNVF6SnNVMkp4ZHpaUVNrSTRkVUo2YkVVelF6WnlZemxWUzJkUWVURmFNVkE0VjNaWk9ISjVZMDFoZGtwYU1tY3dRbFJrUms1MWFrZEVabG8xWVhNdmRVRnZabEZJY1Voc2FVNTJjRXQyVVVWUWJYSllOV0p2UldwR1JuSjBlR1IyY0d4RE5URnVLMm94ZFVwcldDczVlbGRUUTNkMk5YUjZaV0p2T0V0bVRXVnVLMjVvWlhGcUszTTVSRklyTUhWMGVFcDNlaTg0TTFCM1QwUllNVEpIYUV0a01HY3lSRWRsYjAwdlpIVTVaVVJ2VUdSSU1uY3ZPVmRvTkVSdldXRm5USFpzYUZCQlQzTkRjVk5YUlZCb2RYcEtTVXhxWW13eFRWcEpjbG96WW10aVJ6RkhORU5UY1d0M1l6QkNlSGxXUkZJNGEzUmtkblJqZHowOS5BLXREWDYtS21scUVRVFI3R1hLLVBvdTF1WXRXbmtVWURxSUF5bDVmQUZCQ2VPM0xKWFAyY3dMcFYtSmpxS1VmLUR2OWtKUFpyeWZrN19rek81VkJpUQ%3D%3D&_eventId=submit&submit=SE+CONNECTER";


                //Création de la connection
                URL url = new URL("https://cas.univ-lyon1.fr/cas/login?service=https%3A%2F%2Fiut.univ-lyon1.fr%2Fservlet%2Fcom.jsbsoft.jtf.core.SG%3FPROC%3DIDENTIFICATION_FRONT");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                //Envoi de la requête
                writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data);
                writer.flush();

                //Lecture de la réponse
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne;
                while ((ligne = reader.readLine()) != null) {
                    result+=ligne; //La réponse est enregistrer dans l'atribut string result
                }

            }catch (Exception e) {
                e.printStackTrace();
            }finally{
                try {
                    writer.close(); // Fermeture du writer
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    reader.close(); //Fermeture du reader
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        /******************* Méthode appeler automatiquement après l'exécution de la méthode principale (doInBackground) *******************/
        protected void onPostExecute(Void voids) {

            /******************* Vérification de la réponse *******************/
            if(result.length()==4229){ //Si la réponse contient 4229 caractère alors l'utilisateur à rentré les bons identifiants Lyon1

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), Accueil.class); //Ouverture d'une nouvelle activité
                otherActivity.putExtra("Identifiant", idUtilisateur); //Envoie l'identifiant et le mot de passe dans l'activité accueil
                otherActivity.putExtra("MotDePasse", passwordUtilisateur);
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

            }

            else{
                /******************* Affichage de la boîte de dialogue d'erreur *******************/
                erreur.setTitle("Oups..."); //Titre
                erreur.setMessage("Il semblerait que votre identifiant et/ou votre mot de passe soit incorrect"); //Message
                erreur.setIcon(R.drawable.road_closure); //Ajout de l'émoji caca
                erreur.show(); //Affichage de la boîte de dialogue
            }
        }
    }


    /******************* Gestion du retour en arrière *******************/
    @Override
    public void onBackPressed() {

       super.onBackPressed();
    }

}



