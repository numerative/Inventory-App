package com.michaelhat.inventoryapp;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.michaelhat.inventoryapp.InventoryContract.ProductEntry;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    //Columns to fetch
    String[] projection = {
            ProductEntry._ID,
            ProductEntry.COLUMN_PRODUCT_NAME,
            ProductEntry.COLUMN_PRODUCT_PRICE,
            ProductEntry.COLUMN_PRODUCT_QUANTITY,
            ProductEntry.COLUMN_SUPPLIER_NAME,
            ProductEntry.COLUMN_SUPPLIER_PHONE
    };

    InventoryDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readProduct();
    }

    private void readProduct() {
        String sortOrder =
                ProductEntry._ID + " DESC";

        Cursor cursor = getContentResolver().query(ProductEntry.CONTENT_URI, projection, null,
                null, sortOrder);

        assert cursor != null;
        while (cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME)
            );
            Log.i("Retrieved product", name);
            Log.i("Cursor Size", String.valueOf(cursor.getCount()));
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
