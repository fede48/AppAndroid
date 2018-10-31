package com.example.federico.appandroid;

public class Usuario {
    private String Nombre;
    private String Password;

    public Usuario(){}

    public Usuario(String p_nombre, String p_password)
    {
        this.Nombre = p_nombre;
        this.Password = p_password;
    }

    public String getNombre()
    {
        return this.Nombre;
    }

    public void setNombre(String nombre)
    {
        this.Nombre = nombre;
    }

    public String getPassword()
    {
        return this.Password;
    }

    public void setPassword(String password)
    {
        this.Password = password;
    }






}
