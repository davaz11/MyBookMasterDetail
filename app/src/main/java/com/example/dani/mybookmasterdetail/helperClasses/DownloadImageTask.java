package com.example.dani.mybookmasterdetail.helperClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    ImageView bmImage;

    private List<DownloadImageTaskListener> listeners = new ArrayList<DownloadImageTaskListener>();


    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;

    }

    public DownloadImageTask() {

    }

    public void addListener(DownloadImageTaskListener toAdd) {
        listeners.add(toAdd);
    }


    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap bmp = null;

        try {
            bmp=ConnectSSL(new URL(urldisplay));

           /* bmp=ConnectWithTLSsecurity(urldisplay);
            if(bmp==null){

                bmp=ConnectWithOutsecurity(urldisplay);
            }*/
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bmp;
    }

   /* protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap bmp = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            bmp = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            //onPostExecute(null);
        }
        return bmp;
    }*/


    public void onPostExecute(Bitmap result) {
       if(bmImage!=null) bmImage.setImageBitmap(result);

        // Notify everybody that may be interested.
        for (DownloadImageTaskListener hl : listeners) {
            hl.onLoadImageTaskListener(result);
        }

    }

    private Bitmap ConnectWithOutsecurity(String urlParam){


        try {
            InputStream inputStream=null;
            try {
                URL url = new URL(urlParam);
                InputStream input = new BufferedInputStream(url.openStream());
                Bitmap bmp = BitmapFactory.decodeStream(input);
                return bmp;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }finally {
                if(inputStream!=null) inputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap ConnectSSL(URL url){

        try {
            URLConnection urlConnection = url.openConnection();
            InputStream in = urlConnection.getInputStream();
            Bitmap bmp = BitmapFactory.decodeStream(in);
            return bmp;
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
    }




    private Bitmap ConnectWithTLSsecurity(String urlParam) {

        try {
            HttpsURLConnection connection;
            InputStream inputStream=null;

            try {
                URL url = new URL(urlParam);

                SSLContext ssl = SSLContext.getInstance("TLSv1.2");
                ssl.init(null, null, new SecureRandom());

                connection = (HttpsURLConnection) url.openConnection();
                connection.setSSLSocketFactory(ssl.getSocketFactory());
                inputStream =  connection.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);

                return bmp;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }finally {
               if(inputStream!=null) inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}