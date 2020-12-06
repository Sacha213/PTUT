package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Recherche extends AppCompatActivity implements ListAdapter.OnNoteListener {

    private static final String TAG = "Recherche";
    private FirebaseFirestore db; //Base de donnée Firestore
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ListAdapter listAdapter;

    private List<Affichage> listeUsers = new ArrayList<>();
    private String prenom;
    private String nom;
    private String rslt;

    private ImageView calendrier; //Icônes du menu
    private ImageView notes;
    private ImageView informations;
    private ImageView drive;
    private ImageView messagerie;
    private TextView pseudo;
    private Button button;
    private EditText recherche;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);


        db = FirebaseFirestore.getInstance();

        System.out.println(getIntent().getStringExtra("mode"));

        if(getIntent().getStringExtra("mode").equals("mainlist")) {
            System.out.println("yooo");
            db.collection("Users")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                prenom = document.getString("prenom");
                                nom = document.getString("nom");
                                System.out.println(prenom+" "+nom);
                                listeUsers.add(new Affichage(prenom+" "+nom, null));
                            }
                            recyclerView = findViewById(R.id.recyclerview_pers);

                            layoutManager = new LinearLayoutManager(this);

                            recyclerView.setLayoutManager(layoutManager);

                            listAdapter = new ListAdapter(listeUsers,this);

                            recyclerView.setAdapter(listAdapter);
                        }
                    });
        }

        if(getIntent().getStringExtra("mode").equals("this")) {
            String temp = getIntent().getStringExtra("recherche");

            db.collection("Users")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                rslt = document.getString("prenom")+" "+document.getString("nom");
                                if (Pattern.matches(".*"+temp+".*".toLowerCase(), rslt.toLowerCase()))
                                {
                                    prenom = document.getString("prenom");
                                    nom = document.getString("nom");
                                    listeUsers.add(new Affichage(prenom+" "+nom, null));
                                }
                            }
                            recyclerView = findViewById(R.id.recyclerview_pers);

                            layoutManager = new LinearLayoutManager(this);

                            recyclerView.setLayoutManager(layoutManager);

                            listAdapter = new ListAdapter(listeUsers,this);

                            recyclerView.setAdapter(listAdapter);
                        }
                    });
        }








        this.calendrier = findViewById(R.id.calendrier);
        this.notes = findViewById(R.id.notes);
        this.informations = findViewById(R.id.informations);
        this.drive = findViewById(R.id.drive);
        this.messagerie = findViewById(R.id.messagerie);
        this.button = findViewById(R.id.button2);
        this.recherche = findViewById(R.id.recherche);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = recherche.getText().toString();
                System.out.println(temp);
                Intent intent = new Intent(getApplicationContext(), Recherche.class);
                intent.putExtra("mode", "this");
                intent.putExtra("recherche",temp);//Ouverture d'une nouvelle activité
                startActivity(intent);
                finish();//Fermeture de l'ancienne activité
                overridePendingTransition(0, 0);
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

    @Override
    public void onNoteClick(int position) {
        Log.d(TAG, "onNoteClick: ");
        Intent intent = new Intent(this,MainActivity.class);
        String tmp = listeUsers.get(position).getPseudo();
        intent.putExtra("users", tmp);
        startActivity(intent);
        finish(); //Fermeture de l'ancienne activité
        overridePendingTransition(0, 0);
    }
}




