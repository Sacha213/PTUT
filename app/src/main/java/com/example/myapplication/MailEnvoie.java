package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailEnvoie extends AppCompatActivity {


    /******************* Attribut *******************/
    private ImageView calendrier; //Icônes du menu
    private ImageView notes;
    private ImageView informations;
    private ImageView drive;
    private ImageView messagerie;

    private EditText contenu;
    private EditText destinataire;
    private EditText object;
    private ImageView envoyer;


    private static String HOST = "smtpbv.univ-lyon1.fr";
    private static String LOGIN = "p1908066";
    private static String ACCOUNT = "sacha.montel@etu.univ-lyon1.fr";
    private static String PASSWORD = "31052001sM";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_envoie);


    /******************* Initialisation des variables *******************/
        this.calendrier = findViewById(R.id.calendrier);
        this.notes = findViewById(R.id.notes);
        this.informations = findViewById(R.id.informations);
        this.drive = findViewById(R.id.drive);
        this.messagerie = findViewById(R.id.messagerie);

        this.envoyer = findViewById(R.id.imageEnvoyer);
        this.destinataire = findViewById(R.id.editTextDestinataire);
        this.contenu = findViewById(R.id.textMessage);
        this.object = findViewById(R.id.editTextObject);


        /******************* Mise en place d'écouteur *******************/
        envoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /******************* Envoie d'un mail *******************/
                EnvoieMessage message = new EnvoieMessage(); // On instanci l'objet message de la classe EnvoieMessage qui est dans une AsyncTask
                message.execute(object.getText().toString(),contenu.getText().toString(),destinataire.getText().toString(),"sachamontel@yahoo.fr");

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

    public class EnvoieMessage extends AsyncTask<String, Void, Void>{

        /******************* Attribut *******************/
        private String text;
        private String subject;
        private String destinataire;
        private String copyDest;

        @Override
        protected Void doInBackground(String... strings) {

            //Séparation des paramètres
            subject = strings[0];
            text = strings[1];
            destinataire = strings[2];
            copyDest = strings[3];

            //Création de la session
            Properties properties = new Properties();
            properties.setProperty("mail.transport.protocol", "smtp");
            properties.setProperty("mail.smtp.host", HOST);
            properties.setProperty("mail.smtp.user", LOGIN);
            properties.setProperty("mail.from", ACCOUNT);
            properties.setProperty("mail.smtp.port","587");
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.starttls.enable", "true");


            Session session = Session.getInstance(properties);

            //Création du message
            MimeMessage message = new MimeMessage(session);
            try {
                message.setText(text);
                message.setSubject(subject);
                message.addRecipients(Message.RecipientType.TO, destinataire);
                message.addRecipients(Message.RecipientType.CC, copyDest);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            //Envoi du message
            Transport transport = null; //On est obliger d'initialiser la variable transport
            try {
                transport = session.getTransport("smtp");
                transport.connect(LOGIN, PASSWORD);
                transport.sendMessage(message, new Address[] { new InternetAddress(destinataire),
                        new InternetAddress(copyDest) });
            } catch (MessagingException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (transport != null) {
                        transport.close();
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        protected void onPostExecute(Void voids) {
            System.out.println("Message envoyer");
        }
    }



}