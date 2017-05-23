package com.example.android.smartfridgeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Class Name: ProductToBuyAdapter
 * Description: The adapter class which inflates product's
 * details to the list view  of products to buy activity.
 * @author: Deniss Timofejevs B00066599
 */

public class ProductsToBuyAdapter extends ArrayAdapter<ShoppingList> {

    private final Context context;
    private final List<ShoppingList> values;


    public ProductsToBuyAdapter(Context context, List<ShoppingList> list) {
        super(context, R.layout.product_to_buy_rowlayout, list);

        this.context = context;
        this.values = list;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.product_to_buy_rowlayout, parent, false);

        TextView productName = (TextView) rowView.findViewById(R.id.productNameView);
        TextView productBarcode = (TextView) rowView.findViewById(R.id.productBarcodeView);
        productName.setText(values.get(position).getProduct());
        productBarcode.setText(values.get(position).getBarcode());

        return rowView;
    }
}
