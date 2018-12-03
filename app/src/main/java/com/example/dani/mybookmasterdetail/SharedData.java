package com.example.dani.mybookmasterdetail;

import com.example.dani.mybookmasterdetail.modelRealmORM.Book;

import java.util.ArrayList;
import java.util.List;

public class SharedData {


    static Book bookItem=new Book();
    private static List<Book> bookList=new ArrayList<Book>();
    private static List<Book> searchBookList =new ArrayList<>();
    private static List<String> searchDataDropDown=new ArrayList<>();




    public static List<Book> GetBookList(){
        return bookList;
    }

    public static void SetBookList(List<Book> bList){
        bookList=bList;
        for(Book b:bookList){
            searchDataDropDown.add(b.title);
        }

    }

    public static List<Book> GetSearchBookList(){
        return searchBookList;
    }

    public static void SetSearchBookList(List<Book> t){
        searchBookList=t;
    }


    public static List<String> GetSearchDataDropDown(){
        return searchDataDropDown;
    }




}
