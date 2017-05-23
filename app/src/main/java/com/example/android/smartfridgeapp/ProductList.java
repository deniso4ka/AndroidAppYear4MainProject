package com.example.android.smartfridgeapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.List;

/**
 * Class Name: productList
 * Description: The class displays the products
 * stored in the fridge with help of ProductAdapter class.
 * @author: Deniss Timofejevs B00066599
 */

public class ProductList extends AppCompatActivity {


    TextView logedInEmail;
    ListView productsList;
    ProgressBar progressBar;
    AlertDialog progressDialog;
    List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        //text view initialisation
        logedInEmail = (TextView) findViewById(R.id.logedInEmail);

        productsList = (ListView) findViewById(R.id.productsList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        logedInEmail.setText(getIntent().getStringExtra("user"));

        //set on item click listener
        productsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(ProductList.this, DeleteProduct.class);
                intent.putExtra("barcode", products.get(position).getProductBarcode());
                intent.putExtra("userMail", products.get(position).getUserMail());
                intent.putExtra("objectId", products.get(position).getObjectId());

                startActivity(intent);
            }
        });

    }

    public void createProduct(View view) {

        Intent intent = new Intent(ProductList.this, AddTheProduct.class);
        intent.putExtra("user", getIntent().getStringExtra("user"));
        startActivity(intent);

        Toast.makeText(ProductList.this, "Works redirection from Product list to add product", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadProduct();
    }

    public void loadProduct() {

        progressBar.setVisibility(View.VISIBLE);

        if (products != null) {

            products.clear();
        }
        //finds the product user has in frisge and populate them in list view using adapter class
        String whereClause = "userMail = '" + getIntent().getStringExtra("user") + "'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);

        Backendless.Persistence.of(Product.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Product>>() {
            @Override
            public void handleResponse(BackendlessCollection<Product> productBackendlessCollection) {
                products = productBackendlessCollection.getData();

                //calling adapter class and pass list values and contect to the list adapter class
                ProductsAdapter adapter = new ProductsAdapter(ProductList.this, products);
                productsList.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(ProductList.this, "Error: " + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
