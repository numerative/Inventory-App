package com.michaelhat.inventoryapp;

import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {
    public static final String CONTENT_AUTHORITY = "com.michaelhat.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";

    /*Inner class that defines the table contents of the inventory table */
    public static final class ProductEntry implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "products";

        //column id
        public static final String _ID = BaseColumns._ID;

        //Name of the Product
        public static final String COLUMN_PRODUCT_NAME = "name";
        //Price of the Product
        public static final String COLUMN_PRODUCT_PRICE = "price";
        //Quantity of the Product
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        //Name of the Supplier
        public static final String COLUMN_SUPPLIER_NAME = "supplier";
        //Phone Number of the Supplier
        public static final String COLUMN_SUPPLIER_PHONE = "phone";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
    }
}
