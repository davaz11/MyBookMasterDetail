package com.example.dani.mybookmasterdetail;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.dani.mybookmasterdetail.modelFireBase.DataSourceFireBase;
import com.example.dani.mybookmasterdetail.modelRealmORM.Book;
import com.example.dani.mybookmasterdetail.modelRealmORM.BookContent;
import com.example.dani.mybookmasterdetail.modelSQLite.BookSQLite;

import java.util.Date;

import io.realm.Realm;

public class NotificationActionService extends IntentService {

    public static final String ACTION_DELETE_BOOK = "ACTION_DELETE_BOOK";
    public static final String ACTION_DETAIL_BOOK = "ACTION_DETAIL_BOOK";

    public NotificationActionService() {
        super(NotificationActionService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String action = intent.getAction();

            if (ACTION_DELETE_BOOK.equals(action)) {

                String bookTitle=intent.getStringExtra("BOOK_TITLE");

                //borrar local en SQLITE
                BookSQLite.SetContext(getApplicationContext());
               int result=BookSQLite.DeleteBookByName(null,bookTitle);

                //borrar local en Realm
                Realm.init(getApplicationContext());
                Realm realm = Realm.getDefaultInstance();
                 BookContent.DeleteBook(realm,bookTitle);

                 //también borro en Firebase para poder apreciar bien los cambios en la aplicación
                DeleteBookFromFirebase(bookTitle);


                //volvemos a la pantalla principal de la aplicación para mostrar un mensaje emergente
                Intent intentMain=new Intent(getApplicationContext(), BookListActivity.class);
                intentMain.putExtra("notificationDelete",true);
                intentMain.putExtra("bookTitle",bookTitle);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intentMain);




            }else if(ACTION_DETAIL_BOOK.equals(action)){

                String bookTitle=intent.getStringExtra("BOOK_TITLE");


                Book book=null;
                for(Book b:DataSourceFireBase.bookListAppFireBase){

                    if(b.title.equals(bookTitle)){
                        book=b;
                    }
                }

                if(book==null){
                    Intent intentDetail = new Intent(this, BookListActivity.class);
                    intentDetail.putExtra("notificationNotDetail", true);
                    intentDetail.putExtra("bookTitle", bookTitle);
                    intentDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentDetail);

                }else{

                    PendingIntent pending=OpenActivityDetailWithParentStack(book);
                    pending.send();

                    //se colapsan las notificaciones
                    Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                    getApplicationContext().sendBroadcast(it);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PendingIntent OpenActivityDetailWithParentStack(Book selectedBook){

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, BookDetailActivity.class);
        resultIntent.putExtra(BookDetailFragmentPar.ARG_ITEM_ID, selectedBook);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        return resultPendingIntent;
    }



    private void AddBookToFireBase(){
        Book testBook=new Book();
        testBook.author="";
        testBook.title="Borrar";
        testBook.description="";
        testBook.url_imagen="";
        testBook.publication_date=new Date();
        testBook.identificador=11;
        DataSourceFireBase dataSourceFireBase=new DataSourceFireBase();

        dataSourceFireBase.AddFireBaseBook(testBook);
    }


    private void DeleteBookFromFirebase(String bookTitle){

        try {
            DataSourceFireBase dataSourceFireBase=new DataSourceFireBase();


            Book bookToDelete=null;
            for(Book b:dataSourceFireBase.bookListAppFireBase) {
                if(b.title.equals(bookTitle)){
                    bookToDelete=b;
                }
            }

            if(bookToDelete!=null)
                dataSourceFireBase.DeleteFireBaseItem(Integer.toString(bookToDelete.identificador));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

