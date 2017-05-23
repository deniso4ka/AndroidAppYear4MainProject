package com.example.android.smartfridgeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Class Name: SendNotification
 * Description: The class which displays notification about expired product.
 * @author: Deniss Timofejevs B00066599
 */

public class SendNotification extends AppCompatActivity {


    TextView displayText;
    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_notification_layout);


        Bundle dataBundle = getIntent().getExtras();

        displayText = (TextView) findViewById(R.id.displayInfo);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        List<Product> products = (ArrayList<Product>) bundle.getSerializable("listProducts");

        for (Iterator<Product> iterator = products.listIterator(); iterator.hasNext(); ) {

            Product p = iterator.next();

        }


        listView = (ListView) findViewById(R.id.listView2);

        NotificationAdapter adapter = new NotificationAdapter(SendNotification.this, products);
        listView.setAdapter(adapter);

        displayText.setText("Consume Following Products");

    }

}
