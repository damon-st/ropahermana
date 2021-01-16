package com.damon.ropa.models;

import java.io.Serializable;

public class ImagesList implements Serializable {

    private String id;
    private String url;

    public ImagesList() {
    }

    public ImagesList(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
