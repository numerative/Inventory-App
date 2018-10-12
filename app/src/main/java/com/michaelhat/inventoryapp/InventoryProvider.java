package com.michaelhat.inventoryapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.michaelhat.inventoryapp.InventoryContract.ProductEntry;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class InventoryProvider extends ContentProvider {

    //URI Matcher Codes
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.PATH_PRODUCTS, PRODUCTS);

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    private InventoryDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //Get readable database
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor;
        //Check whether URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                //Query the products table.
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //Notifier for the listeners
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            cursor.setNotificationUri(Objects.requireNonNull(
                    getContext()).getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                assert contentValues != null;
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {
        try {
            contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            contentValues.getAsString(ProductEntry.COLUMN_SUPPLIER_NAME);
            contentValues.getAsString(ProductEntry.COLUMN_SUPPLIER_PHONE);
            contentValues.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
            contentValues.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        } catch (IllegalArgumentException e) {
            Log.e("All fields compulsory", e.toString());
        }
        //Get Writable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = database.insert(ProductEntry.TABLE_NAME, null, contentValues);
        //If the ID is -1, then the insertion failed.
        if (id == -1) {
            Log.e("Insert failed at row", uri.toString());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                //Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                //Delete a single row
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            //Notify all listeners
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
            }
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                Toast.makeText(getContext(), "Unsupported Operation", Toast.LENGTH_SHORT).show();
                return 0;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateParty(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Editing is not supported for " + uri);
        }
    }

    private int updateParty(Uri uri, ContentValues contentValues, String selection,
                            String[] selectionArgs) {
        //If there are no values to update, then don't try to update the database
        if (contentValues.size() == 0) {
            return 0;
        }
        //Check that the name is not null
        if (contentValues.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product Name required");
            }
        }
        //Get writeable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ProductEntry.COLUMN_PRODUCT_NAME, contentValues,
                selection, selectionArgs);

        if (rowsUpdated != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
            }
        }

        return rowsUpdated;
    }
}
