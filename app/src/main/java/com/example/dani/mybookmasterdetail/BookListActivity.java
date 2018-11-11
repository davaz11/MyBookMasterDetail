package com.example.dani.mybookmasterdetail;
import com.example.dani.mybookmasterdetail.helperClasses.DeviceType;
import com.example.dani.mybookmasterdetail.helperClasses.NetworkReceiver;
import com.example.dani.mybookmasterdetail.model.BookItem;
import com.example.dani.mybookmasterdetail.modelFireBase.DataSourceFireBase;
import com.example.dani.mybookmasterdetail.modelRealmORM.Book;
import com.example.dani.mybookmasterdetail.modelRealmORM.BookContent;
import com.example.dani.mybookmasterdetail.modelSQLite.BookSQLite;
import com.example.dani.mybookmasterdetail.parserXML.ParserXML;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.example.dani.mybookmasterdetail.modelFireBase.DataSourceFireBaseListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

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
public class BookListActivity extends AppCompatActivity implements DataSourceFireBaseListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    String CHANNEL_ID="123";
    static boolean isActive = false;

    //change type of loging
    private boolean useGmailLogin=false;

    public boolean isTablet;
    public boolean isPhone;

    private boolean mTwoPane;
    public static final String TAG = "MainActivity";
    public List<Book> bookListApp=null;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Realm realm;

    private DataSourceFireBase dataSourceFireBase;

    SwipeRefreshLayout swipeContainer;


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

        createNotificationChannel();

        //inicializando realm
        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();

        //inicializando firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sPref = sharedPrefs.getString("listPref", "Wi-Fi");


        LoadLayout();
        updateConnectedFlags();


        //conexion con base de datos de firebase
        dataSourceFireBase=new DataSourceFireBase();
        dataSourceFireBase.setActivity(this);
        dataSourceFireBase.addListener(this);




       //con conexión a internet
        if(refreshDisplay){
//fE7xOTWA-OY
          String t=dataSourceFireBase.GetFireBaseId();

          if(useGmailLogin) {
              //Login with gmail
              dataSourceFireBase.GmailAuth();
          }else{
              //Default loging
              dataSourceFireBase.SignInAndLoadData("danivaz25@gmail.com","firebaseTest");

          }

        }else{
            //sin conexión a internet

            LoadDataNotInternet();
        }



    }catch (Exception e)
    {
        Log.w(TAG, e.getMessage());
    }
    }


    //evento retorno que se dispara cuando firebase devuelve datos
    //todos los me´todos de firebase estan en DataSourceFireBase
    @Override
    public void onLoadDataFromFireBase(Object returnValue)
    {
        try {
            if(returnValue!=null)
            {
                bookListApp=(List<Book>)returnValue;

                LoadRecliclerView();

                //utilizo dos formas de guardar datos en local, realm y sqlite
                //LOAD DATA TO REALM
                BookContent.SetBooks(realm,bookListApp);
                Log.d(TAG, "Realm DataBase Updated"+BookContent.numberBooksUpdated);
                BookContent.numberBooksUpdated=0;


                //LOAD DATA TO SQLITE
                BookSQLite.InsertBookList(getApplicationContext(),bookListApp);
                Log.d(TAG, "SQLITE DataBase Updated");

                swipeContainer.setRefreshing(false);

            }else{
                LoadDataNotInternet();
            }
        } catch (Exception e) {
            Log.w(TAG, e.getMessage());
        }
    }



    //cuando el usurio se logea con gmail recive este evento
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == dataSourceFireBase.RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                dataSourceFireBase.ReadDatabaseFire();


            } else {

            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        isActive = true;
        if(mAuthListener!=null &mAuth!=null) {
            mAuth.addAuthStateListener(mAuthListener);

        }
    }


    private void createNotificationChannel() {
        try {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "hola", importance);
                channel.setDescription("hola");
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            refreshDisplay=true;
        } else {
            wifiConnected = false;
            mobileConnected = false;
            refreshDisplay=false;
        }
    }


    //endregion


    //region LAYOUT  B
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



        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        RefreshWithSwipe();
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
                        arguments.putSerializable(BookDetailFragmentPar.ARG_ITEM_ID, item);
                        BookDetailFragmentPar fragment = new BookDetailFragmentPar();
                        fragment.setArguments(arguments);
                        mParentActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        //Se envia el item seleccionado en el intent para utilizarlo en la siguiente pantalla
                        Context context = view.getContext();
                        Intent intent = new Intent(context, BookDetailActivity.class);
                        intent.putExtra(BookDetailFragmentPar.ARG_ITEM_ID, item);
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


        int positionCard=1;
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            try {

                View view;
                if(positionCard%2==0) {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_list_content_impar, parent, false);

                }else{
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_list_content_par, parent, false);
                }

                positionCard++;
                return new ViewHolder(view);

            }catch (Exception e) {
                Log.w(TAG,e.getMessage());
                return null;

            }
        }






        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            try{


                holder.mIdView.setText(Integer.toString(mValues.get(position).identificador));
                holder.mContentView.setText(mValues.get(position).title);



                holder.itemView.setTag(mValues.get(position));
                holder.itemView.setOnClickListener(mOnClickListener);


                setAnimation(holder.itemView, position);

            }catch (Exception e)
            {
                String u=e.getMessage();
            }
        }

        private int lastPosition = -1;
        /**
         * Here is the key method to apply the animation
         */
        private void setAnimation(View viewToAnimate, int position)
        {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition)
            {
                Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
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


    private void RefreshWithSwipe()
    {

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                updateConnectedFlags();

                if(refreshDisplay){


                    dataSourceFireBase.SignInAndLoadData("danivaz25@gmail.com","firebaseTest");


                }else{

                    LoadDataNotInternet();
                }

            }
        });

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


    //region LOAD_DATA_DATABASE
    private void LoadDataNotInternet()
    {
        try {
            bookListApp= BookContent.GetBooks(realm);
            LoadRecliclerView();

            List<Book> bListSqlite=BookSQLite.GetBookList(getApplicationContext(),null);



        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            swipeContainer.setRefreshing(false);

        }

    }

    //endregion

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        //now getIntent() should always return the last received intent
    }

    @Override
    public void onResume(){
        super.onResume();


        //se muestra dialogo cuando se borra un libro
        if (getIntent().getBooleanExtra("notificationDelete", false) == true) {
            getIntent().putExtra("notificationDelete", false);
            String title="Libro "+getIntent().getStringExtra("bookTitle") +" eliminado";
            ShowDialog(title);

            //se colapsan las notificaciones
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            getApplicationContext().sendBroadcast(it);

            //se muestra dialogo cuando no hay detalle y propone crear un libro nuevo
        }else if(getIntent().getBooleanExtra("notificationNotDetail", false) == true){
            getIntent().putExtra("notificationNotDetail", false);
            ShowDialogDetail("No existe Detalle de este libro ¿Desea Crearlo?",getIntent().getStringExtra("bookTitle"));

            //se colapsan las notificaciones
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            getApplicationContext().sendBroadcast(it);

        }

    }


    private void ShowDialogDetail(String title,final String titleBook){
        try {

            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle(title);



            builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                    Book testBook=new Book();
                    testBook.author="";
                    testBook.title=titleBook;
                    testBook.description="";
                    testBook.url_imagen="";
                    testBook.publication_date=new Date();
                    testBook.identificador=11;
                    dataSourceFireBase.AddFireBaseBook(testBook);
                }
            });
            builder.setNegativeButton(R.string.no,null);

            AlertDialog dialog= builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void ShowDialog(String title){
        try {

            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle(title);

            builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
          //  builder.setNegativeButton(R.string.no,null);

            AlertDialog dialog= builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isActive = false;
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


      //  dataSourceFireBase.LogOutAuth();

    }


}
