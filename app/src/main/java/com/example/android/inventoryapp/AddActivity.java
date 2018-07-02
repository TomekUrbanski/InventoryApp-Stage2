package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class AddActivity extends AppCompatActivity implements
        android.app.LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EXISTING_INVENTORY_LOADER = 0;

    private Uri mCurrentInventoryUri;

    private EditText mProductName;
    private EditText mPrice;
    private EditText mQuantity;
    private EditText mSupplierName;
    private EditText mPhoneNumber;


    private boolean mItemHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();
        final Button call = findViewById(R.id.call);
        final Button plus = findViewById(R.id.plus);
        final Button minus = findViewById(R.id.minus);

        if (mCurrentInventoryUri == null) {
            setTitle(getString(R.string.add_title));
            plus.setVisibility(View.GONE);
            minus.setVisibility(View.GONE);
            invalidateOptionsMenu();
        } else {

            setTitle(getString(R.string.edit_title));
            call.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        mProductName = findViewById(R.id.edit_product_name);
        mProductName.setOnTouchListener(mTouchListener);
        mPrice = findViewById(R.id.edit_price);
        mPrice.setOnTouchListener(mTouchListener);
        mQuantity = findViewById(R.id.edit_quantity);
        mQuantity.setOnTouchListener(mTouchListener);
        mSupplierName = findViewById(R.id.edit_supplier_name);
        mSupplierName.setOnTouchListener(mTouchListener);
        mPhoneNumber = findViewById(R.id.edit_phone_number);
        mPhoneNumber.setOnTouchListener(mTouchListener);

        plus.setOnTouchListener(mTouchListener);
        minus.setOnTouchListener(mTouchListener);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = mQuantity.getText().toString().trim();
                int quantity = Integer.parseInt(quantityString);
                if (quantity < 99999) {
                    quantity = Integer.parseInt(quantityString);
                } else {
                    return;
                }

                mQuantity.setText(String.valueOf(quantity + 1));
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = mQuantity.getText().toString().trim();
                int quantity = Integer.parseInt(quantityString);
                if (quantity > 0) {
                    quantity = Integer.parseInt(quantityString);
                } else {
                    return;
                }

                mQuantity.setText(String.valueOf(quantity - 1));
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String number = mPhoneNumber.getText().toString().trim();
                Uri call = Uri.parse("tel:" + number);
                Intent intent = new Intent(Intent.ACTION_DIAL, call);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });


    }

    private void saveItem() {

        String nameProduct = mProductName.getText().toString().trim();

        String priceString = mPrice.getText().toString().trim();
        int price = 0;

        String quantityString = mQuantity.getText().toString().trim();
        int quantity = 0;

        String nameSupplier = mSupplierName.getText().toString().trim();

        String phoneString = mPhoneNumber.getText().toString().trim();
        int phone = 0;


        if (mCurrentInventoryUri == null && TextUtils.isEmpty(nameProduct) || TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(nameSupplier) || TextUtils.isEmpty(phoneString)) {
            Toast.makeText(this, R.string.empty_fields_alert, Toast.LENGTH_LONG).show();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameProduct);

        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(InventoryEntry.COLUMN_PRICE, price);


        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(InventoryEntry.COLUMN_QUANTITY, quantity);

        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, nameSupplier);

        if (!TextUtils.isEmpty(phoneString)) {
            phone = Integer.parseInt(phoneString);
        }
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, phone);

        if (mCurrentInventoryUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, R.string.insert_item_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.insert_item_successful, Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, R.string.insert_item_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.insert_item_successful, Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentInventoryUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                saveItem();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:

                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(AddActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE};


        return new CursorLoader(this,
                mCurrentInventoryUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);

            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            int phone = cursor.getInt(phoneColumnIndex);


            mProductName.setText(name);
            mPrice.setText(Integer.toString(price));
            mQuantity.setText(Integer.toString(quantity));
            mSupplierName.setText(supplier);
            mPhoneNumber.setText(Integer.toString(phone));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mProductName.setText("");
        mPrice.setText("");
        mQuantity.setText("");
        mSupplierName.setText("");
        mPhoneNumber.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteItem() {

        if (mCurrentInventoryUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.insert_item_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.insert_item_successful, Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }
}
