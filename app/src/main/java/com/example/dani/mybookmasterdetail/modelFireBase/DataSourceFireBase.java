package com.example.dani.mybookmasterdetail.modelFireBase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import com.example.dani.mybookmasterdetail.logger.Log;
import com.example.dani.mybookmasterdetail.modelRealmORM.Book;
import com.example.dani.mybookmasterdetail.modelRealmORM.BookContent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class DataSourceFireBase{

    public  final String TAG = "DataSourceFireBase";

    protected  AppCompatActivity activity;

    protected  FirebaseAuth mAuth;
    protected  FirebaseDatabase database;
    protected  FirebaseAuth.AuthStateListener mAuthListener;

    private   List<DataSourceFireBaseListener> listeners = new ArrayList<DataSourceFireBaseListener>();




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


    //https://firebase.google.com/docs/auth/android/start/
    public void SignInAndLoadData(String email, String password)
    {
        try {

            //conexion con base de datos de firebase
            mAuth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:TRUE", null);


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



    protected  void ReadDatabaseFire()
    {

        database.getReference().addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    Log.d(TAG, "Data from FireBase changed");

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
