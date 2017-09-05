package com.arriwe.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper  extends SQLiteOpenHelper {
    //version number to upgrade database version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Wayndr.db";

    public DBHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    String CREATE_TABLE_VISITED_LOC = "CREATE TABLE IF NOT EXISTS " + FrequentlyVisitedLoc.TABLE  + "("
            + FrequentlyVisitedLoc.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + FrequentlyVisitedLoc.KEY_place_name + " TEXT UNIQUE , "
            + FrequentlyVisitedLoc.KEY_user_saved_name + " TEXT UNIQUE , "
            + FrequentlyVisitedLoc.KEY_Address + " TEXT, "
            + FrequentlyVisitedLoc.KEY_lat + " DOUBLE, "
            + FrequentlyVisitedLoc.KEY_long + " DOUBLE )";



    String CREATE_TABLE_TEMP_VISITED_LOC = "CREATE TABLE IF NOT EXISTS " + FrequentlyVisitedLoc.TEMPTABLE  + "("
            + FrequentlyVisitedLoc.TEMP_KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + FrequentlyVisitedLoc.TEMP_KEY_place_name + " TEXT UNIQUE , "
            + FrequentlyVisitedLoc.TEMP_KEY_user_saved_name + " TEXT UNIQUE , "
            + FrequentlyVisitedLoc.TEMP_KEY_Address + " TEXT, "
            + FrequentlyVisitedLoc.TEMP_KEY_lat + " DOUBLE, "
            + FrequentlyVisitedLoc.TEMP_KEY_long + " DOUBLE )";



    @Override
    public void onCreate(SQLiteDatabase db) {



       /* String CREATE_TABLE_CONTANCT = "CREATE TABLE IF NOT EXISTS " + ContactDetailsList.TABLE  + "("
                + ContactDetailsList.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + ContactDetailsList.KEY_name + " TEXT UNIQUE , "
                + ContactDetailsList.KEY_number + " TEXT UNIQUE , "
                + ContactDetailsList.KEY_contactPhoto + " TEXT, "
                + ContactDetailsList.KEY_contactLocation + " TEXT )";
*/
        db.execSQL(CREATE_TABLE_VISITED_LOC);
        db.execSQL(CREATE_TABLE_TEMP_VISITED_LOC);
       // db.execSQL(CREATE_TABLE_CONTANCT);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FrequentlyVisitedLoc.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FrequentlyVisitedLoc.TEMPTABLE);
    }

}
