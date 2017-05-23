package com.example.android.smartfridgeapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dmax.dialog.SpotsDialog;

/**
 * Class Name: MenuActivity
 * Description: The class redirects to the add product activity,
 * display products activity,create edit shopping list activity, display products to buy activity,
 * or display recipes activity.
 * @author: Deniss Timofejevs B00066599
 */

public class MenuActivity extends AppCompatActivity {

    AlertDialog progressDialog;
    List<Product> listProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        listProducts = new ArrayList<Product>();

        //creating calendar instance and setting the time for each day to repeat
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

        //getting the service
        Intent intentService = new Intent(getApplicationContext(), MyService.class);
        intentService.putExtra("user", getIntent().getStringExtra("user"));

        PendingIntent pendIntent = PendingIntent.getService(this, 0, intentService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmManager.INTERVAL_DAY, pendIntent);
    }
        //add product activity call
    public void addProduct(View view) {

        Intent intent = new Intent(MenuActivity.this, AddTheProduct.class);
        intent.putExtra("user", getIntent().getStringExtra("user"));
        startActivity(intent);
    }
        // display product list activity
    public void displayProductsList(View view) {

        Intent intent = new Intent(MenuActivity.this, ProductList.class);
        intent.putExtra("user", getIntent().getStringExtra("user"));
        startActivity(intent);
    }
        //remove product activity
    public void removeProduct(View view) {
        Intent intent = new Intent(MenuActivity.this, DeleteProduct.class);
        intent.putExtra("user", getIntent().getStringExtra("user"));
        startActivity(intent);
    }
        //shopping list activity
    public void createEditList(View view) {
        Intent intent = new Intent(MenuActivity.this, CreateEditShoppingList.class);
        intent.putExtra("user", getIntent().getStringExtra("user"));
        startActivity(intent);
    }
    //products to buy list activity
    public void displayProductsToBuy(View view) {
        Intent intent = new Intent(MenuActivity.this, DisplayProductsToBuy.class);
        intent.putExtra("user", getIntent().getStringExtra("user"));
        startActivity(intent);
    }
    //display recipes
    public void displayReceipes(View view) {
        //check the products are stored in user fridge and pass the product list to the recipe activity
        String whereClause = "userMail = '" + getIntent().getStringExtra("user") + "'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);

        Backendless.Persistence.of(Product.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Product>>() {
            @Override
            public void handleResponse(BackendlessCollection<Product> productBackendlessCollection) {

                listProducts = productBackendlessCollection.getData();

                Intent intent = new Intent(MenuActivity.this, RecipesDisplay.class);
                Bundle bundle = new Bundle();
                intent.putExtra("user", getIntent().getStringExtra("user"));
                bundle.putSerializable("listProducts", (Serializable) listProducts);
                intent.putExtra("bundle", bundle);
                startActivity(intent);

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
    }
    //if log out clicked then to exit application
    public void exitClicked(View view) {
        progressDialog = new SpotsDialog(MenuActivity.this, R.style.Custom);
        progressDialog.show();
        Backendless.UserService.logout(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void aVoid) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
                MenuActivity.this.finish();
                progressDialog.dismiss();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(MenuActivity.this, "Error " + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
