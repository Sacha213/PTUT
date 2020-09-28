package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private static String ACCOUNT = "sacha.montel@etu.univ-lyon1.fr";
    private static String PASSWORD = "31052001sM";

    private int numeroMail;

    private TextView contenu;
    private TextView sujet;
    private TextView expediteur;

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
            properties.setProperty("mail.store.protocol", "pop3");
            properties.setProperty("mail.pop3.host", HOST);
            properties.setProperty("mail.pop3.user", LOGIN);
            Session session = Session.getInstance(properties);

            // Les dossiers
            Store store = null;
            Folder defaultFolder = null;
            Folder inbox = null;
            try {
                store = session.getStore(new URLName("pop3://" + HOST));
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

    }

    private void printMessages(Folder folder) {
        try {
            folder.open(Folder.READ_ONLY);


            Message message = folder.getMessage(numeroMail);

            String cont = lectureContenuMail(message);//Récupération du contenu du mail
            String object = message.getSubject();//récupération du sujet
            //Résupération de l'adresse de l'expéditeur
            Address[] addresses = new Address[0];
            try {
                addresses = message.getFrom();
            } catch (MessagingException e) {
                e.printStackTrace();
            }


            affichageDesMail(object,addresses,cont,numeroMail); //gerer l'affichage des mails dans l'activité



        } catch (Exception e) {
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

    private void affichageDesMail(String object,Address[] addresses,String cont ,int numMail) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Ajout de l'expéditeur
                String[] nomDest = addresses[0].toString().split("<");//Transformation en chaîne de caractère et on va enlever les informations superflux (<adresse mail>)

                //On enlève les "" si le nom de l'expéditeur en contient
                String textExpediteur= nomDest[0];
                if (textExpediteur.substring(0, 1).equals("\"")){
                    textExpediteur=textExpediteur.substring(1,textExpediteur.length()-2);
                }

                expediteur.setText(textExpediteur);

                //Ajout du sujet
                sujet.setText(object);

                //Ajout de la description
                contenu.setText(cont);

            }
        });
    }

    /******************* Fonction qui transforme un inputstream en string *******************/
    private static String getStringFromInputStream(InputStream inputStream) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(inputStream));
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

    public String lectureContenuMail(Message message) {

        String contenu = null;
        try {
            DataSource dataSource = message.getDataHandler().getDataSource();
            MimeMultipart mimeMultipart = new MimeMultipart(dataSource);


            if (message.isMimeType("multipart/mixed")){
                int multiPartCount = mimeMultipart.getCount();

                for (int i = 0; i < multiPartCount; i++ ) {
                    BodyPart bp = mimeMultipart.getBodyPart(i);
                    contenu += processBodyPart(bp);
                }
            }
            else{
                if (message.isMimeType("text/plain")) {

                    System.out.println("plain");
                    contenu  = getStringFromInputStream(message.getInputStream());

                } else if (message.isMimeType("text/html")) {
                    System.out.println("html");
                    contenu = Jsoup.parse(getStringFromInputStream(message.getInputStream())).text();
                }
            }


        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }


        return contenu;
    }

    private String processBodyPart(BodyPart bp) throws UnsupportedEncodingException, MessagingException {
        String contenu = "rien";
        try {
            System.out.println("Type : " + bp.getContentType());

            if (bp.isMimeType("text/plain")) {
                contenu = getStringFromInputStream(bp.getInputStream());

            }

            else if(bp.isMimeType("text/html")) {
                contenu = Jsoup.parse(getStringFromInputStream(bp.getInputStream())).text();
            }

            else{
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

        return contenu;
    }


}
