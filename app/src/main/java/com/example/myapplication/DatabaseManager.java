package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

/******************* Classe représentant notre base de données *******************/
public class DatabaseManager extends SQLiteOpenHelper {

    /******************* Attribut *******************/

    private static final String DATABASE_NAME = "Etu.bd"; //Nom de la base de données
    private static final int DATABASE_VERSION = 7; //Version de la base de données

    /******************* Constructeur *******************/
    public DatabaseManager( Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    /******************* Méthode appelé automatiquement lors de la première utilisation *******************/
    @Override
    public void onCreate(SQLiteDatabase db) {
        String strSql1 = "create table USERS (idEtudiant varchar2(8) primary key)"; //Génération de la requette SQL pour créer un table Users qui va contenir les identifiants de l'utilisateur
        db.execSQL(strSql1); //On exécute la requette

        String strSql2 = "create table MATIERES (matiere text primary key)"; //Génération de la requette SQL pour créer un table Matiere de l'utilisateur
        db.execSQL(strSql2); //On exécute la requette

        String strSql3 = "create table NOTES (note text, description text, matiere text, datePub text, foreign key (matiere) references MATIERES(matiere))"; //Génération de la requette SQL pour créer un table Users qui va contenir les identifiants de l'utilisateur
        db.execSQL(strSql3); //On exécute la requette

    }

    /******************* Méthode appelé automatiquement si la version de la base de données a changée *******************/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //On réinitionalise la base de donné en supprimant toutes ses tables
        db.execSQL("DROP TABLE USERS");
        db.execSQL("DROP TABLE MATIERES");
        db.execSQL("DROP TABLE NOTES");
        onCreate(db);

    }

    /******************* Méthode pour insérer l'identifiant de l'utilisateur dans la table Users *******************/
    public void insertIdentifiant(String id){
        String strSql = "insert into Users values ('"+id+"')"; //Génération de la requette SQL

        this.getWritableDatabase().execSQL(strSql); //Exécution de la requette
    }

    /******************* Méthode qui permet de connaître l'identifiant de l'utilisateur préalablement enregistrés dans la base de données *******************/
    public String getIdentifiant(){

        String strsql = "select idEtudiant from Users"; //Génération de la requette SQL
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null); //Création d'un curseur qui va nous permettre de parcourir les résultat de la requette (ligne par ligne)
        cursor.moveToFirst(); //On déplace le curseur à la première ligne

        String id = cursor.getString(0); //On enregistre le résultat de la colone 1 dans la variable string id
        cursor.close(); //On ferme le curseur

        return id;
    }


    /******************* Méthode qui permet de supprimmer les données de la table Users (provisoire) *******************/
    public void deleteAllUsers(){
        String strsql = "DELETE FROM USERS "; //Génération de la requette SQL

        this.getWritableDatabase().execSQL(strsql); //Exécution de la requette
    }

    /******************* Méthode qui permet d'insérer une matière *******************/
    public void insertMatiere(String matiere){
        String strSql = "insert into MATIERES values ('"+matiere+"')"; //Génération de la requette SQL

        this.getWritableDatabase().execSQL(strSql); //Exécution de la requette
    }

    /******************* Méthode qui permet récupérer toutes les matières *******************/
    public String getMatieres(){

        String strsql = "select matiere from MATIERES"; //Génération de la requette SQL
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null); //Création d'un curseur qui va nous permettre de parcourir les résultat de la requette (ligne par ligne)

        cursor.moveToFirst(); //On déplace le curseur à la première ligne
        String matieres = "";
        while (!cursor.isAfterLast()) { //On parcours tout les résultats
            matieres +=  cursor.getString(0)+"/"; //On enregistre le résultat de la colone 1 dans la variable string matieres
            cursor.moveToNext(); //On avance de ligne
        }
        cursor.close(); //On ferme le curseur

        return matieres;
    }

    /******************* Méthode qui permet de suprimmer les données de la table Matière *******************/
    public void deleteAllMatieres(){
        String strsql = "DELETE FROM MATIERES "; //Génération de la requette SQL

        this.getWritableDatabase().execSQL(strsql); //Exécution de la requette
    }

    /******************* Méthode qui permet d'insérer une note *******************/
    public void insertNote(String note, String description, String matiere, String datePub){
        String strSql = "insert into NOTES values ('"+note+"','"+description+"','"+matiere+"','"+datePub+"')"; //Génération de la requette SQL

        this.getWritableDatabase().execSQL(strSql); //Exécution de la requette
    }


    /******************* Méthode qui permet récupérer toutes les notes *******************/
    public String getNotes(String mat){

        String strsql = "select * from NOTES where matiere = '"+mat+"'"; //Génération de la requette SQL
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null); //Création d'un curseur qui va nous permettre de parcourir les résultat de la requette (ligne par ligne)

        cursor.moveToFirst(); //On déplace le curseur à la première ligne
        String notes = "";
        while (!cursor.isAfterLast()) { //On parcours tout les résultats
            notes +=  cursor.getString(0)+" --"+cursor.getString(1)+cursor.getString(3)+"---"; //On enregistre le résultat de la colone 1 dans la variable string matieres
            cursor.moveToNext(); //On avance de ligne
        }
        cursor.close(); //On ferme le curseur

        return notes;
    }


    /******************* Méthode qui permet de suprimmer toutes les notes *******************/
    public void deleteAllNotes(){
        String strsql = "DELETE FROM NOTES "; //Génération de la requette SQL

        this.getWritableDatabase().execSQL(strsql); //Exécution de la requette
    }







}
