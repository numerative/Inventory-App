package com.michaelhat.inventoryapp;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.michaelhat.inventoryapp.InventoryContract.ProductEntry;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.empty_view)
    View emptyView;

    //Columns to fetch
    String[] projection = {
            ProductEntry._ID,
            ProductEntry.COLUMN_PRODUCT_NAME,
            ProductEntry.COLUMN_PRODUCT_PRICE,
            ProductEntry.COLUMN_PRODUCT_QUANTITY,
    };
    ProductCursorApdapter productCursorApdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ListView listView = findViewById(R.id.list_view);
        listView.setEmptyView(emptyView); //Setting Empty View

        readProduct();
        Cursor cursor = getContentResolver().query(ProductEntry.CONTENT_URI, projection, null,
                null, null);
        productCursorApdapter = new ProductCursorApdapter(this, cursor);
        listView.setAdapter(productCursorApdapter);

        //Setting onItemClickListener on each view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //New intent to open detailscreen
                Intent openDetailScreenActivity = new Intent(MainActivity.this,
                        DetailScreenActivity.class);
                Uri currentUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                //Set the Uri on the data field of the intent
                openDetailScreenActivity.setData(currentUri);
                startActivity(openDetailScreenActivity);
            }
        });
    }

    private void readProduct() {
        String sortOrder =
                ProductEntry.COLUMN_PRODUCT_NAME + " ASC";

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

        switch (item.getItemId()) {
            case (R.id.add_item):
                Intent openDetailScreenIntent = new Intent(MainActivity.this, DetailScreenActivity.class);
                startActivity(openDetailScreenIntent);
                break;
            case (R.id.delete_all):
                break;
            default:
        }

        return true;
    }
}
