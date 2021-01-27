package com.putatoe.putatoeconstructionserviceprovider.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.putatoe.putatoeconstructionserviceprovider.Adapter.PastOrdersAdapter;
import com.putatoe.putatoeconstructionserviceprovider.Adapter.PendingOrdersAdapter;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Order;
import com.putatoe.putatoeconstructionserviceprovider.R;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.paperdb.Paper;
import needle.Needle;
import needle.UiRelatedTask;


public class PendingOrdersFragment extends Fragment {




    private RecyclerView todaysordersRecyclerView;
    PendingOrdersAdapter pendingOrdersAdapter;
    List<Order>  todayOrderList;


    private LinearLayout noOrderLayout;

    List<Order> newOrderList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_pending_orders, container, false);

       checkConnection();
        todaysordersRecyclerView = view.findViewById(R.id.pendingOrdersRecyclerView);
        noOrderLayout = view.findViewById(R.id.noOrderLayout);
        noOrderLayout.setVisibility(View.INVISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        todaysordersRecyclerView.setLayoutManager(linearLayoutManager);

        todayOrderList = new ArrayList<>();
        newOrderList = new ArrayList<>();


        getPendingOrders();

        return view;
    }


    private void getPendingOrders()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+ Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
        );


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todayOrderList.clear();
                newOrderList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {

                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        Order order = dataSnapshot1.getValue(Order.class);
                        if(order.getOrderStatus().equals("Pending"))
                        {
                            todayOrderList.add(order);
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
                        return o2.getCompletionDate().compareTo(o1.getCompletionDate());
                    }
                });




                List<Order> updatedList = new ArrayList<>();
                if(!todayOrderList.isEmpty()) {
                    int count = 0;
                    Order previousOrder = todayOrderList.get(0);
                    for (Order order : todayOrderList) {
                         count++;

                        if (previousOrder.getCompletionDate().equals(order.getCompletionDate())) {
                            updatedList.add(order);

                            if(count == todayOrderList.size())
                            {
                                Collections.sort(updatedList, new Comparator<Order>() {
                                    public int compare(Order o1, Order o2) {
                                        int firstOrderNo = getOrderNo(o1.getOrderId());
                                        int secondOrderNo = getOrderNo(o2.getOrderId());


                                        if (firstOrderNo > secondOrderNo) {
                                            return -1;
                                        } else {
                                            return 1;
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


                                    if (firstOrderNo > secondOrderNo) {
                                        return -1;
                                    } else {
                                        return 1;
                                    }

                                }
                            });








                            newOrderList.addAll(updatedList);
                            updatedList.clear();


                            updatedList.add(order);
                            previousOrder = order;






                            if(count == todayOrderList.size())
                            {
                                newOrderList.add(previousOrder);
                            }




                        }

                    }

                }



                pendingOrdersAdapter = new PendingOrdersAdapter(getContext() , newOrderList);
                todaysordersRecyclerView.setAdapter(pendingOrdersAdapter);

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