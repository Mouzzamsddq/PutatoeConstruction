package com.putatoe.putatoeconstructionserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.putatoe.putatoeconstructionserviceprovider.Adapter.PastOrdersAdapter;
import com.putatoe.putatoeconstructionserviceprovider.Fragment.SummaryDialog;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class
SpecificOrdersActivity extends AppCompatActivity {


    private TextView customerNameTextView,customerNumberTextView;
    private ImageView backImageView;


    private RecyclerView specificOrderRecyclerView;
    PastOrdersAdapter specificOrderAdapter;
    List<Order> specificOrderList;
    private RelativeLayout fabLayout;

    float totalIncoming,totalOutgoing;
    List<Order> newOrderList;


    TextView incomingTextView,outgoingTextView;
    private LinearLayout outgoingLayout;


    private ImageView callImageView,summaryImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_orders);


        //init the views
        newOrderList = new ArrayList<>();
        Paper.init(getApplicationContext());
        customerNameTextView = findViewById(R.id.customerNameTextView);
        callImageView = findViewById(R.id.callImageView);
        customerNumberTextView = findViewById(R.id.customerNumberTextView);
        backImageView = findViewById(R.id.backButton);
        specificOrderRecyclerView = findViewById(R.id.specicOrdersRecyclerView);
        fabLayout = findViewById(R.id.addLayout);
        incomingTextView = findViewById(R.id.income);
        outgoingTextView = findViewById(R.id.Outcome);
        outgoingLayout = findViewById(R.id.outgoingLayout);
        summaryImageView = findViewById(R.id.summaryIcon);
        summaryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SummaryDialog summaryDialog =  new SummaryDialog(Paper.book().read("specificNumber"));
                summaryDialog.show(getSupportFragmentManager() , "Summary Dialog");
            }
        });



                callImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callPhoneNumber();
                    }
                });




        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        specificOrderRecyclerView.setLayoutManager(linearLayoutManager);
        specificOrderList = new ArrayList<>();
        specificOrderAdapter = new PastOrdersAdapter(getApplicationContext() ,newOrderList,false,false);
        specificOrderRecyclerView.setAdapter(specificOrderAdapter);
        //set values
        customerNameTextView.setText(Paper.book().read("specificName"));
        customerNumberTextView.setText(Paper.book().read("specificNumber"));
        getSpecificOrders(Paper.book().read("specificNumber"));
        getTotalIncomingAmount(Paper.book().read("specificNumber"),incomingTextView,outgoingTextView);


        //when click on backImageView
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //when click on fab button
        fabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext() , NewOrderActivity.class));
                finish();
            }
        });
    }



    private void getSpecificOrders(String mobileNumber)
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
        ).child(mobileNumber);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                specificOrderList.clear();

                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    Order order = snapshot1.getValue(Order.class);


                        specificOrderList.add(order);


                }

//
                Collections.sort(specificOrderList, new Comparator<Order>() {
                    public int compare(Order o1, Order o2) {
                        return o2.getCompletionDate().compareTo(o1.getCompletionDate());
                    }
                });



                List<Order> updatedList = new ArrayList<>();
                if(!specificOrderList.isEmpty()) {
                    int count = 0;
                    Order previousOrder = specificOrderList.get(0);
                    for (Order order : specificOrderList) {
                        count++;

                        String previousTimeStamp = previousOrder.getTimestamp();
                        Calendar calendar = Calendar.getInstance(Locale.getDefault());
                        calendar.setTimeInMillis(Long.parseLong(previousTimeStamp));
                        String postedDate= DateFormat.format("dd-MM-yyyy",calendar).toString();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

                        String timestamp = previousOrder.getTimestamp();
                        Calendar calendar1 = Calendar.getInstance(Locale.getDefault());
                        calendar.setTimeInMillis(Long.parseLong(timestamp));
                        String postedDate1= DateFormat.format("dd-MM-yyyy",calendar1).toString();
                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy");


                        try {
                            Date d1 = simpleDateFormat.parse(postedDate);
                            Date d2 = simpleDateFormat1.parse(postedDate1);


                            if (d1.equals(d2)) {


                                updatedList.add(order);

                                if(count == specificOrderList.size())
                                {

                                    Collections.sort(updatedList, new Comparator<Order>() {
                                        public int compare(Order o1, Order o2) {
                                            int firstOrderNo = getOrderNo(o1.getOrderId());
                                            int secondOrderNo = getOrderNo(o2.getOrderId());


                                            if (firstOrderNo < secondOrderNo) {
                                                return 1;
                                            } else {
                                                return -1;
                                            }

                                        }
                                    });








                                    newOrderList.addAll(updatedList);
                                    updatedList.clear();


                                }


                            }
                            else {





                                Collections.sort(updatedList, new Comparator<Order>() {
                                    public int compare(Order o1, Order o2) {
                                        int firstOrderNo = getOrderNo(o1.getOrderId());
                                        int secondOrderNo = getOrderNo(o2.getOrderId());


                                        if (firstOrderNo < secondOrderNo) {
                                            return 1;
                                        } else {
                                            return -1;
                                        }

                                    }
                                });







                                List<String> checkList  = new ArrayList<>();

                                for(Order order1 : updatedList)
                                {
                                    checkList.add(order1.getOrderId());
                                }

                                newOrderList.addAll(updatedList);
                                updatedList.clear();



                                updatedList.add(order);
                                previousOrder = order;





                                if(count == specificOrderList.size())
                                {
                                    newOrderList.add(previousOrder);
                                }




                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }








                    }

                }





                specificOrderAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getTotalIncomingAmount(String mobileNumber,TextView text1 , TextView text2)
    {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
        ).child(mobileNumber);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    Order order = snapshot1.getValue(Order.class);
                    if(order.getOrderStatus().equals("Completed")) {
                        if (order.getTransactionType().equals("Incoming")) {
                            totalIncoming += order.getTotalAmount();

                        } else {
                            totalOutgoing += order.getTotalAmount();
                        }
                    }


                }
                float outstanding=0,advance=0;
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


    public void callPhoneNumber()
    {
        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(SpecificOrdersActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);

                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + Paper.book().read("specificNumber")));
                startActivity(callIntent);

            }
            else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + Paper.book().read("specificNumber")));
                startActivity(callIntent);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        if(requestCode == 101)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callPhoneNumber();
            }
            else
            {
                Log.e("Permission", "Permission not Granted");
            }
        }
    }


    private int getOrderNo(String orderId)
    {
        char [] orderIdArray = orderId.toCharArray();

        String orderNo="";
        for(char ch : orderIdArray)
        {
            if(Character.isDigit(ch))
            {
                orderNo+=ch;
            }
        }


        return Integer.parseInt(orderNo);

    }
}