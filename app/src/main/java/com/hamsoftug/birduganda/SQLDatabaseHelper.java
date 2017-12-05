package com.hamsoftug.birduganda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 10/1/2016.
 */

public class SQLDatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "SQLDatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "birdsugandadb";

    // table name
    private static final String TABLE_USER_PROFILE = "userProfile";

    // Common column names
    private static final String KEY_ID = "key_id";
    private static final String U_NAME = "user_name";
    private static final String U_EMAIL = "user_email";
    private static final String U_NUM = "user_number";
    private static final String U_PW = "user_pw";

    //create statement
    private static final String CREATE_TABLE_EMAILS = "CREATE TABLE "
            + TABLE_USER_PROFILE + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + U_NAME + " TEXT,"
            + U_NUM + " TEXT,"
            + U_PW + " TEXT,"
            + U_EMAIL + " TEXT"
            + ")";

    public SQLDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_EMAILS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROFILE);

        onCreate(db);

    }

    public long saveProfile(String email, String name, String u_number, String u_pw) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(U_EMAIL, email);
        values.put(U_NAME, name);
        values.put(U_NUM, u_number);
        values.put(U_PW, u_pw);
        // insert row
        long todo_id = db.insert(TABLE_USER_PROFILE, null, values);

        Log.e(LOG, "Inserting into " + TABLE_USER_PROFILE);

        return todo_id;
    }

    public void removeProfile()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE from "+ TABLE_USER_PROFILE);
        db.execSQL("VACUUM");
        db.close();
    }

    public List<String> getProfile(){
        List<String> _profile = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM " + TABLE_USER_PROFILE + " ORDER BY "
                + KEY_ID + " DESC";
        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                String names = c.getString(c.getColumnIndex(U_NAME));
                String email = c.getString(c.getColumnIndex(U_EMAIL));
                String pw = c.getString(c.getColumnIndex(U_PW));
                String u_num = c.getString(c.getColumnIndex(U_NUM));

                _profile.add(u_num+"--"+email+"--"+pw+"--"+names);

            } while (c.moveToNext());
        }

        return _profile;
    }

}

