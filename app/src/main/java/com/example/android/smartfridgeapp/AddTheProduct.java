package com.example.android.smartfridgeapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

/**
 * Class Name: AddTheProduct
 * Description: The class adds the product(s) to the cloud as well as
 * finds the product name based on the product barcode.
 * @author: Deniss Timofejevs B00066599
 */


public class AddTheProduct extends AppCompatActivity {

    Calendar calendar;
    EditText productBarCode;
    EditText productName;
    EditText expiryDate;
    int expiryday = 0;
    int month = 0;
    int year = 0;
    int tempexpiryday = 0;
    int tempmonth = 0;
    int tempyear = 0;
    AlertDialog progressDialog;
    CheckBox checkBox;
    String notification = "";
    List<Stock> stockList = null;
    String productNamePicked = "";
    Stock stock;
    boolean hasBeenFound = false;
    String defaultBarcode = "5011026383836";
    int responseCode;
    Intent voiceIntent;
    TextToSpeech textToSpeech;
    String[] listOfWords;
    List<Day> days = new ArrayList<Day>();
    List<Month> months = new ArrayList<Month>();
    List<ExternalFile> listObjects = new ArrayList<ExternalFile>();

    static final String scanner = "com.google.zxing.client.android.SCAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_the_product);


       /* Filling up the array list with product barcodes and their names if it is empty*/

        if (listObjects.size() < 1) {
            InputStream inputStream = getResources().openRawResource(R.raw.barcodes);
            CSVReader csv = new CSVReader(inputStream);
            listObjects = csv.read();
        }

        calendar = Calendar.getInstance();

