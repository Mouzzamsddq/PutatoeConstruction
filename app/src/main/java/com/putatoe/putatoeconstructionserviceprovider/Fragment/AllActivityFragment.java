package com.putatoe.putatoeconstructionserviceprovider.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.putatoe.putatoeconstructionserviceprovider.Adapter.ViewPagerAdapter;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Order;
import com.putatoe.putatoeconstructionserviceprovider.POJO.SearchOutstanding;
import com.putatoe.putatoeconstructionserviceprovider.R;
import com.google.android.material.tabs.TabLayout;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.paperdb.Paper;
import needle.Needle;
import needle.UiRelatedTask;


public class AllActivityFragment extends Fragment {


    static private TabLayout tabLayout;
   static private ViewPager viewPager;

    static ViewPagerAdapter viewPagerAdapter;


    List<SearchOutstanding> outstandingList;
    List<SearchOutstanding>  advanceList;
    List<String>  searchList;


    float totalIncoming=0;
    float totalOutgoing = 0;

    float outstanding,advance;
    //creating a image view for summary icon
    private ImageView summaryIconImageView;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_activity, container, false);

        tabLayout =view.findViewById(R.id.tabLayout);
        viewPager =view.findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(viewPager);
        outstandingList = new ArrayList<>();
        advanceList = new ArrayList<>();
        searchList = new ArrayList<>();

        //init the summary icon image view
        summaryIconImageView = view.findViewById(R.id.summaryIcon);


        //when click onn the summary icon
        summaryIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //show the business summary dialog
                BusinessSummaryDialog businessSummaryDialog = new BusinessSummaryDialog(Paper.book().read("TotalOutstanding"),Paper.book().read("TotalAdvance"));
                businessSummaryDialog.show(getFragmentManager(),"businessSummary");


            }
        });

        setUpViewPager();

        getAllNumbers();



        return view;
    }


    private void setUpViewPager()
    {


        viewPagerAdapter.addFragment(new TodayOrdersFragment(),"Today's Orders");
        viewPagerAdapter.addFragment(new PastOrdersFragment(),"Past Orders");
        viewPagerAdapter.addFragment(new SearchFragment(),"Search");

        viewPagerAdapter.notifyDataSetChanged();

        viewPager.setAdapter(viewPagerAdapter);

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("ViewPager","In On Attach");

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
    }


    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        checkConnection();
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


    private void getAllNumbers()
    {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+ Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
        );

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (isAdded()) {

                    searchList.clear();
                    outstandingList.clear();
                    advanceList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        searchList.add(dataSnapshot.getKey());
                    }

                    sortSearchList(searchList);

                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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






                    Paper.book().write("TotalOutstanding", getTotalOutstanding(outstandingList));
                    Paper.book().write("TotalAdvance", getTotalOutstanding(advanceList));
                }






            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private double getTotalOutstanding(List<SearchOutstanding> list)
    {
        double total=0;
        for(SearchOutstanding searchOutstanding : list)
        {
            total+=searchOutstanding.getOutstanding();

        }


    return total;


    }


}