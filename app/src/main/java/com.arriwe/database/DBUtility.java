package com.arriwe.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.arriwe.Model.FavPlaceModel;

import java.util.ArrayList;


/**
 * Created by Abhi1 on 17/08/15.
 */
public class DBUtility {
    private  DBHelper dbHelper;
    private  static  String TAG = "DBUtility.java";

    public DBUtility(Context context) {
        dbHelper = new DBHelper(context);
    }

    public Boolean insertFavLocation(FrequentlyVisitedLoc place) {
        // TODO Auto-generated method stub

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FrequentlyVisitedLoc.KEY_place_name, place.place_name);
        values.put(FrequentlyVisitedLoc.KEY_user_saved_name, place.user_saved_name);
        values.put(FrequentlyVisitedLoc.KEY_Address, place.address);
        values.put(FrequentlyVisitedLoc.KEY_lat,place.latitude);
        values.put(FrequentlyVisitedLoc.KEY_long, place.longitude);

        // Inserting Row
        long student_Id = db.insert(FrequentlyVisitedLoc.TABLE, null, values);
        Log.i(TAG,"Place added to fav,id is "+student_Id);
        db.close(); // Closing database connection

        if(student_Id > 0) {
            return true;
        }
        else {
            return false;
        }
    }



    public Boolean insertTempFavLocation(FrequentlyVisitedLoc place) {
        // TODO Auto-generated method stub

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FrequentlyVisitedLoc.TEMP_KEY_place_name, place.temp_place_name);
        values.put(FrequentlyVisitedLoc.TEMP_KEY_user_saved_name, place.temp_user_saved_name);
        values.put(FrequentlyVisitedLoc.TEMP_KEY_Address, place.temp_address);
        values.put(FrequentlyVisitedLoc.TEMP_KEY_lat,place.temp_latitude);
        values.put(FrequentlyVisitedLoc.TEMP_KEY_long, place.temp_longitude);

        // Inserting Row
        long student_Id = db.insert(FrequentlyVisitedLoc.TEMPTABLE, null, values);
        Log.i(TAG,"Place added to fav,id is "+student_Id);
        db.close(); // Closing database connection

        if(student_Id > 0) {
            return true;
        }
        else {
            return false;
        }
    }




    public ArrayList<FrequentlyVisitedLoc> getFrequentlyVisitedPlaces(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT * FROM "+ FrequentlyVisitedLoc.TABLE;

        ArrayList<FrequentlyVisitedLoc>  locationList=new ArrayList<FrequentlyVisitedLoc>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                FrequentlyVisitedLoc obj = new  FrequentlyVisitedLoc();
                obj.visited_location_ID = cursor.getInt(cursor.getColumnIndex(FrequentlyVisitedLoc.KEY_ID));
                obj.place_name = cursor.getString(cursor.getColumnIndex(FrequentlyVisitedLoc.KEY_place_name));
                obj.address = cursor.getString(cursor.getColumnIndex(FrequentlyVisitedLoc.KEY_Address));
                obj.latitude = cursor.getDouble(cursor.getColumnIndex(FrequentlyVisitedLoc.KEY_lat));
                obj.longitude = cursor.getDouble(cursor.getColumnIndex(FrequentlyVisitedLoc.KEY_long));
                obj.user_saved_name = cursor.getString(cursor.getColumnIndex(FrequentlyVisitedLoc.KEY_user_saved_name));

                locationList.add(obj);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return locationList;

    }




    public ArrayList<FrequentlyVisitedLoc> getTempFrequentlyVisitedPlaces(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT * FROM "+ FrequentlyVisitedLoc.TEMPTABLE;

        ArrayList<FrequentlyVisitedLoc>  locationList=new ArrayList<FrequentlyVisitedLoc>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                FrequentlyVisitedLoc obj = new  FrequentlyVisitedLoc();
                obj.temp_visited_location_ID = cursor.getInt(cursor.getColumnIndex(FrequentlyVisitedLoc.TEMP_KEY_ID));
                obj.temp_place_name = cursor.getString(cursor.getColumnIndex(FrequentlyVisitedLoc.TEMP_KEY_place_name));
                obj.temp_address = cursor.getString(cursor.getColumnIndex(FrequentlyVisitedLoc.TEMP_KEY_Address));
                obj.temp_latitude = cursor.getDouble(cursor.getColumnIndex(FrequentlyVisitedLoc.TEMP_KEY_lat));
                obj.temp_longitude = cursor.getDouble(cursor.getColumnIndex(FrequentlyVisitedLoc.TEMP_KEY_long));
                obj.temp_user_saved_name = cursor.getString(cursor.getColumnIndex(FrequentlyVisitedLoc.TEMP_KEY_user_saved_name));

                locationList.add(obj);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return locationList;

    }


    public void deleteALoc(FavPlaceModel model){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(FrequentlyVisitedLoc.TABLE, FrequentlyVisitedLoc.KEY_Address + "=?", new String[] { model.address });
    }
}
