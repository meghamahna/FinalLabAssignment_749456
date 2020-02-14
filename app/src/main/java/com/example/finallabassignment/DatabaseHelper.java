package com.example.finallabassignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FavouritePlaceDatabase";
    private static final int DATABASE_VERSION =1;
    private static final String TABLE_NAME = "Places";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_DATE = "date";


    public DatabaseHelper(@Nullable Context context) {
        //cursor factory is when you are using your own custom cursor

        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID +" INTEGER NOT NULL CONSTRAINT places_pk PRIMARY KEY AUTOINCREMENT," +
                COLUMN_ADDRESS + " varchar(200) ," +
                COLUMN_LATITUDE + " double NOT NULL," +
                COLUMN_LONGITUDE + " double NOT NULL," +
                COLUMN_DATE + " varchar(200) NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        db.execSQL(sql);
        onCreate(db);
    }

    boolean add(String address,Double latitude ,Double longitude, String date){

        //INORDER OT INSERT ITEM INTO DATABAASE
        //WE NEED A WRITABLE DATABASE
        //THIS METHOS RETURN A SQL DATABASE
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        //WE NEED TO DEFINE A CONTENT INSTANCE

        ContentValues cv = new ContentValues();

        //THE FIRST ARGUMENT OF THE PUT METHOD IS THE COLUMN NAME AND THE SECOND VALUE IS THE SHOWN AS BELOW

        cv.put(COLUMN_ADDRESS, address);
        cv.put(COLUMN_LATITUDE, String.valueOf(latitude));
        cv.put(COLUMN_LONGITUDE, String.valueOf(longitude));
        cv.put(COLUMN_DATE, date);

        //insert method returns row number if the inseriton is successfully and -1 if the unsuccessfull

        return   sqLiteDatabase.insert(TABLE_NAME, null, cv) != -1;


    }

    Cursor getAllPlaces(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    boolean updatePlace(int id, String address, Double latitude, Double longitude, String date){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_ADDRESS, address);
        cv.put(COLUMN_LATITUDE, String.valueOf(latitude));
        cv.put(COLUMN_LONGITUDE, String.valueOf(longitude));
        cv.put(COLUMN_ID, date);

        //this method returns the number of rows effected

        return sqLiteDatabase.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;

    }

    boolean deletePlaces(int id){
        SQLiteDatabase sqLiteDatabase  = getWritableDatabase();

        //the delete method returns the  number of rows effected
        return sqLiteDatabase.delete(TABLE_NAME, COLUMN_ID +"=?", new String[]{String.valueOf(id)}) > 0;
    }
}
