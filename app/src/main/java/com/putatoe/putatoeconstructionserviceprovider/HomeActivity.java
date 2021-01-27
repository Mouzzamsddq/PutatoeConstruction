package com.putatoe.putatoeconstructionserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Merlin;
import com.putatoe.putatoeconstructionserviceprovider.Fragment.AllActivityFragment;
import com.putatoe.putatoeconstructionserviceprovider.Fragment.CalculatorFragment;
import com.putatoe.putatoeconstructionserviceprovider.Fragment.HomeFragment;
import com.putatoe.putatoeconstructionserviceprovider.Fragment.SummaryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.putatoe.putatoeconstructionserviceprovider.Fragment.TrackOrderFragment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import needle.Needle;
import needle.UiRelatedTask;

public class HomeActivity extends AppCompatActivity {



    private FrameLayout fragmentContainer;


     Fragment selectedFragment= null;
    private AppUpdateManager appUpdateManager;
    int MY_REQUEST_CODE=200;

    List<String> materialList,quantityList;


    List<String>  subMaterialList;






    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {


                case R.id.navigation_home:
                    selectedFragment = new HomeFragment();
                    break;

                case R.id.navigation_activitye:
                    selectedFragment = new AllActivityFragment();
                    break;

                case R.id.navigation_summary:
                    selectedFragment = new SummaryFragment();
                    break;

                case R.id.navigation_calculator:
                    selectedFragment = new CalculatorFragment();
                    break;

                case R.id.navigation_trackOrders:
                    selectedFragment = new TrackOrderFragment();
                    break;

            }


            if(selectedFragment != null)
            {
                if(selectedFragment instanceof  HomeFragment ) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment,"home")
                            .commit();
                }
                else
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer , selectedFragment)
                            .commit();
                }
            }

            return true;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_home);

        Paper.init(HomeActivity.this);
        bottomNavigationView = findViewById(R.id.bottomNavigationMenu);
        fragmentContainer = findViewById(R.id.fragmentContainer);
        materialList = new ArrayList<>();
        quantityList = new ArrayList<>();
        subMaterialList = new
                ArrayList<>();
        getMaterialList();
        getQuantityList();


        bottomNavigationView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener);



        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer , new HomeFragment(),"home")
                .commit();


        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this);

        // Don't need to do this here anymore
        // Returns an intent object that you use to check for an update.
        //Task<AppUpdateInfo> appUpdateInfo = appUpdateManager.getAppUpdateInfo();



        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {

                            // Checks that the platform will allow the specified type of update.
                            if ((appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
                                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))
                            {
                                // Request the update.
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            AppUpdateType.IMMEDIATE,
                                            this,MY_REQUEST_CODE
                                    );
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }
                        });






    }


    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {


        if(!(fragment instanceof CalculatorFragment))
        {
            checkConnection();
        }

    }

    private void checkConnection()
    {
        Needle.onBackgroundThread().execute(new UiRelatedTask<Boolean>() {
            /* access modifiers changed from: protected */
            public Boolean doWork() {
                try {
                    InetAddress inetAddress = InetAddress.getByName("www.google.com");
                    Log.e("Inet Address", inetAddress.getHostAddress());
                    return Boolean.valueOf(!inetAddress.equals(""));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    return Boolean.valueOf(false);
                }
            }

            /* access modifiers changed from: protected */
            public void thenDoUiRelatedWork(Boolean result) {

                if (result.booleanValue()) {


                    return;
                }
                Snackbar.make(findViewById(R.id.rootRelative), "No Internet, Please check your internet connection", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.putatoeGreenColor)).show();

                }

        });
    }


    private void getMaterialList()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+ Paper.book().read("BuisnessName")+"/CompanyValue/List/MaterialList"
        );


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                materialList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                   materialList.add(snapshot1.getKey());
                }

                Paper.book().write("MaterialList",materialList);

                getSubMaterialLists();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getQuantityList()

    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+ Paper.book().read("BuisnessName")+"/CompanyValue/List/QuantityList"
        );


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quantityList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    quantityList.add(snapshot1.getKey());
                }

                Paper.book().write("QuantityList",quantityList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getSubMaterialLists()
    {
        List<String> materialList = Paper.book().read("MaterialList");


        if(!materialList.isEmpty()) {


            for (String str : materialList) {
                getItemList(str);
            }
        }
    }



    private void getItemList(String materialType)
    {
        subMaterialList.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")
                        +"/CompanyValue/List/MaterialList");


        databaseReference.child(materialType).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subMaterialList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    subMaterialList.add(snapshot1.getKey());
                }


                Paper.book().write(materialType+"List",subMaterialList);





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }





}
