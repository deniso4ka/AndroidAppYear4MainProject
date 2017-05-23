package com.example.android.smartfridgeapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import dmax.dialog.SpotsDialog;

/**
 * Class Name: EditShoppingList
 * Description: The class adds or removes products from the shopping list.
 * @author: Deniss Timofejevs B00066599
 */

public class EditShoppingList extends AppCompatActivity {

    EditText productName, productBarcode;
    AlertDialog progressDialog;
    TextView questionDialogText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shopping_list);

        productName = (EditText) findViewById(R.id.name);
        productBarcode = (EditText) findViewById(R.id.barcode);

        //retreawing values from intent
        productName.setText(getIntent().getStringExtra("name"));
        productBarcode.setText(getIntent().getStringExtra("barcode"));
    }

    //if button pressed then read the fields and update the data in table
    public void buttonEdit(View view) {

        if (productName.getText().toString().trim().equals("") || productBarcode.getText().toString().trim().equals("")) {
            Toast.makeText(EditShoppingList.this, "Please dont leave the fields empty!!!", Toast.LENGTH_SHORT).show();
        } else {

            if (connectionAvailable()) {

                progressDialog = new SpotsDialog(EditShoppingList.this, R.style.Custom);
                progressDialog.show();
                //find specific product to allow edit it
                Backendless.Persistence.of(ShoppingList.class).findById(getIntent().getStringExtra("objectId"), new AsyncCallback<ShoppingList>() {
                    @Override
                    public void handleResponse(ShoppingList shoppingList) {

                        shoppingList.setProduct(productName.getText().toString().trim());
                        shoppingList.setBarcode(productBarcode.getText().toString().trim());

                        Backendless.Persistence.save(shoppingList, new AsyncCallback<ShoppingList>() {
                            @Override
                            public void handleResponse(ShoppingList shoppingList) {
                                Toast.makeText(EditShoppingList.this, "Data for product " + productName.getText().toString().trim() + " has been successfully updated!!! ", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                Toast.makeText(EditShoppingList.this, "Error" + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();

                            }
                        });
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {

                        Toast.makeText(EditShoppingList.this, "Error" + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();

                    }
                });


            } else {

                Toast.makeText(EditShoppingList.this, "Please make sure you are connected to the Internet", Toast.LENGTH_SHORT).show();

            }
        }
    }
    //if the button delete was pressed then to ask via dialog box if user really wants to delete product from the list
    public void buttonDelete(View v) {

        Toast.makeText(EditShoppingList.this, "Button pressed", Toast.LENGTH_SHORT).show();

        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.delete_question_confirm, null);

        questionDialogText = (TextView) view.findViewById(R.id.questionDialog);


        questionDialogText.setText("Are you sure you want to remove the product from shopping list ?");
        //
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Really?");
        dialog.setView(view);
        dialog.setIcon(R.drawable.question);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (connectionAvailable()) {

                    progressDialog = new SpotsDialog(EditShoppingList.this, R.style.Custom);
                    progressDialog.show();

                    Backendless.Persistence.of(ShoppingList.class).findById(getIntent().getStringExtra("objectId"), new AsyncCallback<ShoppingList>() {
                        @Override
                        public void handleResponse(ShoppingList shoppingList) {
                            Backendless.Persistence.of(ShoppingList.class).remove(shoppingList, new AsyncCallback<Long>() {
                                @Override
                                public void handleResponse(Long aLong) {
                                    Toast.makeText(EditShoppingList.this, "The product has been successfully removed !!!", Toast.LENGTH_SHORT).show();
                                    EditShoppingList.this.finish();
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    Toast.makeText(EditShoppingList.this, "Error " + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Toast.makeText(EditShoppingList.this, "Error " + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });

                } else {
                    Toast.makeText(EditShoppingList.this, "Please connect to the Internet first!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog.show();

    }

    //return boolean variable true if connection to internet is available
    private boolean connectionAvailable() {

        boolean connected = false;

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {  //connected to wi-fi network

                connected = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) { //connected to mobiile network

                connected = true;
            }
        } else {
            connected = false; //not connected to internet
        }

        return connected;
    }

}
