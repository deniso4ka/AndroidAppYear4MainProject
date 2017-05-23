package com.example.android.smartfridgeapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static android.graphics.BitmapFactory.decodeStream;

/**
 * Class Name: RecipesAdapter
 * Description: The adapter class which inflates recipe's details to the list view.
 * @author: Deniss Timofejevs B00066599
 */

public class RecipesAdapter extends ArrayAdapter<Recipe> {

    private final Context context;
    private final List<Recipe> values;
    private Bitmap bitmap;

    public RecipesAdapter(Context context, List<Recipe> list) {

        super(context, R.layout.recipe_one_row_layout, list);
        this.context = context;
        this.values = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.recipe_one_row_layout, parent, false);

        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView ingredient = (TextView) rowView.findViewById(R.id.ingredients);
        TextView productExpiryDate = (TextView) rowView.findViewById(R.id.expiryDate);

        title.setText(values.get(position).getTitle());
        ingredient.setText(values.get(position).getIngredients());

        return rowView;
    }
}

