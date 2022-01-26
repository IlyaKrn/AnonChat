package com.example.anonchat.SQLiteDatabase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.*;
import android.content.ContentValues;

import com.example.anonchat.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import android.database.Cursor;

public class SQLiteDbManager {
    
    private Context context;
    private static SQLiteDbHelper myDbHelper;
    private static SQLiteDatabase db;
    
    public SQLiteDbManager(Context context){
        this.context = context;
        myDbHelper = new SQLiteDbHelper(context);
    }
    
    public static void openDb(){
        db = myDbHelper.getWritableDatabase();
    }
    public static void closeDb(){
        myDbHelper.close();
    }
    
    public void insertToDb(String name, String password){
        ContentValues cv = new ContentValues();
        cv.put(SQLiteDbConstants.NAME, name);
        cv.put(SQLiteDbConstants.PASSWORD, password);
        openDb();
        db.insert(SQLiteDbConstants.TABLE_NAME, null, cv);
        closeDb();
    }
    
    public ArrayList<User> getDbUserList(){
        openDb();
        ArrayList<User> list = new ArrayList<>();
        Cursor cursor = db.query(SQLiteDbConstants.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            @SuppressLint("Range") String n = cursor.getString(cursor.getColumnIndex(SQLiteDbConstants.NAME));
            @SuppressLint("Range") String p = cursor.getString(cursor.getColumnIndex(SQLiteDbConstants.PASSWORD));
            list.add(new User (n,p));
            
        }
        cursor.close();
        closeDb();
        return list;
    }
    public ArrayList<String> getDbUserNameList(){
        openDb();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = db.query(SQLiteDbConstants.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            @SuppressLint("Range") String n = cursor.getString(cursor.getColumnIndex(SQLiteDbConstants.NAME));
            list.add(n);

        }
        cursor.close();
        closeDb();
        return list;
    }
    public void deleteNote(String id) {
        openDb();
        db.delete(SQLiteDbConstants.TABLE_NAME, SQLiteDbConstants._ID + " = ?",
            new String[] { String.valueOf(id) });
        db.close();
    }
    public void clear() {
        openDb();
        db.delete(SQLiteDbConstants.TABLE_NAME, null, null);
        closeDb();
    }
}
