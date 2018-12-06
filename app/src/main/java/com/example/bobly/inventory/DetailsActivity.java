package com.example.bobly.inventory;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bobly.inventory.data.InventoryContract;

/**
 * Created by bobly on 26/12/17.
 */

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EXISTING_BOOK_LOADER = 0;
    private int mQuantity;
    // configure uri
    private Uri mImageUri;
    private Uri mCurrentBookUri;
    // configure EditText and image
    private EditText mNameBook;
    private EditText mSupplierPhone;
    private ImageView mImage;
    private EditText mPrice;
    private EditText mSupplierName;
    private EditText mSupplierEmail;
    private EditText mQuantityEdit;
    //configure Buttons
    private Button mIncreaseButton;
    private Button mDecreaseButton;
    private Button mImportImage;
    private Button mOrder;
    //variable tell us when happen change
    private boolean mBookHasChanged = false;

    // a touch event is dispatched to a view.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };
    // boolean variable for require field
    boolean RequiredValues = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.ADD_BOOK));
            invalidateOptionsMenu();

        } else {
            setTitle(getString(R.string.EDIT_BOOK));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this).forceLoad();

        }
        //assign variable to location in xml file
        mNameBook = (EditText) findViewById(R.id.edit_name);
        mPrice = (EditText) findViewById(R.id.edit_price);
        mQuantityEdit = (EditText) findViewById(R.id.edit_quantity);
        mSupplierName = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierEmail = (EditText) findViewById(R.id.edit_supplier_email);
        mSupplierPhone = (EditText) findViewById(R.id.edit_phone_number);
        mImage = (ImageView) findViewById(R.id.addOrEdit);
        mIncreaseButton = (Button) findViewById(R.id.increase);
        mDecreaseButton = (Button) findViewById(R.id.decrease);
        mImportImage = (Button) findViewById(R.id.importImage);
        mOrder = (Button) findViewById(R.id.order);


        mNameBook.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mQuantityEdit.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierEmail.setOnTouchListener(mTouchListener);
        mSupplierPhone.setOnTouchListener(mTouchListener);

        //button to increase Quantity
        mIncreaseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                increaseQuality(view);
            }
        });
        //button to decrease Quantity
        mDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseQuality(view);
            }
        });
        //button to import image from gallery
        mImportImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectorImage();
                mBookHasChanged = true;
            }
        });
        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderBook();
            }
        });


    }

    // methods for decrease and increase Buttons
    public void increaseQuality(View view) {
        mQuantity++;
        showQuantity();
    }

    public void decreaseQuality(View view) {
        if (mQuantity == 0) {
            Toast.makeText(this, "Sorry the limit of Quantity is 0", Toast.LENGTH_SHORT).show();

        } else {
            mQuantity--;
            showQuantity();
        }
    }

    public void showQuantity() {
        mQuantityEdit.setText(String.valueOf(mQuantity));
    }


    // permission to pick image from gallery
    public void selectorImage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        openSelector();
    }

    private void openSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openSelector();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mImageUri = data.getData();
                mImage.setImageURI(mImageUri);
                mImage.invalidate();
            }
        }
    }

    // setup order button to send order by Email
    public void orderBook() {
        Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:" + mSupplierEmail.getText().toString().trim()));
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "I Chooses Book: " +
                mNameBook.getText().toString().trim() +
                " " + mPrice.getText().toString().trim());
        String message = "Please send to me the book  " +
                mNameBook.getText().toString().trim() +
                " " +
                mPrice.getText().toString().trim() + "." +
                "\n" +
                "In WEEkEND" +
                "\n" +
                "Thank You" + "\n";
        intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        startActivity(intent);
    }

    // save Insert Book we add
    public boolean saveInsert() {
        int quantity;
        String nameBook = mNameBook.getText().toString().trim();
        String priceBook = mPrice.getText().toString().trim();
        String quantityBook = mQuantityEdit.getText().toString().trim();
        String supplierNB = mSupplierName.getText().toString().trim();
        String supplierEB = mSupplierEmail.getText().toString().trim();
        String supplierPB = mSupplierPhone.getText().toString().trim();

        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(nameBook) &&
                TextUtils.isEmpty(priceBook) &&
                TextUtils.isEmpty(quantityBook) &&
                TextUtils.isEmpty(supplierNB) &&
                TextUtils.isEmpty(supplierEB) &&
                TextUtils.isEmpty(supplierPB) &&
                mImageUri == null) {
            RequiredValues = true;
            return RequiredValues;

        }
        ContentValues values = new ContentValues();
        if (TextUtils.isEmpty(nameBook)) {
            Toast.makeText(this, getString(R.string.Require), Toast.LENGTH_SHORT).show();
            return RequiredValues;
        } else {
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME, nameBook);
        }
        if (TextUtils.isEmpty(priceBook)) {
            Toast.makeText(this, getString(R.string.Require1), Toast.LENGTH_SHORT).show();
            return RequiredValues;
        } else {
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE, priceBook);
        }
        if (TextUtils.isEmpty(quantityBook)) {
            Toast.makeText(this, getString(R.string.Require2), Toast.LENGTH_SHORT).show();
            return RequiredValues;
        } else {
            // If the quantity is not provided by the user, don't try to parse the string into an
            // integer value. Use 0 by default.
            quantity = Integer.parseInt(quantityBook);
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantity);
        }
        if (mImageUri == null) {
            Toast.makeText(this, getString(R.string.Require3), Toast.LENGTH_SHORT).show();
            return RequiredValues;
        } else {
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRODUCT_IMAGE, mImageUri.toString());
        }
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NAME, supplierNB);
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_EMAIL, supplierEB);
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_PHONE, supplierPB);

        if (mCurrentBookUri == null) {
            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.insertFailed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.insertSuccessful),
                        Toast.LENGTH_SHORT).show();
            }

        } else {
            // Otherwise this is an EXISTING product, so update the product with content URI: mCurrentProductUri
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.updateFailed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.updateSuccessful),
                        Toast.LENGTH_SHORT).show();
            }

        }

        RequiredValues = true;
        return RequiredValues;
    }

    // create options menu in Details layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_data);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.save_all:
                // Save product to database
                saveInsert();
                if (RequiredValues == true) {
                    // Exit activity
                    finish();
                }
                return true;
            case R.id.delete_data:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRODUCT_IMAGE,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_EMAIL,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_PHONE
        };
        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            int imageColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRODUCT_IMAGE);
            int supplierNCI = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NAME);
            int supplierECI = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_EMAIL);
            int supplierPCI = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_PHONE);

            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String image = cursor.getString(imageColumnIndex);
            String sName = cursor.getString(supplierNCI);
            String sEmail = cursor.getString(supplierECI);
            String sPhone = cursor.getString(supplierPCI);
            mQuantity = quantity;
            mImageUri = Uri.parse(image);

            mNameBook.setText(name);
            mPrice.setText(price);
            mSupplierName.setText(sName);
            mSupplierEmail.setText(sEmail);
            mSupplierPhone.setText(sPhone);
            mImage.setImageURI(mImageUri);
            mQuantityEdit.setText(Integer.toString(quantity));


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameBook.setText("");
        mPrice.setText("");
        mQuantityEdit.setText("");
        mSupplierName.setText("");
        mSupplierEmail.setText("");
        mSupplierPhone.setText("");
        mImage.setImageResource(R.drawable.add);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.change);
        builder.setPositiveButton(R.string.descared, discardButtonClickListener);
        builder.setNegativeButton(R.string.editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deleteItem);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        // Only perform the delete if this is an existing product.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.deletebook1),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.deletebook),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }


}
