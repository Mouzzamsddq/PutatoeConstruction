package com.putatoe.putatoeconstructionserviceprovider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import io.paperdb.Paper;

public class ChangeDateDialog  extends DialogFragment  implements DatePicker.selectedDate {



    private TextView completionDateTextview;


    private TextView  okTextView,cancelTextView;

    private Context context;
    private Order order;

    public ChangeDateDialog(Context context,Order order) {

        this.context = context;
        this.order = order;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_date,null);
        completionDateTextview = view.findViewById(R.id.completionDateTextView);
        okTextView = view.findViewById(R.id.okTextView);
        cancelTextView   = view.findViewById(R.id.cancelTextView);




        okTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 dismiss();
                if(TextUtils.isEmpty(completionDateTextview.getText().toString()))
                {
                    Snackbar.make(getActivity().findViewById(R.id.rootRelative), "Please Select Completion date", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ContextCompat.getColor(getContext(), R.color.putatoeGreenColor)).show();
                }
                else {
                    String completionDate = completionDateTextview.getText().toString();

                    SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        Date d = f.parse(completionDate);
                        long milliseconds = d.getTime();





                         updateOrder(order, String.valueOf(milliseconds));



                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }




            }
        });
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



        completionDateTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePicker datePicker = new DatePicker();
                datePicker.show(getFragmentManager() , "Date_Picker");

            }
        });
        builder.setView(view);








        return  builder.create();

    }

    @Override
    public void setDate(String selectedDate, int value) {
        completionDateTextview.setText(selectedDate);

    }
    private void  updateOrder(Order order, String completionDate)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                "https://putatoeapp.firebaseio.com/PUTATOECONSTRUCTION/"+ Paper.book().read("BuisnessName")+
                        "/ALLACTIVITY/ORDERS"
        ).child(order.getCustomerNumber());
        String orderId  = order.getOrderId();
        char[] characterList = orderId.toCharArray();


        String updatedOrderId="";
        for(int i=0;i<characterList.length;i++)
        {
            if(i==0)
            {
                continue;
            }
            else {
                updatedOrderId+=characterList[i];
            }
        }
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("completionDate",completionDate);
        databaseReference.child(updatedOrderId).updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "Order Completion date updated", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
