package com.example.android.smartfridgeapp;

import android.util.Log;
import android.widget.Toast;

import java.util.List;


/**
 * Class Name: ProductCheckFromFileList
 * Description: Class finds out the product name based on
 * barcode from array list which was wed from external file.
 * @author: Deniss Timofejevs B00066599
 */

public class ProductCheckFromFileList {

    List<ExternalFile> list;
    String productBarcode;
    String productReturn = "";

    public ProductCheckFromFileList(List<ExternalFile> productList, String product) {

        this.list = productList;
        this.productBarcode = product;

    }

    public String check() {

        for (ExternalFile ef : list) {

            if (ef.getBarcode().equals(productBarcode)) {

                productReturn = ef.getName();
                Log.i("testing ", productReturn);

            }
        }
        return productReturn;
    }

}