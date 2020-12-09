package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;


public class MailLecture extends AppCompatActivity {

    /******************* Attribut *******************/
    private Menu menu;

    private static String HOST = "accesbv.univ-lyon1.fr";
    private static String LOGIN;
    private static String PASSWORD;

    private int numeroMail;

    private WebView contenu;
    private TextView sujet;
    private TextView expediteur;
    private ImageView pastille;
    private LinearLayout layout;

    private ProgressBar progressBar;
    private TextView textChargement;

    //Base de données local
    private DatabaseManager databaseManager;
    private FirebaseFirestore db; //Base de donnée Firestore
    private FirebaseAuth mAuth;

    // Les dossiers
    private Store store = null;
    private Folder defaultFolder = null;
    private Folder inbox = null;

    private Message message;
    boolean lu = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_lecture);

        /******************* Initialisation des variables *******************/
        Intent intent = getIntent();//On récupaire les données transmise venant de l'ancienne activité
        numeroMail = intent.getIntExtra("Numéro",1);

        this.contenu = findViewById(R.id.scrollLectureMail);
        this.sujet = findViewById(R.id.textSujet);
        this.expediteur = findViewById(R.id.textExpediteur);
        this.pastille = findViewById(R.id.pastille);
        this.layout = findViewById(R.id.layoutPieceJoint);

        this.progressBar = findViewById(R.id.barChargement);
        this.textChargement = findViewById(R.id.textChargement);

        databaseManager = new DatabaseManager(this);
        db = FirebaseFirestore.getInstance(); // Acces à la base de donnée cloud firestore
        mAuth = FirebaseAuth.getInstance();

        this.menu = new Menu(this,databaseManager);

        //On récupère le login de l'utilisateur
        LOGIN=databaseManager.getIdentifiant();

        /******************* Reception des mails *******************/

        // On récupère le mot de passe de l'utilisateur
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            PASSWORD = decrypt(document.getString("Password"),databaseManager.getCle());

                            /******************* Reception des mails *******************/
                            LectureMail mails = new LectureMail(); // On instanci l'objet mails de la classe ReceptionMail qui est dans une AsyncTask
                            mails.execute();
                        }
                        else{
                            System.out.println("Erreur");
                        }

                    }
                });



    }

    public class LectureMail extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... voids) {

            // Création de la session
            Properties properties = new Properties();
            properties.setProperty("mail.store.protocol", "imaps");
            properties.setProperty("mail.imaps.host", HOST);
            properties.setProperty("mail.imaps.user", LOGIN);
            Session session = Session.getInstance(properties);

            // Les dossiers
            try {
                store = session.getStore(new URLName("imaps://" + HOST));
                store.connect(LOGIN, PASSWORD);
                defaultFolder = store.getDefaultFolder();

                inbox = defaultFolder.getFolder("INBOX");

                printMessages(inbox);

            } catch (Exception e) {
                e.printStackTrace();
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
            folder.open(Folder.READ_WRITE); //On peut lire et modifier des propriété du message


            message = folder.getMessage(numeroMail);


            //Récupération du contenu du mail
            String contenu = lectureContenuMail(message);

            //récupération du sujet
            String sujet = message.getSubject();
            //On transforme le text s'il est encoder en utf-8 ou IS0
            sujet = degodage(sujet);

            Address addresses = message.getFrom()[0];//Résupération de l'adresse de l'expéditeur
            String[] nomDest = addresses.toString().split("<");//Transformation en chaîne de caractère et on va enlever les informations superflux (<adresse mail>)

            //On enlève les "" si le nom de l'expéditeur en contient
            String textExpediteur = nomDest[0];
            if (textExpediteur.substring(0, 1).equals("\"")) {
                textExpediteur = textExpediteur.substring(1, textExpediteur.length() - 2);
            }
            //On transforme le text s'il est encoder en utf-8 ou IS0
            textExpediteur = degodage(textExpediteur);

            affichageDuMail(textExpediteur,sujet,contenu, message);


        } catch (MessagingException | UnsupportedEncodingException e) {
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


    private void affichageDuMail(String textExpediteur, String object, String textContenu, Message message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Ajout de l'expéditeur
                expediteur.setText(textExpediteur);

                //Ajout du sujet
                sujet.setText(object);

                //Ajout du contenu dans la webview
                contenu.loadDataWithBaseURL(null, textContenu, "text/html", "utf-8", null);

                /******************* Ecouteur sur l'image pastille *******************/
                pastille.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //On va changer de pastille
                        pastille.setImageResource(R.drawable.cercle_plein);

                        //On modifie la variable lu pour que le message soit marquer comme non lu
                        lu = false;

                    }
                });
            }
        });


    }

    public String lectureContenuMail(Message message) {

        String cont = "";
        try {
            DataSource dataSource = message.getDataHandler().getDataSource();
            MimeMultipart mimeMultipart = new MimeMultipart(dataSource);

            System.out.println("Type : "+message.getContentType());

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
                    BodyPart bp2 = mimeMultipart.getBodyPart(i);
                    cont += processBodyPartContenu(bp2);
                }
            }

            else if (bp.isMimeType("application/pdf") || bp.isMimeType("image/jpeg") || bp.isMimeType("image/png") ) {

               //Code
            }

            else System.out.println("else "+bp.getContentType());


        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

        return cont;
    }


    /******************* Récupération du charset *******************/
    public String getCharset(String contentType){
        String charset="";
        try {
            charset = contentType.split("charset=")[1];//On récupère la partie après le charset
        }
        catch (Exception e){
            charset = "utf-8";
        }
        if (charset.substring(0,1) == "\"") { //On suprime les "" qui se trouvent au extrémité du texte
            charset = charset.substring(1, charset.length() - 1);
        }
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

    public String degodage(String text) throws UnsupportedEncodingException {

        int position;
        //On cherche si le texte est codé en UTF-8
        if((position=text.indexOf("=?UTF-8")) != -1 )
        {
            String partie1 = text.substring(0,position);
            String partie2 = text.substring(position);
            text = partie1 + MimeUtility.decodeText(partie2);

        }
        else if ((position=text.indexOf("=?utf-8")) != -1) {

            String partie1 = text.substring(0,position);
            String partie2 = text.substring(position);
            text = partie1 + MimeUtility.decodeText(partie2);

        }
        //On cherche si le texte est codé en ISO-8859-1
        else if ((position=text.indexOf("=?iso-8859-1")) != -1){
            String partie1 = text.substring(0,position);
            String partie2 = text.substring(position);
            text = partie1 + MimeUtility.decodeText(partie2);
        }
        else if ((position=text.indexOf("=?ISO-8859-1")) != -1){
            String partie1 = text.substring(0,position);
            String partie2 = text.substring(position);
            text = partie1 + MimeUtility.decodeText(partie2);
        }
        return text;
    }

    public class FermetureMail extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            //On va marquer le mail comme non lu ou non en fonction de la variable lu
            try {
                message.setFlag(Flags.Flag.SEEN,lu);
                message.saveChanges();
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            try {
                // On ferme les dossier ouvert
                close(inbox);
                close(defaultFolder);

                if (store != null && store.isConnected()) {
                    store.close();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public String decrypt(String cryptePassword, String key){

        byte[] bytesPassword =  android.util.Base64.decode(cryptePassword.getBytes(), Base64.DEFAULT);

        try
        {
            Key clef = new SecretKeySpec(key.getBytes("UTF_8"),"Blowfish");
            Cipher cipher=Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE,clef);

            return new String(cipher.doFinal(bytesPassword), "UTF_8");
        }
        catch (Exception e)
        {
            System.out.println(e);
            return null;
        }
    }

    /******************* Gestion du retour en arrière *******************/
    @Override
    public void onBackPressed() {

        /******************* Changement de page *******************/
        Intent otherActivity = new Intent(getApplicationContext(), MailReception.class); //Ouverture d'une nouvelle activité
        startActivity(otherActivity);

        //On ferme tous ce qu'on a ouvert
        FermetureMail fermetureMail = new FermetureMail();
        fermetureMail.execute();

        //On ferme la database
        databaseManager.close();

        finish();//Fermeture de l'ancienne activité
        overridePendingTransition(0,0);//Suprimmer l'animation lors du changement d'activité

    }


}
