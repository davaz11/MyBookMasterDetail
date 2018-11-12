package com.example.dani.mybookmasterdetail.helperClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
            InputStream in = new java.net.URL(urldisplay).openStream();
            bmp = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            //onPostExecute(null);
        }
        return bmp;
    }
    public void onPostExecute(Bitmap result) {
       if(bmImage!=null) bmImage.setImageBitmap(result);

        // Notify everybody that may be interested.
        for (DownloadImageTaskListener hl : listeners) {
            hl.onLoadImageTaskListener(result);
        }

    }
}