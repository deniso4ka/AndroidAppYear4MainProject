package com.example.android.smartfridgeapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Class Name: MyService
 * Description: Class sends the notification if the product expiry day is close or already passed.
 * @author: Deniss Timofejevs B00066599
 */

public class MyService extends IntentService {

    List<Product> product;
    List<Product> notifyProduct;

    public MyService() {
        super("Intent Service");

        product = new ArrayList<Product>();
        notifyProduct = new ArrayList<Product>();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //return all products for the user from product class(products basicly that are in the fridge)

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // Now use today date.
        c.add(Calendar.DATE, 1); // adding one day to it
        String todaysDayPlusOne = sdf.format(c.getTime());
        Log.i("today day plus one ", todaysDayPlusOne);

        String whereClause = "userMail = '" + intent.getStringExtra("user") + "' AND expiryDate <= '" + todaysDayPlusOne + "000000' ";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);

        //return the  data containing the product which are close to expite
        Backendless.Persistence.of(Product.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Product>>() {
            @Override
            public void handleResponse(BackendlessCollection<Product> productBackendlessCollection) {

                int size = productBackendlessCollection.getTotalObjects();

                product = productBackendlessCollection.getData();

                if (size > 0) {

                    //iterating throug the array list
                    for (Iterator<Product> iterator = product.listIterator(); iterator.hasNext(); ) {

                        Product p = iterator.next();

                        //checking if notification were set to yes then add the product to notification
                        if (p.getNotify() != null) {
                            if ((p.getNotify()).equals("yes")) {

                                notifyProduct.add(p);

                            }
                        }

                        //check if the list is not empty
                        if (notifyProduct != null) {

                           //creating notification
                            Intent repeatingActivity = new Intent(MyService.this, SendNotification.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("listProducts", (Serializable) notifyProduct);
                            repeatingActivity.putExtra("bundle", bundle);
                            NotificationManager notificationManager = (NotificationManager) getSystemService(IntentService.NOTIFICATION_SERVICE);
                            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, repeatingActivity, PendingIntent.FLAG_UPDATE_CURRENT);


                            NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(MyService.this)
                                    .setContentIntent(pendingIntent)
                                    .setSmallIcon(android.R.drawable.presence_busy)
                                    .setContentTitle("Consume Following Product(s)")
                                    .setContentText("Don't Forget to eat the following product(s)!!!")
                                    .setAutoCancel(true);

                            notificationManager.notify(0, builder.build());

                        }
                    }

                }//end of if statement

                Toast.makeText(MyService.this, "Something was found " + size, Toast.LENGTH_LONG).show();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

                Toast.makeText(MyService.this, "Error " + backendlessFault.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
}
