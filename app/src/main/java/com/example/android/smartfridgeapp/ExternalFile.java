package com.example.android.smartfridgeapp;

/**
 * Class Name: ExternalFile
 * Description: The object to hold external file details.
 * @author: Deniss Timofejevs B00066599
 */
public class ExternalFile {

    String name;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String barcode;
}
