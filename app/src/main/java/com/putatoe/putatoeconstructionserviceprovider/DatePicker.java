package com.putatoe.putatoeconstructionserviceprovider;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.putatoe.putatoeconstructionserviceprovider.Fragment.DeliveredOrderFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatePicker  extends DialogFragment implements  DatePickerDialog.OnDateSetListener{


    private selectedDate selectedDateListener;
    List<Fragment> fragmentList;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {




        Calendar mCalender = Calendar.getInstance();
        int year = mCalender.get(Calendar.YEAR);
        int month = mCalender.get(Calendar.MONTH);
        int dayOfMonth = mCalender.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, dayOfMonth);


    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {

        Calendar mCalender = Calendar.getInstance();

        mCalender.set(Calendar.YEAR, year);
        mCalender.set(Calendar.MONTH, month);
        mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String fromDate= DateFormat.format("dd-MM-yyyy",mCalender).toString();
        if(getTag() != null && getTag().equals("From_Date_Picker"))
        {
            selectedDateListener.setDate(fromDate,0);

        }
        else if(getTag().equals("Home"))
        {
            selectedDateListener.setDate(fromDate,-1);
        }
        else if(getTag().equals("New Order"))
        {
            selectedDateListener.setDate(fromDate,-1);
        }
        else if(getTag().equals("Date_Picker"))
        {
            selectedDateListener .setDate(fromDate,-1);
        }
        else if(getTag().equals("summary"))
        {
            selectedDateListener.setDate(fromDate,-1);
        }
        else
        {

            selectedDateListener.setDate(fromDate,1);
        }






    }

    public interface selectedDate
    {
        void setDate(String selectedDate,int value);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);



        Log.d("kkk","getTag:"+getTag());

        if(getTag().equals("Home")) {
            selectedDateListener = (selectedDate) getActivity().getSupportFragmentManager().getFragments().get(0);
        }
        else if(getTag().equals("New Order")){
            selectedDateListener = (selectedDate) context;
        }
        else if(getTag().equals("Date_Picker"))
        {
            fragmentList = getFragmentManager().getFragments();
            selectedDateListener = (selectedDate) fragmentList.get(1);
        }
        else if(getTag().equals("summary"))
        {

//            selectedDateListener = (selectedDate) getFragmentManager().getFragments().get(1);

            fragmentList = getFragmentManager().getFragments();
            for(Fragment fragment : fragmentList)
            {
                if(fragment instanceof DeliveredOrderFragment)
                {
                    selectedDateListener  = (selectedDate) fragment;
                }
            }
        }

        else
        {
            fragmentList = getFragmentManager().getFragments();
            selectedDateListener = (selectedDate) fragmentList.get(1);
        }




    }
}
