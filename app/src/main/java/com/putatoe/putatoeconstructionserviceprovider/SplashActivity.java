package com.putatoe.putatoeconstructionserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import needle.Needle;
import needle.UiRelatedTask;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //code to remove the action bar and status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);




        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                Paper.init(SplashActivity.this);
                Boolean result = Paper.book().contains("owner");
                Boolean result1 = Paper.book().contains("customer");
                Boolean result2 = Paper.book().contains("owners");





                if (result)
                {
                    return "owner";
                }
                else if(result1)
                {
                    return "customer";
                }
                else if(result2)
                {
                    return  "owners";
                }
                return "none";

            }

            @Override
            protected void thenDoUiRelatedWork(final String result) {

                if(result.equals("owner") || result.equals("owners")){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                            finish();
                        }
                    }, 2000);

                }
                else if(result.equals("customer"))
                {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SplashActivity.this,AllOrdersActivity.class));
                            finish();
                        }
                    }, 2000);


                }

                else{

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                            finish();
                        }
                    }, 2500);

                }
            }
        });



    }










}