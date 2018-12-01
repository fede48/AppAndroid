package com.example.federico.appandroid;


import android.content.Context;

public class Posts
{
    public  String uid;
    public String time;
    public String date;
    public String description;
    public String postimage;
    public String fullname;
    public String zona;
    public String direccion;
    public String tipo;

    public Posts()
    {

    }

    public Posts(String uid, String time, String date, String description, String postimage, String fullname,String zona,String direccion,String tipo) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.description = description;
        this.postimage = postimage;
        this.fullname = fullname;
        this.zona = zona;
        this.direccion = direccion;
        this.tipo = tipo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getZona() { return zona; }

    public void setZona(String zona) { this.zona = zona; }

    public String getDireccion() {return direccion;}

    public void setDireccion(String direccion) { this.direccion = direccion;}

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo;}

}
