package com.damon.ropa.models;

import java.io.Serializable;

public class UserM implements Serializable{

    private String uid;
    private String names;
    private String rol;

    public UserM() {
    }

    public UserM(String uid, String names, String rol) {
        this.uid = uid;
        this.names = names;
        this.rol = rol;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
