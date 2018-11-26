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

import com.example.dani.mybookmasterdetail.helperClasses.DownloadImageTask;
import com.example.dani.mybookmasterdetail.modelRealmORM.Book;
import com.squareup.picasso.Picasso;

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

        try {
            if (getArguments().containsKey(ARG_ITEM_ID)) {

               // Book item = (Book)getArguments().getSerializable(ARG_ITEM_ID);

                Book item=SharedData.bookItem;
                Activity activity = this.getActivity();


                //se setea el título
                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(item.title);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Book item = (Book)getArguments().getSerializable(ARG_ITEM_ID);
        View vPar=inflater.inflate(R.layout.fragment_detail_par, container, false);
        setContentPar(item,vPar);
        return vPar;

    }




    private void setContentPar(Book item,View activity)
    {
        try {
            //se setea texto
            DateFormat formater = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            String dateFormated=formater.format(item.publication_date);

            TextView tViewDate = (TextView) activity.findViewById(R.id.textView_detail_par_date2);
            tViewDate.setText(dateFormated);

            TextView tViewNameAutor = (TextView) activity.findViewById(R.id.textView_detail_par_nameAutor2);
            tViewNameAutor.setText(item.author);

            TextView tViewNameDesc = (TextView) activity.findViewById(R.id.textView_detail_par_nameDescription2);
            tViewNameDesc.setText(item.description);

            //se cargan las imagenes de forma dinámica desde los datos en el xml
           // int idImage = getResources().getIdentifier(getContext().getPackageName() + ":drawable/" + item.url_imagen, null, null);
            ImageView image = activity.findViewById(R.id.imageView);

           //new DownloadImageTask(image).execute(item.url_imagen);
           Picasso.with(getContext()).load(item.url_imagen).into(image);

            //image.setImageResource(idImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
