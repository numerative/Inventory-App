package com.michaelhat.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.michaelhat.inventoryapp.InventoryContract.ProductEntry;

public class ProductCursorApdapter extends CursorAdapter {
    public ProductCursorApdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Inflating party_list_item layout
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Find fields to populate
        TextView productNameView = view.findViewById(R.id.list_item_item_name);
        TextView productPriceView = view.findViewById(R.id.list_item_price);
        TextView productQuantityView = view.findViewById(R.id.list_item_quantity);
        Button saleButton = view.findViewById(R.id.list_item_sale_button);

        //Extract properties from cursor
        String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME));
        String productPrice = String.valueOf(
                cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE)));
        String productQuantity = String.valueOf(
                cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY)));

        //Code not working
        //Reduce Quantity by 1
        int afterSaleQuantity = Integer.valueOf(productQuantity) - 1;
        Log.v("after sale", String.valueOf(afterSaleQuantity));
        final ContentValues quantityAfterSaleForDb = new ContentValues();
        quantityAfterSaleForDb.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, afterSaleQuantity);

/*        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri productUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, cursor.getLong(cursor.getColumnIndexOrThrow(ProductEntry._ID)));
                Log.v("Uri", String.valueOf(productUri));
                int rowsUpdated = context.getContentResolver().update(productUri, quantityAfterSaleForDb, null, null);
                Log.v("Rows updated", String.valueOf(rowsUpdated));
            }
        });*/

        //Set text
        productNameView.setText(productName);
        productPriceView.setText(productPrice);
        productQuantityView.setText(productQuantity);
    }
}
