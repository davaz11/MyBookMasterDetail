package com.example.dani.mybookmasterdetail.modelRealmORM;

import com.google.firebase.database.DataSnapshot;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;

public class BookContent {

    public static int numberBooksUpdated=0;


    public static void SetBooks(Realm realm,List<Book> bookList)
    {
        try {
            realm.beginTransaction();

            for(Book b:bookList){

                if(!ExistBooks(realm,b)) {

                    Book bookRealm = realm.createObject(Book.class, b.identificador); // Create managed objects directly
                    bookRealm.url_imagen = b.url_imagen;
                    bookRealm.title = b.title;
                    bookRealm.publication_date = b.publication_date;

                    bookRealm.author = b.author;
                    bookRealm.description = b.description;
                    numberBooksUpdated++;
                }
            }
            realm.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<Book> GetBooks(Realm realm)
    {
        realm.beginTransaction();
        List<Book> b = realm.where(Book.class).findAll();
        realm.commitTransaction();

        return b;

    }

    public static boolean ExistBooks(Realm realm,Book book)
    {
        Book b = realm.where(Book.class).equalTo("identificador", book.identificador).findFirst();

        if(b!=null){
            return true;
        }else{
            return false;
        }

    }



    public static List<Book> ParseFireBaseDataToObject(DataSnapshot dataSnapshot)
    {

        try {
            List<Book> bookList=new ArrayList<Book>();
            int idBook=1;

            for (DataSnapshot imageSnapshot: dataSnapshot.getChildren()) {

                for (DataSnapshot im: imageSnapshot.getChildren()) {

                    Book book=new Book();
                    String a=im.child("author").getValue().toString();
                    String b=im.child("description").getValue().toString();
                    String date=im.child("publication_date").getValue().toString();
                    String d=im.child("title").getValue().toString();
                    String e=im.child("url_image").getValue().toString();

                    DateFormat formater = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                    Date dt=formater.parse(date);

                    book.author=a;
                    book.description=b;
                    book.identificador=idBook;
                    book.publication_date=dt;
                    book.title=d;
                    book.url_imagen=e;

                    bookList.add(book);

                    idBook++;
                }

            }

            return bookList;

        } catch (ParseException e) {

            e.printStackTrace();
            return null;
        }


    }




    public static void UpdateBook(Realm realm,Book realmBook,Book toUpdateBook){

    realmBook.description=toUpdateBook.description;
    realmBook.author=toUpdateBook.author;
    realmBook.identificador=toUpdateBook.identificador;
    realmBook.publication_date=toUpdateBook.publication_date;
    realmBook.title=toUpdateBook.title;
    realmBook.url_imagen=toUpdateBook.url_imagen;
}

}
