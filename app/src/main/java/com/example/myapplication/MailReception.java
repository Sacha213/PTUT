package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.common.util.IOUtils;

import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

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

    private boolean echape ;

    private boolean messageEnvoye;


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

        Intent intent = getIntent();//On récupaire les données transmise venant de l'ancienne activité
        messageEnvoye = intent.getBooleanExtra("MessageEnvoyer",false);

        echape = false;


        /******************* Reception des mails *******************/
        ReceptionMail mails = new ReceptionMail(); // On instanci l'objet mails de la classe ReceptionMail qui est dans une AsyncTask
        mails.execute();

        /******************* Affichage de la boite de dialogue si message envoyer *******************/
        if (messageEnvoye){
            AlertDialog.Builder msgEnvoyer = new AlertDialog.Builder(this);

            msgEnvoyer.setTitle("Mail envoyé"); //Titre
            msgEnvoyer.setIcon(R.drawable.valider); //Ajout de l'émoji valider
            msgEnvoyer.show(); //Affichage de la boîte de dialogue
        }


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

            for (int i = count; i > 0; i-- ) {

                Message message = folder.getMessage(i);



                String contenu = lectureContenuMail(message);//Récupération du contenu du mail
                String sujet = message.getSubject();//récupération du sujet
                //Résupération de l'adresse de l'expéditeur
                Address[] addresses = new Address[0];
                try {
                    addresses = message.getFrom();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                int numMail = message.getMessageNumber();

                affichageDesMail(sujet,addresses,contenu,numMail); //gerer l'affichage des mails dans l'activité

                // Fin de l'asynctask plus tôt (si on quite l'activité)
                if (echape) break;
            }

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

    private void affichageDesMail(String object,Address[] addresses,String contenu ,int numMail) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Ajout de l'expéditeur
                TextView expediteur = new TextView(getApplicationContext());



                String[] nomDest = addresses[0].toString().split("<");//Transformation en chaîne de caractère et on va enlever les informations superflux (<adresse mail>)

                //On enlève les "" si le nom de l'expéditeur en contient
                String textExpediteur= nomDest[0];
                if (textExpediteur.substring(0, 1).equals("\"")){
                    textExpediteur=textExpediteur.substring(1,textExpediteur.length()-2);
                }
                expediteur.setText(textExpediteur);

                expediteur.setTextSize(20);//Taille du text
                expediteur.setTypeface(null, Typeface.BOLD);//Gras
                expediteur.setTextColor(Color.BLACK);//text en noir
                expediteur.setLines(1);//Une ligne max
                expediteur.setEllipsize(TextUtils.TruncateAt.END);//ajout des ...
                layout.addView(expediteur);



                //Ajout du sujet
                TextView sujet = new TextView(getApplicationContext());
                sujet.setText(object);
                sujet.setTextSize(15);//Taille du text
                sujet.setLines(1);//Une ligne max
                sujet.setEllipsize(TextUtils.TruncateAt.END);//ajout des ...
                layout.addView(sujet);




                //Ajout de la description
                TextView description = new TextView(getApplicationContext());
                description.setText(contenu);
                description.setTextSize(10);//Taille du text
                description.setMaxLines(3);//3 lignes max
                description.setEllipsize(TextUtils.TruncateAt.END);//ajout des ...
                layout.addView(description);


                //Ajout du diviseur
                View diviseur = new View(getApplicationContext());
                diviseur.setBackgroundColor(Color.GRAY);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(layout.getWidth(), 1);
                params.setMargins(0,20,0,20);
                diviseur.setLayoutParams(params);
                layout.addView(diviseur);

                /******************* Mise en place d'écouteur *******************/
                sujet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /******************* Changement de page *******************/
                        Intent otherActivity = new Intent(getApplicationContext(), MailLecture.class); //Ouverture d'une nouvelle activité
                        otherActivity.putExtra("Numéro",numMail); //Envoie de donner dans la nouvelle activité (numéro du mail)
                        startActivity(otherActivity);

                        echape=true;//On stop l'async task

                        finish();//Fermeture de l'ancienne activité
                        overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité
                    }
                });

                expediteur.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /******************* Changement de page *******************/
                        Intent otherActivity = new Intent(getApplicationContext(), MailLecture.class); //Ouverture d'une nouvelle activité
                        otherActivity.putExtra("Numéro",numMail); //Envoie de donner dans la nouvelle activité (numéro du mail)
                        startActivity(otherActivity);

                        echape=true;//On stop l'async task

                        finish();//Fermeture de l'ancienne activité
                        overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité
                    }
                });

                description.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /******************* Changement de page *******************/
                        Intent otherActivity = new Intent(getApplicationContext(), MailLecture.class); //Ouverture d'une nouvelle activité
                        otherActivity.putExtra("Numéro",numMail); //Envoie de donner dans la nouvelle activité (numéro du mail)
                        startActivity(otherActivity);

                        echape=true;//On stop l'async task

                        finish();//Fermeture de l'ancienne activité
                        overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité
                    }
                });

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

        String contenu = "rienn";
        try {
            DataSource dataSource = message.getDataHandler().getDataSource();
            MimeMultipart mimeMultipart = new MimeMultipart(dataSource);

            if (message.isMimeType("text/plain")) {

                System.out.println("plain");
                contenu  = getStringFromInputStream(message.getInputStream());

            } else if (message.isMimeType("text/html")) {
                System.out.println("html");
                contenu = Jsoup.parse(getStringFromInputStream(message.getInputStream())).text();
            }
            else {
                System.out.println("else");
                int multiPartCount = mimeMultipart.getCount();
                System.out.println("Il y a " + multiPartCount + " partie(s) dans ce message.");

                int part;
                if (mimeMultipart.getBodyPart(0).isMimeType("text/plain") || mimeMultipart.getBodyPart(0).isMimeType("text/html")){
                    part=0;
                }
                else{
                    part=1;
                }
                System.out.println(mimeMultipart.getBodyPart(0).getContentType());
                System.out.println(mimeMultipart.getBodyPart(1).getContentType());
                BodyPart bp = mimeMultipart.getBodyPart(part);
                contenu = processBodyPart(bp);

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

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

        //Transformation du charcet en UTF-8
        if(bp.getContentType().equals("text/plain; charset=\"iso-8859-1\"")  || bp.getContentType().equals("text/html; charset=\"iso-8859-1\"") ){
            contenu =  new String(contenu.getBytes("ISO-8859-1"), "UTF-8");
            System.out.println("trad");
        }

        return contenu;
    }


}
