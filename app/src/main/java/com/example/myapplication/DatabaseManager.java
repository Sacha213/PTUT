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
    private static final int DATABASE_VERSION = 10; //Version de la base de données

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

        String strSql4 = "create table CALENDRIER(IDcal varchar2(16) primary key,HDEB number(4) not null,HFIN number(4) not null,date text not null, nom text, salle text, prof text)";
        db.execSQL(strSql4); //On exécute la requette
    }

    /******************* Méthode appelé automatiquement si la version de la base de données a changée *******************/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //On réinitionalise la base de donné en supprimant toutes ses tables
        db.execSQL("DROP TABLE USERS");
        db.execSQL("DROP TABLE MATIERES");
        db.execSQL("DROP TABLE NOTES");
        db.execSQL("DROP TABLE CALENDRIER");
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

    /******************* Méthode pour insérer un cours de l'utilisateur dans la table Users *******************/
    public void insertCours(String idcal, int HDEB,int HFIN,String date,String nom, String salle, String prof){
        String strSql = "insert into CALENDRIER values ('"+idcal+"',"+HDEB+","+HFIN+",'"+date+"','"+nom+"','"+salle+"','"+prof+"')"; //Génération de la requette SQL

        this.getWritableDatabase().execSQL(strSql); //Exécution de la requette
    }

    /******************* Méthode qui permet de connaître les données de la table CALENDRIER préalablement enregistrés dans la base de données *******************/
    public String[] getCours(String dateJour){

        String strsql = "select IDcal from CALENDRIER where date ='"+dateJour+"'"; //Génération de la requette SQL
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null); //Création d'un curseur qui va nous permettre de parcourir les résultat de la requette (ligne par ligne)


        cursor.moveToFirst(); //On déplace le curseur à la première ligne
        String[] idcal = new String[cursor.getCount()];
        int i=0;
        while (!cursor.isAfterLast()) { //On parcours tout les résultats
            idcal[i] =  cursor.getString(0); //On enregistre le résultat de la première colone dans la variable string IDcal
            cursor.moveToNext(); //On avance de ligne
            i+=1;
        }

        cursor.close(); //On ferme le curseur

        return idcal;
    }
    /******************* Méthode qui permet de connaître l'heure de debut d'un cours en fonction de l'id de la table CALENDRIER préalablement enregistrés dans la base de données *******************/
    public int getHDEB(String idcal){

        String strsql = "select HDEB from CALENDRIER where IDcal='"+idcal+"'"; //Génération de la requette SQL
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null); //Création d'un curseur qui va nous permettre de parcourir les résultat de la requette (ligne par ligne)
        cursor.moveToFirst(); //On déplace le curseur à la première ligne

        int hdeb = cursor.getInt(cursor.getColumnIndex("HDEB")); //On enregistre le résultat de la colone 1 dans la variable hdeb
        cursor.close(); //On ferme le curseur

        return hdeb;
    }
    /******************* Méthode qui permet de connaître l'heure de fin d'un cours en fonction de l'id de la table CALENDRIER préalablement enregistrés dans la base de données *******************/
    public int getHFIN(String idcal){

        String strsql = "select HFIN from CALENDRIER where IDcal ='"+idcal+"'"; //Génération de la requette SQL
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null); //Création d'un curseur qui va nous permettre de parcourir les résultat de la requette (ligne par ligne)
        cursor.moveToFirst(); //On déplace le curseur à la première ligne

        int hfin = cursor.getInt(0); //On enregistre le résultat de la colone 1 dans la variable string id
        cursor.close(); //On ferme le curseur

        return hfin;
    }

    /******************* Méthode qui permet de connaître le nom d'un cours en fonction de l'id de la table CALENDRIER préalablement enregistrés dans la base de données *******************/
    public String getNomCours(String idcal){

        String strsql = "select nom from CALENDRIER where IDcal ='"+idcal+"'"; //Génération de la requette SQL
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null); //Création d'un curseur qui va nous permettre de parcourir les résultat de la requette (ligne par ligne)
        cursor.moveToFirst(); //On déplace le curseur à la première ligne

        String nom = cursor.getString(0); //On enregistre le résultat de la colone 1 dans la variable string id
        cursor.close(); //On ferme le curseur

        return nom;
    }

    /******************* Méthode qui permet de connaître la salle d'un cours en fonction de l'id de la table CALENDRIER préalablement enregistrés dans la base de données *******************/
    public String getSalle(String idcal){

        String strsql = "select salle from CALENDRIER where IDcal ='"+idcal+"'"; //Génération de la requette SQL
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null); //Création d'un curseur qui va nous permettre de parcourir les résultat de la requette (ligne par ligne)
        cursor.moveToFirst(); //On déplace le curseur à la première ligne

        String salle = cursor.getString(0); //On enregistre le résultat de la colone 1 dans la variable string id
        cursor.close(); //On ferme le curseur

        return salle;
    }

    /******************* Méthode qui permet de connaître le professeur d'un cours en fonction de l'id de la table CALENDRIER préalablement enregistrés dans la base de données *******************/
    public String getProf(String idcal){

        String strsql = "select prof from CALENDRIER where IDcal ='"+idcal+"'"; //Génération de la requette SQL
        Cursor cursor = this.getReadableDatabase().rawQuery(strsql, null); //Création d'un curseur qui va nous permettre de parcourir les résultat de la requette (ligne par ligne)
        cursor.moveToFirst(); //On déplace le curseur à la première ligne

        String prof = cursor.getString(0); //On enregistre le résultat de la colone 1 dans la variable string id
        cursor.close(); //On ferme le curseur

        return prof;
    }
    /******************* Méthode qui permet de suprimmer les données de la table Calendrier *******************/
    public void deleteAllCours(){
        String strsql = "DELETE FROM CALENDRIER "; //Génération de la requette SQL

        this.getWritableDatabase().execSQL(strsql); //Exécution de la requette
    }

}
