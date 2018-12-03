package com.example.federico.appandroid;

public class Comments
{
    public String comentario,date, time, username;

    public Comments()
    {

    }

    public Comments(String comentario, String date, String time, String username) {
        this.comentario = comentario;
        this.date = date;
        this.time = time;
        this.username = username;
    }

    public String getComment() {
        return comentario;
    }

    public void setComment(String comment) {
        this.comentario = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
