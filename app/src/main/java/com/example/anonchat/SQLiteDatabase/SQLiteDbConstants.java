package com.example.anonchat.SQLiteDatabase;

public class SQLiteDbConstants {
    public static final String TABLE_NAME ="table_name";
    public static final String _ID = "_id";
    public static final String NAME ="name";
    public static final String PASSWORD ="password";
    public static final String DB_NAME = "my_db.db";
    public static final int DB_VERSION = 1;
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String TABLE_STRUCTURE = 
    "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + 
    NAME + " TEXT,"+ PASSWORD + " TEXT)";
    
    
   
    
    
    
    
    
}
