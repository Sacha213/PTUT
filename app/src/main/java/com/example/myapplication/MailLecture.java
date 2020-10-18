package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeMultipart;


public class MailLecture extends AppCompatActivity {

    /******************* Attribut *******************/
    private ImageView calendrier; //Icônes du menu
    private ImageView notes;
    private ImageView informations;
    private ImageView drive;
    private ImageView messagerie;

    private static String HOST = "accesbv.univ-lyon1.fr";
    private static String LOGIN = "p1908066";
    private static String PASSWORD = "31052001sM";

    private int numeroMail;

    private WebView contenu;
    private TextView sujet;
    private TextView expediteur;

    private ProgressBar progressBar;
    private TextView textChargement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_lecture);

        /******************* Initialisation des variables *******************/
        this.calendrier = findViewById(R.id.calendrier);
        this.notes = findViewById(R.id.notes);
        this.informations = findViewById(R.id.informations);
        this.drive = findViewById(R.id.drive);
        this.messagerie = findViewById(R.id.messagerie);

        Intent intent = getIntent();//On récupaire les données transmise venant de l'ancienne activité
        numeroMail = intent.getIntExtra("Numéro",1);

        this.contenu = findViewById(R.id.scrollLectureMail);
        this.sujet = findViewById(R.id.textSujet);
        this.expediteur = findViewById(R.id.textExpediteur);

        this.progressBar = findViewById(R.id.barChargement);
        this.textChargement = findViewById(R.id.textChargement);

        /******************* Reception des mails *******************/

        LectureMail mails = new LectureMail(); // On instanci l'objet mails de la classe ReceptionMail qui est dans une AsyncTask
        mails.execute();



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

    public class LectureMail extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... voids) {

            // Création de la session
            Properties properties = new Properties();
            properties.setProperty("mail.store.protocol", "pop3s");
            properties.setProperty("mail.pop3s.host", HOST);
            properties.setProperty("mail.pop3s.user", LOGIN);
            Session session = Session.getInstance(properties);

            // Les dossiers
            Store store = null;
            Folder defaultFolder = null;
            Folder inbox = null;
            try {
                store = session.getStore(new URLName("pop3s://" + HOST));
                store.connect(LOGIN, PASSWORD);
                defaultFolder = store.getDefaultFolder();

                inbox = defaultFolder.getFolder("INBOX");

                printMessages(inbox);

            } catch (Exception e) {
                e.printStackTrace();
            } finally { // Ne pas oublier de fermer tout ça !
                close(inbox);
                close(defaultFolder);
                try {
                    if (store != null && store.isConnected()) {
                        store.close();
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //On cache l'affichage du chargement
            progressBar.setVisibility(View.INVISIBLE);
            textChargement.setVisibility(View.INVISIBLE);
        }
    }

    private void printMessages(Folder folder) {
        try {
            folder.open(Folder.READ_ONLY);


            Message message = folder.getMessage(numeroMail);

            String contenu = lectureContenuMail(message);//Récupération du contenu du mail

            String sujet = message.getSubject();//récupération du sujet

            Address addresses = message.getFrom()[0];//Résupération de l'adresse de l'expéditeur
            String[] nomDest = addresses.toString().split("<");//Transformation en chaîne de caractère et on va enlever les informations superflux (<adresse mail>)

            //On enlève les "" si le nom de l'expéditeur en contient
            String textExpediteur = nomDest[0];
            if (textExpediteur.substring(0, 1).equals("\"")) {
                textExpediteur = textExpediteur.substring(1, textExpediteur.length() - 2);
            }



            affichageDuMail(textExpediteur,sujet,contenu);


        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    private static void close(Folder folder) {
        if (folder != null && folder.isOpen()) {
            try {
                folder.close(false); // false -> On n'efface pas les messages marqués DELETED ???
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void affichageDuMail(String textExpediteur, String object, String textContenu) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Ajout de l'expéditeur
                expediteur.setText(textExpediteur);

                //Ajout du sujet
                sujet.setText(object);

                //Ajout du contenu
                contenu.loadDataWithBaseURL(null, textContenu, "text/html", "utf-8", null);

            }
        });


    }

    public String lectureContenuMail(Message message) {

        String cont = "";
        try {
            DataSource dataSource = message.getDataHandler().getDataSource();
            MimeMultipart mimeMultipart = new MimeMultipart(dataSource);


            //Une seul partie
            if(message.isMimeType("text/html") || message.isMimeType("text/plain")){
                cont  = getStringFromInputStream(message.getInputStream(),getCharset(message.getContentType()));
            }


            //Le mail a plusieurs parties
            else if (message.isMimeType("multipart/mixed") || message.isMimeType("multipart/related") || message.isMimeType("multipart/alternative")){ //Voir autre type multipat
                int multiPartCount = mimeMultipart.getCount();

                for (int i = 0; i < multiPartCount; i++ ) {
                    BodyPart bp = mimeMultipart.getBodyPart(i);
                    cont += processBodyPartContenu(bp);
                }
            }



        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }


        return cont;
    }

    private String processBodyPartContenu(BodyPart bp) throws UnsupportedEncodingException, MessagingException {
        String cont = "";
        try {

            //On affiche que le text html
            if (bp.isMimeType("text/html")) {
                cont = getStringFromInputStream(bp.getInputStream(),getCharset(bp.getContentType()));
            }

            //Si la partie contient plusiseurs partie
            else if (bp.isMimeType("multipart/mixed") || bp.isMimeType("multipart/related") || bp.isMimeType("multipart/alternative")){ //Voir autre type multipat
                DataSource dataSource = bp.getDataHandler().getDataSource();
                MimeMultipart mimeMultipart = new MimeMultipart(dataSource);
                int multiPartCount = mimeMultipart.getCount();

                for (int i = 0; i < multiPartCount; i++ ) {
                    System.out.println("Sa mère");
                    BodyPart bp2 = mimeMultipart.getBodyPart(i);
                    cont += processBodyPartContenu(bp2);
                }
            }

            else if (bp.isMimeType("image/jpeg") || bp.isMimeType("image/png")){

                /*
                DataHandler dh = bp.getDataHandler();
                File file = new File(PATH_TO_DATA + "/received_" + fileName);
                FileOutputStream fos = new FileOutputStream(file);
                dh.writeTo(fos);
                */
                System.out.println("piece jointe");
            }

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

        return cont;
    }


    /******************* Récupération du charset *******************/
    public String getCharset(String contentType){
        String charset = contentType.split("charset=")[1];//On récupère la partie après le charset
        charset = charset.substring(1,charset.length()-1);//On suprime les "" qui se trouvent au extrémité du texte

        return charset;
    }

    /******************* Fonction qui transforme un inputstream en string *******************/
    private static String getStringFromInputStream(InputStream inputStream, String encodage) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(inputStream,encodage));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    /******************* Gestion du retour en arrière *******************/
    @Override
    public void onBackPressed() {

        /******************* Changement de page *******************/
        Intent otherActivity = new Intent(getApplicationContext(), MailReception.class); //Ouverture d'une nouvelle activité
        startActivity(otherActivity);


        finish();//Fermeture de l'ancienne activité
        overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

    }


}
