package com.example.dani.mybookmasterdetail;
import com.example.dani.mybookmasterdetail.helperClasses.DeviceType;
import com.example.dani.mybookmasterdetail.helperClasses.NetworkReceiver;
import com.example.dani.mybookmasterdetail.logger.Log;
import com.example.dani.mybookmasterdetail.model.BookItem;
import com.example.dani.mybookmasterdetail.modelRealmORM.Book;
import com.example.dani.mybookmasterdetail.modelRealmORM.BookContent;
import com.example.dani.mybookmasterdetail.modelSQLite.BookSQLite;
import com.example.dani.mybookmasterdetail.parserXML.ParserXML;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.example.dani.mybookmasterdetail.helperClasses.InternetCheck;

import org.xmlpull.v1.XmlPullParserException;
import io.realm.Realm;


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
    public List<Book> bookListApp=null;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Realm realm;
    private boolean isLogin=false;



    //region NETWORK_PROPERTIES
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";

    private static boolean wifiConnected = false;
    private static boolean mobileConnected = false;
    public static boolean refreshDisplay = true;

    public static String sPref = null;

    private NetworkReceiver receiver = new NetworkReceiver();
    //endregion




    @Override
    protected void onCreate(Bundle savedInstanceState) {
    try {
        super.onCreate(savedInstanceState);

        //TO LOAD REALM DATA
        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();

        //conexion con base de datos de firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sPref = sharedPrefs.getString("listPref", "Wi-Fi");

        updateConnectedFlags();
        LoadLayout();

        if(refreshDisplay){


            SignInAndLoadData("danivaz25@gmail.com","firebaseTest");

        }else{

            LoadDataNotInternet();
        }


 //region TRASH
/*
//DETECTAR CONEXIÓN A INTERNET
       InternetCheck.Consumer con=new InternetCheck.Consumer() {
            @Override
            public void accept(Boolean internet) {



                if(internet)
                {


                    //mAuth.addAuthStateListener(mAuthListener);

                    BookListActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {



                            //LOAD FIREBASE DATA

                           // ReadDatabaseFire();


                           // SignInAndLoadData("danivaz25@gmail.com","firebaseTest");


                        }
                    });


                }else
                {


                    BookListActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            LoadDataNotInternet();

                        }
                    });


                }
            }
        };
        InternetCheck ch=new InternetCheck(con);


      /*  LoadCreateActivity();

        //LOAD FIREBASE DATA
       FirebaseDataBaseConnection();
        SignInWithEmailAndPassword("danivaz25@gmail.com","firebaseTest");

*/









        //TO LOAD XML DATA
       //bookItemList=parseXMLbooks();







/*
        //LOAD NOT INTERNET
        if(! isOnline()){
            LoadDataNotInternet();
        }else{
            //LOAD FIREBASE DATA
            FirebaseDataBaseConnection();
            SignInWithEmailAndPassword("danivaz25@gmail.com","firebaseTest");
        }
*/
//endregion

    }catch (Exception e)
    {
        String u=e.getMessage();
    }
    }



    @Override
    public void onStart() {
        super.onStart();

        if(mAuthListener!=null &mAuth!=null) {
            mAuth.addAuthStateListener(mAuthListener);

        }
    }



    //region NETWORKCONNECTION


    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    public void updateConnectedFlags() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected())
        {

            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }


    //endregion


    //region LAYOUT
    private void  LoadLayout(){

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


        //código para saber si se está ejecutando desde una tablet o desde un móvi
        // Para saber si es tablet
        isTablet= DeviceType.isTablet(getApplicationContext());
        // Para saber si es teléfono
        isPhone=DeviceType.isPhone(getApplicationContext());


    }


    private void LoadRecliclerView(){

        try {
            View recyclerView = findViewById(R.id.item_list);
            assert recyclerView != null;
            setupRecyclerView((RecyclerView) recyclerView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, bookListApp, mTwoPane));
    }



    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {


        private final BookListActivity mParentActivity;
        private final List<Book> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Book item = (Book) view.getTag();
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putSerializable(BookDetailFragmentImpar.ARG_ITEM_ID, item);
                        BookDetailFragmentImpar fragment = new BookDetailFragmentImpar();
                        fragment.setArguments(arguments);
                        mParentActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        //Se envia el item seleccionado en el intent para utilizarlo en la siguiente pantalla
                        Context context = view.getContext();
                        Intent intent = new Intent(context, BookDetailActivity.class);
                        intent.putExtra(BookDetailFragmentImpar.ARG_ITEM_ID, item);
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        SimpleItemRecyclerViewAdapter(BookListActivity parent,
                                      List<Book> items,
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
                holder.mContentView.setText(mValues.get(position).title);

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






    //endregion


    //region LOAD_DATA_FROM_XML
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

    //endregion

    //region LOAD_DATA_FROM_FIREBASE
    private void FirebaseDataBaseConnection()
    {
        try {


            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        GetCurrentUser();
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());


                        GetCurrentUser();
                        ReadDatabaseFire();

                    } else {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");

                        LoadDataNotInternet();
                    }
                    // ...
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        //https://firebase.google.com/docs/auth/android/start/
        private void SignInAndLoadData(String email, String password)
        {
            try {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "signInWithEmail:TRUE", null);

                                    GetCurrentUser();
                                    ReadDatabaseFire();



                                }else {
                                    Log.w(TAG, "signInWithEmail:FALSE", task.getException());

                                    LoadDataNotInternet();

                                }



                            }


                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        private void GetCurrentUser()
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {

                String name = user.getDisplayName();
                String email = user.getEmail();
                String uid = user.getUid();
                String uid2 = user.getUid();
            }
        }



        private void ReadDatabaseFire()
        {

           database.getReference().addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {
                       // dataFromFireBase = (Map<String, Object>) dataSnapshot.getValue();

                        Log.d(TAG, "Data from FireBase changed");


                        bookListApp=BookContent.ParseFireBaseDataToObject(dataSnapshot);

                        LoadRecliclerView();

                        //LOAD DATA TO REALM
                        BookContent.SetBooks(realm,bookListApp);

                        Log.d(TAG, "Realm DataBase Updated"+BookContent.numberBooksUpdated);
                        BookContent.numberBooksUpdated=0;


                        //LOAD DATA TO SQLITE
                        BookSQLite.InsertBookList(getApplicationContext(),bookListApp);
                        Log.d(TAG, "SQLITE DataBase Updated");




                    } catch (Exception e) {
                        Log.w(TAG, "Error - Data from FireBase changed");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                    LoadDataNotInternet();

                }
            });
        }


      private List<Book> DataSnapshotToList(DataSnapshot dataSnapshot)
      {

          try {


              GenericTypeIndicator<List<Book>> t = new GenericTypeIndicator<List<Book>>() {};
              Object o=dataSnapshot.getValue(t);
              List<Book> booksList = dataSnapshot.getValue(t);
              return booksList;


              /*Gson gson = new Gson();
              JsonElement s3 = gson.toJsonTree(dataSnapshot.getValue());
              Object obj=dataSnapshot.getValue();
              String s1 = gson.toJson(dataSnapshot.getValue());

              s3.isJsonArray();
              boolean v=s3.isJsonObject();

              JsonArray ar=s3.getAsJsonArray();

              JSONArray jsonArray = new JSONArray();
              realm.beginTransaction();
              realm.createOrUpdateAllFromJson(Book.class,jsonArray);
              realm.commitTransaction();*/
          } catch (Exception e) {
              e.printStackTrace();
              return null;
          }
      }


//endregion

    //region LOAD_DATA_DATABASE
    private void LoadDataNotInternet()
    {
        try {
            bookListApp= BookContent.GetBooks(realm);
            LoadRecliclerView();

            List<Book> bListSqlite=BookSQLite.GetBookList(getApplicationContext(),null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //endregion

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregisters BroadcastReceiver when app is destroyed.
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }

    }

}
