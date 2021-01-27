package com.putatoe.putatoeconstructionserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.putatoe.putatoeconstructionserviceprovider.POJO.Order;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Owner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import needle.Needle;
import needle.UiRelatedTask;

public class NewOrderActivity extends AppCompatActivity implements DatePicker.selectedDate {



    private ImageView backImageView;

    private Spinner materialTypeSpinner;
    List<String> materialTypeList;


    //quantity Spinner
    private Spinner quantitySpinner;
    List<String>  quantityList;


    private TextView orderIdTextView;
    private EditText customerNumberEditText,customerNameEditText,
            descriptionEditText,totalAmountEditText,quantityEditText;

    String materialType, quantity;


    //update button
    Button updateButton;
    int position3 =0;


    String orderNo = "PUTGA";

    String updatedByName;

    long ordersNo;


    //Radio Button
    private RadioButton incomingRadioButton,outgoingRadioButton;


    String transactionType,orderStatus;
    float totalAmount;

    private TextView materialTypeTextView,quantityTypeTextView;
    int position1=0,position2=0;


    List<String> list;


    private CheckBox pendingCheckBox,completeCheckBox;

    private TextView completionDateTextView;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);


        backImageView = findViewById(R.id.backButton);

        descriptionEditText = findViewById(R.id.descriptionEditText);
        totalAmountEditText = findViewById(R.id.totalAmountEditText);
        customerNameEditText = findViewById(R.id.customerNameEditText);
        customerNumberEditText = findViewById(R.id.customerNumberEditText);
        updateButton = findViewById(R.id.updateButton);
        quantityEditText = findViewById(R.id.quantityEditText);
        orderIdTextView = findViewById(R.id.orderIdTextView);
        incomingRadioButton = findViewById(R.id.incomingRadioButton);
        outgoingRadioButton = findViewById(R.id.outgoingRadioButton);
        materialTypeTextView =  findViewById(R.id.materialTypeTextView);
        quantityTypeTextView = findViewById(R.id.quantityTypeTextView);
        pendingCheckBox = findViewById(R.id.pendingCheckBox);
        completeCheckBox = findViewById(R.id.completeCheckBox);
        completionDateTextView = findViewById(R.id.completionDateTextView);



        Paper.init(getApplicationContext());
        customerNameEditText.setText(Paper.book().read("specificName"));
        customerNumberEditText.setText(Paper.book().read("specificNumber"));

        //get current date
        String timestamp  = String.valueOf(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String completionDate= DateFormat.format("dd-MM-yyyy",calendar).toString();
        completionDateTextView.setText(completionDate);

        completionDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                com.putatoe.putatoeconstructionserviceprovider.DatePicker datePicker = new com.putatoe.putatoeconstructionserviceprovider.DatePicker();
                datePicker.show(getSupportFragmentManager(),"New Order");
            }
        });


          quantityTypeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


             AlertDialog.Builder builder = new AlertDialog.Builder(NewOrderActivity.this);



                list = Paper.book().read("QuantityList");
                ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.select_dialog_singlechoice,list);
                builder.setTitle("Select Quantity Type");
                builder.setSingleChoiceItems(arrayAdapter, position1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        position1 = which;
                        quantityTypeTextView.setText(list.get(position1));
                        dialog.dismiss();

                    }
                });


                builder.create().show();
