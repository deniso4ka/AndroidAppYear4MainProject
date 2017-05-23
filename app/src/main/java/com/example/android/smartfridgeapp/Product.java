package com.example.android.smartfridgeapp;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Date;

/**
 * Class Name: Product
 * Description: Product object which holds parameters of the product.
 * @author: Deniss Timofejevs B0066599
 */

public class Product implements Serializable {

    //creating field for the table
    private String productName;
    private String productBarcode;
    private String notify;
    private Date expiryDate;
    private String userMail;
    private String objectId;
    private Date created;
    private Date updated;

    public Product() {
        this.productName = null;
        this.productBarcode = null;
        this.notify = null;
        this.expiryDate = null;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String objectId) {
        this.userMail = objectId;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}


