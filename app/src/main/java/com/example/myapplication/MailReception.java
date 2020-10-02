package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.common.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailReception extends AppCompatActivity {

    /******************* Attribut *******************/
    private ImageView calendrier; //Icônes du menu
    private ImageView notes;
    private ImageView informations;
    private ImageView drive;
    private ImageView messagerie;

    private ImageView ecrire;

    private LinearLayout layout;

    private static String HOST = "accesbv.univ-lyon1.fr";
    private static String LOGIN = "p1908066";
    private static String ACCOUNT = "sacha.montel@etu.univ-lyon1.fr";
    private static String PASSWORD = "31052001sM";

    private boolean echape = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_reception);

        /******************* Initialisation des variables *******************/
        this.calendrier = findViewById(R.id.calendrier);
        this.notes = findViewById(R.id.notes);
        this.informations = findViewById(R.id.informations);
        this.drive = findViewById(R.id.drive);
        this.messagerie = findViewById(R.id.messagerie);

        this.ecrire = findViewById(R.id.imageEcrire);

        this.layout = findViewById(R.id.scrollReceptionMail);

        /******************* Reception des mails *******************/
        ReceptionMail mails = new ReceptionMail(); // On instanci l'objet mails de la classe ReceptionMail qui est dans une AsyncTask
        mails.execute();

        /******************* Mise en place d'écouteur *******************/
        ecrire.setOnClickListener(new View.OnClickListener() { //Lors q'un clic sur le bouton connexion
            @Override
            public void onClick(View v) {

                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), MailEnvoie.class); //Ouverture d'une nouvelle activité
                startActivity(otherActivity);

                echape=true;//On stop l'async task

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

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

    public class ReceptionMail extends AsyncTask<Void, Void, Void> {

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
                System.out.println("defaultFolder : " + defaultFolder.getName());

                for (Folder folder : defaultFolder.list()) {
                    System.out.println(folder.getName());
                }
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
            int count = folder.getMessageCount();
            int unread = folder.getUnreadMessageCount();
            System.out.println("Il y a " + count + " messages, dont " + unread + " non lus.");
            for (int i = count; i > 0; i-- ) {

                Message message = folder.getMessage(i);
                System.out.println("Message n° " + i);
                System.out.println("Sujet : " + message.getSubject());

                System.out.println("Content : ");

                    System.out.println(message.toString());// A modifier

                affichageDesMail(message);

                // Fin de l'asynctask plus tôt
                if (echape) break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void close(Folder folder) {
        if (folder != null && folder.isOpen()) {
            try {
                folder.close(false); // false -> On n'efface pas les messages marqués DELETED
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void affichageDesMail( Message message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Ajout de l'expéditeur
                TextView expediteur = new TextView(getApplicationContext());

                Address[] addresses = new Address[0];
                try {
                    addresses = message.getFrom();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                for (Address address : addresses) {

                    String[] nomDest = address.toString().split("<");//Transformation en chaîne de caractère et on va enlever les informations superflux (<adresse mail>)
                    expediteur.setText(nomDest[0]);
                }

                expediteur.setTextSize(20);//Taille du text
                expediteur.setTypeface(null, Typeface.BOLD);
                expediteur.setTextColor(Color.BLACK);
                layout.addView(expediteur);

                //Ajout du sujet
                TextView sujet = new TextView(getApplicationContext());
                try {
                    sujet.setText(message.getSubject());
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                sujet.setTextSize(15);//Taille du text
                layout.addView(sujet);


                /*
                //Ajout de la description
                TextView description = new TextView(getApplicationContext());
                try {
                    description.setText(message.getDescription());
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                description.setTextSize(10);//Taille du text
                layout.addView(description);
                */

                //Ajout du diviseur
                View diviseur = new View(getApplicationContext());
                diviseur.setBackgroundColor(Color.GRAY);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(layout.getWidth(), 1);
                params.setMargins(0,20,0,20);
                diviseur.setLayoutParams(params);
                layout.addView(diviseur);
            }
        });
    }


}