package com.example.dani.mybookmasterdetail;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ImageViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dani.mybookmasterdetail.model.BookItem;
import com.example.dani.mybookmasterdetail.model.Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained
 * in two-pane mode (on tablets)
 * on handsets.
 */
public class BookDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */

    private BookItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (getArguments().containsKey(ARG_ITEM_ID)) {


                BookItem item = (BookItem)getArguments().getSerializable(ARG_ITEM_ID);
                Activity activity = this.getActivity();


                //se setea el título
                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(item.titulo);
                }


                //se separan los dos layaouts
                if(item.identificador%2==0) {


                    //se setea texto
                    DateFormat formater = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                    String dateFormated=formater.format(item.dataPublicacio);

                    TextView tViewDate = (TextView) activity.findViewById(R.id.textView_detail_par_date2);
                    tViewDate.setText(dateFormated);

                    TextView tViewNameAutor = (TextView) activity.findViewById(R.id.textView_detail_par_nameAutor2);
                    tViewNameAutor.setText(item.autor);

                    TextView tViewNameDesc = (TextView) activity.findViewById(R.id.textView_detail_par_nameDescription2);
                    tViewNameDesc.setText(item.descripcio+item.descripcio+item.descripcio+item.descripcio+item.descripcio);

                    //se cargan las imagenes de forma dinámica desde los datos en el xml
                    int idImage = getResources().getIdentifier(getContext().getPackageName() + ":drawable/" + item.urlImagen, null, null);
                    ImageView image = activity.findViewById(R.id.imageView);
                    image.setImageResource(idImage);
                }else{


                    TextView tViewNameDesc = (TextView) activity.findViewById(R.id.textView_item_detail);
                    //lleno de texto para ver el scrollview
                    tViewNameDesc.setText(item.descripcio+item.descripcio+item.descripcio+item.descripcio+item.descripcio+item.descripcio+item.descripcio+item.descripcio+item.descripcio+item.descripcio);


                    //se cargan las imagenes de forma dinámica desde los datos en el xml
                    int idImage = getResources().getIdentifier(getContext().getPackageName() + ":drawable/" + item.urlImagen, null, null);
                    ImageView image = activity.findViewById(R.id.imageView2);
                    image.setImageResource(idImage);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.titulo);
        }

        return rootView;
    }


}
