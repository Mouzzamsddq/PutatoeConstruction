package com.putatoe.putatoeconstructionserviceprovider.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.putatoe.putatoeconstructionserviceprovider.POJO.Order;
import com.putatoe.putatoeconstructionserviceprovider.POJO.SearchOutstanding;
import com.putatoe.putatoeconstructionserviceprovider.R;
import com.putatoe.putatoeconstructionserviceprovider.SpecificOrdersActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import io.paperdb.Paper;

public class SearchAdapter extends  RecyclerView.Adapter<SearchAdapter.ViewHolder> {



    private Context context;
    private List<SearchOutstanding> searchList;
    float totalIncoming, totalOutgoing,outstanding;

    public SearchAdapter(Context context, List<SearchOutstanding> searchList) {
        this.context = context;
        this.searchList = searchList;
        Paper.init(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        String customerNumber = searchList.get(position).getMobileNumber();
        holder.customerNumberTextView.setText(customerNumber);

        getCustomerName(holder.customerNameTextView , customerNumber);
        getTotalIncomingAmount(customerNumber,holder.outstandingTextView,holder.outstandingLayout,holder.advanceTextView);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Paper.book().write("specificNumber",customerNumber);
                Paper.book().write("specificName",holder.customerNameTextView.getText().toString());
                Intent intent = new Intent(context , SpecificOrdersActivity.class);
                context.startActivity(intent);

            }
        });







    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private TextView customerNameTextView,customerNumberTextView;
        private LinearLayout outstandingLayout;
        private TextView outstandingTextView;
        private TextView advanceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            customerNameTextView  = itemView.findViewById(R.id.customerNameTextView);
            customerNumberTextView  = itemView.findViewById(R.id.customerNumberTextView);
            outstandingLayout = itemView.findViewById(R.id.outstandingLayout);
            outstandingTextView = itemView.findViewById(R.id.outstandingTextView);
            advanceTextView = itemView.findViewById(R.id.advanceTextView);




        }
    }



    private void getCustomerName(TextView textView,String customerNumber)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                context.getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName") +"/ALLACTIVITY/ORDERS"
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


    private void getTotalIncomingAmount(String mobileNumber,TextView text1,LinearLayout linearLayout,TextView advanceTextView)
    {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                context.getResources().getString(R.string.database_url)+Paper.book().read("BuisnessName")+"/ALLACTIVITY/ORDERS"
        ).child(mobileNumber);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalIncoming=0;
                totalOutgoing=0;


                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    Order order = snapshot1.getValue(Order.class);
                    if(order.getOrderStatus().equals("Completed")) {
                        if (order.getTransactionType().equals("Incoming")) {
                            totalIncoming += order.getTotalAmount();

                        } else {
                            totalOutgoing += order.getTotalAmount();
                        }
                    }


                }


                if(totalIncoming < totalOutgoing)
                {
                    linearLayout.setVisibility(View.VISIBLE);
                    linearLayout.setBackgroundColor(ContextCompat.getColor(context , R.color.colorRed));
                    advanceTextView.setText("Outstanding");
                    outstanding = totalOutgoing-totalIncoming;
                    text1.setText("₹"+outstanding);
                }
                else if(totalOutgoing == totalIncoming)
                {
                    linearLayout.setVisibility(View.INVISIBLE);
                }
                else
                {
                    linearLayout.setVisibility(View.VISIBLE);
                    linearLayout.setBackgroundColor(ContextCompat.getColor(context , R.color.green));
                    advanceTextView.setText("Advance");
                    float advanceAmount = totalIncoming-totalOutgoing;
                    text1.setText("₹"+advanceAmount);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }



}
