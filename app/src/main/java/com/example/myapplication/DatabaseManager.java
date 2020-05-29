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

    /******************* Constructeur *******************/
    public DatabaseManager( Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    /******************* Méthode appelé automatiquement lors de la première utilisation *******************/
    @Override
    public void onCreate(SQLiteDatabase db) {
        String strSql = "create table Users (idEtudiant varchar2(8) primary key, password text not null)"; //Génération de la requette SQL pour créer un table Users qui va contenir les identifiants de l'utilisateur
        db.execSQL(strSql); //On exécute la requette
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



}
