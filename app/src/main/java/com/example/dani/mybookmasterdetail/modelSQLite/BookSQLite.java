package com.example.dani.mybookmasterdetail.modelSQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.dani.mybookmasterdetail.modelRealmORM.Book;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookSQLite {


    private static final String TAG="BookSQLite";

    private static BookDbHelper mDbHelper =null;
    private static SQLiteDatabase db = null;
    private static Context context=null;




    public BookSQLite(){}


    public static void SetContext(Context c){
        context=c;
    }

    public static void InsertSQLiteTest(Context c){

        try {
            BookDbHelper mDbHelper = new BookDbHelper(c);

            // Gets the data repository in write mode
            SQLiteDatabase db = mDbHelper.getWritableDatabase();


            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(BookReaderContract.BookEntry.COLUMN_NAME_ID, "1");
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

    public static void InsertBookList(Context c, List<Book> bookList){

        try {

            if(context==null)context=c;
            if(mDbHelper==null) mDbHelper = new BookDbHelper(context);
            if(db==null)db = mDbHelper.getWritableDatabase();


            List<Book> bookListFromDB=GetBookList(c,mDbHelper);

            for(Book b:bookList) {

               if(! bookListFromDB.contains(b)) {

                   ContentValues values = new ContentValues();
                   //values.put(BookReaderContract.BookEntry.COLUMN_NAME_ID, b.identificador);
                   values.put(BookReaderContract.BookEntry.COLUMN_NAME_TITLE, b.title);
                   values.put(BookReaderContract.BookEntry.COLUMN_NAME_AUTHOR, b.author);
                   values.put(BookReaderContract.BookEntry.COLUMN_NAME_DESCRIPTION, b.description);
                   values.put(BookReaderContract.BookEntry.COLUMN_NAME_URL_IMAGEN, b.url_imagen);
                   values.put(BookReaderContract.BookEntry.COLUMN_NAME_PUBLICATION_DATE,  b.publication_date.getTime());

                   long newRowId = db.insert(BookReaderContract.BookEntry.TABLE_NAME, null, values);
                   Log.d(TAG, "Elemento añadido="+newRowId);
               }else {
                   Log.d(TAG, "La base de datos contiene este elemento");
               }

            }
        } catch (Exception e) {
            Log.w(TAG, "Error insertando lista de elementos de SQLITE="+e.getMessage());
            e.printStackTrace();
        }

    }



    public static int DeleteBookByName(Context c,String name){

        try {
            if(context==null)context=c;
            if(mDbHelper==null) mDbHelper = new BookDbHelper(context);
            if(db==null)db = mDbHelper.getWritableDatabase();

            int result2=db.delete("book","title= ?",new String[] {name});

            return result2;

        } catch (Exception e) {

            e.printStackTrace();
            return 0;
        }


    }


    public static List<Book> GetBookByName(String name){

        try {
            if(mDbHelper==null) mDbHelper = new BookDbHelper(context);
            if(db==null)db = mDbHelper.getWritableDatabase();

            Cursor cursor= db.rawQuery("Select *  FROM book as b where b.title= ?",new String[] {name});
            if(cursor!=null){
              List<Book>  listBook=CreateListBooksFromCursor(cursor);
              return listBook;
            }else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public static List<Book> CreateListBooksFromCursor(Cursor cursor){

        try {
            List<Book> itemBookList = new ArrayList<Book>();
            while(cursor.moveToNext()) {
                Book b=new Book();
                b.identificador=cursor.getColumnIndex(BookReaderContract.BookEntry.COLUMN_NAME_TITLE);
                b.title= cursor.getString(cursor.getColumnIndexOrThrow(BookReaderContract.BookEntry.COLUMN_NAME_TITLE));
                b.author= cursor.getString(cursor.getColumnIndexOrThrow(BookReaderContract.BookEntry.COLUMN_NAME_AUTHOR));
                b.url_imagen= cursor.getString(cursor.getColumnIndexOrThrow(BookReaderContract.BookEntry._ID));

                long dateTime=cursor.getLong(cursor.getColumnIndexOrThrow(BookReaderContract.BookEntry.COLUMN_NAME_PUBLICATION_DATE));
                b.publication_date= new Date(dateTime);
                b.description= cursor.getString(cursor.getColumnIndexOrThrow(BookReaderContract.BookEntry.COLUMN_NAME_DESCRIPTION));

                itemBookList.add(b);
            }
            cursor.close();

            return itemBookList;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;

        }

    }


        public static List<Book> GetBookList(Context c,BookDbHelper mDbHelper){


            try {


                if(mDbHelper==null){
                    mDbHelper = new BookDbHelper(c);
                }

                // Gets the data repository in write mode
                SQLiteDatabase db = mDbHelper.getReadableDatabase();

               Cursor cursor= db.rawQuery("Select * FROM book",null);



                List<Book> itemBookList = new ArrayList<Book>();
                while(cursor.moveToNext()) {
                    Book b=new Book();
                    b.identificador=cursor.getColumnIndex(BookReaderContract.BookEntry.COLUMN_NAME_TITLE);
                    b.title= cursor.getString(cursor.getColumnIndexOrThrow(BookReaderContract.BookEntry.COLUMN_NAME_TITLE));
                    b.author= cursor.getString(cursor.getColumnIndexOrThrow(BookReaderContract.BookEntry.COLUMN_NAME_AUTHOR));
                    b.url_imagen= cursor.getString(cursor.getColumnIndexOrThrow(BookReaderContract.BookEntry._ID));

                    long dateTime=cursor.getLong(cursor.getColumnIndexOrThrow(BookReaderContract.BookEntry.COLUMN_NAME_PUBLICATION_DATE));
                    b.publication_date= new Date(dateTime);
                    b.description= cursor.getString(cursor.getColumnIndexOrThrow(BookReaderContract.BookEntry.COLUMN_NAME_DESCRIPTION));

                    itemBookList.add(b);
                }
                cursor.close();
                Log.d(TAG, "Se devuelve lista de elementos de SQLITE");

                return itemBookList;
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "Error devolviendo lista de elementos de SQLITE="+e.getMessage());
                e.printStackTrace();
                return null;
            }

        }

}
