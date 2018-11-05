package com.example.dani.mybookmasterdetail.modelFireBase;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.dani.mybookmasterdetail.modelRealmORM.Book;
import com.example.dani.mybookmasterdetail.modelRealmORM.BookContent;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DataSourceFireBase{

    public  final String TAG = "DataSourceFireBase";
    public  final int RC_SIGN_IN = 123;

    protected  AppCompatActivity activity;

    protected  FirebaseAuth mAuth;
    protected  FirebaseDatabase database;
    protected  FirebaseAuth.AuthStateListener mAuthListener;

    private   List<DataSourceFireBaseListener> listeners = new ArrayList<DataSourceFireBaseListener>();


    private boolean isConnectedToFireBase=false;


    public DataSourceFireBase(){

    }

    public void addListener(DataSourceFireBaseListener toAdd) {
        listeners.add(toAdd);
    }

    public  void setActivity(AppCompatActivity ac) {
        activity = ac;
    }

    public  void setFirebaseDatabase(FirebaseDatabase ac) {
        database = ac;
    }

    public  void setFirebaseAuth(FirebaseAuth ac) {
        mAuth = ac;
    }



    public String GetFireBaseId(){

        return FirebaseInstanceId.getInstance().getId();
    }

    public void LogOutAuth(){

        AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    public void GmailAuth(){

        try {

            //conexion con base de datos de firebase
            mAuth = FirebaseAuth.getInstance();

            //no hace falta volver a conectar si ya hay un usuario logeado
            FirebaseUser user=mAuth.getCurrentUser();
            if(user!=null){
                ReadDatabaseFire();
                return;
            }



            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            // Create and launch sign-in intent
            activity.startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



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

                       // LoadDataNotInternet();
                    }
                    // ...
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Se autentica en firebase
    public void SignInAndLoadData(String email, String password)
    {
        try {

            //conexion con base de datos de firebase
            mAuth = FirebaseAuth.getInstance();

            //no hace falta volver a conectar si ya hay un usuario logeado
            FirebaseUser user=mAuth.getCurrentUser();
            if(user!=null){
                ReadDatabaseFire();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:TRUE", null);

                                //si la autenticación es correcta se cargan datos
                                ReadDatabaseFire();


                            }else {
                                Log.w(TAG, "signInWithEmail:FALSE", task.getException());

                                // Notify everybody that may be interested.
                                for (DataSourceFireBaseListener hl : listeners) {
                                    hl.onLoadDataFromFireBase(null);
                                }

                            }



                        }


                    }).getException();
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

    //se cargan datos de firebase
    public  void ReadDatabaseFire()
    {

        database = FirebaseDatabase.getInstance();

        database.getReference().addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    Log.d(TAG, "Data from FireBase changed");

                    //se pasan los datos de firebase a una List de Books,
                    // no me han servido los  métodos genéricos que se proponen en la práctica así que he tenido que crear objeto nuevo y pasar atributo por atributo
                    // me prodrías mandar un ejemplo de como se hace la forma genérica?
                   List<Book> bookListApp= BookContent.ParseFireBaseDataToObject(dataSnapshot);

                    // Notify everybody that may be interested.
                    for (DataSourceFireBaseListener hl : listeners) {
                        hl.onLoadDataFromFireBase(bookListApp);
                    }


                } catch (Exception e) {
                    Log.w(TAG, "Error - Data from FireBase changed");
                    e.printStackTrace();
                }finally {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                // Notify everybody that may be interested.
                for (DataSourceFireBaseListener hl : listeners) {
                    hl.onLoadDataFromFireBase(null);
                }

            }
        });
    }




//endregion






}
