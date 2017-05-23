package com.example.android.smartfridgeapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import dmax.dialog.SpotsDialog;

/**
 * Class Name: SuccessDelete
 * Description: The class removes product and displays the success toast.
 * @author: Deniss Timofejevs B00066599
 */


public class SuccessDelete extends AppCompatActivity {

    AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_delete);

        if (connectionAvailable()) {

            progressDialog = new SpotsDialog(SuccessDelete.this, R.style.Custom);
            progressDialog.show();


           /*after the object id has been found based on barcode then remove that product*/


            Backendless.Persistence.of(Product.class).findById(getIntent().getStringExtra("objectId"), new AsyncCallback<Product>() {
                @Override
                public void handleResponse(Product product) {
                    Backendless.Persistence.of(Product.class).remove(product, new AsyncCallback<Long>() {
                        @Override
                        public void handleResponse(Long aLong) {
                            Toast.makeText(SuccessDelete.this, "Product successfully deleted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SuccessDelete.this, DeleteProduct.class);
                            startActivity(intent);
                            SuccessDelete.this.finish();
                            progressDialog.dismiss();

                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Toast.makeText(SuccessDelete.this, "Product successfully deleted " + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {

                    Toast.makeText(SuccessDelete.this, "Product successfully deleted " + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }

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
