package com.example.dani.mybookmasterdetail.modelSQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.example.dani.mybookmasterdetail.modelRealmORM.Book;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookSQLite {


    public BookSQLite(){}



    public static void InsertSQLite(Context c){

        try {
            BookDbHelper mDbHelper = new BookDbHelper(c);

            // Gets the data repository in write mode
            SQLiteDatabase db = mDbHelper.getWritableDatabase();


            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(BookReaderContract.BookEntry.COLUMN_NAME_TITLE, "book");
            values.put(BookReaderContract.BookEntry.COLUMN_NAME_AUTHOR, "book");
            values.put(BookReaderContract.BookEntry.COLUMN_NAME_DESCRIPTION, "book");
            //values.put(BookReaderContract.BookEntry.COLUMN_NAME_PUBLICATION_DATE, new Date());
            values.put(BookReaderContract.BookEntry.COLUMN_NAME_URL_IMAGEN, "book");


            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(BookReaderContract.BookEntry.TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void InsertBook(Context c, Book book){

        BookDbHelper mDbHelper = new BookDbHelper(c);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(BookReaderContract.BookEntry.COLUMN_NAME_TITLE, "book");
        //values.put(BookReaderContract.BookEntry.COLUMN_NAME_SUBTITLE, "book");

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(BookReaderContract.BookEntry.TABLE_NAME, null, values);
    }

public static void GetBookList(Context c){


    BookDbHelper mDbHelper = new BookDbHelper(c);

    // Gets the data repository in write mode
    SQLiteDatabase db = mDbHelper.getReadableDatabase();

    // Define a projection that specifies which columns from the database
    // you will actually use after this query.
    String[] projection = {
            BaseColumns._ID,
            BookReaderContract.BookEntry.COLUMN_NAME_TITLE,
            BookReaderContract.BookEntry.COLUMN_NAME_AUTHOR
    };

    // Filter results WHERE "title" = 'My Title'
    String selection = BookReaderContract.BookEntry.COLUMN_NAME_TITLE + " = ?";
    String[] selectionArgs = { "My Title" };

    // How you want the results sorted in the resulting Cursor
    String sortOrder =  BookReaderContract.BookEntry.COLUMN_NAME_AUTHOR + " DESC";

    Cursor cursor = db.query(
            BookReaderContract.BookEntry.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            selection,              // The columns for the WHERE clause
            selectionArgs,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
    );



    List itemIds = new ArrayList<>();
    while(cursor.moveToNext()) {
        long itemId = cursor.getLong(
                cursor.getColumnIndexOrThrow(BookReaderContract.BookEntry._ID));
        itemIds.add(itemId);
    }
    cursor.close();
    return;

    }

}
