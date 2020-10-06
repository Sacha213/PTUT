package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/******************* Classe représentant notre base de données *******************/
public class DatabaseManager extends SQLiteOpenHelper {

    /******************* Attribut *******************/

    private static final String DATABASE_NAME = "Etu.bd"; //Nom de la base de données
    private static final int DATABASE_VERSION = 2; //Version de la base de données

    /******************* Constructeur *******************/
    public DatabaseManager( Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    /******************* Méthode appelé automatiquement lors de la première utilisation *******************/
    @Override
    public void onCreate(SQLiteDatabase db) {
        String strSql1 = "create table USERS (idEtudiant varchar2(8) primary key, password text not null)"; //Génération de la requette SQL pour créer un table Users qui va contenir les identifiants de l'utilisateur
        db.execSQL(strSql1); //On exécute la requette

        String strSql2 = "create table MAILS (numMail integer primary key, expediteur text not null, sujet text not null, description text not null, contenu text not null)"; //Génération de la requette SQL pour créer un table Users qui va contenir les identifiants de l'utilisateur
        db.execSQL(strSql2); //On exécute la requette
    }

    /******************* Méthode appelé automatiquement si la version de la base de données a changée *******************/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE USERS");//On réinitionalise la base de donné
        onCreate(db);

    }

    /******************* Méthode pour insérer les identifiants de l'utilisateur dans la table Users *******************/
    public void insertIdentifiant(String id, String mdp){
        String strSql = "insert into Users values ('"+id+"','"+mdp+"')"; //Génération de la requette SQL

        this.getWritableDatabase().execSQL(strSql); //Exécution de la requette
    }

    /******************* Méthode qui permet de connaître les identifiants de l'utilisateur préalablement enregistrés dans la base de données *******************/
    public String getIdentifiant(){

        String strsql = "select * from Users"; //Génération de la requette SQL
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null); //Création d'un curseur qui va nous permettre de parcourir les résultat de la requette (ligne par ligne)
        cursor.moveToFirst(); //On déplace le curseur à la première ligne

        String id= cursor.getString(0) + cursor.getString(1); //On enregistre le résultat de la colone 1 et 2 dans la variable string id
        cursor.close(); //On ferme le curseur

        return id;
    }

    /******************* Méthode qui permet de supprimmer les données de la table Users (provisoire) *******************/
    public void deleteAllUsers(){
        String strsql = "DELETE FROM USERS "; //Génération de la requette SQL

        this.getWritableDatabase().execSQL(strsql); //Exécution de la requette
    }

    /******************* Méthode pour insérer un mail de l'utilisateur dans la table Mail *******************/
    public void insertMail(int numMail, String expediteur, String sujet, String description, String contenu){
        String strSql = "insert into Mails values ("+numMail+",'"+expediteur+"','"+sujet+"','"+description+"','"+contenu+"')"; //Génération de la requette SQL

        this.getWritableDatabase().execSQL(strSql); //Exécution de la requette
    }

    /******************* Récupération de l'expediteur du mail *******************/
    public String getExpediteurMail(int num){

        String strsql = "select expediteur from Mails WHERE numMail = "+num; //Génération de la requette SQL
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null); //Création d'un curseur qui va nous permettre de parcourir les résultat de la requette (ligne par ligne)
        cursor.moveToFirst(); //On déplace le curseur à la première ligne

        String expediteur = cursor.getString(0);
        cursor.close(); //On ferme le curseur

        return expediteur;
    }

    /******************* Récupération du sujet du mail *******************/
    public String getSujetMail(int num){

        String strsql = "select sujet from Mails WHERE numMail = "+num; //Génération de la requette SQL
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null); //Création d'un curseur qui va nous permettre de parcourir les résultat de la requette (ligne par ligne)
        cursor.moveToFirst(); //On déplace le curseur à la première ligne

        String sujet = cursor.getString(0);
        cursor.close(); //On ferme le curseur

        return sujet;
    }

    /******************* Récupération de la description du mail *******************/
    public String getDescriptionMail(int num){

        String strsql = "select description from Mails WHERE numMail = "+num; //Génération de la requette SQL
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null); //Création d'un curseur qui va nous permettre de parcourir les résultat de la requette (ligne par ligne)
        cursor.moveToFirst(); //On déplace le curseur à la première ligne

        String description = cursor.getString(0) ;
        cursor.close(); //On ferme le curseur

        return description;
    }

    /******************* Récupération du contenu du mail *******************/
    public String getContenuMail(int num){

        String strsql = "select contenu from Mails WHERE numMail = "+num; //Génération de la requette SQL
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null); //Création d'un curseur qui va nous permettre de parcourir les résultat de la requette (ligne par ligne)
        cursor.moveToFirst(); //On déplace le curseur à la première ligne

        String contenu = cursor.getString(0);
        cursor.close(); //On ferme le curseur

        return contenu;
    }

    /******************* Méthode qui permet de récupérer le numéro du dernier mail *******************/
    public int getDernierNumMail(){

        String strsql = "select numMail from Mails ORDER BY numMail DESC LIMIT 1 "; //Génération de la requette SQL
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null); //Création d'un curseur qui va nous permettre de parcourir les résultat de la requette (ligne par ligne)
        cursor.moveToFirst(); //On déplace le curseur à la première ligne

        int num = cursor.getInt(0); //On enregistre le résultat de la colone 1  dans la variable string mail
        cursor.close(); //On ferme le curseur

        return num;
    }

    /******************* Méthode qui permet de supprimmer les données de la table Mails (provisoire) *******************/
    public void deleteAllMails(){
        String strsql = "DELETE FROM MAILS "; //Génération de la requette SQL

        this.getWritableDatabase().execSQL(strsql); //Exécution de la requette
    }



}
