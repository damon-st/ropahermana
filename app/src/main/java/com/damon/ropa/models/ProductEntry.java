package com.damon.ropa.models;


import java.io.Serializable;

public class ProductEntry implements Serializable {

    public  String title;
    public  String url;
    public  double price;
    public  String description;
    public int id;

    public ProductEntry() {
    }

    public ProductEntry(String title, String url, double price, String description, int id) {
        this.title = title;
        this.url = url;
        this.price = price;
        this.description = description;
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
}
