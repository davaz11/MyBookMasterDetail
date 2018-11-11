package com.example.dani.mybookmasterdetail;

import com.example.dani.mybookmasterdetail.helperClasses.DownloadImageTask;
import com.example.dani.mybookmasterdetail.helperClasses.DownloadImageTaskListener;
import com.example.dani.mybookmasterdetail.modelFireBase.DataSourceFireBase;
import com.example.dani.mybookmasterdetail.modelRealmORM.Book;
import com.example.dani.mybookmasterdetail.modelSQLite.BookSQLite;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService implements DownloadImageTaskListener
{

    public static final String ACTION_DELETE_BOOK = "ACTION_DELETE_BOOK";
    public static final String ACTION_DETAIL_BOOK = "ACTION_DETAIL_BOOK";

    DownloadImageTask imageTask=null;
    String CHANNEL_ID="123";
    RemoteMessage remoteMessageFromFireBase=null;

    @Override
    public void onSendError(String msgId, Exception exception) {

    String t="";
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Mostrar una notificación al recibir un mensaje de Firebase
        sendExtendNotification(remoteMessage);


    }
    private void sendExtendNotification(RemoteMessage remoteMessage){


        try {


            remoteMessageFromFireBase=remoteMessage;

            RemoteMessage.Notification notification=remoteMessageFromFireBase.getNotification();
            String stringBody=notification.getBody();


            //si el cuerpo del mensaje es una url se busca la imagen y se muestra en la notificación
            if(stringBody.contains("https:")){

                imageTask=new DownloadImageTask();
                imageTask.addListener(this);
                imageTask.execute(stringBody);

            }else{
                LoadExtendNotification(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLoadImageTaskListener(Object returnValue) {

        Bitmap myBitmap=(Bitmap)returnValue;
        LoadExtendNotification(myBitmap);

    }




    private PendingIntent DeleteBookPendingIntent(){
        try {
            Intent actionDeleteIntent = new Intent(this, NotificationActionService.class)
                    .setAction(ACTION_DELETE_BOOK);
            actionDeleteIntent.putExtra("BOOK_TITLE", remoteMessageFromFireBase.getNotification().getTitle());
            PendingIntent actionDeletePendingIntent = PendingIntent.getService(this, 0,
                    actionDeleteIntent,0);

            return actionDeletePendingIntent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private PendingIntent DetailBookPendingIntent(){
        try {
            Intent actionDetailIntent = new Intent(this, NotificationActionService.class)
                    .setAction(ACTION_DETAIL_BOOK);
            actionDetailIntent.putExtra("BOOK_TITLE", remoteMessageFromFireBase.getNotification().getTitle());
            PendingIntent actionDeletePendingIntent = PendingIntent.getService(this, 0,
                    actionDetailIntent, 0);

            return actionDeletePendingIntent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    //se muestra el detalle del libro, el titulo de la notificación debe coincidir con el titulo del libro sinó no muestra mensaje
    private PendingIntent LoadBookDetailPendingIntent(){

        try {
            Book selectedBook=null;
            String titleFromNotification=null;
            for(Book b:DataSourceFireBase.bookListAppFireBase){

                String title1=remoteMessageFromFireBase.getNotification().getTitle();
                titleFromNotification=title1;
                String title2=b.title;
                if(title1!=null) title1=title1.replace(" ","").toLowerCase();

                if(title2!=null) title2=title2.replace(" ","").toLowerCase();

                if(title1.equals(title2)){
                    selectedBook=b;
                }
            }

            if(selectedBook==null){
                Intent intentDetail = new Intent(this, BookListActivity.class);
                intentDetail.putExtra("notificationNotDetail", true);
                intentDetail.putExtra("bookTitle", titleFromNotification);
               // intentDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentDetail, PendingIntent.FLAG_CANCEL_CURRENT);

                return pendingIntent;
            }else{

               return  OpenActivityDetailWithParentStack(selectedBook);

            }



        } catch (Exception e) {
            e.printStackTrace();
            return null;
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


    //el problema que no he podido solucionar es que si desde la notificación se elimina un item y se desde
    //la misma notificación se quiere ver el detalle de ese mismo item no se puede, porque las acciones de los botones

    private void LoadExtendNotification(Bitmap myBitmap){


        try {
            PendingIntent deletePendingIntent=DeleteBookPendingIntent();
            PendingIntent detailPendingIntent=DetailBookPendingIntent();

            int notificationId=1234;

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle(remoteMessageFromFireBase.getNotification().getTitle())
                    .setContentText(remoteMessageFromFireBase.getNotification().getBody())
                    .setLargeIcon(myBitmap)
                    .addAction(new NotificationCompat.Action(R.drawable.common_google_signin_btn_icon_light,
                            "Delete", deletePendingIntent))
                    .addAction(R.drawable.common_google_signin_btn_icon_dark, "Visualize", detailPendingIntent) // #0

                   // .addAction(new NotificationCompat.Action(R.drawable.common_google_signin_btn_icon_dark,
                     //       "Visualize", detailPendingIntent))
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(myBitmap)
                            .bigLargeIcon(null))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(notificationId, mBuilder.build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }







}
