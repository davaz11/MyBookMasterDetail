package com.example.dani.mybookmasterdetail.modelSQLite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.Date;

public final class BookReaderContract {



    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + BookEntry.TABLE_NAME + " (" +
                    BookEntry._ID + " INTEGER PRIMARY KEY," +
                    BookEntry.COLUMN_NAME_TITLE + " TEXT," +
                    BookEntry.COLUMN_NAME_AUTHOR + " TEXT," +
                    BookEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    BookEntry.COLUMN_NAME_URL_IMAGEN + " TEXT," +
                    BookEntry.COLUMN_NAME_PUBLICATION_DATE + " LONG)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME;



    private BookReaderContract() {}







    /* Inner class that defines the table contents */
    public static class BookEntry implements BaseColumns {
        public static final String TABLE_NAME = "book";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_AUTHOR= "author";
        public static final String COLUMN_NAME_DESCRIPTION= "description";
        public static final String COLUMN_NAME_URL_IMAGEN= "url_imagen";
        public static final String COLUMN_NAME_PUBLICATION_DATE= "publication_date";
        public static final String COLUMN_NAME_ID="id";

    }





}