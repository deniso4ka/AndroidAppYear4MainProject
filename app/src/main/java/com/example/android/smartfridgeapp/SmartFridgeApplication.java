package com.example.android.smartfridgeapp;

import android.app.Application;

import com.backendless.Backendless;

/**
 * Class Name: SmartFridgeApplication
 * Description: The registration with backendless.com helper class.
 * @author: Deniss Timofejevs B00066599
 */

public class SmartFridgeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /* app initialisation, we passing the id of our application which we
        are taking from backendless page, after registering our app.
        and passing android secret key
        and the version of application number */

        Backendless.initApp(this, "30344F0A-207D-ACA3-FF61-C611607D6400", "4069573C-0A60-2716-FFCF-3B33670CBD00","v1");
    }
}
