package com.damon.ropa.models;

import java.io.Serializable;

public class CatgoriasM implements Serializable {

    private String id;

    public CatgoriasM() {
    }

    public CatgoriasM(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
