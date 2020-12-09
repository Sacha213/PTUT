package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class Drive extends AppCompatActivity {

    /******************* Attribut *******************/
    private WebView webView; //Navigateur web

    private Menu menu;

    private FirebaseAuth mAuth;
    private DatabaseManager databaseManager;//Base de données
    private FirebaseFirestore db; //Base de donnée Firestore

    private String idUtilisateur;
    private String passwordUtilisateur;

    private String urlClaroline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        /******************* Initialisation des variables *******************/
        this.webView = findViewById(R.id.webView);

        urlClaroline = "https://cas.univ-lyon1.fr/cas/login?service=https%3A%2F%2Fclarolineconnect.univ-lyon1.fr%2Flogin_check";

        databaseManager = new DatabaseManager(this);
        db = FirebaseFirestore.getInstance(); // Acces à la base de donnée cloud firestore
        mAuth = FirebaseAuth.getInstance();

        this.menu = new Menu(this,databaseManager);

        idUtilisateur = databaseManager.getIdentifiant();

        // On récupère le mot de passe de l'utilisateur
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            passwordUtilisateur = decrypt(document.getString("Password"),databaseManager.getCle());
                        }
                        else{
                            System.out.println("Erreur");
                        }

                    }
                });


        /******************* Gestion du navigateur web *******************/

        webView.getSettings().setJavaScriptEnabled(true);//Activer le javascript sur le navigateur
        //webView.getSettings().setDomStorageEnabled(true);

        webView.loadUrl(urlClaroline);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url){

                if (url.equals(urlClaroline)){
                view.loadUrl("javascript:var x = document.getElementById('username').value = '"+idUtilisateur+"';");
                view.loadUrl("javascript:var x = document.getElementById('password').value = '"+passwordUtilisateur+"';");
                //view.loadUrl("javascript:var x = document.getElementsByName('submit')[0].click();");
                }
            }

        });


    }
    public String decrypt(String cryptePassword, String key){

        byte[] bytesPassword =  Base64.decode(cryptePassword.getBytes(),Base64.DEFAULT);

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

        if (webView.canGoBack()){
            webView.goBack();
        }
        else {
            //On ferme la database
            databaseManager.close();

            super.onBackPressed();
        }

    }
}