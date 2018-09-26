package com.example.dani.mybookmasterdetail.model;

import java.io.Serializable;
import java.util.Date;
//utilizo esta clase como modelo de datos, es serializable para poderse enviar en un Intent
public class BookItem implements Serializable{

    public final int identificador;
    public final String titulo;
    public final String autor;
    public final Date dataPublicacio;
    public final String descripcio;
    public final String urlImagen;

    public BookItem(int identificador, String titulo, String autor,Date dataPublicacio,String descripcio,String urlImage) {

        this.identificador = identificador;
        this.titulo = titulo;
        this.autor = autor;
        this.dataPublicacio=dataPublicacio;
        this.descripcio=descripcio;
        this.urlImagen=urlImage;

    }

    @Override
    public String toString() {
        return descripcio;
    }
}