package com.example.dani.mybookmasterdetail;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class BuyBookWebView extends AppCompatActivity {

    private WebView webView;


    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.web_view);

            //creo una el control de la toolbar para poder salir de la pantalla hacia atrás
            Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar2);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayShowTitleEnabled(false);


            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);

            }

            //se carga el html desde raw files
            WebView webView = (WebView) findViewById(R.id.buy_book_web_view);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl("file:///android_res/raw/form.html");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    private void StringToRawFile(String str, String fileName) throws IOException {

            OutputStreamWriter osw=null;
            try {

                FileOutputStream fOut = openFileOutput(fileName,
                        MODE_PRIVATE);
                osw = new OutputStreamWriter(fOut);
                osw.write(str);

            } catch (IOException e) {
            }finally{
                if(osw!=null){

                    osw.flush();
                    osw.close();
                }

            }


    }



    private String RawFileToString(int idRawFile){
        StringBuilder contentBuilder = new StringBuilder();
        try {
            InputStream is = getApplicationContext().getResources().openRawResource(idRawFile);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr, 8192);
            String str;
            while ((str = br.readLine()) != null) {
                contentBuilder.append(str);
            }
            br.close();
        } catch (IOException e) {
        }
        return contentBuilder.toString();

    }
}