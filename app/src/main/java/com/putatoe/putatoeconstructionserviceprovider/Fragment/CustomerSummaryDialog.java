package com.putatoe.putatoeconstructionserviceprovider.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Order;
import com.putatoe.putatoeconstructionserviceprovider.R;

import io.paperdb.Paper;

public class CustomerSummaryDialog extends DialogFragment {


    private Button okButton;
    private TextView incomingTextView,outgoingTextView,customerNameTextView;
    String mobileNumber;
    int totalIncoming,totalOutgoing;


    public CustomerSummaryDialog(String mobileNumber) {
        this.mobileNumber = mobileNumber;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custum_summary_dialog,null);
        okButton = view.findViewById(R.id.okButton);
        incomingTextView = view.findViewById(R.id.incomingTextView);
        outgoingTextView = view.findViewById(R.id.outgoingTextView);
        customerNameTextView = view.findViewById(R.id.customerNameTextView);
        getCustomerName(customerNameTextView , mobileNumber);
        getTotalIncomingAmount(mobileNumber , incomingTextView , outgoingTextView);



        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();

            }
        });
        builder.setView(view);
        return builder.create();



    }


    private void getCustomerName(TextView textView,String customerNumber)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getContext().getResources().getString(R.string.database_url)+ Paper.book().read("BuisnessName") +"/ALLACTIVITY/ORDERS"
        ).child(customerNumber);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Order order = dataSnapshot.getValue(Order.class);
                    textView.setText(order.getCustomerName());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getTotalIncomingAmount(String mobileNumber,TextView text1 , TextView text2)
    {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
        ).child(mobileNumber);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    Order order = snapshot1.getValue(Order.class);
                    if(order.getTransactionType().equals("Incoming"))
                    {
                        totalIncoming+=order.getTotalAmount();

                    }
                    else
                    {
                        totalOutgoing+=order.getTotalAmount();
                    }


                }


                text1.setText("₹"+totalOutgoing);
                text2.setText("₹"+totalIncoming);













            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }









}
