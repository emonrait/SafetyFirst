package com.raihan.safetyfirst.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "safety.db";
    public static final String TABLE_NAME = "Person";
    public static final String NAME = "namen";
    public static final String EMAILN = "emailn";
    public static final String PHONEN = "phonen";
    public static final String FLAGN = "flagn";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable;
        createTable = "CREATE TABLE " + TABLE_NAME +
                " ( ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME + " TEXT ," +
                EMAILN + " TEXT ," +
                PHONEN + " TEXT ," +
                FLAGN + " TEXT ) ";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean insertData(String name, String email, String phone, String flag) {
        long r = 0;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("namen", name);
        values.put("emailn", email);
        values.put("phonen", phone);
        values.put("flagn", flag);
        r = db.insert(TABLE_NAME, null, values);
        db.close();
        if (r != 0) {
            return false;
        } else {
            return true;
        }

    }
}
