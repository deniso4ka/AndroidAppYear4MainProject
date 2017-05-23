package com.example.android.smartfridgeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Class Name: ShoppingListAdapter
 * Description: The adapter class which holds product details and
 * is used in the list view of shopping list activity.
 * @author: Deniss Timofejevs B00066599
 */

public class ShoppingListAdapter extends ArrayAdapter<ShoppingList> {


    private final Context context;
    private final List<ShoppingList> values;

    public ShoppingListAdapter(Context context, List<ShoppingList> list) {
        super(context, R.layout.shopping_list_rowlayout, list);
        this.context = context;
        this.values = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //inflating external layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.shopping_list_rowlayout, parent, false);

        TextView productName = (TextView) rowView.findViewById(R.id.productName);
        TextView productBarcode = (TextView) rowView.findViewById(R.id.prodBarcode);

        productName.setText(values.get(position).getProduct());
        productBarcode.setText(values.get(position).getBarcode());

        return rowView;
    }
}
