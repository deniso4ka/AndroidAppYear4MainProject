package com.example.android.smartfridgeapp;

import java.util.Date;

/**
 * Class Name: ShoppingList
 * Description: The object holding product details of the shopping list.
 * @author: Deniss Timofejevs B00066599
 */

public class ShoppingList {

    private String product;
    private String barcode;
    private String userMail;
    private String objectId;
    private Date created;
    private Date updated;

    public ShoppingList() {
        this.product = null;
        this.barcode = null;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }
}
