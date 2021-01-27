package com.putatoe.putatoeconstructionserviceprovider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.putatoe.putatoeconstructionserviceprovider.Fragment.TrackOrderFragment;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;

public class EditDialog  extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private TextView okButton,cancelButton,editDateTextView,editStatusTextView;

    private RadioButton statusRadioButton,comletionRadioButton;


    private Order order;
    private Context context;
    int position =0;








    public EditDialog(Order order,Context context) {
        this.order= order;
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_dialog,null);

        okButton = view.findViewById(R.id.okButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        comletionRadioButton = view.findViewById(R.id.completionRadioButton);
        statusRadioButton = view.findViewById(R.id.statusRadioButton);
        editDateTextView = view.findViewById(R.id.editDateTextView);
        editStatusTextView = view.findViewById(R.id.editStatusTextView);


        editStatusTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comletionRadioButton.isChecked())
                {
                    comletionRadioButton.setChecked(false);
                    statusRadioButton.setChecked(true);
                }
                else
                {
                    statusRadioButton.setChecked(true);
                }
            }
        });

        editDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statusRadioButton.isChecked())
                {
                    statusRadioButton.setChecked(false);
                    comletionRadioButton.setChecked(true);
                }
                else
                {
                    comletionRadioButton.setChecked(true);
                }
            }
        });
        comletionRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comletionRadioButton.isChecked())
                {
                    statusRadioButton.setChecked(false);
                }
            }
        });

        statusRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statusRadioButton.isChecked())
                {
                    comletionRadioButton.setChecked(false);
                }
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!statusRadioButton.isChecked()  && !comletionRadioButton.isChecked())
                {
                    Snackbar.make(getActivity().findViewById(R.id.rootRelative), "Please choose any one option", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ContextCompat.getColor(getContext(), R.color.putatoeGreenColor)).show();
                }
                else
                {
                     if(statusRadioButton.isChecked())
                     {
                         dismiss();
                         showDialog(order);
                     }
                     else
                     {

                         dismiss();

                           ChangeDateDialog changeDateDialog = new ChangeDateDialog(context,order);
                           changeDateDialog.show(getFragmentManager(),"change date");
                     }
                }


            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dismiss();
            }

        });
        builder.setView(view);








        return builder.create();
    }


    private void showDialog(Order order)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change Order Status");
        List<String> orderStatusList = new ArrayList<>();
        orderStatusList.add("Pending");
        orderStatusList.add("Completed");
        orderStatusList.add("Declined");

        ArrayAdapter arrayAdapter  = new ArrayAdapter(getActivity(), android.R.layout.select_dialog_singlechoice,orderStatusList);
        builder.setSingleChoiceItems(arrayAdapter, position , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                position = which;
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

//
                updateOrder(order,orderStatusList.get(position),getActivity());

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private void  updateOrder(Order order, String orderStatus, FragmentActivity fragmentActivity)
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
        String completionTimeStamp = String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("orderStatus",orderStatus);
        hashMap.put("completionDate",completionTimeStamp);
        databaseReference.child(updatedOrderId).updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "order status updated", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {

    }
}
