package com.example.android.smartfridgeapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import dmax.dialog.SpotsDialog;

/**
 * Class Name: MainActivity
 * Description: The class redirects to
 * registration page, log in to the application, or reset password.
 * @author: Deniss Timofejevs B00066599
 */

public class MainActivity extends AppCompatActivity {

    EditText emailField;
    EditText passwordField;
    EditText resetEmailField;
    AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);

    }

    //after login button pressed to login the user to the backendless account
    public void loginButton(View view) {

        String username = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        //if field are empty then show toast
        if (username.equals("") || password.equals("")) {

            Toast.makeText(MainActivity.this, "Please enter all fields !!!", Toast.LENGTH_SHORT).show();
        } else {

            //check if internet connection is available
            if (connectionAvailable()) {

                progressDialog = new SpotsDialog(MainActivity.this, R.style.Custom);
                progressDialog.show();

                //login using username, password, and by passing true saying to keep loged in the user
                Backendless.UserService.login(username, password, new AsyncCallback<BackendlessUser>() {

                    //if logged in successfully the display toast with success message
                    @Override
                    public void handleResponse(BackendlessUser backendlessUser) {

                        Toast.makeText(MainActivity.this, backendlessUser.getEmail() + " successfully logged in", Toast.LENGTH_SHORT).show();

                        //redirect to the menu page and pass the user email address
                        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                        intent.putExtra("user", backendlessUser.getEmail());
                        startActivity(intent);
                        progressDialog.dismiss();
                    }

                    //if something goes wrong then display toast
                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {

                        Toast.makeText(MainActivity.this, "Error: " + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }, true);
            }
            // if no connection available then display toast
            else {

                Toast.makeText(MainActivity.this, "Please check your internet connection!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //after registration button pressed to redirect to registration page
    public void registrationButton(View view) {
        Intent intent = new Intent(MainActivity.this, CreateAccount.class);
        startActivity(intent);
    }

    //after reset password button pressed to reset the password
    public void resetPasswordButton(View view) {

        Toast.makeText(MainActivity.this, "Works up to here", Toast.LENGTH_SHORT).show();

        //checking if internet connection is available then call reset activity
        if (connectionAvailable()) {

            //making connection to external layout password reset
            LayoutInflater inflater = getLayoutInflater();
            final View newView = inflater.inflate(R.layout.password_reset, null);

            resetEmailField = (EditText) newView.findViewById(R.id.resetEmailField);

            //create the alert dialog
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Reset Password");
            dialog.setView(newView);
            dialog.setIcon(R.mipmap.reset);

            //first button for reset
            dialog.setPositiveButton("RESET", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //displaying progress dialog
                    MainActivity.this.progressDialog = new SpotsDialog(MainActivity.this, R.style.Custom);
                    MainActivity.this.progressDialog.setTitle("Please wait while we are reseting your password");
                    MainActivity.this.progressDialog.show();

                    //reseting the users password with given email

                    final String email = resetEmailField.getText().toString().trim();

                    Backendless.UserService.restorePassword(email, new AsyncCallback<Void>() {
                        //if everything went fine while reseting the password then successful toast will be displayed
                        @Override
                        public void handleResponse(Void aVoid) {
                            Toast.makeText(MainActivity.this, "Reseting instruction has been sent to " + email, Toast.LENGTH_SHORT).show();
                            //dismiss dialog box
                            MainActivity.this.progressDialog.dismiss();
                        }

                        //if something goes wrong while reseting password the display warning toast message
                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {

                            Toast.makeText(MainActivity.this, "Error: " + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                            //dismiss dialog box
                            MainActivity.this.progressDialog.dismiss();

                        }
                    });
                }
            });//end of reset button


            //second button for cancel
            dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            dialog.show();

        }
        //if no internet connection then display toast message
        else {
            Toast.makeText(MainActivity.this, "Please connect to the internet!!!", Toast.LENGTH_SHORT).show();
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

}
