package com.example.dani.mybookmasterdetail;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dani.mybookmasterdetail.model.BookItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;



public class BookDetailFragmentPar extends Fragment {



    public static final String ARG_ITEM_ID = "item_id";




    public BookDetailFragmentPar() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            BookItem item = (BookItem)getArguments().getSerializable(ARG_ITEM_ID);
            Activity activity = this.getActivity();


            //se setea el título
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(item.titulo);
            }

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        BookItem item = (BookItem)getArguments().getSerializable(ARG_ITEM_ID);
        View vPar=inflater.inflate(R.layout.fragment_detail_par, container, false);
        setContentPar(item,vPar);
        return vPar;

    }




    private void setContentPar(BookItem item,View activity)
    {
        try {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
