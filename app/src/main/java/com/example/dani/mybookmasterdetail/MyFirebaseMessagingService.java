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
import android.media.RingtoneManager;
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


            String urlImage=remoteMessage.getData().get("url_image");


            //si el segundo parámetro es una url buscará la imagen
            if(urlImage!=null & urlImage!=""){

                imageTask=new DownloadImageTask();
                imageTask.addListener(this);
                imageTask.execute(urlImage);

            }else{
                LoadExtendNotification(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLoadImageTaskListener(Object returnValue) {

        if(returnValue==null) {
            LoadExtendNotification(null);
        }else{
            Bitmap myBitmap=(Bitmap)returnValue;
            LoadExtendNotification(myBitmap);
        }
    }




    private PendingIntent DeleteBookPendingIntent(String idBook){
        try {
            Intent actionDeleteIntent = new Intent(this, NotificationActionService.class)
                    .setAction(ACTION_DELETE_BOOK);
            actionDeleteIntent.putExtra("BOOK_ID",idBook);
            PendingIntent actionDeletePendingIntent = PendingIntent.getService(this, 0,
                    actionDeleteIntent,0);

            return actionDeletePendingIntent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private PendingIntent DetailBookPendingIntent(String idBook){
        try {
            Intent actionDetailIntent = new Intent(this, NotificationActionService.class)
                    .setAction(ACTION_DETAIL_BOOK);
            actionDetailIntent.putExtra("BOOK_ID", idBook);
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

            String idBook=remoteMessageFromFireBase.getData().get("book_position");

            if(idBook==null | idBook=="")return;

            PendingIntent deletePendingIntent=DeleteBookPendingIntent(idBook);
            PendingIntent detailPendingIntent=DetailBookPendingIntent(idBook);

            int notificationId=1234;

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_import_contacts_black_24dp)
                    .setContentTitle(remoteMessageFromFireBase.getNotification().getTitle())
                    .setContentText(remoteMessageFromFireBase.getNotification().getBody())
                    .setLargeIcon(myBitmap)
                    .addAction(new NotificationCompat.Action(R.drawable.common_google_signin_btn_icon_light,
                            "Delete", deletePendingIntent))
                    .addAction(R.drawable.common_google_signin_btn_icon_dark, "Visualize", detailPendingIntent) // #0
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(myBitmap)
                            .bigLargeIcon(null))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    //Vibration
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                    //LED
                    .setLights(getResources().getColor(R.color.NotificationBlue), 3000, 3000)
                    //Ton
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(notificationId, mBuilder.build());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }







}
