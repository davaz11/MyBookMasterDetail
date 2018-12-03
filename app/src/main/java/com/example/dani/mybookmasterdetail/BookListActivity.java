package com.example.dani.mybookmasterdetail;
import com.example.dani.mybookmasterdetail.helperClasses.DeviceType;
import com.example.dani.mybookmasterdetail.helperClasses.DownloadImageTask;
import com.example.dani.mybookmasterdetail.helperClasses.NetworkReceiver;
import com.example.dani.mybookmasterdetail.menuAction.ShareData;
import com.example.dani.mybookmasterdetail.model.BookItem;
import com.example.dani.mybookmasterdetail.modelFireBase.DataSourceFireBase;
import com.example.dani.mybookmasterdetail.modelRealmORM.Book;
import com.example.dani.mybookmasterdetail.modelRealmORM.BookContent;
import com.example.dani.mybookmasterdetail.modelSQLite.BookSQLite;
import com.example.dani.mybookmasterdetail.parserXML.ParserXML;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.util.IOUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import com.example.dani.mybookmasterdetail.modelFireBase.DataSourceFireBaseListener;

//import com.mikepenz.materialdrawer.DrawerBuilder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;
import io.realm.Realm;

import static java.security.AccessController.getContext;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link BookDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class BookListActivity extends AppCompatActivity implements
        DataSourceFireBaseListener,
        NavigationView.OnNavigationItemSelectedListener
 {

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
    DrawerLayout drawer;

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
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {

                    TextView textName=(TextView)findViewById(R.id.drawer_name_user);
                    TextView textEmail=(TextView)findViewById(R.id.drawer_email_user);
                    if(textName!=null) textName.setText(user.getDisplayName());
                    if(textEmail!=null)textEmail.setText(user.getEmail());
                    Uri urlImage =user.getPhotoUrl();

                       if(urlImage!=null){
                         String imageString= urlImage.toString();
                       }


                }


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


    //region SEARCH_MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_main_toolbar, menu);


            // Get the search menu.
            android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView)menu.findItem(R.id.app_bar_menu_search).getActionView();


            android.support.v7.widget.SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, SharedData.GetSearchDataDropDown());

            searchAutoComplete.setAdapter(newsAdapter);

            // Listen to search view item on click event.
            searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                    String query=(String)adapterView.getItemAtPosition(itemIndex);

                    bookListApp=SearchBooks(query);
                    LoadRecliclerView();

               }
            });

            searchAutoComplete.setOnFocusChangeListener(new AdapterView.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
               if(!hasFocus) {
                   bookListApp = SharedData.GetBookList();
                   LoadRecliclerView();
               }
                }

            });

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<Book> SearchBooks(String query){
        try {
            SharedData.GetSearchBookList().clear();

            //se busca en la lista total de libros
            for(Book b:SharedData.GetBookList()){
                if(b.title==query){

                    //la busqueda se guarda en una lista aparte
                    SharedData.GetSearchBookList().add(b);
                }
            }

            //la lista de la activity es siempre lo que se visualiza en pantalla
            return SharedData.GetSearchBookList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    //endregion



    //region LAYOUT
    private void  LoadLayout(){


        //setContentView(R.layout.activity_item_list);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }


     private void WriteResourceFileToAndroidDataFile(int idRawFile){
         InputStream inputStream = null;
        // OutputStream outputStream = null;
         FileOutputStream outputStream=null;

         try {

             inputStream = getApplicationContext().getResources().openRawResource(idRawFile);


             String fileDirectory=getApplicationContext().getFilesDir()+File.separator+"images"+File.separator;

             File file = new File(fileDirectory, "profile_image.jpg");
             outputStream = new FileOutputStream(file);

             int read = 0;
             byte[] bytes = new byte[1024];

             while ((read = inputStream.read(bytes)) != -1) {
                 outputStream.write(bytes, 0, read);
             }

             outputStream.close();


         } catch (IOException e) {
             e.printStackTrace();
         } finally {
             if (inputStream != null) {
                 try {
                     inputStream.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
             if (outputStream != null) {
                 try {
                     // outputStream.flush();
                     outputStream.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }

             }
         }

     }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.share: {

                try {
                    //se guarda una imagen de resources  en disco
                    WriteResourceFileToAndroidDataFile(R.raw.profile_image);


                    // String ur="content://" + getPackageName() + "/images/profile_image.jpg";
                    String fileDirectory=getApplicationContext().getFilesDir()+File.separator+"images"+File.separator+"profile_image.jpg";
                    File fileFromDisc=new File(fileDirectory);



                    InputStream inputStream = getApplicationContext().getResources().openRawResource(R.raw.profile_image);

                    File fileFromResource= File.createTempFile("jpg",".jpg");
                    OutputStream ou = new FileOutputStream(fileFromResource);
                    IOUtils.copyStream(inputStream, ou);
                    ou.close();






                    Uri ur=FileProvider.getUriForFile(this,"com.example.dani.mybookmasterdetail.fileprovider",fileFromDisc);

                    //Uri uri= Uri.fromFile(f);
                    // Uri uri=Uri.parse("R.drawable.america");
                    // Uri contentUri = FileProvider.getUriForFile(this, "com.mydomain.fileprovider", f);
                    //Uri imageUri = Uri.parse(ur);
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Aplicació Android sobre llibres");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, ur);
                    shareIntent.setType("image/*");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent, "Compartir con"));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            }case R.id.copy: {

                // Gets a handle to the clipboard service.
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);

                ClipData clip = ClipData.newPlainText("simple text", "MyBookMasterDetail copypaste!");

                // Set the clipboard's primary clip.
                clipboard.setPrimaryClip(clip);

                //do somthing
                break;
            }case R.id.share_whatsapp: {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                whatsappIntent.setPackage("com.whatsapp");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
                try {
                    startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {

                }

                break;
            }
        }
        //close navigation drawer
        item.setChecked(false);

        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        try {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, bookListApp, mTwoPane));
        } catch (Exception e) {
            e.printStackTrace();
        }


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

                   SharedData.bookItem=item;

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
                        //intent.putExtra(BookDetailFragmentPar.ARG_ITEM_ID, item);
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

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_content_par, parent, false);

                return new ViewHolder(view);

            }catch (Exception e) {
                Log.w(TAG,e.getMessage());
                return null;

            }
        }






        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            try{


                //se cargan las imagenes de forma dinámica desde los datos en el xml
                // int idImage = getResources().getIdentifier(getContext().getPackageName() + ":drawable/" + item.url_imagen, null, null);
               //

               // new DownloadImageTask(image).execute(mValues.get(position).url_imagen);
               String urlImage=mValues.get(position).url_imagen;

               if(!urlImage.equals("")) {
                   ImageView image = holder.itemView.findViewById(R.id.imageView2);
                   Picasso.with(holder.mContentView.getContext()).load(urlImage).into(image);
               }


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
            ShowDialogDetail("No existe Detalle de este libro ¿Desea Crearlo?",getIntent().getStringExtra("bookId"));

            //se colapsan las notificaciones
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            getApplicationContext().sendBroadcast(it);

        }else if(getIntent().getBooleanExtra("notificationNotDelete", false) == true){
            getIntent().putExtra("notificationNotDelete", false);
            ShowDialog("El libro no existe");

            //se colapsan las notificaciones
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            getApplicationContext().sendBroadcast(it);
        }

    }


    private void ShowDialogDetail(String title,final String bookId){
        try {

            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle(title);




            builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                    Book testBook=new Book();
                    testBook.author="";
                    testBook.title="Book"+bookId;
                    testBook.description="";
                    testBook.url_imagen="";
                    testBook.publication_date=new Date();
                    testBook.identificador=Integer.parseInt(bookId);
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
