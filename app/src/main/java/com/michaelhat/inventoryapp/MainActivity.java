package com.michaelhat.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.michaelhat.inventoryapp.InventoryContract.ProductEntry;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    InventoryDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new InventoryDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Store a demo product
        saveProduct(db);
        readProduct();
    }

    private void saveProduct(SQLiteDatabase db) {
        ContentValues values1 = new ContentValues();
        //Item No. 1
        values1.put(ProductEntry.COLUMN_PRODUCT_NAME, "SOAP");
        values1.put(ProductEntry.COLUMN_PRODUCT_PRICE, 6);
        values1.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 100);
        values1.put(ProductEntry.COLUMN_SUPPLIER_NAME, "HUL");
        values1.put(ProductEntry.COLUMN_SUPPLIER_PHONE, "9876543210");

        db.insert(ProductEntry.TABLE_NAME, null, values1);


        ContentValues values2 = new ContentValues();
        //Item No. 1
        values2.put(ProductEntry.COLUMN_PRODUCT_NAME, "TOOTHBRUSH");
        values2.put(ProductEntry.COLUMN_PRODUCT_PRICE, 5);
        values2.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 2505);
        values2.put(ProductEntry.COLUMN_SUPPLIER_NAME, "ORALB");
        values2.put(ProductEntry.COLUMN_SUPPLIER_PHONE, "0123456789");

        db.insert(ProductEntry.TABLE_NAME, null, values2);
    }

    private void readProduct() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sortOrder =
                ProductEntry._ID + " DESC";

        Cursor cursor = db.query(ProductEntry.TABLE_NAME, null, null, null, null, null, sortOrder);

        while (cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME)
            );
            Log.i("Retrieved product", name);
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_view_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }
}
