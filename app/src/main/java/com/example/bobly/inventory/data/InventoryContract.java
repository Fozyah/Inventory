package com.example.bobly.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by bobly on 21/12/17.
 */

public class InventoryContract {

    public InventoryContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.bobly.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOK = "book";

    public static final class InventoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOK);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOK;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOK;

        public final static String TABLE_NAME = "book";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_INVENTORY_NAME = "name";
        public final static String COLUMN_INVENTORY_PRICE = "price";
        public final static String COLUMN_INVENTORY_QUANTITY = "quantity";
        public final static String COLUMN_INVENTORY_PRODUCT_IMAGE = "image";
        public final static String COLUMN_INVENTORY_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_INVENTORY_SUPPLIER_EMAIL = "supplier_email";
        public final static String COLUMN_INVENTORY_SUPPLIER_PHONE = "supplier_phone";


        public static final String CREATE_TABLE_INVENTORY = "CREATE TABLE " +
                InventoryContract.InventoryEntry.TABLE_NAME + "(" +
                InventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME + " TEXT NOT NULL, " +
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE + " TEXT NOT NULL, " +
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRODUCT_IMAGE + " TEXT NOT NULL, " +
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NAME + " TEXT NOT NULL, " +
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_EMAIL + " TEXT NOT NULL, " +
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_PHONE + " TEXT NOT NULL );";

    }
}
