package com.example.dani.mybookmasterdetail.modelRealmORM;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@IgnoreExtraProperties
public class Book extends RealmObject implements Serializable {
    @PrimaryKey
    public int identificador;

    public String title;
    public String author;
    public Date publication_date;
    public String description;
    public String url_imagen;

    public Book(){}

    public Book(int identificador, String titulo, String autor,Date dataPublicacio,String descripcio,String urlImage) {

        this.identificador = identificador;
        this.title = titulo;
        this.author = autor;
        this.publication_date=dataPublicacio;
        this.description=descripcio;
        this.url_imagen=urlImage;

    }

    @Override
    public String toString() {
        return description;
    }

}
