package com.example.bobly.inventory;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bobly.inventory.data.InventoryContract;


/**
 * Created by bobly on 26/12/17.
 */

public class BooksCursorAdapter extends CursorAdapter {
    public BooksCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameView = (TextView) view.findViewById(R.id.Product_Name);
        TextView priceView = (TextView) view.findViewById(R.id.Price);
        TextView quantityView = (TextView) view.findViewById(R.id.Quantity);
        Button saleButton = (Button) view.findViewById(R.id.sale);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_product);


        final int idColumnIndex = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry._ID));
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY);
        int imageColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRODUCT_IMAGE);


        String bookName = cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        final int bookQuantity = cursor.getInt(quantityColumnIndex);
        String bookImage = cursor.getString(imageColumnIndex);
        Uri productImageUri = Uri.parse(bookImage);


        nameView.setText(bookName);
        priceView.setText(bookPrice);
        quantityView.setText(String.valueOf(bookQuantity));
        imageView.setImageURI(productImageUri);

        saleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Uri productUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, idColumnIndex);
                saleBookQuantity(context, productUri, bookQuantity);
            }
        });

    }

    private void saleBookQuantity(Context context, Uri productUri, int currentQuantity) {
        int newQuantityValue = (currentQuantity >= 1) ? currentQuantity - 1 : 0;
        if (currentQuantity == 0) {
            Toast.makeText(context.getApplicationContext(), R.string.emptyQuantity, Toast.LENGTH_SHORT).show();
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, newQuantityValue);
        context.getContentResolver().update(productUri, contentValues, null, null);


    }
}
