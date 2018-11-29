package com.example.federico.appandroid;

public class Usuario {

    public  String Apellido,Documento, Email, Nombre, Zona,Rol;

    public Usuario()
    {

    }

    public Usuario(String apellido, String documento, String email, String nombre, String zona, String rol) {
        Apellido = apellido;
        Documento = documento;
        Email = email;
        Nombre = nombre;
        Zona = zona;
        Rol = rol;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getDocumento() {
        return Documento;
    }

    public void setDocumento(String documento) {
        Documento = documento;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getZona() {
        return Zona;
    }

    public void setZona(String zona) {
        Zona = zona;
    }

    public String getRol() {
        return Rol;
    }

    public void setRol(String rol) {
        Rol = rol;
    }


}
