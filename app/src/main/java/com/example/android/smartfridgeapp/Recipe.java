package com.example.android.smartfridgeapp;

import java.io.Serializable;

/**
 * Class Name: Recipe
 * Description: The object holding the recipe's details.
 * @author: Deniss Timofejevs B00066599
 */

public class Recipe implements Serializable {

    private String title;
    private String href;
    private String ingredients;
    private String picture;

    public Recipe(String titlePassed, String hrefPassed, String ingredientsPassed, String picturePassed) {

        title = titlePassed;
        href = hrefPassed;
        ingredients = ingredientsPassed;
        picture = picturePassed;

    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
