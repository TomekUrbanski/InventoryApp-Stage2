package com.example.android.inventoryapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.android.inventoryapp.data.InventoryDbHelper;

public class MainActivity extends AppCompatActivity {

    private InventoryDbHelper mDbHelper;
    private RelativeLayout specificBar;
    private TextView displayView;
    private EditText specificId;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });
        FloatingActionButton confirmId = findViewById(R.id.accept_number_id);
        confirmId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                specificId = findViewById(R.id.edit_specific_id);
                String idString = specificId.getText().toString();

                if (idString.isEmpty() || idString.length() == 0 || idString.equals("") || idString == null) {
                    Toast.makeText(getBaseContext(), "Error with finding a product", Toast.LENGTH_SHORT).show();
                } else {
                    displaySpecificInfo();
                }
            }
        });

        mDbHelper = new InventoryDbHelper(this);

    }

    private void displayDatabaseInfo() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME};

        Cursor cursor = db.query(
                InventoryEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        displayView = findViewById(R.id.text_view_pet);

        try {

            displayView.setText("The inventory table contains " + cursor.getCount() + " items.\n\n");
            displayView.append(InventoryEntry._ID + " - " +
                    InventoryEntry.COLUMN_PRODUCT_NAME + "\n");

            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);

                displayView.append(("\n" + currentID + " - " +
                        currentName));
            }
        } finally {
            cursor.close();
        }
    }

    private void displaySpecificInfo() {

        specificId = findViewById(R.id.edit_specific_id);
        String idString = specificId.getText().toString();
        int id = Integer.parseInt(idString);


        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE};

        Cursor cursor = db.query(
                InventoryEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        if (id > cursor.getCount()) {
            Toast.makeText(getBaseContext(), "Error with finding a product", Toast.LENGTH_SHORT).show();
        } else {

            displayView = findViewById(R.id.text_view_pet);
            displayView.setText("");

            try {

                int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
                int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
                int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
                int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
                int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
                int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);

                cursor.move(id);

                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                int currentPhone = cursor.getInt(phoneColumnIndex);

                displayView.append(("\n" + currentName + "\n" +
                        "Price - " + currentPrice + " $" + "\n" +
                        "Available - " + currentQuantity + "\n\n" +
                        currentSupplier + " \n" +
                        currentPhone));

            } finally {
                cursor.close();
            }

        }
        cursor.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        specificBar = findViewById(R.id.specific_bar);
        displayView = findViewById(R.id.text_view_pet);

        switch (item.getItemId()) {

            case R.id.show_whole_data:
                specificBar.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm.isAcceptingText()) {
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
                displayDatabaseInfo();
                return true;

            case R.id.show_specific:
                displayView.clearComposingText();
                specificBar.setVisibility(View.VISIBLE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
