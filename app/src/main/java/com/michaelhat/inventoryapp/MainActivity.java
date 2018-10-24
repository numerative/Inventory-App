package com.michaelhat.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.michaelhat.inventoryapp.InventoryContract.ProductEntry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.list_view)
    ListView listView;
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
        listView.setEmptyView(emptyView); //Setting Empty View

        productCursorApdapter = new ProductCursorApdapter(this, null);
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

        //Start the loader
        getLoaderManager().initLoader(0, null, this);
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
                showDeleteConfirmationDialog();
                break;
            default:
        }

        return true;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_product_alertdialog_message, getString(R.string.all_products)));
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAllProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAllProduct() {
        int numberOfRowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null,
                null);
        deleteAllToast(numberOfRowsDeleted);
    }

    private void deleteAllToast(int numberOfRowsDeleted) {
        switch (numberOfRowsDeleted) {
            case 0:
                Toast.makeText(this, R.string.toast_delete_all_fail, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, R.string.toast_delete_all_success, Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(
                MainActivity.this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        //Swap cursor till a cursor is available
        if (cursor != null && cursor.getCount() > 0) {
            //Updating the Cursor
            productCursorApdapter.swapCursor(cursor);
        } else {
            productCursorApdapter.swapCursor(null);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        productCursorApdapter.swapCursor(null);
    }
}
