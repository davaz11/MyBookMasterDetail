package com.example.dani.mybookmasterdetail.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 ESTO NO LO USO
 */
public class Model {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<ModelItem> ITEMS = new ArrayList<ModelItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, ModelItem> ITEM_MAP = new HashMap<String, ModelItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createModelItem(i));
        }
    }

    private static void addItem(ModelItem item) {
        ITEMS.add(item);
        ITEM_MAP.put( Integer.toString(item.identificador), item);
    }

    private static ModelItem createModelItem(int position) {
        return new ModelItem(position,"Libro"+position," ",new Date(2000,1,1)," "," ");
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class ModelItem {

        public final int identificador;
        public final String titulo;
        public final String autor;
        public final Date dataPublicacio;
        public final String descripcio;
        public final String urlImagen;

        public ModelItem(int identificador, String titulo, String autor,Date dataPublicacio,String descripcio,String urlImage) {

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



}