package com.putatoe.putatoeconstructionserviceprovider.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.putatoe.putatoeconstructionserviceprovider.Adapter.PastOrdersAdapter;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Order;
import com.putatoe.putatoeconstructionserviceprovider.R;
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


public class PastOrdersFragment extends Fragment {




       private RecyclerView pastOrderRecyclerView;
       List<Order> pastOrderList;
       List<Order> newOrderList;
       PastOrdersAdapter pastOrdersAdapter;


       //no order layout
       private LinearLayout noOrderLayout;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_past_orders, container, false);
        pastOrderRecyclerView = view.findViewById(R.id.pastOrdersRecyclerView);
        noOrderLayout = view.findViewById(R.id.noOrderLayout);
        noOrderLayout.setVisibility(View.INVISIBLE);
        newOrderList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        pastOrderRecyclerView.setLayoutManager(linearLayoutManager);
        pastOrderList = new ArrayList<>();
        Paper.init(getContext());


        getPastOrders();


        return view;
    }



    private void getPastOrders()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+ Paper.book().read("BuisnessName") +"/ALLACTIVITY/ORDERS"
        );


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               pastOrderList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {

                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        Order order = dataSnapshot1.getValue(Order.class);

                        String timestamp = order.getCompletionDate();

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

                            if(d1.before(d2))
                            {
                                if(order.getOrderStatus().equals("Completed"))
                                {
                                    pastOrderList.add(order);
                                }


                            }


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }
                }


                if(pastOrderList.isEmpty())
                {
                    noOrderLayout.setVisibility(View.VISIBLE);
                    pastOrderRecyclerView.setVisibility(View.INVISIBLE);
                }





                Collections.sort(pastOrderList, new Comparator<Order>() {
                    public int compare(Order o1, Order o2) {
                        return o2.getCompletionDate().compareTo(o1.getCompletionDate());
                    }
                });




                List<Order> updatedList = new ArrayList<>();
                if(!pastOrderList.isEmpty()) {
                    int count = 0;
                    Order previousOrder = pastOrderList.get(0);
                    for (Order order : pastOrderList) {
                        count++;




                        if (previousOrder.getCompletionDate().equals(order.getCompletionDate())) {
                            updatedList.add(order);

                            if(count == pastOrderList.size())
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








                            newOrderList.addAll(updatedList);
                            updatedList.clear();



                            updatedList.add(order);
                            previousOrder = order;





                            if(count == pastOrderList.size())
                            {
                                newOrderList.add(previousOrder);
                            }




                        }

                    }

                }



                pastOrdersAdapter = new PastOrdersAdapter(getContext() , newOrderList,false,false);

                pastOrderRecyclerView.setAdapter(pastOrdersAdapter);






            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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