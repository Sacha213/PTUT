package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note extends AppCompatActivity {

    /******************* Attribut *******************/
    private Menu menu;

    private LinearLayout layoutVertical;
    private ProgressBar progressBar;
    private TextView textChargement;

    //Base de données
    private DatabaseManager databaseManager;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        /******************* Initialisation des variables *******************/

        this.layoutVertical = findViewById(R.id.dynamiqueLayoutNotes);

        databaseManager = new DatabaseManager(this);
        this.menu = new Menu(this, databaseManager);

        this.progressBar = findViewById(R.id.barChargement);
        this.textChargement = findViewById(R.id.textChargement);


        url = databaseManager.getLienTomuss();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE); //Gestionnaire connexion réseau
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo(); //Information du réseau

        /******************* Téléchargement du fichier rss *******************/

        if (networkInfo != null) { //On vérifie qu'il y a une connexion à internet

            TelechargementNotes note = new TelechargementNotes();
            note.execute();
        }

        else {
            //On cache l'affichage du chargement
            progressBar.setVisibility(View.INVISIBLE);
            textChargement.setVisibility(View.INVISIBLE);

            //On préviens l'utilisateur qu'on n'a pas pu actualisé la bd
            AlertDialog.Builder erreurInternet = new AlertDialog.Builder(this);
            erreurInternet.setTitle("Oups..."); //Titre
            erreurInternet.setMessage("Il semblerait que vous n'êtes pas connecté à internet."); //Message
            erreurInternet.setIcon(R.drawable.wifi); //Ajout de l'image
            erreurInternet.show(); //Affichage de la boîte de dialogue

            affichageNotes();//On affiche les notes de la bd sans les actualisé
        }

    }

    /******************* Gestion du retour en arrière *******************/
    @Override
    public void onBackPressed() {

        /******************* Changement de page *******************/
        Intent otherActivity = new Intent(getApplicationContext(), Information.class); //Ouverture d'une nouvelle activité
        startActivity(otherActivity);

        //On ferme la database
        databaseManager.close();


        finish();//Fermeture de l'ancienne activité
        overridePendingTransition(0, 0);//Suprimmer l'animation lors du changement d'activité

    }

    public class TelechargementNotes extends AsyncTask<Void, Void, Void>{

        private AlertDialog.Builder dialogProblem;
        private boolean erreur = false;


        @Override
        protected void onPreExecute() {

            dialogProblem = new AlertDialog.Builder(Note.this);

        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                String codeRss = codeSource(); //On récupère le code rss
                decodage(codeRss);
            }
            catch (Exception e){ //S'il y a une erreur on précise à l'utilisateur qu'il y a un pb avec le lien renseigné
                erreur = true;
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //On affiche les notes
            affichageNotes();

            //On cache l'affichage du chargement
            progressBar.setVisibility(View.INVISIBLE);
            textChargement.setVisibility(View.INVISIBLE);

            if(erreur){
                dialogProblem.setTitle("Oups..."); //Titre
                dialogProblem.setMessage("Un problème est survenu.\nVeuillez vérifier le lien du flux RSS Tomuss."); //Message
                dialogProblem.setIcon(R.drawable.road_closure); //Ajout de l'icone valider
                dialogProblem.show(); //Affichage de la boîte de dialogue
            }

        }

        public String codeSource(){

            String codeRss = "";
            HttpURLConnection conn = null;

            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.connect();

                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());

                byte[] bytes = new byte[1024];
                int tmp ;
                while( (tmp = bis.read(bytes) ) != -1 ) {
                    String chaine = new String(bytes,0,tmp);
                    codeRss += chaine;
                }



            } catch (IOException e) {
                e.printStackTrace();
                erreur = true;
            }
            finally {
                if (conn != null) {
                    conn.disconnect();//On ferme la connection
                }
            }

            return codeRss;
        }

        public void decodage(String codeRss){

            String[] codeRssItem = codeRss.split("<item>"); //On sépare le texte par balise <item>

            for (int i=1; i < codeRssItem.length; i++){
                String [] codeRssTitle = codeRssItem[i].split("<title>"); //On sépare le texte par balise <title>

                String description = codeRssTitle[1].split(":")[1]; //On récupère la description de la note
                String note = codeRssTitle[1].split(":")[2].split("</title>")[0]; //On récupère la note

                String[] codeRssDescription = codeRssItem[i].split("<description>"); //On sépare le texte par balise <description>

                String matiere = codeRssDescription[1].split(",")[0]; //On récupère la matière de la note

                //On enlève la partie M12.. qui n'est pas interresente et on garde juste l'intitulé de la matière
                int nbCaractere = matiere.split(" ")[0].length();//On compte le nombre de caractère à enlever grace au split
                matiere = matiere.substring(nbCaractere+1);//On fait +1 pour enlever l'espace

                //On va récuperer la date de publication de la note
                String dateNote = codeRssItem[i].split("<pubDate>")[1]; //On récupère la partie après le <pubDate>
                dateNote = dateNote.split("</pubDate>")[0];//On récupère la partie avant </pubDate>
                Date date = new Date(dateNote); //On transforme la date dans le format Date
                SimpleDateFormat formateur = new SimpleDateFormat("dd/MM/yyyy");//On créer un foprmateur pour la date
                String strDateNote = formateur.format(date);//On formate la date de publication

                stockageNote(matiere,description,note,strDateNote);
            }
        }

        public void stockageNote(String matiere, String description, String note, String dateNote){

            //On récupère les matières de la BD
            String[] tabMatiere = databaseManager.getMatieres().split("/");

            //On parcours les matière pour vérifié son existence
            boolean trouverMat = false;
            for(String mat : tabMatiere){
                if (mat.equals(matiere)) {
                    trouverMat = true;
                    break;
                }
            }

            if(!trouverMat)databaseManager.insertMatiere(matiere);//Si la matière n'existe pas dans la BD on l'ajoute

            //On récupère les notes de la BD
            String[] tabNote = databaseManager.getNotes(matiere).split("---");

            //On parcours les notes pour vérifié son existence
            boolean trouverNote = false;
            for(String not : tabNote){
                if (not.equals(note+" --"+description+dateNote)) {
                    trouverNote = true;
                    break;
                }
            }




            //On ajoute la note dans la BD si elle n'existe pas dans la base
            if(!trouverNote)databaseManager.insertNote(note, description, matiere, dateNote);
        }
    }





    public void affichageNotes(){
        //Etape 1 : On récupère les matières puis on les stockes dans un tableau

        String[] tabMatiere;
        if (databaseManager.getMatieres().length() > 0){ //Si il y a plus de 0 matière alors on remplie le tableau tabMatiere de celle ci
            tabMatiere = databaseManager.getMatieres().split("/");
        }
        else{//Sinon on initialise le tableau avec une taille de 0
            tabMatiere = new String[0];
        }

        //Etape 2 : On parcours les matières
        for (String mat : tabMatiere){
            //Etape 3 : On affiche la matière

            TextView titreMatiere = new TextView(getApplicationContext());
            titreMatiere.setText(mat);
            titreMatiere.setTextSize(20);//Taille de la matiere
            titreMatiere.setTextColor(getResources().getColor(R.color.white));
            titreMatiere.setTypeface(null, Typeface.BOLD);
            titreMatiere.setPadding(5,5,5,5);
            titreMatiere.setPadding(10,10,10,10);
            //titreMatiere.setGravity(Gravity.CENTER);
            titreMatiere.setBackgroundColor(getResources().getColor(R.color.rougePale));
            LinearLayout.LayoutParams paramsM = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsM.setMargins(0,50,0,20);
            titreMatiere.setLayoutParams(paramsM);
            layoutVertical.addView(titreMatiere);//On l'ajoute au layout


            //Etape 4 : On récupère toutes les notes de la matière
            String[] tabNote;
            if (databaseManager.getNotes(mat).length() > 0){ //Si il y a plus de 0 note alors on remplie le tableau tabNote de celle ci
                tabNote = databaseManager.getNotes(mat).split("---");
            }
            else{//Sinon on initialise le tableau avec une taille de 0
                tabNote = new String[0];
            }

            //Etape 5 : On parcours les notes

            //On créer un layout horizontale pour pouvoir y ajouter les notes
            LinearLayout layoutHorizontale = new LinearLayout(getApplicationContext());
            layoutHorizontale.setOrientation(LinearLayout.HORIZONTAL);
            layoutHorizontale.setWeightSum(4);

            for(int i=0; i< tabNote.length;i++ ){

                if(i%3==0 && i!=0){ //On va créer un nouveau linearlayout horizontale toutes les 3 notes
                    //On ajoute le layout Horizontale au layout vertical
                    layoutVertical.addView(layoutHorizontale);

                    layoutHorizontale = new LinearLayout(getApplicationContext()); //On crée un nouveau layout
                    layoutHorizontale.setOrientation(LinearLayout.HORIZONTAL);
                    layoutHorizontale.setWeightSum(4);
                }

                //Etape 6 : On affiche la note et sa description

                String textNot = tabNote[i].substring(0,tabNote[i].length()-10);//On enlève la partie de la date qui fait 10 caractère
                String note = textNot.split("--")[0];//On sépare la note de sa description
                String description = textNot.split("--")[1];
                String textND = "<strong>"+note+"</strong>"+"<br/>"+description;

                String datePub = tabNote[i].substring(tabNote[i].length()-10);//On garde la partie de la date

                Date dateToday = new Date();//ON récupère la date d'aujourd'hui
                SimpleDateFormat formateur = new SimpleDateFormat("dd/MM/yyyy");//On créer un foprmateur pour la date
                String strDateToday = formateur.format(dateToday);//Pareil pour la date d'aujourd'hui

                TextView textNote = new TextView(getApplicationContext());
                textNote.setText(Html.fromHtml(textND));
                textNote.setTextSize(15);//Taille de la matiere


                if(datePub.equals(strDateToday)){//Si la date de publication est celle d'aujourd'hui on met une couleur d'affiche différente
                    textNote.setBackgroundResource(R.drawable.background_note_nouvelle);
                    textNote.setTextColor(getResources().getColor(R.color.white));
                }
                else{ //Sinon on affiche la couleur de base
                    textNote.setBackgroundResource(R.drawable.background_note_ancienne);
                }

                textNote.setPadding(10,10,10,10);
                LinearLayout.LayoutParams paramsN = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1f);
                paramsN.setMargins(10,10,10,10);
                textNote.setLayoutParams(paramsN);
                textNote.setLines(3);
                textNote.setEllipsize(TextUtils.TruncateAt.END);//ajout des ...
                layoutHorizontale.addView(textNote);//On l'ajoute au layout

            }

            //On ajoute le layout Horizontale au layout vertical
            layoutVertical.addView(layoutHorizontale);

        }
    }



}