package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/******************* Classe représentant notre base de données *******************/
public class DatabaseManager extends SQLiteOpenHelper {

    /******************* Attribut *******************/

    private static final String DATABASE_NAME = "Etu.bd"; //Nom de la base de données
    private static final int DATABASE_VERSION = 1; //Version de la base de données
    static int id = 0;

    /******************* Constructeur *******************/
    public DatabaseManager( Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    /******************* Méthode appelé automatiquement lors de la première utilisation *******************/
    @Override
    public void onCreate(SQLiteDatabase db) {
        String strSql = "create table Users (idEtudiant varchar2(8) primary key, password text not null)"; //Génération de la requette SQL pour créer un table Users qui va contenir les identifiants de l'utilisateur
        db.execSQL(strSql); //On exécute la requette

        String strSql2 = "create table Pseudo (pseudo varchar2(255) primary key)";
        db.execSQL(strSql2);

        String strSql3 = "create table Message (id number(255) primary key, pseudoSender varchar2(255), message varchar(2))";
        db.execSQL(strSql3);
    }

    /******************* Méthode appelé automatiquement si la version de la base de données a changée *******************/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
    public void deleteAll(){
        String strsql = "DELETE FROM USERS "; //Génération de la requette SQL

        this.getWritableDatabase().execSQL(strsql); //Exécution de la requette
    }



    public void insertPseudo(String newPseudo) {

        String strSql = "insert into Pseudo values ('"+newPseudo+"')";

        this.getWritableDatabase().execSQL(strSql);
    }

    public String getPseudo() {

        String strsql = "select * from Pseudo"; //Génération de la requette SQL
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null); //Création d'un curseur qui va nous permettre de parcourir les résultat de la requette (ligne par ligne)
        cursor.moveToFirst(); //On déplace le curseur à la première ligne

        String pseudo = cursor.getString(0); //On enregistre le résultat de la colone 1 dans la variable string pseudo
        cursor.close(); //On ferme le curseur

        return pseudo;

    }


    public void insertMessage(String message, String pseudoSender)
    {
        String strSql = "insert into Message values ('"+id+"','"+pseudoSender+"','"+message+"')";
        id++;
        this.getWritableDatabase().execSQL(strSql);
    }

    public String[] listMessage(String pseudoSender)
    {
        String[] list = new String[20];

        String strsql = "select message from Message where pseudoSender = '"+pseudoSender+"'";
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null);
        cursor.moveToFirst();

        for(int i = 0; cursor.getString(0) != null; i++)
        {
            list[i] = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close(); //On ferme le curseur
        return list;
    }



}
