package com.putatoe.putatoeconstructionserviceprovider.Fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.putatoe.putatoeconstructionserviceprovider.Adapter.SearchAdapter;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Order;
import com.putatoe.putatoeconstructionserviceprovider.POJO.SearchOutstanding;
import com.putatoe.putatoeconstructionserviceprovider.R;
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


public class SearchFragment extends Fragment {



    private RecyclerView searchRecyclerView;
    SearchAdapter searchAdapter;
    List<String>  searchList;


    private EditText searchEditText;
    private LinearLayout noOrderLayout;

    float totalIncoming=0;
    float totalOutgoing = 0;

    float outstanding,advance;


    List<SearchOutstanding> outstandingList;
    List<SearchOutstanding>  advanceList;


    List<String> employeeList;


    //Create a progress abr object
    private ProgressBar circularProgressBar;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);





        searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        outstandingList = new ArrayList<>();
        advanceList = new ArrayList<>();
        noOrderLayout = view.findViewById(R.id.noOrderLayout);
        //.init the circular progress bar
        circularProgressBar = view.findViewById(R.id.circularProgressBar);
        noOrderLayout.setVisibility(View.INVISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        searchRecyclerView.setLayoutManager(linearLayoutManager);
        searchList = new ArrayList<>();

        //attach search adapter to the recycler view
        searchAdapter = new SearchAdapter(getContext() , searchList);
        searchRecyclerView.setAdapter(searchAdapter);

        Paper.init(getContext());
        employeeList = Paper.book().read("EmployeeList");

        checkConnection();



        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if(TextUtils.isEmpty(s.toString()))
                {
                    getAllNumbers();
                    return;
                }
                boolean isNumber=false;
                try {
                    Integer.parseInt(s.toString());
                    isNumber = true;
                }
                catch (NumberFormatException e)
                {
                    isNumber = false;
                }

                if(isNumber)
                {

                    getFilteredList(s.toString());
                }
                else
                {
                    getFilterByName(s.toString());
                }




            }


            @Override
            public void afterTextChanged(Editable s) {



            }
        });


        getAllNumbers();




        return  view;
    }



    private void getAllNumbers()
    {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+ Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
        );

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(isAdded()) {
                    searchList.clear();
                    outstandingList.clear();
                    advanceList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {


                        if (Paper.book().read("userOrService").equals("owner")) {
                            if (!employeeList.contains(dataSnapshot.getKey())) {
                                searchList.add(dataSnapshot.getKey());
                            }
                        } else {
                            searchList.add(dataSnapshot.getKey());
                        }

                    }

                    sortSearchList(searchList);


                    if (searchList.isEmpty()) {
                        noOrderLayout.setVisibility(View.VISIBLE);
                        searchRecyclerView.setVisibility(View.INVISIBLE);
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getFilteredList(String searchText) {




        outstandingList.clear();
        advanceList.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
        );

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String customerNumber = dataSnapshot.getKey();
                    if(customerNumber.toLowerCase().contains(searchText.toLowerCase()))
                    {
                        if (Paper.book().read("userOrService").equals("owner")) {
                            {
                                if(!employeeList.contains(customerNumber))
                                {
                                    searchList.add(customerNumber);
                                }
                            }
                        }
                        else {
                            searchList.add(customerNumber);
                        }
                    }
                }

                sortSearchList(searchList);




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


    private void getFilterByName(String searchText)
    {



        outstandingList.clear();
        advanceList.clear();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
        );






        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {

                    String customerNumber = snapshot1.getKey();


                    for(DataSnapshot snapshot2 : snapshot1.getChildren())
                    {
                        Order order1 = snapshot2.getValue(Order.class);

                        String customerName = order1.getCustomerName();


                        if(customerName.toLowerCase().contains(searchText.toLowerCase()))
                        {

                            if (Paper.book().read("userOrService").equals("owner")) {
                                if(!employeeList.contains(snapshot1.getKey()))
                                {
                                    searchList.add(snapshot1.getKey());
                                }
                            }
                            else
                            {
                                searchList.add(snapshot1.getKey());
                            }
                        }


                    }





                }


                removeDuplicates(searchList);



                sortSearchList(Paper.book().read("SearchList"));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public static List<String> removeDuplicates(List<String> list)
    {

        // Create a new ArrayList
        List<String> newList = new ArrayList();

        // Traverse through the first list
        for (String element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        Paper.book().write("SearchList",newList);

        // return the new list
        return newList;
    }


    public static List<SearchOutstanding> removeDuplicates1(List<SearchOutstanding> list)
    {

        // Create a new ArrayList
        List<SearchOutstanding> newList = new ArrayList();

        // Traverse through the first list
        for (SearchOutstanding element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }


        Paper.book().write("NewList",newList);


        // return the new list
        return newList;
    }



    private void sortSearchList(List<String> searchList)
    {

        if(isAdded()) {



            for (String contactNumber : searchList) {
                getSpecificOutstanding(contactNumber);
            }
        }




    }




    private void getSpecificOutstanding(String mobileNumber)
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
        ).child(mobileNumber);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalIncoming=0;
                totalOutgoing=0;
                outstanding=0;


                if(isAdded()) {


                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Order order = snapshot1.getValue(Order.class);
                        if (order.getOrderStatus().equals("Completed")) {
                            if (order.getTransactionType().equals("Incoming")) {
                                totalIncoming += order.getTotalAmount();

                            } else {
                                totalOutgoing += order.getTotalAmount();
                            }
                        }


                    }

                    if (totalIncoming < totalOutgoing) {
                        outstanding = totalOutgoing - totalIncoming;
                        SearchOutstanding searchOutstanding = new SearchOutstanding(mobileNumber, outstanding);
                        outstandingList.add(searchOutstanding);


                    } else {
                        advance = totalIncoming - totalOutgoing;
                        SearchOutstanding searchAdvance = new SearchOutstanding(mobileNumber, advance);
                        advanceList.add(searchAdvance);
                    }


                    Collections.sort(outstandingList, new Comparator<SearchOutstanding>() {
                        @Override
                        public int compare(SearchOutstanding o1, SearchOutstanding o2) {
                            if (o1.getOutstanding() < o2.getOutstanding()) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }
                    });
                    Collections.sort(advanceList, new Comparator<SearchOutstanding>() {
                        @Override
                        public int compare(SearchOutstanding o1, SearchOutstanding o2) {
                            if (o1.getOutstanding() < o2.getOutstanding()) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }
                    });





                    List<SearchOutstanding> newList = new ArrayList<>();
                    newList.addAll(outstandingList);
                    newList.addAll(advanceList);


                    List<SearchOutstanding> searchOutstandings = removeDuplicates1(newList);

                    searchList.clear();
                    for (SearchOutstanding searchOutstanding : searchOutstandings) {
                        searchList.add(searchOutstanding.getMobileNumber());
                    }


                    searchAdapter.notifyDataSetChanged();


                }















            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }




}