//

            }
        });





        materialTypeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(NewOrderActivity.this);
                list = Paper.book().read("MaterialList");
                ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.select_dialog_singlechoice,list);
                builder.setTitle("Select Material");
                builder.setSingleChoiceItems(arrayAdapter, position2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        position2 = which;
                        materialTypeTextView.setText(list.get(position2));
                        dialog.dismiss();



                        List<String>  newList = Paper.book().read(list.get(position2)+"List");

                        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(NewOrderActivity.this);
                        builder1.setCancelable(false);

                        ArrayAdapter arrayAdapter1 = new ArrayAdapter(NewOrderActivity.this , android.R.layout.select_dialog_singlechoice,newList);
                        builder1.setSingleChoiceItems(arrayAdapter1, position2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                position3 = which;

                                materialTypeTextView.setText(newList.get(position3));

                                dialog.dismiss();
                            }
                        });

                        builder1.create().show();


                    }
                });



                AlertDialog alertDialog = builder.create();
                alertDialog.show();




            }
        });


        pendingCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pendingCheckBox.isChecked())
                {
                    completeCheckBox.setChecked(false);
                }


            }
        });

        completeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(completeCheckBox.isChecked())
                {
                    pendingCheckBox.setChecked(false);
                }
            }
        });













        incomingRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(incomingRadioButton.isChecked())
                {
                    outgoingRadioButton.setChecked(false);
                }

            }
        });

        outgoingRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(outgoingRadioButton.isChecked())
                {
                    incomingRadioButton.setChecked(false);
                }
            }
        });


        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





        getOrderNo();



        if (Paper.book().read("userOrService").equals("owners")) {
            Owner owner = Paper.book().read("owners");
            getOwnerName(owner.getContact());
        }
        else
        {
            Owner owner = Paper.book().read("owner");
            getEmployeeName(owner.getContact());
        }





        //when click on update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                        if (!result.booleanValue()) {

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(NewOrderActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.no_internet,null);
                            builder.setCancelable(false);
                            builder.setView(view);
                            builder.create().show();


                            return;
                        }



                        String customerName = customerNameEditText.getText().toString();
                        String customerNumber = customerNumberEditText.getText().toString();
                        String description ;
                        if(TextUtils.isEmpty(descriptionEditText.getText().toString()))
                        {
                            description = "null";
                        }
                        else
                        {
                            description = descriptionEditText.getText().toString();
                        }

                        if(TextUtils.isEmpty(totalAmountEditText.getText().toString()))
                        {
                            totalAmount = 00;
                        }
                        else
                        {
                            totalAmount = Float.parseFloat(totalAmountEditText.getText().toString());
                        }

                        Owner owner = Paper.book().read("owner");
                        String updatedByNumber = owner.getContact();
                        getOrderNo();

                        String timestamp = String.valueOf(System.currentTimeMillis());



                        long milliseconds=0;
                        String date = completionDateTextView.getText().toString();
                        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            Date d = f.parse(date);
                            milliseconds = d.getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        String completionDate = "";

                        if(milliseconds == 0)
                        {
                            completionDate="null";
                        }
                        else
                        {
                            completionDate=String.valueOf(milliseconds);
                        }





                        if(incomingRadioButton.isChecked())
                        {
                            transactionType="Incoming";
                        }
                        else
                        {
                            if(outgoingRadioButton.isChecked())
                            {
                                transactionType="Outgoing";
                            }
                        }

                        String materialType;
                        if(materialTypeTextView.getText().toString().equals("None") || TextUtils.isEmpty(materialTypeTextView.getText().toString()))
                        {
                            materialType = "Not Selected";
                        }
                        else {
                            materialType = materialTypeTextView.getText().toString();
                        }
                        float quantity;

                        if(TextUtils.isEmpty(quantityEditText.getText().toString()))
                        {
                            quantity = 0;
                        }
                        else
                        {
                            if(quantityEditText.getText().toString().equals("None") || TextUtils.isEmpty(quantityTypeTextView.getText().toString()))
                            {
                                Snackbar.make(findViewById(R.id.rootRelative), "Please select quantity unit", Snackbar.LENGTH_SHORT)
                                        .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.putatoeGreenColor)).show();
                                return;
                            }
                            else
                            {
                                quantity = Float.parseFloat(quantityEditText.getText().toString());
                            }
                        }






                        if (TextUtils.isEmpty(customerName)) {
                            Snackbar.make(findViewById(R.id.rootRelative), "Customer name must not be empty", Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.putatoeGreenColor)).show();
                            return;
                        }



                        if(TextUtils.isEmpty(transactionType))
                        {
                            Snackbar.make(findViewById(R.id.rootRelative), "Please select transaction type", Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.putatoeGreenColor)).show();
                            return;
                        }


                        if(pendingCheckBox.isChecked())
                        {
                            orderStatus="Pending";
                        }
                        else {
                            if(completeCheckBox.isChecked())
                            {
                                orderStatus="Completed";
                            }
                        }


                        if(TextUtils.isEmpty(orderStatus))
                        {
                            Snackbar.make(findViewById(R.id.rootRelative), "Please select order type", Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.putatoeGreenColor)).show();
                            return;
                        }




                        if (validatePhone()) {

                            if(TextUtils.isEmpty(customerNumber))
                            {
                                customerNumber="null";
                            }
                            Order order = new Order(customerName, customerNumber, materialType, quantityTypeTextView.getText().toString(), description,  updatedByName, updatedByNumber,"#"+Paper.book().read("order"),transactionType
                                    ,timestamp,orderStatus,completionDate,totalAmount,quantity);
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                                    getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
                            ).child(customerNumber);


                            String finalCustomerNumber = customerNumber;
                            databaseReference.child(Paper.book().read("order")).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "order updated", Toast.LENGTH_SHORT).show();
                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReferenceFromUrl(
                                                getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")+"/CompanyValue/OrdersCount"
                                        );

                                        databaseReference1.child("orders").setValue(Paper.book().read("orderNo"));

                                        Paper.book().write("specificNumber", Paper.book().read("specificNumber"));
                                        Paper.book().write("specificName",Paper.book().read("specificName"));
                                        Intent intent = new Intent(getApplicationContext() , SpecificOrdersActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                }
                            });


                        }


                    }

                });




            }
        });


    }





    private void getOrderNo()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")+"/CompanyValue"
        ).child("OrdersCount");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    ordersNo = (long) snapshot1.getValue();
                    ordersNo=ordersNo+1;
                    Paper.book().write("orderNo",ordersNo);
                    Paper.book().write("order",orderNo+ordersNo);
                    orderIdTextView.setText("#"+orderNo+ordersNo);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getEmployeeName(String mobileNumber)
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")+"/CompanyValue"
        ).child("Employees").child(mobileNumber);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {

                    updatedByName = (String) snapshot1.getValue();
                    Log.d("updateByName",""+updatedByName);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public boolean validatePhone() {
        String phoneInput = customerNumberEditText.getText().toString().trim();
        if (phoneInput.isEmpty()) {


            return true;
        } else if (!Patterns.PHONE.matcher(phoneInput).matches()) {
            Snackbar.make(findViewById(R.id.rootRelative), "Please enter a valid Phone Number", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.putatoeGreenColor)).show();
            return false;
        } else if (phoneInput.length() < 10) {
            Snackbar.make(findViewById(R.id.rootRelative), "Please enter a valid Phone Number", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.putatoeGreenColor)).show();
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void setDate(String selectedDate, int value) {
        completionDateTextView.setText(selectedDate);
    }


    private void getOwnerName(String mobileNumber)
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")+"/CompanyValue"
        ).child("Owners").child(mobileNumber);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {

                    updatedByName = (String) snapshot1.getValue();
                    Log.d("updateByName",""+updatedByName);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}