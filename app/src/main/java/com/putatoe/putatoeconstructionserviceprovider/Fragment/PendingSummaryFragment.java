package com.putatoe.putatoeconstructionserviceprovider.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.putatoe.putatoeconstructionserviceprovider.Adapter.PastOrdersAdapter;
import com.putatoe.putatoeconstructionserviceprovider.Adapter.PendingOrdersAdapter;
import com.putatoe.putatoeconstructionserviceprovider.DatePicker;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Order;
import com.putatoe.putatoeconstructionserviceprovider.R;

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


public class PendingSummaryFragment extends Fragment  implements DatePicker.selectedDate {





    private TextView selectDateTextView;

    private RecyclerView pendingSummaryRecyclerView;

    List<Order> pendingOrderList;
    PendingOrdersAdapter pendingAdapter;

    private LinearLayout noOrderLayout;
    List<Order> newOrderList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending_summary, container, false);
        selectDateTextView = view.findViewById(R.id.selectDateTextView);
        pendingSummaryRecyclerView = view.findViewById(R.id.pendingSummaryRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                pendingSummaryRecyclerView.setLayoutManager(linearLayoutManager);
        noOrderLayout = view.findViewById(R.id.noOrderLayout);
        noOrderLayout.setVisibility(View.INVISIBLE);
        pendingOrderList = new ArrayList<>();
        newOrderList = new ArrayList<>();
        pendingAdapter = new PendingOrdersAdapter(getContext() , pendingOrderList);
        pendingSummaryRecyclerView.setAdapter(pendingAdapter);
        checkConnection();


        getPendingOrders();


        String timestamp = String.valueOf(System.currentTimeMillis());

        Calendar calendar1 = Calendar.getInstance(Locale.getDefault());
        calendar1.setTimeInMillis(Long.parseLong(timestamp));
        String todayDate = DateFormat.format("dd-MM-yyyy",calendar1).toString();
        selectDateTextView.setText(todayDate);





        selectDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePicker datePicker = new DatePicker();
                datePicker.show(getFragmentManager() , "summary");


            }
        });


        selectDateTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getSpecificPendingOrders(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });









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
                pendingOrderList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Order order = dataSnapshot1.getValue(Order.class);
                        String timestamp = order.getCompletionDate();
                        //convert timestamp to dd/mm/yyyy mm:hh am/pm
                        Calendar calendar = Calendar.getInstance(Locale.getDefault());
                        calendar.setTimeInMillis(Long.parseLong(timestamp));
                        String postedDate = DateFormat.format("dd-MM-yyyy", calendar).toString();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");


                        try {
                            Date d1 = simpleDateFormat.parse(postedDate);
                            String currentTimeStamp = String.valueOf(System.currentTimeMillis());
                            Calendar calendar1 = Calendar.getInstance(Locale.getDefault());
                            calendar1.setTimeInMillis(Long.parseLong(currentTimeStamp));
                            String currentTime = DateFormat.format("dd-MM-yyyy", calendar1).toString();
                            Date d2 = simpleDateFormat.parse(currentTime);

                            if (d1.equals(d2)) {
                                if (order.getOrderStatus().equals("Pending")) {
                                    pendingOrderList.add(order);
                                }


                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }



                if(pendingOrderList.isEmpty())
                {
                    noOrderLayout.setVisibility(View.VISIBLE);
                    pendingSummaryRecyclerView.setVisibility(View.INVISIBLE);
                }






                Collections.sort(pendingOrderList, new Comparator<Order>() {
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



                pendingAdapter.notifyDataSetChanged();

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



    private void getSpecificPendingOrders(String date)
    {
        pendingSummaryRecyclerView.setVisibility(View.VISIBLE);
        noOrderLayout.setVisibility(View.INVISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+ Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
        );


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pendingOrderList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Order order = dataSnapshot1.getValue(Order.class);
                        String timestamp = order.getCompletionDate();
                        //convert timestamp to dd/mm/yyyy mm:hh am/pm
                        Calendar calendar = Calendar.getInstance(Locale.getDefault());
                        calendar.setTimeInMillis(Long.parseLong(timestamp));
                        String postedDate = DateFormat.format("dd-MM-yyyy", calendar).toString();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");


                        try {
                            Date d1 = simpleDateFormat.parse(postedDate);

                            Date d2 = new SimpleDateFormat("dd-MM-yyyy").parse(date);

                            if (d1.equals(d2)) {
                                if (order.getOrderStatus().equals("Pending")) {

                                    pendingOrderList.add(order);
                                }


                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }



                if(pendingOrderList.isEmpty()) {
                    noOrderLayout.setVisibility(View.VISIBLE);
                    pendingSummaryRecyclerView.setVisibility(View.INVISIBLE);
                }


                Collections.sort(pendingOrderList, new Comparator<Order>() {

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

                pendingAdapter = new PendingOrdersAdapter(getContext() ,  pendingOrderList);
                pendingSummaryRecyclerView.setAdapter(pendingAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void setDate(String selectedDate, int value) {
        selectDateTextView.setText(selectedDate);
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