package com.putatoe.putatoeconstructionserviceprovider.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.putatoe.putatoeconstructionserviceprovider.Adapter.PastOrdersAdapter;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Order;
import com.putatoe.putatoeconstructionserviceprovider.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
import needle.Needle;
import needle.UiRelatedTask;


public class TodayOrdersFragment extends Fragment {



    private RecyclerView todaysordersRecyclerView;
    PastOrdersAdapter pastOrdersAdapter;
    List<Order>  todayOrderList;


    private LinearLayout noOrderLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today_orders, container, false);
         checkConnection();

        todaysordersRecyclerView = view.findViewById(R.id.todayOrdersRecyclerView);
        noOrderLayout = view.findViewById(R.id.noOrderLayout);
        noOrderLayout.setVisibility(View.INVISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        todaysordersRecyclerView.setLayoutManager(linearLayoutManager);

        todayOrderList = new ArrayList<>();
        pastOrdersAdapter = new PastOrdersAdapter(getContext() , todayOrderList,false,false);
        todaysordersRecyclerView.setAdapter(pastOrdersAdapter);

        getTodayOrders();



        return view;
    }



    private void getTodayOrders()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+ Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
        );


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todayOrderList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {

                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        Order order = dataSnapshot1.getValue(Order.class);
                        String timestamp  = order.getCompletionDate();
                        //convert timestamp to dd/mm/yyyy mm:hh am/pm
                        Calendar calendar = Calendar.getInstance(Locale.getDefault());
                        calendar.setTimeInMillis(Long.parseLong(timestamp));
                        String postedDate= DateFormat.format("dd-MM-yyyy",calendar).toString();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");


                        try {
                            Date d1  = simpleDateFormat.parse(postedDate);

                            String currentTimeStamp = String.valueOf(System.currentTimeMillis());
                            Calendar calendar1 = Calendar.getInstance(Locale.getDefault());
                            calendar1.setTimeInMillis(Long.parseLong(currentTimeStamp));
                            String currentTime= DateFormat.format("dd-MM-yyyy",calendar1).toString();
                            Date d2 = simpleDateFormat.parse(currentTime);

                            if(d1.equals(d2))
                            {
                                if(order.getOrderStatus().equals("Completed"))
                                {

                                    todayOrderList.add(order);
                                }


                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }
                }


                if(todayOrderList.isEmpty())
                {
                    noOrderLayout.setVisibility(View.VISIBLE);
                    todaysordersRecyclerView.setVisibility(View.INVISIBLE);
                }

                Collections.sort(todayOrderList, new Comparator<Order>() {
                    
                    public int compare(Order o1, Order o2) {
                        int firstOrderNo = getOrderNo(o1.getOrderId());
                        int secondOrderNo = getOrderNo(o2.getOrderId());
                        


                        if(firstOrderNo < secondOrderNo)
                        {
                            return 1;
                        }
                        else
                        {
                            return -1;
                        }
                    }
                });


                pastOrdersAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                Snackbar.make(getActivity().findViewById(R.id.rootRelative), "No Internet, Please check your internet connection", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(ContextCompat.getColor(getContext(), R.color.putatoeGreenColor)).show();

            }

        });
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