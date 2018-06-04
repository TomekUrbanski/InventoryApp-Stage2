package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.android.inventoryapp.data.InventoryDbHelper;

public class AddActivity extends AppCompatActivity {
    
    private EditText mProductName;
    private EditText mPrice;
    private EditText mQuantity;
    private EditText mSupplierName;
    private EditText mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);
        
        mProductName = findViewById(R.id.edit_product_name);
        mPrice = findViewById(R.id.edit_price);
        mQuantity = findViewById(R.id.edit_quantity);
        mSupplierName = findViewById(R.id.edit_supplier_name);
        mPhoneNumber = findViewById(R.id.edit_phone_number);
    }
  private void insertProduct(){
        
        String nameProduct = mProductName.getText().toString().trim();
        String priceString = mPrice.getText().toString().trim();
        int price = Integer.parseInt(priceString);
        String quantityString = mQuantity.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);
        String nameSupplier = mSupplierName.getText().toString().trim();
        String phuneString = mPhoneNumber.getText().toString().trim();
        int phone = Integer.parseInt(phuneString);

      InventoryDbHelper mDbHelper = new InventoryDbHelper(this);
      SQLiteDatabase db = mDbHelper.getWritableDatabase();

      ContentValues values = new ContentValues();
      values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameProduct);
      values.put(InventoryEntry.COLUMN_PRICE, price);
      values.put(InventoryEntry.COLUMN_QUANTITY, quantity);
      values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, nameSupplier);
      values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, phone);

      long newRowId = db.insert(InventoryEntry.TABLE_NAME, null, values);

      if (newRowId == -1) {
          Toast.makeText(this, "Error with saving product", Toast.LENGTH_SHORT).show();
      } else {
          Toast.makeText(this, "Product saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
      }
  }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                insertProduct();
                finish();
                return true;

            case android.R.id.home:

                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
