package com.example.android.smartfridgeapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.List;

import dmax.dialog.SpotsDialog;

/**
 * Class Name: DeleteProduct
 * Description: The class removes the product from the fridge.
 * @author: Deniss Timofejevs B00066599
 */

public class DeleteProduct extends AppCompatActivity {

    static final String SCAN = "com.google.zxing.client.android.SCAN";
    EditText codeDisplay;
    String barcode = "";
    String userEmail = "";
    String objectId="";
    AlertDialog progressDialog;
    String contents = "";
    List<Product> products = null;
    String localObjectId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product);
        codeDisplay = (EditText) findViewById(R.id.codeDisplay);

             if(getIntent()!=null && getIntent().getStringExtra("barcode")!=null){
                 Toast.makeText(DeleteProduct.this, "This way might work", Toast.LENGTH_SHORT).show();
                 barcode = getIntent().getStringExtra("barcode");
                 userEmail = getIntent().getStringExtra("userMail");
                 objectId = getIntent().getStringExtra("objectId");
                 codeDisplay.setText(barcode);
             }
    }

    //when the button pressed to call scanner app and pass the barcode trough the intent
    public void scanButtonPressed(View view){
        try{
            Intent intent = new Intent (SCAN);
            // in.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        }catch (ActivityNotFoundException e) {

            showDialog(DeleteProduct.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    public void removeButtonPressed(View view){

        contents = codeDisplay.getText().toString().trim();

    if(connectionAvailable()) {

        progressDialog = new SpotsDialog(DeleteProduct.this, R.style.Custom);
        progressDialog.show();

       if ((!objectId.equals(""))) {
            //find product and remove it
           Backendless.Persistence.of(Product.class).findById(getIntent().getStringExtra("objectId"), new AsyncCallback<Product>() {
                @Override
                public void handleResponse(Product product) {

                        Backendless.Persistence.of(Product.class).remove(product, new AsyncCallback<Long>() {
                            @Override
                            public void handleResponse(Long aLong) {

                                Toast.makeText(DeleteProduct.this, "deleted syccessfuly!!!", Toast.LENGTH_SHORT).show();
                                codeDisplay.setText("");
                                DeleteProduct.this.finish();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {

                                Toast.makeText(DeleteProduct.this, "Sorry cant delete this product!" + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    Toast.makeText(DeleteProduct.this, "Sorry cant delete this product!" + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
     else if (contents.equals("")) {
           Toast.makeText(DeleteProduct.this, "You must enter product barcode before you can remowe it", Toast.LENGTH_SHORT).show();
           progressDialog.dismiss();
       } else {

           Toast.makeText(DeleteProduct.this, "The value of content is "+contents, Toast.LENGTH_SHORT).show();
            String whereClause = "productBarcode = '" + contents + "'";
            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause);

            Backendless.Persistence.of(Product.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Product>>() {
                @Override
                public void handleResponse(BackendlessCollection<Product> productBackendlessCollection) {

                    products = productBackendlessCollection.getData();

                    if(products.size()>0) {
                        localObjectId = products.get(0).getObjectId();

                        Intent intent = new Intent(DeleteProduct.this, SuccessDelete.class);

                        intent.putExtra("objectId", localObjectId);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(DeleteProduct.this, "No such a product with provided barcode please reenter the barcode!!!", Toast.LENGTH_SHORT).show();
                    }
                            progressDialog.dismiss();

                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {

                }
            });

         }//end of method

         }//end of connection check

        }

    //to show dialog if not found scanner application on mobile phone

    private Dialog showDialog(final Activity act, CharSequence title,
                              CharSequence message, CharSequence Yes, CharSequence No) {


        AlertDialog.Builder download = new AlertDialog.Builder(act);
        download.setTitle(title);
        download.setMessage(message);
        download.setPositiveButton(Yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i){

                //redirecting to download the barcode reader
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try{
                    act.startActivity(intent);
                }catch(ActivityNotFoundException error){

                }
            }
        });
        download.setNegativeButton(No, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        return download.show();
    }

    //if the code been recognized the to display it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if(requestCode ==0){
            if(resultCode == RESULT_OK){

                contents = intent.getStringExtra("SCAN_RESULT");

                // String format =  in.getStringExtra("SCAN_RESULT_FORMAT") ;
                Toast.makeText(this, contents, Toast.LENGTH_LONG).show();

                    codeDisplay.setText(contents);
            }
        }
    }

    //return boolean variable true if connection to internet is available
    private boolean connectionAvailable(){

        boolean connected = false;

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null){

            if(activeNetwork.getType() ==ConnectivityManager.TYPE_WIFI){  //connected to wi-fi network

                connected = true;
            }
            else if(activeNetwork.getType() ==ConnectivityManager.TYPE_MOBILE){ //connected to mobiile network

                connected = true;
            }
        }
        else{
            connected = false; //not connected to internet
        }

        return connected;
    }
}
