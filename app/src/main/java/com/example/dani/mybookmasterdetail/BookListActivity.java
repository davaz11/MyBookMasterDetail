package com.example.dani.mybookmasterdetail;
import com.example.dani.mybookmasterdetail.helperClasses.DeviceType;
import com.example.dani.mybookmasterdetail.model.BookItem;
import com.example.dani.mybookmasterdetail.parserXML.ParserXML;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.dani.mybookmasterdetail.model.Model;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.example.dani.mybookmasterdetail.logger.Log;
import com.example.dani.mybookmasterdetail.logger.LogFragment;
import com.example.dani.mybookmasterdetail.logger.LogWrapper;
import com.example.dani.mybookmasterdetail.logger.MessageOnlyLogFilter;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link BookDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class BookListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public boolean isTablet;
    public boolean isPhone;

    private boolean mTwoPane;
    public static final String TAG = "MainActivity";
    public List<BookItem> bookItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    try {

    //Se cargan los datos desde un xml
    bookItemList=parseXMLbooks();

    setContentView(R.layout.activity_item_list);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setTitle(getTitle());

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    });

    if (findViewById(R.id.item_detail_container) != null) {
        mTwoPane = true;
    }

    View recyclerView = findViewById(R.id.item_list);
    assert recyclerView != null;
    setupRecyclerView((RecyclerView) recyclerView);



        //código para saber si se está ejecutando desde una tablet o desde un móvi
        // Para saber si es tablet
        isTablet= DeviceType.isTablet(getApplicationContext());
        // Para saber si es teléfono
        isPhone=DeviceType.isPhone(getApplicationContext());


    }catch (Exception e)
    {
        String u=e.getMessage();
    }
    }





    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, bookItemList, mTwoPane));
    }


    //Los libros se almacenan en un xml, se utiliza un parser para leerlo
    private  List<BookItem> parseXMLbooks()
    {

        try {
            Context context = getApplicationContext();
            InputStream s = context.getResources().openRawResource(R.raw.book_items_model);

            ParserXML parser=new ParserXML();
            List<BookItem> bookList=parser.parsear(s);
            return bookList;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }





    }

    /** Create a chain of targets that will receive log data */
  /*   public void initializeLogging() {
        try {
            // Wraps Android's native log framework.
            LogWrapper logWrapper = new LogWrapper();
            // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
            Log.setLogNode(logWrapper);

            // Filter strips out everything except the message text.
            MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
            logWrapper.setNext(msgFilter);

            // On screen logging via a fragment with a TextView.


            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            LogFragment fragment = new LogFragment();
            fragmentTransaction.add(R.id.log_fragment, fragment);
            fragmentTransaction.commit();

            /*FragmentManager fragMan=getSupportFragmentManager();
            com.example.dani.mybookmasterdetail.logger.LogFragment logFragment2 =(com.example.dani.mybookmasterdetail.logger.LogFragment)fragMan.findFragmentById(R.id.log_fragment);
            LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.log_fragment);
            msgFilter.setNext(logFragment.getLogView());
            logFragment.getLogView().setTextAppearance(this, R.style.Log);
            logFragment.getLogView().setBackgroundColor(Color.WHITE);


            Log.i(TAG, "Ready");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {


        private final BookListActivity mParentActivity;
        private final List<BookItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BookItem item = (BookItem) view.getTag();
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(BookDetailFragment.ARG_ITEM_ID, Integer.toString(item.identificador));
                        BookDetailFragment fragment = new BookDetailFragment();
                        fragment.setArguments(arguments);
                        mParentActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        //Se envia el item seleccionado en el intent para utilizarlo en la siguiente pantalla
                        Context context = view.getContext();
                        Intent intent = new Intent(context, BookDetailActivity.class);
                        intent.putExtra(BookDetailFragment.ARG_ITEM_ID, item);
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        SimpleItemRecyclerViewAdapter(BookListActivity parent,
                                      List<BookItem> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            try {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_content, parent, false);

                return new ViewHolder(view);

            }catch (Exception e)
            {
                String u=e.getMessage();
                return null;
            }
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

        try{


            holder.mIdView.setText(Integer.toString(mValues.get(position).identificador));
            holder.mContentView.setText(mValues.get(position).titulo);

            //se pintan la card de diferente color segun sea par o impar

            if(mValues.get(position).identificador %2==0){

                holder.cardView.setCardBackgroundColor(holder.vi.getResources().getColor(R.color.backGroundCardPar));

            }else{

                holder.cardView.setCardBackgroundColor(holder.vi.getResources().getColor(R.color.backGroundCard));
            }

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);


        }catch (Exception e)
        {
            String u=e.getMessage();
        }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {


            final TextView mIdView;
            final TextView mContentView;
            final CardView cardView;
            //he tenido que crear guardar la View en esta clase para poder acceder
            // al resource de color desde , no sé si es la mejor manera
            final View vi;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
                cardView=(CardView) view.findViewById(R.id.card_view);
                vi=view;
            }


        }





    }






}
