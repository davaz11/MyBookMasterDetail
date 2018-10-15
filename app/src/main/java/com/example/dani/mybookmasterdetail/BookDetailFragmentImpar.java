package com.example.dani.mybookmasterdetail;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dani.mybookmasterdetail.model.BookItem;
import com.example.dani.mybookmasterdetail.modelRealmORM.Book;


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained
 * in two-pane mode (on tablets)
 * on handsets.
 */
public class BookDetailFragmentImpar extends Fragment {
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
    public BookDetailFragmentImpar() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (getArguments().containsKey(ARG_ITEM_ID)) {

                Book item = (Book)getArguments().getSerializable(ARG_ITEM_ID);
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



    private void setContentImpar(Book item,View activity){

        try {
            TextView tViewNameDesc = (TextView) activity.findViewById(R.id.textView_item_detail);
            //lleno de texto para ver el scrollview
            tViewNameDesc.setText(item.description+item.description+item.description+item.description+item.description+item.description+item.description+item.description+item.description+item.description);

            //se cargan las imagenes de forma dinámica desde los datos en el xml
            int idImage = getResources().getIdentifier(getContext().getPackageName() + ":drawable/" + item.url_imagen, null, null);
            ImageView image = activity.findViewById(R.id.imageView2);
            image.setImageResource(idImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Book item = (Book)getArguments().getSerializable(ARG_ITEM_ID);
        View vImpar = inflater.inflate(R.layout.fragment_detail_impar, container, false);
        setContentImpar(item,vImpar);
        return vImpar;

    }


}
