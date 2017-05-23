package com.example.android.smartfridgeapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

/**
 * Class Name: CreateEditShoppingList
 * Description: The class creates a product(s) shopping list.
 * @author: Deniss Timofejevs B00066599
 */

public class CreateEditShoppingList extends AppCompatActivity {


    EditText productField;
    AlertDialog progressDialog;
    EditText barcodeField;
    List<Stock> stockList;
    String productNamePicked;
    String defaultBarcode = "5011026383836";
    boolean hasBeenFound = false;
    List<ShoppingList> shoppingList;
    ListView listView;
    static final String SCAN = "com.google.zxing.client.android.SCAN";
    Intent voiceIntent;
    List<ExternalFile> listObjects = new ArrayList<ExternalFile>();
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_shopping_list);
        productField = (EditText) findViewById(R.id.productField);
        barcodeField = (EditText) findViewById(R.id.barcodeField);
        listView = (ListView) findViewById(R.id.listView);

        //adding voice recognition
        voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK);

        //end of voice recognition
        if (listObjects.size() < 1) {
            InputStream inputStream = getResources().openRawResource(R.raw.barcodes);
            CSVReader csv = new CSVReader(inputStream);
            listObjects = csv.read();
        }

        //if the item from the list has been clicked then redirect user to edit or delete product from product list activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(CreateEditShoppingList.this, EditShoppingList.class);
                intent.putExtra("name", shoppingList.get(position).getProduct());
                intent.putExtra("barcode", shoppingList.get(position).getBarcode());
                intent.putExtra("userMail", shoppingList.get(position).getUserMail());
                intent.putExtra("objectId", shoppingList.get(position).getObjectId());
                startActivity(intent);
            }
        });

        barcodeField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                //checking if user stopped typing the barode and and the user went to the second text field
                if (!b) {
                    Toast.makeText(CreateEditShoppingList.this, "The barcode is " + barcodeField.getText().toString().trim(), Toast.LENGTH_SHORT).show();

                    progressDialog = new SpotsDialog(CreateEditShoppingList.this, R.style.Custom);
                    progressDialog.show();

                    if (connectionAvailable()) {
                        //find the product based on their barcode
                        String whereClause = "productBarcode = '" + barcodeField.getText().toString().trim() + "'";
                        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                        dataQuery.setWhereClause(whereClause);

                        Backendless.Persistence.of(Stock.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Stock>>() {
                            @Override
                            public void handleResponse(BackendlessCollection<Stock> stockBackendlessCollection) {

                                int itemsFound = stockBackendlessCollection.getTotalObjects();
                                //if there exist item with given barcode
                                if (itemsFound > 0) {

                                    stockList = stockBackendlessCollection.getData();
                                    productNamePicked = stockList.get(0).getProductName();
                                    productField.setText(productNamePicked);
                                    progressDialog.dismiss();
                                } else {
                                    // if not then check it online
                                    DownloadJsonFile download = new DownloadJsonFile();
                                    defaultBarcode = barcodeField.getText().toString().trim();
                                    download.execute("http://www.searchupc.com/handlers/upcsearch.ashx?request_type=3&access_token=53F9D66C-ACA2-46A4-83CC-F243AA930D80&upc=" + defaultBarcode + "");
                                    //if no items has been found from barcode database website

                                    if (hasBeenFound) {

                                        progressDialog.dismiss();
                                    }

                                    if (hasBeenFound == false) {

                                        String checkProductField = productField.getText().toString().trim();

                                        if (checkProductField.equals("")) {

                                            Toast.makeText(CreateEditShoppingList.this, "Please enter product", Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                    }

                                }

                            }

                            //if nothing has been found the to prompt user to enter the product name manually
                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                Toast.makeText(CreateEditShoppingList.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }
        });

        //text field listener, when product will be scanned the listener picks up the barcode and checking in database for it's name
        barcodeField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() >= 5) {

                    ProductCheckFromFileList product = new ProductCheckFromFileList(listObjects, barcodeField.getText().toString().trim());
                    String productN = product.check();

                     productField.setText(productN);
                }

            }
        });


    }

    /*adding product to the shopping list */
    public void addProductToTheList(View view) {

        //checking the internet connection
        if (connectionAvailable()) {

            //if field is left empty the to notify user
            if ((productField.getText().toString().trim().equals("")) || (barcodeField.getText().toString().trim().equals(""))) {
                Toast.makeText(CreateEditShoppingList.this, "Please enter the product name and barcode!!!", Toast.LENGTH_SHORT).show();
            } else {

                ShoppingList shopList = new ShoppingList();
                shopList.setBarcode(barcodeField.getText().toString().trim());
                String productNameToLoverCase = (productField.getText().toString().trim()).toLowerCase();
                shopList.setProduct(productNameToLoverCase);
                shopList.setUserMail(getIntent().getStringExtra("user"));

                progressDialog = new SpotsDialog(CreateEditShoppingList.this, R.style.Custom);
                progressDialog.show();

                Backendless.Persistence.save(shopList, new AsyncCallback<ShoppingList>() {
                    @Override
                    public void handleResponse(ShoppingList shoppingList) {

                        Toast.makeText(CreateEditShoppingList.this, "The product has been succesfully added to the shopping list", Toast.LENGTH_SHORT).show();
                        //CreateEditShoppingList.this.finish();
                        barcodeField.setText("");
                        productField.setText("");
                        loadData();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(CreateEditShoppingList.this, "Error" + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        } else {
            Toast.makeText(CreateEditShoppingList.this, "Please connect first to the Internet", Toast.LENGTH_SHORT).show();
        }
    }

    //lounch the scanner if scanner not found to allow user to install it from android market
    public void scanLouncher(View view) {
        try {
            Intent intent = new Intent(SCAN);
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException e) {

            showDialog(CreateEditShoppingList.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    //to show dialog if not found scanner application on mobile phone
    private Dialog showDialog(final Activity act, CharSequence title,
                              CharSequence message, CharSequence Yes, CharSequence No) {

        AlertDialog.Builder download = new AlertDialog.Builder(act);
        download.setTitle(title);
        download.setMessage(message);
        download.setPositiveButton(Yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                //redirecting to download the barcode reader
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException activityNotFoundException) {
                }
            }
        });
        download.setNegativeButton(No, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        return download.show();
    }

    //if the code been recognized the to display it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                barcodeField.setText(contents);
                ProductCheckFromFileList product = new ProductCheckFromFileList(listObjects, barcodeField.getText().toString().trim());
                String productN = product.check();

                productField.setText(productN);

                Toast.makeText(CreateEditShoppingList.this, contents, Toast.LENGTH_SHORT).show();
                barcodeField.setText(contents);
            }
        }

        //voice recognision if voice has been send then to process it
        if (requestCode == 50 && resultCode == RESULT_OK) {
            List<String> results = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String text = results.get(0);

            textToSpeech.speak("You are adding to shopping list " + text, TextToSpeech.QUEUE_FLUSH, null, null);

            productField.setText(text);
        }
    }

    //check tihe product name in online UPC barcode storage
    public class DownloadJsonFile extends AsyncTask<String, Void, String> {
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
                // String data = jsonObject.getString("0");
                if (jsonObject.length() > 0) {
                    JSONObject json2 = jsonObject.getJSONObject("0");
                    String product = json2.getString("productname");

                    if (product.equals(" ")) {
                        hasBeenFound = false;

                    } else {
                        hasBeenFound = true;
                        productField.setText(product);
                        hasBeenFound = false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadData() {
        //clear the list if it is not empty and fill with a new data, this way we are avoiding the duplicated data

        if (shoppingList != null) {
            shoppingList.clear();

        }
        //making the query which will return shopping list for specific user
        String whereClause = "userMail = '" + getIntent().getStringExtra("user") + "'";
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause(whereClause);

        Backendless.Persistence.of(ShoppingList.class).find(query, new AsyncCallback<BackendlessCollection<ShoppingList>>() {
            @Override
            public void handleResponse(BackendlessCollection<ShoppingList> shoppingListBackendlessCollection) {

                shoppingList = shoppingListBackendlessCollection.getData();

                ShoppingListAdapter adapter = new ShoppingListAdapter(CreateEditShoppingList.this, shoppingList);
                listView.setAdapter(adapter);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(CreateEditShoppingList.this, "Error" + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //every time when user returns to the same activity on resume method has been callaed
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    public void productVoice(View view) {

        voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please say product Name");
        startActivityForResult(voiceIntent, 50);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
    }

    //return boolean variable true if connection to internet is available
    private boolean connectionAvailable() {

        boolean connected = false;

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {  //connected to wi-fi network
                connected = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) { //connected to mobiile network
                connected = true;
            }
        } else {
            connected = false; //not connected to internet
        }
        return connected;
    }
}
