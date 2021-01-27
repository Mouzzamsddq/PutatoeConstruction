package com.putatoe.putatoeconstructionserviceprovider.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.putatoe.putatoeconstructionserviceprovider.Adapter.ViewPagerAdapter;
import com.putatoe.putatoeconstructionserviceprovider.R;


public class TrackOrderFragment extends Fragment {



    static private TabLayout tabLayout;
    static private ViewPager viewPager;

    static ViewPagerAdapter viewPagerAdapter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_track_order, container, false);


        tabLayout =view.findViewById(R.id.tabLayout);
        viewPager =view.findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(viewPager);

        setUpViewPager();





        return view;
    }


    private void setUpViewPager()
    {


        viewPagerAdapter.addFragment(new PendingOrdersFragment(),"Pending");
        viewPagerAdapter.addFragment(new DeliveredOrderFragment(),"Completed");
        viewPagerAdapter.addFragment(new DeclineFragment(),"Declined");

        viewPagerAdapter.notifyDataSetChanged();

        viewPager.setAdapter(viewPagerAdapter);



    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
    }

}