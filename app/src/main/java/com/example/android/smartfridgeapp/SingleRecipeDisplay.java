package com.example.android.smartfridgeapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

import static android.graphics.BitmapFactory.*;

/**
 * Class Name: SingleRecipeDisplay
 * Description: The class displays the information about single product that had been clicked.
 * @author: Deniss Timofejevs B00066599
 */

public class SingleRecipeDisplay extends AppCompatActivity {

    TextView title;
    TextView ingredients;
    ImageView image;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_recipe_display);
        title = (TextView) findViewById(R.id.titleOfRecipe);
        ingredients = (TextView) findViewById(R.id.recipeIngredients);
        image = (ImageView) findViewById(R.id.imageOfRecipe);

        title.setText(getIntent().getStringExtra("title"));
        ingredients.setText(getIntent().getStringExtra("ingredients"));

        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();


        if (networkInfo.isConnected()) {

            new DownloadAnImage().execute(getIntent().getStringExtra("thumbnail"));

        } else {
            Toast.makeText(SingleRecipeDisplay.this, "No network connectio available please try again later", Toast.LENGTH_SHORT).show();
        }

    }

    //download the image for specific recipe
    private class DownloadAnImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            String url = urls[0];

            try {

                bitmap = decodeStream(new URL(url).openConnection().getInputStream());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap returnedBitmap) {

            image.setImageBitmap(returnedBitmap);
        }
    }

    public void redirectToWebPage(View view) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getIntent().getStringExtra("href")));
        startActivity(intent);
    }


}
