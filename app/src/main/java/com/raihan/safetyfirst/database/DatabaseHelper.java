package com.raihan.safetyfirst.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;

import com.raihan.safetyfirst.util.Model;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "safety.db";
    public static final String TABLE_NAME = "Person";
    public static final String NAME = "namen";
    public static final String EMAILN = "emailn";
    public static final String PHONEN = "phonen";
    public static final String FLAGN = "flagn";
    public static final String ID = "id";
    private SQLiteDatabase database;


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
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("namen", name);
            values.put("emailn", email);
            values.put("phonen", phone);
            values.put("flagn", flag);
            r = db.insert(TABLE_NAME, null, values);
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (r == 0) {
            return false;
        } else {
            return true;
        }

    }

    public Cursor fetch() {
        database = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = this.database.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.ID, DatabaseHelper.NAME, DatabaseHelper.EMAILN, DatabaseHelper.PHONEN, DatabaseHelper.FLAGN}, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursor;
    }

    public Cursor searchData(String id) {
        Cursor cursor = null;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //String qry = "SELECT * FROM "+TABLE_NAME+" WHERE ID="+id;
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID=" + id, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }

}
