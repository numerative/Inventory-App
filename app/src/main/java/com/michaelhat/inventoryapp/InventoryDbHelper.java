package com.michaelhat.inventoryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.michaelhat.inventoryapp.InventoryContract.StockEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {
    //If you change the database schema, you must implement the database version
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Inventory.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " +
                    StockEntry.TABLE_NAME + " (" +
                    StockEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    StockEntry.COLUMN_PRODUCT_NAME + TEXT_TYPE + COMMA_SEP +
                    StockEntry.COLUMN_PRODUCT_PRICE + INTEGER_TYPE + COMMA_SEP +
                    StockEntry.COLUMN_PRODUCT_QUANTITY + INTEGER_TYPE + COMMA_SEP +
                    StockEntry.COLUMN_SUPPLIER_NAME + TEXT_TYPE + COMMA_SEP +
                    StockEntry.COLUMN_SUPPLIER_PHONE + TEXT_TYPE + " );";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS" + StockEntry.TABLE_NAME;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES); //Discard old when database changes
        onCreate(sqLiteDatabase); //Then create new database
    }

    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }

}
