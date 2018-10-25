package com.michaelhat.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

        long product_ID = cursor.getLong(cursor.getColumnIndexOrThrow(ProductEntry._ID));
        //Sale Button
        saleButton.setOnClickListener(new saleButtonClickListener(context, product_ID, productQuantity));

        //Set text
        productNameView.setText(productName);
        productPriceView.setText(productPrice);
        productQuantityView.setText(productQuantity);
    }

    //Custom OnClickListener
    public class saleButtonClickListener implements View.OnClickListener {
        Context context;
        Long product_ID;
        String productQuantity;

        public saleButtonClickListener(Context context, long product_ID, String productQuantity) {
            this.context = context;
            this.product_ID = product_ID;
            this.productQuantity = productQuantity;
        }

        @Override
        public void onClick(View view) {
            int afterSaleQuantity = Integer.valueOf(productQuantity) - 1;
            if (afterSaleQuantity >= 0) {
                ContentValues quantityAfterSaleForDb = new ContentValues();
                quantityAfterSaleForDb.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, afterSaleQuantity);
                Uri productUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, product_ID);
                context.getContentResolver().update(productUri, quantityAfterSaleForDb, null, null);
            } else {
                Toast.makeText(context, R.string.stock_zero, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
