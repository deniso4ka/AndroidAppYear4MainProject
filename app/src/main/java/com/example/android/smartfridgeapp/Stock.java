package com.example.android.smartfridgeapp;

import java.util.Date;

/**
 * Class Name:Stock
 * Description: The object that allows to hold product details.
 * @author: Deniss Timofejevs B00066599
 */

public class Stock {

    private String productName;
    private String productBarcode;


    public Stock() {
        this.productName = null;
        this.productBarcode = null;

    }

    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

}

