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

import com.google.android.material.snackbar.Snackbar;
import com.putatoe.putatoeconstructionserviceprovider.Adapter.ViewPagerAdapter;
import com.putatoe.putatoeconstructionserviceprovider.R;
import com.google.android.material.tabs.TabLayout;

import java.net.InetAddress;
import java.net.UnknownHostException;

import needle.Needle;
import needle.UiRelatedTask;


public class AllActivityFragment extends Fragment {


    static private TabLayout tabLayout;
   static private ViewPager viewPager;

    static ViewPagerAdapter viewPagerAdapter;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_activity, container, false);

        tabLayout =view.findViewById(R.id.tabLayout);
        viewPager =view.findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(viewPager);

        setUpViewPager();

//        viewPager.setCurrentItem(0);
        Log.d("ViewPager","In On Create AllActivity");


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


}