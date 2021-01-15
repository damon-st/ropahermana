package com.damon.ropa.models;


import java.io.Serializable;

public class ProductEntry implements Serializable {

    public  String title;
    public  String url;
    public  double price;
    public  String description;
    private int cantidad;
    public String id;
    private String marca;

    private String category;

    public ProductEntry() {
    }


    public ProductEntry(String title, String url, double price, String description, int cantidad, String id, String marca, String category) {
        this.title = title;
        this.url = url;
        this.price = price;
        this.description = description;
        this.cantidad = cantidad;
        this.id = id;
        this.marca = marca;
        this.category = category;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
