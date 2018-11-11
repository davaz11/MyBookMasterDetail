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
