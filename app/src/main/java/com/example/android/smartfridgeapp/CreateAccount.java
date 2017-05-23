package com.example.android.smartfridgeapp;


import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import dmax.dialog.SpotsDialog;

/**
 * Class Name: CreateAccount
 * Description: The class creates an account for user and
 * then user can access whole app.
 * @author: Deniss Timofejevs B0066599
 */

public class CreateAccount extends AppCompatActivity {

    EditText nameField;
    EditText surnameField;
    EditText emailField;
    EditText passwordField;
    EditText repasswordField;
    AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //text fields initialisation
        nameField = (EditText) findViewById(R.id.nameField);
        surnameField = (EditText) findViewById(R.id.surnameField);
        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        repasswordField = (EditText) findViewById(R.id.repasswordField);
    }

    public void createButton(View view) {
        //read the text fields
        String name = nameField.getText().toString().trim();
        String surname = surnameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String repassword = repasswordField.getText().toString().trim();

        // checking if all fields are not left empty
        if (name.equals("") || surname.equals("") || email.equals("") || password.equals("") || repassword.equals("")) {
            Toast.makeText(CreateAccount.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
        }
        //checking if password are the same as retype password
        else if (!password.equals(repassword)) {
            Toast.makeText(CreateAccount.this, "Please make sure your passwords match !!!", Toast.LENGTH_SHORT).show();
        }
        //if everything is fine then start connection
        else {
            //if connection available, we are checking by calling connectionAvailable method
            if (connectionAvailable()) {
                //creating an object of user and adding all our parameters received from text field
                BackendlessUser newUser = new BackendlessUser();

                if (isEmailValid(email)) {
                    newUser.setProperty("email", email);
                    newUser.setProperty("name", name + " " + surname);
                    newUser.setPassword(password);

                    //displaying progress bar dialog while data has been stored online
                    progressDialog = new SpotsDialog(CreateAccount.this, R.style.Custom);
                    progressDialog.show();

                    //sending onn separate thread the object with all data receivet from text fields
                    Backendless.UserService.register(newUser, new AsyncCallback<BackendlessUser>() {

                        //if all went fine display toast with success message
                        @Override
                        public void handleResponse(BackendlessUser backendlessUser) {

                            Toast.makeText(CreateAccount.this, "Account has been created successfully!!!", Toast.LENGTH_SHORT).show();

                            //dismiss dialog box
                            progressDialog.dismiss();
                            CreateAccount.this.finish();

                        }

                        //if something goes wrong then display toast with attention message
                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {

                            Toast.makeText(CreateAccount.this, "Error: " + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(CreateAccount.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                }
            }
            //if connection not available then send apropriate toast notification
            else {
                Toast.makeText(CreateAccount.this, "Please connect to the internet first", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //email validation
    public static boolean isEmailValid(String emailAddress) {
        return !(emailAddress == null || TextUtils.isEmpty(emailAddress)) && android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
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
