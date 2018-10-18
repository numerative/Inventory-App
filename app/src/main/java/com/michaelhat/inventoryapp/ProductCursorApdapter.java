package com.michaelhat.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        //Extract properties from cursor
        String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME));
        String productPrice = String.valueOf(
                cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE)));
        String productQuantity = String.valueOf(
                cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY)));

        //Set text
        productNameView.setText(productName);
        productPriceView.setText(productPrice);
        productQuantityView.setText(productQuantity);

    }
}
