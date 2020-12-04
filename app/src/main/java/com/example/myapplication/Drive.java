package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class Drive extends AppCompatActivity {

    /******************* Attribut *******************/
    private WebView webView; //Navigateur web

    private Menu menu;

    private FirebaseAuth mAuth;
    private DatabaseManager databaseManager;//Base de données
    private FirebaseFirestore db; //Base de donnée Firestore

    private String idUtilisateur;
    private String passwordUtilisateur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        /******************* Initialisation des variables *******************/
        this.webView = findViewById(R.id.webView);

        this.menu = new Menu(this);

        databaseManager = new DatabaseManager(this);
        db = FirebaseFirestore.getInstance(); // Acces à la base de donnée cloud firestore
        mAuth = FirebaseAuth.getInstance();

        idUtilisateur = databaseManager.getIdentifiant();

        // On récupère le mot de passe de l'utilisateur
        db.collection("users")
                .whereEqualTo("Uid",mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                passwordUtilisateur = document.getString("Password");

                            }
                        } else {
                            System.out.println("Erreur ");
                        }
                    }
                });

        /******************* Gestion du navigateur web *******************/

        webView.getSettings().setJavaScriptEnabled(true);//Activer le javascript sur le navigateur
        //webView.getSettings().setDomStorageEnabled(true);

        webView.loadUrl("https://cas.univ-lyon1.fr/cas/login?service=https%3A%2F%2Fclarolineconnect.univ-lyon1.fr%2Flogin_check");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url){
                view.loadUrl("javascript:var x = document.getElementById('username').value = '"+idUtilisateur+"';");
                view.loadUrl("javascript:var x = document.getElementById('password').value = '"+passwordUtilisateur+"';");
                view.loadUrl("javascript:document.getElementsByName('submit')[0].click();");
            }
        });


    }

    /******************* Gestion du retour en arrière *******************/
    @Override
    public void onBackPressed() {

        if (webView.canGoBack()){
            webView.goBack();
        }
        else {
            super.onBackPressed();
        }

    }
}