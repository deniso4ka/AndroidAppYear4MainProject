package com.example.android.smartfridgeapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class Name: RecipesDisplay
 * Description: The class displays the recipes.
 * @author: Deniss Timofejevs B00066599
 */

public class RecipesDisplay extends AppCompatActivity {

    List<Product> products;
    List<String> productsNames;
    ListView listRecipes;
    String productsAddNames = "";
    List<Recipe> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_display);

        listRecipes = (ListView) findViewById(R.id.recipesListView);
        recipeList = new ArrayList<Recipe>();
        products = new ArrayList<Product>();
        productsNames = new ArrayList<String>();

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        products = (ArrayList<Product>) bundle.getSerializable("listProducts");

        //iterate through the list of products and add the products names to the separate list
        for (Iterator<Product> iterator = products.listIterator(); iterator.hasNext(); ) {

            Product p = iterator.next();
            productsNames.add(p.getProductName());

        }

        //iterate through the product names and add all elements to the string

        for (Iterator<String> iterator = productsNames.listIterator(); iterator.hasNext(); ) {

            String name = iterator.next();

            if (productsAddNames.equals("")) {
                productsAddNames = productsAddNames + name;
            } else {
                productsAddNames = productsAddNames + "," + name;
            }
        }
        //download the recipes based on product(s) from fridge
        DownloadTask task = new DownloadTask();
        task.execute("http://www.recipepuppy.com/api/?i=" + productsAddNames + "");
        //if specific precipe is clicked then display more information about specific recipe
        listRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(RecipesDisplay.this, SingleRecipeDisplay.class);

                intent.putExtra("title", recipeList.get(position).getTitle());
                intent.putExtra("href", recipeList.get(position).getHref());
                intent.putExtra("ingredients", recipeList.get(position).getIngredients());
                intent.putExtra("thumbnail", recipeList.get(position).getPicture());
                RecipesDisplay.this.finish();
                startActivity(intent);
            }
        });

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();


                while (data != -1) {
                    char current = (char) data;

                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find anything", Toast.LENGTH_LONG);
            }

            return null;


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                String message = "";
                JSONObject jsonObject = new JSONObject(result);
                JSONArray array = jsonObject.getJSONArray("results");

                for (int i = 0; i < array.length(); i++) {

                    JSONObject object = array.getJSONObject(i);
                    String title = object.getString("title");
                    title = title.replaceAll("\\s+","");
                    String href = object.getString("href");
                    String ingrediens = object.getString("ingredients");
                    ingrediens = ingrediens.replaceAll("\\s+","");
                    String image = object.getString("thumbnail");
                    recipeList.add(new Recipe(title, href, ingrediens, image));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            RecipesAdapter adapter = new RecipesAdapter(RecipesDisplay.this, recipeList);
            listRecipes.setAdapter(adapter);
        }

    }

}
