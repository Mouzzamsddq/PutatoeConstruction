package com.putatoe.putatoeconstructionserviceprovider.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Order;
import com.putatoe.putatoeconstructionserviceprovider.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import needle.Needle;
import needle.UiRelatedTask;


public class PastSummaryFragment extends Fragment implements com.putatoe.putatoeconstructionserviceprovider.DatePicker.selectedDate {




    private TextView selectFilterTextView;
    List<String> list;
    int position=0; //default position

    private TextView selectedMaterialTextView;
    private LinearLayout specificLinearLayout;
    //text view to show total incoming and outgoings
    private TextView todayTotalIncoming,todayTotalOutgoing;



    // store total incoming and outgoings
    float totalIncoming, totalOutgoing;
    float specificTotalIncoming,specificTotalOutgoing;

    //specific text view to show total incoming and outgoing for specific  item
    private TextView specificTotalIncomingTextView,specificTotalOutgoingTextView;


    private TextView fromDateTextView, toDateTextView;

    int cday, cmonth, cyear;
    String resultMessage;


    private RelativeLayout rootRelativeLayout;


    private TextView incomingQuantityTextView,outgoingQuantityTextView;
    private LinearLayout quantityLinearLayout;


    float incomingQuantity,outgoingQuantity;

    ArrayAdapter arrayAdapter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_past_summary, container, false);


        checkConnection();


        selectFilterTextView = view.findViewById(R.id.selectMaterialType);
        selectedMaterialTextView = view.findViewById(R.id.selectedMaterialTextView);
        specificLinearLayout = view.findViewById(R.id.specificMaterialLayout);
        todayTotalIncoming = view.findViewById(R.id.todayTotalIncoming);
        todayTotalOutgoing = view.findViewById(R.id.todayTotalOutgoing);
        specificTotalIncomingTextView = view.findViewById(R.id.specificIncomingTextView);
        specificTotalOutgoingTextView = view.findViewById(R.id.specificOutgoingTextView);
        fromDateTextView = view.findViewById(R.id.fromDateTextView);
        toDateTextView = view.findViewById(R.id.toDateTextView);
        rootRelativeLayout = view.findViewById(R.id.rootRelativeLayout);
        incomingQuantityTextView = view.findViewById(R.id.incomingQuantityTextView);
        outgoingQuantityTextView = view.findViewById(R.id.outgoingQuantityTextView);
        quantityLinearLayout = view.findViewById(R.id.quantityLinearLayout);

        list = Paper.book().read("MaterialList");
        arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.select_dialog_singlechoice,list);



     getTotalIncomingOutgoing();



     fromDateTextView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             com.putatoe.putatoeconstructionserviceprovider.DatePicker datePicker = new com.putatoe.putatoeconstructionserviceprovider.DatePicker();
             datePicker.show(getFragmentManager() , "From_Date_Picker");
         }
     });


     fromDateTextView.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {

         }

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {

         }

         @Override
         public void afterTextChanged(Editable s) {

             if (!TextUtils.isEmpty(toDateTextView.getText().toString())) {
                 String fromDate = s.toString();
                 String toDate = toDateTextView.getText().toString();
                 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                 try {
                     Date d1 = simpleDateFormat.parse(fromDate);
                     Date d2 = simpleDateFormat.parse(toDate);

                     if (d2.before(d1)) {

                         Snackbar.make(getActivity().findViewById(R.id.rootRelative), "Please select a valid date", Snackbar.LENGTH_SHORT)
                                 .setBackgroundTint(ContextCompat.getColor(getContext(), R.color.putatoeGreenColor)).show();
                         return;
                     } else {

                         specificLinearLayout.setVisibility(View.VISIBLE);
                         specificTotalIncoming = 0;
                         specificTotalOutgoing = 0;
                         getTotalTransactionAccordingDate(fromDate, toDate);

                     }

                 } catch (ParseException e) {
                     e.printStackTrace();
                 }


             }
         }
     });


     toDateTextView.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {

         }

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {


         }

         @Override
         public void afterTextChanged(Editable s) {

                 String fromDate = fromDateTextView.getText().toString();
                 String toDate = s.toString();
                 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
             try {
                 Date d1 = simpleDateFormat.parse(fromDate);
                 Date d2 = simpleDateFormat.parse(toDate);

                if(d2.before(d1) )
                {

                    Snackbar.make(getActivity().findViewById(R.id.rootRelative), "Please select a valid date", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(ContextCompat.getColor(getContext(), R.color.putatoeGreenColor)).show();
                    return;
                }
                else
                {

                    specificLinearLayout.setVisibility(View.VISIBLE);
                    specificTotalIncoming=0;
                    specificTotalOutgoing=0;
                    getTotalTransactionAccordingDate(fromDate,toDate);

                }

             } catch (ParseException e) {
                 e.printStackTrace();
             }
         }
     });


     toDateTextView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             if(TextUtils.isEmpty(fromDateTextView.getText().toString()))
             {
                 Snackbar.make(getActivity().findViewById(R.id.rootRelative), "First select the from date", Snackbar.LENGTH_SHORT)
                         .setBackgroundTint(ContextCompat.getColor(getContext(), R.color.putatoeGreenColor)).show();
             }
             else {


                 com.putatoe.putatoeconstructionserviceprovider.DatePicker datePicker = new com.putatoe.putatoeconstructionserviceprovider.DatePicker();
                 datePicker.show(getFragmentManager(), "To_Date_Picker");
             }
         }
     });



        selectFilterTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s.toString()))
                {
                    specificLinearLayout.setVisibility(View.GONE);
                    selectedMaterialTextView.setVisibility(View.GONE);
                    quantityLinearLayout.setVisibility(View.GONE);
                }

                else if(s.toString().equals("None"))
                {
                    selectFilterTextView.setText("");
                    selectFilterTextView.setHint("Select Material Type");
                    if(TextUtils.isEmpty(fromDateTextView.getText().toString()) && TextUtils.isEmpty(toDateTextView.getText().toString())) {
                        specificLinearLayout.setVisibility(View.GONE);
                        selectedMaterialTextView.setVisibility(View.GONE);
                        quantityLinearLayout.setVisibility(View.GONE);

                    }
                    else
                    {
                        specificTotalIncoming =0;
                        specificTotalIncoming =0;
                        specificLinearLayout.setVisibility(View.VISIBLE);
                        quantityLinearLayout.setVisibility(View.VISIBLE);
                        getTotalTransactionAccordingDate(fromDateTextView.getText().toString() , toDateTextView.getText().toString());
                    }

                }
                else
                {
                    specificLinearLayout.setVisibility(View.VISIBLE);
                    selectedMaterialTextView.setVisibility(View.VISIBLE);
                    quantityLinearLayout.setVisibility(View.VISIBLE);
                    selectedMaterialTextView.setText("Selected Material:"+selectFilterTextView.getText().toString());
                    if(TextUtils.isEmpty(fromDateTextView.getText().toString()) && TextUtils.isEmpty(toDateTextView.getText().toString())) {
                        getSpecificIncomingOutgoing(selectFilterTextView.getText().toString());
                    }
                    else
                    {
                           specificTotalIncoming=0;
                           specificTotalOutgoing=0;
                            getTotalTransactionAccordingDate(fromDateTextView.getText().toString(),toDateTextView.getText().toString());
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        selectFilterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


                builder.setTitle("Select Material");
                builder.setSingleChoiceItems(arrayAdapter, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        position = which;
                        dialog.dismiss();



                        List<String>  newList = Paper.book().read(list.get(position)+"List");

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());

                        ArrayAdapter arrayAdapter1 = new ArrayAdapter(getContext() , android.R.layout.select_dialog_singlechoice,newList);
                        builder1.setSingleChoiceItems(arrayAdapter1, position, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                position = which;

                                selectFilterTextView.setText(newList.get(position));

                                dialog.dismiss();
                            }
                        });

                        builder1.create().show();

                    }
                });



                builder.create().show();




            }
        });



        return view;
    }


    private void getSpecificIncomingOutgoing(String materialType)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
        );


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                specificTotalIncoming = 0;
                specificTotalOutgoing =0;
                incomingQuantity=0;
                outgoingQuantity=0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {

                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        Order order = dataSnapshot1.getValue(Order.class);

                        if(order.getMaterialType() != null && order.getMaterialType().equals(materialType))
                        {

                            if(order.getOrderStatus().equals("Completed")) {
                                if (order.getTransactionType().equals("Incoming")) {
                                    specificTotalIncoming += order.getTotalAmount();
                                    incomingQuantity += order.getQuantity();
                                } else {
                                    specificTotalOutgoing += order.getTotalAmount();
                                    outgoingQuantity += order.getQuantity();
                                }
                            }
                        }

                    }
                }


                specificTotalIncomingTextView.setText("₹"+specificTotalIncoming);
                specificTotalOutgoingTextView.setText("₹"+specificTotalOutgoing);
                outgoingQuantityTextView.setText(outgoingQuantity+" "+materialType);
                incomingQuantityTextView.setText(incomingQuantity+" "+materialType);






            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    private void getTotalIncomingOutgoing()
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+ Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
        );


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalIncoming = 0;
                totalOutgoing = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {

                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        Order order = dataSnapshot1.getValue(Order.class);


                        if(order.getOrderStatus().equals("Completed")) {
                            if (order.getTransactionType().equals("Incoming")) {
                                totalIncoming += order.getTotalAmount();
                            } else {
                                totalOutgoing += order.getTotalAmount();
                            }
                        }







                    }
                }


                todayTotalIncoming.setText("₹"+totalIncoming);
                todayTotalOutgoing.setText("₹"+totalOutgoing);








            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void setDate(String selectedDate, int value) {
        if(value == 0)
        {
            fromDateTextView.setText(selectedDate);
        }
        else
        {

            toDateTextView.setText(selectedDate);
        }
    }


    private void getTotalTransactionAccordingDate(String fromDate , String toDate)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
        );


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                specificTotalOutgoing = 0;
                specificTotalOutgoing = 0;
                incomingQuantity=0;
                outgoingQuantity=0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {

                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        Order order = dataSnapshot1.getValue(Order.class);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

                        try {
                            Date d1 = simpleDateFormat.parse(fromDate);
                            Date d2 = simpleDateFormat.parse(toDate);

                            String timestamp  = order.getCompletionDate();

                            //convert timestamp to dd/mm/yyyy mm:hh am/pm
                            Calendar calendar = Calendar.getInstance(Locale.getDefault());
                            calendar.setTimeInMillis(Long.parseLong(timestamp));
                            String orderedDate= DateFormat.format("dd-MM-yyyy",calendar).toString();
                            Date d3 = simpleDateFormat.parse(orderedDate);

                            if((d3.equals(d1) || d3.after(d1)) && (d3.before(d2) || d3.equals(d2)))
                            {
                                String materialType = selectFilterTextView.getText().toString();
                                if(TextUtils.isEmpty(materialType)) {
                                    if (order.getTransactionType().equals("Incoming")) {
                                        specificTotalIncoming += order.getTotalAmount();
                                    } else {
                                        specificTotalOutgoing += order.getTotalAmount();
                                    }
                                }
                                else
                                {
                                    if(order.getMaterialType() != null && order.getMaterialType().equals(materialType))
                                    {
                                        if (order.getTransactionType().equals("Incoming")) {
                                            specificTotalIncoming += order.getTotalAmount();
                                            incomingQuantity+=order.getQuantity();
                                        } else {
                                            specificTotalOutgoing += order.getTotalAmount();
                                            outgoingQuantity+=order.getQuantity();
                                        }
                                    }
                                }
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }
                }




                specificTotalIncomingTextView.setText("₹"+specificTotalIncoming);
                specificTotalOutgoingTextView.setText("₹"+specificTotalOutgoing);
                outgoingQuantityTextView.setText(outgoingQuantity+" "+selectFilterTextView.getText().toString());
                incomingQuantityTextView.setText(incomingQuantity+" "+selectFilterTextView.getText().toString());









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


}