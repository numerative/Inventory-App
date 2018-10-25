package com.michaelhat.inventoryapp;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.michaelhat.inventoryapp.InventoryContract.ProductEntry;

import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
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
    //Current Product Name
    String productName;
    String productPrice;
    String productQuantity;
    String supplierName;
    String supplierPhone;

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
    @BindView(R.id.button_decrease_qty)
    Button minusButton;
    @BindView(R.id.button_increase_qty)
    Button plusButton;
    @BindView(R.id.call_button)
    Button callButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_screen);
        ButterKnife.bind(this); //Butterknife binding views
        Intent intent = getIntent();
        currentUri = intent.getData();
        //Checking whether current uri is null
        if (currentUri != null) {
            setTitle(R.string.title_edit_product);
            //Use content provider to populate fields
            getProductDetails();
            minusButton.setOnClickListener(new plusMinusButtonClickListener());
            plusButton.setOnClickListener(new plusMinusButtonClickListener());
            productNameEditText.setEnabled(false);
            priceEditText.setEnabled(false);
            supplierNameEditText.setEnabled(false);
            supplierPhoneEditText.setEnabled(false);
        } else {
            setTitle(R.string.title_add_product);
            disableQuantityButtons();
            invalidateOptionsMenu(); //To prevent delete option on a new entry.
        }

        callButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + Objects.requireNonNull(supplierPhoneEditText.getText()).toString()));
                startActivity(dialIntent);
            }
        });
    }

    private void disableQuantityButtons() {
        minusButton.setEnabled(false);
        plusButton.setEnabled(false);
    }

    private void getProductDetails() {
        Cursor cursor = getContentResolver().query(currentUri,
                projection, null, null, null);
        assert cursor != null;
        cursor.moveToNext();
        //Storing all values to variables
        productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME));
        productPrice = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE));
        productQuantity = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        supplierName = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_SUPPLIER_NAME));
        supplierPhone = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_SUPPLIER_PHONE));
        //Set Text
        productNameEditText.setText(productName);
        priceEditText.setText(productPrice);
        quantityEditText.setText(productQuantity);
        supplierNameEditText.setText(supplierName);
        supplierPhoneEditText.setText(supplierPhone);
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
                //emptyFieldCheck();
                extractStringsFromEditText();
                Boolean fieldsAreEmpty = validateFieldsForEmpty();
                Boolean fieldsAreNegative = false;
                if (!fieldsAreEmpty) {
                    fieldsAreNegative = validateFieldsForNegative();
                }
                if (fieldsAreEmpty || fieldsAreNegative) {
                    Toast.makeText(this, R.string.toast_errors_on_form, Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    saveProduct();
                    finish();
                }
                break;
            case R.id.edit_record:
                productNameEditText.setEnabled(true);
                priceEditText.setEnabled(true);
                supplierNameEditText.setEnabled(true);
                supplierPhoneEditText.setEnabled(true);
                break;
            case R.id.action_single_delete:
                showDeleteConfirmationDialog();
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(DetailScreenActivity.this);
                break;
        }
        return true;
    }

    private Boolean validateFieldsForNegative() {
        if (Integer.valueOf(productPrice) < 0) {
            priceEditText.setError(getString(R.string.positive_only));
            return true;
        } else if (Integer.valueOf(productQuantity) < 0) {
            quantityEditText.setError(getString(R.string.positive_only));
            return true;
        } else {
            return false;
        }
    }

    private Boolean validateFieldsForEmpty() {
        if (TextUtils.isEmpty(productName)) {
            productNameEditText.setError(getString(R.string.required));
            return true;
        } else if (TextUtils.isEmpty(productPrice)) {
            priceEditText.setError(getString(R.string.required));
            return true;
        } else if (TextUtils.isEmpty(productQuantity)) {
            quantityEditText.setError(getString(R.string.required));
            return true;
        } else if (TextUtils.isEmpty(supplierName)) {
            supplierNameEditText.setError(getString(R.string.required));
            return true;
        } else if (TextUtils.isEmpty(supplierPhone)) {
            supplierPhoneEditText.setError(getString(R.string.required));
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("NewApi")
    private void extractStringsFromEditText() {
        productName = Objects.requireNonNull(productNameEditText.getText()).toString().trim();
        productPrice = Objects.requireNonNull(priceEditText.getText()).toString().trim();
        productQuantity = Objects.requireNonNull(quantityEditText.getText()).toString().trim();
        supplierName = Objects.requireNonNull(supplierNameEditText.getText()).toString().trim();
        supplierPhone = Objects.requireNonNull(supplierPhoneEditText.getText()).toString().trim();
    }

    private void deleteProduct() {
        int rowsDeleted = getContentResolver().delete(currentUri, null, null);
        deleteProductToast(rowsDeleted);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_product_alertdialog_message, productName));
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteProduct();
                finish();
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

    private void deleteProductToast(int rowsDeleted) {
        switch (rowsDeleted) {
            case 1:
                Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                break;
            case 0:
                Toast.makeText(this, R.string.delete_error, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NewApi")
    private void saveProduct() {
        //Prepare the contentValues
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, Objects.requireNonNull(productNameEditText.getText()).toString().trim());
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, Objects.requireNonNull(priceEditText.getText()).toString().trim());
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, Objects.requireNonNull(quantityEditText.getText()).toString().trim());
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, Objects.requireNonNull(supplierPhoneEditText.getText()).toString().trim());
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, Objects.requireNonNull(supplierNameEditText.getText()).toString().trim());

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

    //Custom OnClickListener
    public class plusMinusButtonClickListener implements View.OnClickListener {

        plusMinusButtonClickListener() {
            //Empty Constructor
        }

        @Override
        public void onClick(View view) {
            String[] quantityProjection = {
                    ProductEntry.COLUMN_PRODUCT_QUANTITY
            };
            Cursor cursor = getContentResolver().query(currentUri, quantityProjection, null,
                    null, null);
            assert cursor != null;
            cursor.moveToNext();
            int productQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY));
            int afterSaleQuantity;

            if (view.getId() == R.id.button_decrease_qty) {
                afterSaleQuantity = productQuantity - 1;
            } else {
                afterSaleQuantity = productQuantity + 1;
            }

            if (afterSaleQuantity >= 0) {
                ContentValues quantityAfterSaleForDb = new ContentValues();
                quantityAfterSaleForDb.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, afterSaleQuantity);
                Uri productUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, ContentUris.parseId(currentUri));
                int rowsUpdated = getContentResolver().update(productUri, quantityAfterSaleForDb, null, null);
                if (rowsUpdated == 1) {
                    quantityEditText.setText(String.valueOf(afterSaleQuantity));
                }
            } else {
                Toast.makeText(DetailScreenActivity.this, R.string.stock_zero, Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        }
    }
}
