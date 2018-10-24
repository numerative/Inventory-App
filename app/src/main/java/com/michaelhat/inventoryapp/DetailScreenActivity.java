package com.michaelhat.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.michaelhat.inventoryapp.InventoryContract.ProductEntry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailScreenActivity extends AppCompatActivity {

    //Columns to be fetched
    String[] projection = {
            ProductEntry._ID,
            ProductEntry.COLUMN_PRODUCT_NAME,
            ProductEntry.COLUMN_PRODUCT_PRICE,
            ProductEntry.COLUMN_PRODUCT_QUANTITY,
            ProductEntry.COLUMN_SUPPLIER_NAME,
            ProductEntry.COLUMN_SUPPLIER_PHONE
    };

    //Current Pet URI
    Uri currentUri;

    @BindView(R.id.product_name_edit_text)
    TextInputEditText productNameEditText;
    @BindView(R.id.price_edit_text)
    TextInputEditText priceEditText;
    @BindView(R.id.quantity_edit_text)
    TextInputEditText quantityEditText;
    @BindView(R.id.supplier_name_edit_text)
    TextInputEditText supplierNameEditText;
    @BindView(R.id.supplier_phone_edit_text)
    TextInputEditText supplierPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_screen);
        ButterKnife.bind(this); //Butterknife binding views
        Intent intent = getIntent();
        currentUri = intent.getData();
        //Checking whether current uri is null
        if (currentUri != null) {
            setTitle(R.string.title_edit_product);
            //Use content provider to populate fields
            getProductDetails();
        } else {
            setTitle(R.string.title_add_product);
            invalidateOptionsMenu(); //To prevent delete option on a new entry.
        }
    }

    private void getProductDetails() {
        Cursor cursor = getContentResolver().query(currentUri,
                projection, null, null, null);
        assert cursor != null;
        cursor.moveToNext();
        //Storing all values to variables
        String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME));
        String productPrice = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE));
        String productQuantity = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        String supplierName = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_SUPPLIER_NAME));
        String supplierPhone = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_SUPPLIER_PHONE));
        //Set Text
        productNameEditText.setText(productName);
        priceEditText.setText(productPrice);
        quantityEditText.setText(productQuantity);
        supplierNameEditText.setText(supplierName);
        supplierPhoneEditText.setText(supplierPhone);

        Log.v("Product Name", cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME)));
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentUri == null) {
            MenuItem deleteMenuItem = menu.findItem(R.id.action_single_delete);
            MenuItem editMenuItem = menu.findItem(R.id.edit_record);
            deleteMenuItem.setVisible(false);
            editMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                break;
            case R.id.edit_record:
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(DetailScreenActivity.this);
                break;
        }
        return true;
    }

    private void saveProduct() {
        //Prepare the contentValues
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, productNameEditText.getText().toString().trim());
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceEditText.getText().toString().trim());
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityEditText.getText().toString().trim());
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneEditText.getText().toString().trim());
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierNameEditText.getText().toString().trim());

        if (currentUri != null) { //if Product to be edited
            int rowsUpdated = getContentResolver().update(currentUri, values, null, null);
            displayEditToast(rowsUpdated);
        } else { //else it is a new product to be saved
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            displaySaveToast(newUri);
        }
    }

    private void displaySaveToast(Uri newUri) {
        if (newUri != null) {
            Toast.makeText(this, R.string.toast_save_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.toast_save_failure, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayEditToast(int rowsUpdated) {
        switch (rowsUpdated) {
            case 0:
                Toast.makeText(this, R.string.toast_edit_error, Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(this, R.string.toast_edit_saved, Toast.LENGTH_SHORT).show();
        }
    }
}
