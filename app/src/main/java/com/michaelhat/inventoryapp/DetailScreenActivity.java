package com.michaelhat.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.material.textfield.TextInputEditText;
import com.michaelhat.inventoryapp.InventoryContract.ProductEntry;

import androidx.appcompat.app.AppCompatActivity;
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
}
