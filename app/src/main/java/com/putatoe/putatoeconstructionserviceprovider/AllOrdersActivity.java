package com.putatoe.putatoeconstructionserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.putatoe.putatoeconstructionserviceprovider.Adapter.PastOrdersAdapter;
import com.putatoe.putatoeconstructionserviceprovider.Fragment.CustomerSummaryDialog;
import com.putatoe.putatoeconstructionserviceprovider.Fragment.SummaryDialog;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Customer;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Order;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.paperdb.Paper;
import needle.Needle;
import needle.UiRelatedTask;

//comment to check branch is created or not

public class AllOrdersActivity extends AppCompatActivity {


    private RecyclerView orderRecyclerView;
    PastOrdersAdapter orderAdapter;
    List<Order> orderList;


    private ShimmerFrameLayout shimmerFrameLayout;

    private AlertDialog alertDialog;

    private RelativeLayout rootRelativeLayout;


  private TextView logoutTextView,buisnessNameTextView;


    private AppUpdateManager appUpdateManager;
    private ImageView summaryImageView;



    int MY_REQUEST_CODE=200;


    private LinearLayout noOrderLayout;


    int totalIncoming,totalOutgoing;


    TextView incomingTextView,outgoingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);




        orderRecyclerView = findViewById(R.id.orderDetailsRecyclerView);
        shimmerFrameLayout = findViewById(R.id.shimmerViewContainer);
        rootRelativeLayout = findViewById(R.id.rootRelativeLayout);
        logoutTextView = findViewById(R.id.logoutTextView);
        summaryImageView = findViewById(R.id.summaryIcon);


        buisnessNameTextView = findViewById(R.id.buisnessNameTextView);
        noOrderLayout = findViewById(R.id.noOrderLayout);
        incomingTextView = findViewById(R.id.income);
        outgoingTextView = findViewById(R.id.Outcome);
        noOrderLayout.setVisibility(View.INVISIBLE);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        orderRecyclerView.setLayoutManager(linearLayoutManager);
        orderList = new ArrayList<>();
        orderAdapter = new PastOrdersAdapter(AllOrdersActivity.this,orderList,true,false);
        orderRecyclerView.setAdapter(orderAdapter);


        //set buisness name on action bar
        buisnessNameTextView.setText(Paper.book().read("BuisnessName"));
        Customer customer = Paper.book().read("customer");
        getTotalIncomingAmount(customer.getContact(),incomingTextView,outgoingTextView);
        summaryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerSummaryDialog customerSummaryDialog =  new CustomerSummaryDialog(customer.getContact());
                customerSummaryDialog.show(getSupportFragmentManager() , "Customer Summary Dialog");
            }
        });



        //when click on logout layout
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Needle.onBackgroundThread().execute(new UiRelatedTask<Boolean>() {
                    /* access modifiers changed from: protected */
                    public Boolean doWork() {
                        Paper.init(AllOrdersActivity.this);
                        Paper.book().destroy();
                        return Boolean.valueOf(true);
                    }

                    /* access modifiers changed from: protected */
                    public void thenDoUiRelatedWork(Boolean result) {
                        startActivity(new Intent(AllOrdersActivity.this, LoginActivity.class));
                        finishAffinity();
                    }
                });

            }
        });


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
                    AllOrdersActivity.this.shimmerFrameLayout.stopShimmerAnimation();
                    AllOrdersActivity.this.shimmerFrameLayout.setVisibility(View.GONE);
                    rootRelativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.putatoeBackground));
                    AllOrdersActivity.this.orderRecyclerView.setVisibility(View.VISIBLE);

                    return;
                }
                AllOrdersActivity.this.orderRecyclerView.setVisibility(View.INVISIBLE);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AllOrdersActivity.this);
                alertBuilder.setView((int) R.layout.no_internet);
                alertBuilder.setCancelable(false);
                AllOrdersActivity.this.alertDialog = alertBuilder.create();
                AlertDialog alertDialog2 = AllOrdersActivity.this.alertDialog;
                if (alertDialog2 != null && !alertDialog2.isShowing()) {
                    AllOrdersActivity.this.alertDialog.show();
                }
            }
        });



        getOrders();



    }


    private void getOrders()
    {
        Customer customer = Paper.book().read("customer");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS")
                .child(customer.getContact());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    Order order = snapshot1.getValue(Order.class);
                    orderList.add(order);
                }



                if(orderList.isEmpty())
                {
                    noOrderLayout.setVisibility(View.VISIBLE);
                    orderRecyclerView.setVisibility(View.INVISIBLE);
                }



                Collections.sort(orderList, new Comparator<Order>() {
                    public int compare(Order o1, Order o2) {
                        return o1.getCompletionDate().compareTo(o2.getCompletionDate());
                    }
                });
                orderAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        AllOrdersActivity.this.shimmerFrameLayout.startShimmerAnimation();
    }


    private void getTotalIncomingAmount(String mobileNumber,TextView text1 , TextView text2)
    {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
        ).child(mobileNumber);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalIncoming=0;
                totalOutgoing=0;


                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    Order order = snapshot1.getValue(Order.class);
                    if(order.getTransactionType().equals("Incoming"))
                    {
                        totalIncoming+=order.getTotalAmount();

                    }
                    else
                    {
                        totalOutgoing+=order.getTotalAmount();
                    }


                }
                int outstanding=0,advance=0;
                if(totalIncoming < totalOutgoing)
                {
                    outstanding = totalOutgoing - totalIncoming;

                }
                else
                {
                    advance= totalIncoming-totalOutgoing;
                }
                text1.setText("₹"+advance);
                text2.setText("₹"+outstanding);













            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }






}