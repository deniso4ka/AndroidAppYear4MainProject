package com.example.android.smartfridgeapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dmax.dialog.SpotsDialog;

/**
 * Class Name: DisplayProductToBuy
 * Description: The class is comparing the shopping list and the products
 * stored in the fridge and displays only products
 * that are left to buy.
 * @author: Deniss Timofejevs B00066599
 */

public class DisplayProductsToBuy extends AppCompatActivity {


    List<Product> localProductArray;
    List<ShoppingList> localShoppingListArray;
    List<ProductsToBuy> localProductsToBuy;
    ListView listOfProducts;
    int shoppingListSize;
    int productListSize;
    AlertDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_products_to_buy);
        listOfProducts = (ListView) findViewById(R.id.list);
        localProductArray = new ArrayList<Product>();
        localShoppingListArray = new ArrayList<ShoppingList>();
        localProductsToBuy = new ArrayList<ProductsToBuy>();


        //set list view listener , when item is selected from list add product activity will be called and passed the clicked item values
        listOfProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(DisplayProductsToBuy.this, AddTheProduct.class);
                //pass some values through intent
                intent.putExtra("product", localShoppingListArray.get(position).getProduct());
                intent.putExtra("productBarcode", localShoppingListArray.get(position).getBarcode());
                intent.putExtra("user", localShoppingListArray.get(position).getUserMail());
                intent.putExtra("objectid", localShoppingListArray.get(position).getObjectId());
                DisplayProductsToBuy.this.finish();
                startActivity(intent);

                localShoppingListArray.remove(position);
                Toast.makeText(DisplayProductsToBuy.this, "position " + position, Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //checking if the internet connection is available
        if (connectionAvailable()) {
            //on resume will be called methods which generates the products to buy list
            loadData();
        }
    }

    public void loadData() {

        //start displaying progress bar
        progressDialog = new SpotsDialog(DisplayProductsToBuy.this, R.style.Custom);
        progressDialog.show();

        //if the list is not empty then to empty it
        if (localShoppingListArray != null) {
            localShoppingListArray.clear();
        }

        productsRetrieve();

    }

    //find the product for specific user
    public void productsRetrieve() {

        //return all products for the user from product class(products basicly that are in the fridge)
        String wherClause = "userMail = '" + getIntent().getStringExtra("user") + "'";
        BackendlessDataQuery datQuery = new BackendlessDataQuery();
        datQuery.setWhereClause(wherClause);

        //find and returns the list of the products from backendless
        //the products which are in fridge
        Backendless.Persistence.of(Product.class).find(datQuery, new AsyncCallback<BackendlessCollection<Product>>() {

            @Override
            //if everything went ok while processing data query then call handle response method and pass data to it
            public void handleResponse(BackendlessCollection<Product> productBackendlessCollection) {

                //assigning the data received to the list
                localProductArray = productBackendlessCollection.getData();
                //checking the number of objects received from backendless
                productListSize = productBackendlessCollection.getTotalObjects();

                //calling the second method
                shoppingListRetrieve();
            }

            //if something went wrong while data has been queried then to handle the exemption and display the tost message with the error type
            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(DisplayProductsToBuy.this, "Error " + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void shoppingListRetrieve() {

        //return all products for the user from product class(products basicly that are in the fridge)
        String whereClause = "userMail = '" + getIntent().getStringExtra("user") + "'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);

        //return the  data containing the shopping list
        Backendless.Persistence.of(ShoppingList.class).find(dataQuery, new AsyncCallback<BackendlessCollection<ShoppingList>>() {

            @Override
            //if everything went ok while processing data query then call handle response method and pass data to it
            public void handleResponse(BackendlessCollection<ShoppingList> shoppingListBackendlessCollection) {

                localShoppingListArray = shoppingListBackendlessCollection.getData();

                shoppingListSize = shoppingListBackendlessCollection.getTotalObjects();

                //if shopping list is not empty the call method to compare the product in the fridge with product list and display products to buy list
                if (shoppingListSize > 0) {

                    compareTheProductsInFridgeWithShoppingList();

                } else {
                    Toast.makeText(DisplayProductsToBuy.this, "There is nothing to display!!!", Toast.LENGTH_SHORT).show();
                    DisplayProductsToBuy.this.finish();
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(DisplayProductsToBuy.this, "Error " + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void compareTheProductsInFridgeWithShoppingList() {


        // creating the list of products to buy based on products from Shopping List
        int count = 0;

        Toast.makeText(DisplayProductsToBuy.this, "the leng of shopping list is " + localShoppingListArray.size(), Toast.LENGTH_LONG).show();
        Toast.makeText(DisplayProductsToBuy.this, "the leng of product  list is " + localProductArray.size(), Toast.LENGTH_LONG).show();

        /// might need to be replaced to the iterator in th future
        for (Product p : localProductArray) {


            for (Iterator<ShoppingList> iter = localShoppingListArray.listIterator(); iter.hasNext(); ) {

                ShoppingList s = iter.next();

                //checking if  products in shopping list does not repeat with product that are in fridge, if they are equal then removing from list to buy
                if ((s.getProduct()).equals(p.getProductName())) {

                    iter.remove();
                    Toast.makeText(DisplayProductsToBuy.this, "position" + count, Toast.LENGTH_SHORT).show();
                    Toast.makeText(DisplayProductsToBuy.this, "There are equal " + p.getProductName() + "  is indeed equal to " + s.getProduct(), Toast.LENGTH_SHORT).show();

                }

            }

            //start new product check
            count = 0;

        }

        //populate the list of the product left to buy
        ProductsToBuyAdapter adapter = new ProductsToBuyAdapter(DisplayProductsToBuy.this, localShoppingListArray);
        listOfProducts.setAdapter(adapter);
        //dismiss the progress bar
        progressDialog.dismiss();

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