        //text fields initialisation
        productBarCode = (EditText) findViewById(R.id.productBarCode);
        productName = (EditText) findViewById(R.id.productName);
        expiryDate = (EditText) findViewById(R.id.expiryDate);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        //adding voice recognision
        voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK);




        //checking if intent is passing values from displayproductstobuy class list
        if (getIntent() != null && getIntent().getStringExtra("product") != null) {

            productName.setText(getIntent().getStringExtra("product"));
            productBarCode.setText(getIntent().getStringExtra("productBarcode"));
        }

        productBarCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                //checking if user stopped typing the barode and the user mowed to product name field
                if (!b) {
                    Toast.makeText(AddTheProduct.this, "The barcode is " + productBarCode.getText().toString().trim(), Toast.LENGTH_SHORT).show();

                    progressDialog = new SpotsDialog(AddTheProduct.this, R.style.Custom);
                    progressDialog.show();

                    // if there is internet connection available then looking for product name from database based on barcode
                    if (connectionAvailable()) {
                        String whereClause = "productBarcode = '" + productBarCode.getText().toString().trim() + "'";
                        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                        dataQuery.setWhereClause(whereClause);

                        Backendless.Persistence.of(Stock.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Stock>>() {
                            @Override
                            public void handleResponse(BackendlessCollection<Stock> stockBackendlessCollection) {

                                int itemsFound = stockBackendlessCollection.getTotalObjects();

                                //if there exist item with given barcode then show it in text field
                                if (itemsFound > 0) {

                                    stockList = stockBackendlessCollection.getData();
                                    productNamePicked = stockList.get(0).getProductName();
                                    productName.setText(productNamePicked);
                                    progressDialog.dismiss();
                                } else {
                                    //if product name was not found in the database then to go online and check the product in UPC database
                                    DownloadJsonFile download = new DownloadJsonFile();

                                    defaultBarcode = productBarCode.getText().toString().trim();
                                    download.execute("http://www.searchupc.com/handlers/upcsearch.ashx?request_type=3&access_token=53F9D66C-ACA2-46A4-83CC-F243AA930D80&upc=" + defaultBarcode + "");

                                    //if no items has been found from barcode database website
                                    if (hasBeenFound) {
                                        progressDialog.dismiss();
                                    }

                                    if (hasBeenFound == false) {

                                        String checkProductField = productName.getText().toString().trim();

                                        if (checkProductField.equals("")) {

                                            Toast.makeText(AddTheProduct.this, "Please enter product", Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                    }
                                }
                            }

                            //if nothing was found the to prompt user to enter the product name manually
                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                Toast.makeText(AddTheProduct.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }
        });
        //text field listener, when product will be scanned the listener picks up the barcode and checking in database for it's name
        productBarCode.addTextChangedListener(new TextWatcher() {

                                                  @Override
                                                  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                  }

                                                  @Override
                                                  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                  }

                                                  @Override
                                                  public void afterTextChanged(Editable editable) {

                                                      if (editable.length() >= 5) {
                                                          ProductCheckFromFileList product = new ProductCheckFromFileList(listObjects, productBarCode.getText().toString().trim());
                                                          String productN = product.check();
                                                          productName.setText(productN);
                                                      }

                                                  }
                                              });

}
    //when the button pressed to call scanner app and pass the barcode through the intent
    public void scanBarCode(View v) {
        try {
            Intent intent = new Intent(scanner);
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException e) {

            showDialog(AddTheProduct.this, "No Scanner Found", "Download a scanner activity?", "Yes", "No").show();
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
                Intent in = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(in);
                } catch (ActivityNotFoundException e) {

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

    //if the code been recognized then to display it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                Toast.makeText(AddTheProduct.this, contents, Toast.LENGTH_SHORT).show();
                productBarCode.setText(contents);

                ///adding product checker from the external file
                ProductCheckFromFileList product = new ProductCheckFromFileList(listObjects, productBarCode.getText().toString().trim());
                String productN = product.check();
                productName.setText(productN);
            }
        }
        //voice recognision if voice has been send then to process it
        if (requestCode == 50 && resultCode == RESULT_OK) {
            List<String> results = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String text = results.get(0);

            textToSpeech.speak("You are adding to the fridge " + text, TextToSpeech.QUEUE_FLUSH, null, null);
            productName.setText(text);
        }

        //voice recognision if voice has been send then to process it
        if (requestCode == 80 && resultCode == RESULT_OK) {
            List<String> results = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String text = results.get(0);

            //split the string value to numbers
            listOfWords = text.split(" ");

            if (listOfWords.length == 3) {
                days();
                months();
                //find the right day
                for (int i = 0; i < days.size(); i++) {

                    if (listOfWords[0].equals(days.get(i).getInput())) {
                        tempexpiryday = days.get(i).getNumber();
                    }
                }

                //find the right day
                for (int i = 0; i < months.size(); i++) {
                    if (listOfWords[1].equals(months.get(i).getInput())) {
                        tempmonth = months.get(i).getNumber();
                    }
                }
                tempyear = Integer.parseInt(listOfWords[2]);

                //check if all required parameters are received
                if (tempexpiryday != 0 && tempmonth != 0 && tempyear != 0) {

                    //check if the given month has so many days
                    Calendar calendar = new GregorianCalendar();
                    calendar.set(tempyear, tempmonth - 1, 1);

                    int maxNumberOfDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                    if (tempexpiryday <= maxNumberOfDays) {
                        expiryday = tempexpiryday;
                        month = tempmonth;
                        year = tempyear;
                        expiryDate.setText(expiryday + " / " + month + " /  " + year);
                    }
                }
            }//end of if which checks if tree parametrs are got back

            textToSpeech.speak("The product expiry date is " + text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    //when the callendar button pressed then launching calendar
    public void calendarButton(View view) {
        new DatePickerDialog(AddTheProduct.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        //receiving all calendar dates
        @Override
        public void onDateSet(DatePicker datePicker, int i1, int i2, int i3) {
            Toast.makeText(AddTheProduct.this, " Day " + i3 + " Month " + (i2 + 1) + "  YEAR " + i1, Toast.LENGTH_SHORT).show();
            expiryDate.setText(i3 + " / " + (i2 + 1) + " /  " + i1);
            expiryday = i3;
            month = i2 + 1;
            year = i1;
        }
    };

    public void saveProduct(View view) throws ParseException {

        //checking internet connection
        if (connectionAvailable()) {

            //checking if is left some fields blank
            if ((productBarCode.getText().toString().trim().equals("")) || (productName.getText().toString().trim().equals("")) || (expiryDate.getText().toString().trim().equals(""))) {
                Toast.makeText(AddTheProduct.this, "Please fill all field !!!", Toast.LENGTH_SHORT).show();
            } else {
                //creating an object of product and adding all details to it
                //checking the check box, if it is checked then store the status to database
                if (checkBox.isChecked()) {
                    notification = "yes";
                }
                String inputDate = year + "-" + month + "-" + expiryday;
                DateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd");
                Date insertDate = dateForm.parse(inputDate);

                Product product = new Product();
                //need to test this part
                String productNameToLoverCase = (productName.getText().toString().trim()).toLowerCase();
                product.setProductName(productNameToLoverCase);
                product.setProductBarcode(productBarCode.getText().toString().trim());
                product.setNotify(notification);
                product.setExpiryDate(insertDate);
                product.setUserMail(getIntent().getStringExtra("user"));
                //emptying variable
                notification = "";

                //adding barcode and name of the product to database
                stock = new Stock();
                //Giving permition for anyone to write and read to Stock table

                Backendless.Data.Permissions.FIND.grantForAllUsers(stock, new AsyncCallback<Stock>() {
                    @Override
                    public void handleResponse(Stock stock) {

                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {

                    }
                });

                Backendless.Data.Permissions.UPDATE.grantForAllUsers(stock, new AsyncCallback<Stock>() {
                    @Override
                    public void handleResponse(Stock stock) {

                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {

                    }
                });

                Backendless.Data.Permissions.REMOVE.grantForAllUsers(stock, new AsyncCallback<Stock>() {
                    @Override
                    public void handleResponse(Stock stock) {

                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {

                    }
                });

                //if internet connection is available then to find the product in the stock table if there is no such a product then save it stock table
                if (connectionAvailable()) {

                    String whereClause = "productBarcode = '" + productBarCode.getText().toString().trim() + "'";
                    BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                    dataQuery.setWhereClause(whereClause);

                    Backendless.Persistence.of(Stock.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Stock>>() {
                        @Override
                        public void handleResponse(BackendlessCollection<Stock> stockBackendlessCollection) {

                            int itemsFound = stockBackendlessCollection.getTotalObjects();
                            //if there is not exist item with given barcode
                            if (itemsFound == 0) {

                                //setting product name and product bar code
                                String productNameToLoverCase = (productName.getText().toString().trim()).toLowerCase();
                                stock.setProductName(productNameToLoverCase);
                                stock.setProductBarcode(productBarCode.getText().toString().trim());
                                //save the product to stock
                                Backendless.Persistence.save(stock, new AsyncCallback<Stock>() {
                                    //displaying the toast message if everything went ok
                                    @Override
                                    public void handleResponse(Stock stock) {
                                        Toast.makeText(AddTheProduct.this, "Product " + productName.getText().toString().trim() + " was succesfully addeed to our database", Toast.LENGTH_SHORT).show();
                                    }

                                    //if something goes wrong while inserting data about product then display error toast message
                                    @Override
                                    public void handleFault(BackendlessFault backendlessFault) {
                                        Toast.makeText(AddTheProduct.this, "Sorry but we cant add the product " + backendlessFault.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        //if nothing has been found the to prompt user to enter the product name manually
                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {

                        }
                    });
                }

                //run the progress bar
                progressDialog = new SpotsDialog(AddTheProduct.this, R.style.Custom);
                progressDialog.show();
                //store all data to backendless
                Backendless.Persistence.save(product, new AsyncCallback<Product>() {

                    //if everything went fine then display toast
                    @Override
                    // public void handleResponse(Stock product) {
                    public void handleResponse(Product product) {
                        Toast.makeText(AddTheProduct.this, "Product " + productName.getText().toString().trim() + " succesfully added to database", Toast.LENGTH_SHORT).show();
                        AddTheProduct.this.finish();
                        progressDialog.dismiss();
                    }

                    //if something went wrong display apropriate message on toast
                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        progressDialog.dismiss();
                        Toast.makeText(AddTheProduct.this, "Error: " + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(AddTheProduct.this, "No internet connection please connect first", Toast.LENGTH_SHORT).show();
        }
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

    public class DownloadJsonFile extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int response = urlConnection.getResponseCode();

                // checking if server is down then then send message

                if (response > 200) {
                    Toast.makeText(getApplicationContext(), "UPC Data Base is Down", Toast.LENGTH_LONG);
                    result = "UPC Data Base is Down";
                    return result;
                }
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Could not find anything", Toast.LENGTH_LONG);

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                String message = "";

                if (!result.equals("Server Is Down")) {

                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.length() > 0) {
                        JSONObject json2 = jsonObject.getJSONObject("0");
                        String product = json2.getString("productname");

                        if (product.equals(" ")) {
                            hasBeenFound = false;
                        } else {
                            hasBeenFound = true;
                            productName.setText(product);
                            hasBeenFound = false;
                        }
                    }
                }//end of if server is down checker
                else {
                    hasBeenFound = false;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //voice recognision
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

    public void calendarVoice(View view) {
        voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please say product expiry date");
        startActivityForResult(voiceIntent, 80);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {


            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    //fill up the aaray list of days with days
    public void days() {

        Day day1 = new Day();
        day1.setInput("1st");
        day1.setNumber(1);
        days.add(day1);

        Day day2 = new Day();
        day2.setInput("2nd");
        day2.setNumber(2);
        days.add(day2);

        Day day3 = new Day();
        day3.setInput("3rd");
        day3.setNumber(3);
        days.add(day3);

        for (int i = 4; i < 21; i++) {

            Day day = new Day();

            day.setInput(i + "th");
            day.setNumber(i);
            days.add(day);
        }
        Day day21 = new Day();
        day21.setInput("21st");
        day21.setNumber(21);
        days.add(day21);

        Day day22 = new Day();
        day22.setInput("22nd");
        day22.setNumber(22);
        days.add(day22);

        Day day23 = new Day();
        day23.setInput("23rd");
        day23.setNumber(23);
        days.add(day23);

        for (int i = 24; i < 31; i++) {

            Day day = new Day();
            day.setInput(i + "th");
            day.setNumber(i);
            days.add(day);
        }
        Day day31 = new Day();
        day31.setInput("31st");
        day31.setNumber(31);
        days.add(day31);

        for (int i = 0; i < days.size(); i++) {
            Log.i("day ", days.get(i).getInput() + " " + days.get(i).getNumber());
        }
    }

    //convert the months
    public void months() {

        for (int i = 0; i < 12; i++) {
            Month month = new Month();
            String montName = new DateFormatSymbols().getMonths()[i];
            month.setInput(montName);
            month.setNumber(i + 1);
            months.add(month);
        }
        for (int i = 0; i < months.size(); i++) {
            Log.i("month test", months.get(i).getInput() + " " + months.get(i).getNumber());
        }
    }
}



















