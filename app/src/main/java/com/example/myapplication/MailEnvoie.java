package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private Menu menu;

    private EditText contenu;
    private EditText destinataire;
    private EditText object;
    private ImageView envoyer;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db; //Base de donnée Firestore
    private DatabaseManager databaseManager;//Base de données local


    private static String HOST = "smtpbv.univ-lyon1.fr";
    private static String LOGIN;
    private static String ACCOUNT;
    private static String PASSWORD;
    private static String NOM;
    private static String PRENOM;

    private boolean sent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_envoie);


        /******************* Initialisation des variables *******************/
        this.menu = new Menu(this);

        this.envoyer = findViewById(R.id.imageEnvoyer);
        this.destinataire = findViewById(R.id.editTextDestinataire);
        this.contenu = findViewById(R.id.textMessage);
        this.object = findViewById(R.id.editTextObject);

        mAuth = FirebaseAuth.getInstance();  // Initialize Firebase Auth
        db = FirebaseFirestore.getInstance(); // Acces à la base de donnée cloud firestore
        databaseManager = new DatabaseManager(this);

        LOGIN = databaseManager.getIdentifiant();

        ACCOUNT = databaseManager.getMail();

        // On récupère le mot de passe de l'utilisateur
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            PASSWORD = document.getString("Password");
                            NOM = document.getString("Nom");
                            PRENOM = document.getString("Prénom");

                            //On ajoute la signature à la fin du mail
                            contenu.setText("Bonjour,\n\n\n\nCordialement,\n"+PRENOM+" "+NOM);
                        }
                        else{
                            System.out.println("Erreur");
                        }

                    }
                });


        /******************* Gestion de l'envoi d'un mail *******************/

        /******************* Mise en place d'écouteur *******************/
        envoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /******************* Envoie d'un mail *******************/
                EnvoieMessage message = new EnvoieMessage(); // On instanci l'objet message de la classe EnvoieMessage qui est dans une AsyncTask
                message.execute(object.getText().toString(), contenu.getText().toString(), destinataire.getText().toString());


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
            //copyDest = strings[3];

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
                //message.addRecipients(Message.RecipientType.CC, copyDest);
            } catch (MessagingException e) {
                e.printStackTrace();

                sent=false;//Le message n'est pas envoyé
            }

            //Envoi du message
            Transport transport = null; //On est obliger d'initialiser la variable transport
            try {
                transport = session.getTransport("smtp");
                transport.connect(LOGIN, PASSWORD);
                transport.sendMessage(message, new Address[] { new InternetAddress(destinataire)});

                sent=true;//Le message est envoyé
            } catch (MessagingException e) {
                e.printStackTrace();

                sent=false;//Le message n'est pas envoyé
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

            if(sent){//S'il n'y a pas eu d'erreur alors on retourne à la page mail
                /******************* Changement de page *******************/
                Intent otherActivity = new Intent(getApplicationContext(), MailReception.class); //Ouverture d'une nouvelle activité
                otherActivity.putExtra("MessageEnvoyer", true); //Envoie de donner dans la nouvelle activité (message envoyé)
                startActivity(otherActivity);

                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0, 0);//Suprimmer l'animation lors du changement d'activité
            }
            else{

                /******************* Affichage de la boîte de dialogue d'erreur *******************/
                AlertDialog.Builder erreur = new AlertDialog.Builder(MailEnvoie.this);
                erreur.setTitle("Oups..."); //Titre
                erreur.setMessage("Une erreur est survenue, avez-vous mis une adresse mail valide ?"); //Message
                erreur.setIcon(R.drawable.road_closure); //Ajout de l'émoji caca
                erreur.show(); //Affichage de la boîte de dialogue



            }

        }
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
