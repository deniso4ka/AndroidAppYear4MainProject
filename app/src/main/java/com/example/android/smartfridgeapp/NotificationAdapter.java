package com.example.android.smartfridgeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Class Name: NotificationAdapter
 * Description: Adapter class inflates the different product details to the list view of notification class.
 * @author: Deniss Timofejevs B00066599
 */


public class NotificationAdapter extends ArrayAdapter<Product> {


    private final Context context;
    private final List<Product> values;

    public NotificationAdapter(Context context, List<Product> list) {

        super(context, R.layout.notification_one_row_layout, list);
        this.context = context;
        this.values = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.notification_one_row_layout, parent, false);

        TextView productName = (TextView) rowView.findViewById(R.id.productNam);
        TextView productBarcode = (TextView) rowView.findViewById(R.id.productBar);
        TextView productExpiryDate = (TextView) rowView.findViewById(R.id.expiryDate);

        productName.setText(values.get(position).getProductName());
        productBarcode.setText("Barcode " + values.get(position).getProductBarcode());

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        if (values.get(position).getExpiryDate() != null) {
            String dateToDisplay = dateFormat.format(values.get(position).getExpiryDate());

            productExpiryDate.setText(dateToDisplay);
        }

        return rowView;
    }
}


