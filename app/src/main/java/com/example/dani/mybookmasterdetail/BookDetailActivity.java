package com.example.dani.mybookmasterdetail;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.example.dani.mybookmasterdetail.model.BookItem;
import com.example.dani.mybookmasterdetail.modelRealmORM.Book;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link BookListActivity}.
 */
public class BookDetailActivity extends AppCompatActivity {


   // View recyclerView = findViewById(R.id.item_list);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);


            Book item =(Book)getIntent().getSerializableExtra(BookDetailFragmentImpar.ARG_ITEM_ID);

                setContentView(R.layout.activity_item_detail);



            Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });


            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            //desde el fragment se cargan los datos del layout
            if (savedInstanceState == null) {



                Fragment fragment=null;

                //se elige un layout y el fragment
                if(item.identificador%2==0)
                {
                    fragment = new BookDetailFragmentPar();

                }else{
                    fragment = new BookDetailFragmentImpar();

                }


                Bundle arguments = new Bundle();
                arguments.putSerializable(BookDetailFragmentImpar.ARG_ITEM_ID, item);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            NavUtils.navigateUpTo(this, new Intent(this, BookListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
