package com.example.dani.mybookmasterdetail.helperClasses;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.FileProvider;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Manager  {

    private static List<PicassoGetImageListener> listeners = new ArrayList<PicassoGetImageListener>();


    public static void AddListenerPicasso(PicassoGetImageListener t){
        listeners.add(t);
    }


    public static Uri ImagePathToUriTempFile(final Activity activity, String image) {


        String[] t=image.split("(?=\\.|\\/)");

        final String nameImage=t[t.length-2].replace("/","");


        Picasso.with(activity.getApplicationContext()).load(image).into(new Target() {

            @Override
            public void onPrepareLoad(Drawable arg0) {


            }

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {


                File imagePath = new File(activity.getFilesDir(), "temporal");
                imagePath.mkdir();
                File imageFile = new File(imagePath.getPath(), nameImage+"_temporal.png");

                try {
                    FileOutputStream fos = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();



                    for (PicassoGetImageListener hl : listeners) {
                        hl.onLoadImagePicassoListener(imageFile.getAbsolutePath().toString());
                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }
   }

            @Override
            public void onBitmapFailed(Drawable arg0) {


            }
        });



    return null;
    }


    public static Uri ImageResourceToUriTempFile(AppCompatActivity activity, int idResource) {

        try {
            Drawable drawable = activity.getResources().getDrawable(idResource);
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            File imagePath = new File(activity.getFilesDir(), "temporal");
            imagePath.mkdir();
            File imageFile = new File(imagePath.getPath(), idResource+"_temporal.png");

            try {
                FileOutputStream fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return FileProvider.getUriForFile(activity.getApplicationContext(), activity.getPackageName(), imageFile);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }


}


