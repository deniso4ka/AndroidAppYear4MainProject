package com.example.android.smartfridgeapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserIdStorageFactory;

/**
 * Class Name: LoadingPageCheck
 * Description: Class checks if the user already logged in to the system then
 * skips the password entering process and transfer user to the menu page.
 * @author: Deniss Timofejevs B00066599
 */

public class LoadingPageCheck extends AppCompatActivity {

    TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page_check);

        loadingText = (TextView) findViewById(R.id.loadingText);

        //checking if the user is logged in currently and is valid login
        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            //if user is logged in currently
            @Override
            public void handleResponse(Boolean aBoolean) {
                //if user were not loged in
                if (!aBoolean) {
                    Intent intent = new Intent(LoadingPageCheck.this, MainActivity.class);
                    startActivity(intent);
                    LoadingPageCheck.this.finish();
                }
                //if already logged in and kept logged user by pass login page
                else {
                    loadingText.setText("Please wait while we process details !!!");
                    //receiving the id of loged in user
                    String userObjectId = UserIdStorageFactory.instance().getStorage().get();

                    Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                        // if user is found
                        @Override
                        public void handleResponse(BackendlessUser backendlessUser) {

                            Intent intent = new Intent(LoadingPageCheck.this, MenuActivity.class);
                            intent.putExtra("user", backendlessUser.getEmail());
                            startActivity(intent);
                            LoadingPageCheck.this.finish();

                        }

                        // if fault then display toast
                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Toast.makeText(LoadingPageCheck.this, "Error: " + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoadingPageCheck.this, MainActivity.class);
                            startActivity(intent);
                            LoadingPageCheck.this.finish();
                        }
                    });
                }
            }

            //fault message and redirection to the main login page
            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(LoadingPageCheck.this, "Error: " + backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoadingPageCheck.this, MainActivity.class);
                startActivity(intent);
                LoadingPageCheck.this.finish();
            }
        });
    }
}