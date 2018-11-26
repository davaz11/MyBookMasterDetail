package com.example.dani.mybookmasterdetail;


import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.webkit.WebView;

import com.example.dani.mybookmasterdetail.modelFireBase.DataSourceFireBase;
import com.example.dani.mybookmasterdetail.modelRealmORM.Book;
import com.google.android.gms.common.util.IOUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link BookListActivity}.
 */
public class BookDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    Book bookItem;

   // View recyclerView = findViewById(R.id.item_list);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);


           // bookItem =(Book)getIntent().getSerializableExtra(BookDetailFragmentPar.ARG_ITEM_ID);


             //para tener disponible el libro seleccionado lo guardo siempre en una clase estática,
            // ya no lo paso por el intent
             bookItem=SharedData.bookItem;
             setContentView(R.layout.activity_detail);

            toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
            setSupportActionBar(toolbar);


            //Control botón comprar libro
            FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab);
            fab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        //el webview con los datos de compra se muestra desde una nueva activity
                        Intent intent = new Intent(getApplicationContext(), BuyBookWebView.class);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });



            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);

            }
            //desde el fragment se cargan los datos del layout
            if (savedInstanceState == null) {


                Fragment fragment=new BookDetailFragmentPar();

                Bundle arguments = new Bundle();
                arguments.putSerializable(BookDetailFragmentPar.ARG_ITEM_ID, bookItem);
               // BookDetailFragmentImpar fragment = new BookDetailFragmentImpar();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.item_detail_container, fragment)
                        .commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //lo utilizo para abrir el menú desde el detalle (sería una buena opción de diseño tener siempre el menú disponible
    // desde cualquier parte de la aplicación???)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_detail_toolbar, menu);
        return true;
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

    //botones de la toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();
            if (id == android.R.id.home) {

                Intent intent = new Intent(this, BookListActivity.class);
                NavUtils.navigateUpTo(this, intent);
                return true;

            //aunque no es buen sitio para el menú he hecho pruebas para ver si podía abrir el menú desde el detalle
            } else if (id == R.id.action_menu) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
                drawer.openDrawer(nav);

            //he creado un botón para compartir el contenido del detalle del libro(la imagen y el título) a traves del mail
            } else if (id == R.id.action_share) {

            if(bookItem!=null){
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);

                if(bookItem.url_imagen.contains("https://") & bookItem.url_imagen.contains(".jpg")) {

                    shareIntent.putExtra(Intent.EXTRA_STREAM, bookItem.url_imagen);
                }

                shareIntent.putExtra(Intent.EXTRA_TEXT, bookItem.title);
                shareIntent.setType("image/jpg");
                startActivity(shareIntent);
            }



            }



            return super.onOptionsItemSelected(item);
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }

    }
}
