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

import com.putatoe.putatoeconstructionserviceprovider.Adapter.ViewPagerAdapter;
import com.putatoe.putatoeconstructionserviceprovider.R;
import com.google.android.material.tabs.TabLayout;


public class SummaryFragment extends Fragment {




    static private TabLayout tabLayout;
    static private ViewPager viewPager;

    static ViewPagerAdapter viewPagerAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);


        tabLayout =view.findViewById(R.id.tabLayout);
        viewPager =view.findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(viewPager);


        setUpViewPager();

        

        return view;
    }



    private void setUpViewPager()
    {


        Log.d("ViewPager","in Set up view Pager");
        viewPagerAdapter.addFragment(new TodaySummaryFragment(),"Today Summary");
        viewPagerAdapter.addFragment(new PastSummaryFragment(),"Past Summary");
        viewPagerAdapter.addFragment(new PendingSummaryFragment(),"Pending Summary");

        viewPagerAdapter.notifyDataSetChanged();

        viewPager.setAdapter(viewPagerAdapter);



    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
    }

}