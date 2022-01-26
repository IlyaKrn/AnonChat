package com.example.anonchat.SQLiteDatabase;

import android.content.Context;
import android.database.sqlite.*;

public class SQLiteDbHelper extends SQLiteOpenHelper{
    
    public SQLiteDbHelper(Context context){
        super(context, SQLiteDbConstants.DB_NAME, null, SQLiteDbConstants.DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQLiteDbConstants.TABLE_STRUCTURE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(SQLiteDbConstants.DROP_TABLE);
        onCreate(db);
    }
}
