package com.example.android.smartfridgeapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Class Name: ProductAdapter
 * Description: The adapter class which displays  the different details about products
 * in list view of product display activity.
 * @author: Deniss Timofejevs B00066599
 */

public class ProductsAdapter extends ArrayAdapter<Product> {

    private final Context context;
    private final List<Product> values;

    public ProductsAdapter(Context context, List<Product> list) {

        super(context, R.layout.product_rowlayout, list);
        this.context = context;
        this.values = list;
    }

    /*adapter for product list view inflation*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.product_rowlayout, parent, false);

        TextView productName = (TextView) rowView.findViewById(R.id.productName);
        TextView productBarcode = (TextView) rowView.findViewById(R.id.productBarcode);
        TextView productExpiryDate = (TextView) rowView.findViewById(R.id.productExpiryDate);

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
