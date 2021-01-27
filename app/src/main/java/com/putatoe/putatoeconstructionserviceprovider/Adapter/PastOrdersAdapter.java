package com.putatoe.putatoeconstructionserviceprovider.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Order;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Owner;
import com.putatoe.putatoeconstructionserviceprovider.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class PastOrdersAdapter extends RecyclerView.Adapter<PastOrdersAdapter.MyHolder>  {


    private Context context;
    private List<Order> pastOrderList;
    private boolean isCustomer;
    private boolean isDecline;

    public PastOrdersAdapter(Context context, List<Order> pastOrderList,boolean isCustomer,boolean isDecline) {
        this.context = context;
        this.pastOrderList = pastOrderList;
        this.isCustomer=isCustomer;
        this.isDecline = isDecline;
    }




    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(context).inflate(R.layout.order_item,parent , false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        Order order =  pastOrderList.get(position);


        holder.orderIdTextView.setText(order.getOrderId());
        holder.totalAmountTextView.setText("₹"+order.getTotalAmount());
        holder.customerNumberTextView.setText(order.getCustomerNumber());
        holder.customerNameTextView.setText(order.getCustomerName());
        holder.quantityTextView.setText(order.getQuantity()+" "+order.getQuantityType());
        holder.materialTypeTextView.setText(order.getMaterialType());
        holder.descriptionTextView.setText(order.getDescription());


        if(isCustomer)
        {
            if(order.getTransactionType().equals("Incoming"))
            {

                holder.orderLayout.setBackgroundColor(ContextCompat.getColor(context , R.color.light_red));
            }
            else
            {

                holder.orderLayout.setBackgroundColor(ContextCompat.getColor(context , R.color.light_green));
            }
        }

        else
        {
            if(order.getTransactionType().equals("Incoming"))
            {
                holder.orderLayout.setBackgroundColor(ContextCompat.getColor(context , R.color.light_green));
            }
            else
            {
                holder.orderLayout.setBackgroundColor(ContextCompat.getColor(context , R.color.light_red));
            }
        }

        holder.orderStatusTextView.setText(order.getOrderStatus());


        holder.updatedTextView.setText(order.getUpdatedByName());



        String timestamp1 = order.getTimestamp();

        //convert timestamp to dd/mm/yyyy mm:hh am/pm
        Calendar calendar1 = Calendar.getInstance(Locale.getDefault());
        calendar1.setTimeInMillis(Long.parseLong(timestamp1));
        String postedDate= DateFormat.format("dd-MM-yyyy hh:mm a",calendar1).toString();


        if(isDecline)
        {
            holder.completionText.setText("Declined Date");
        }
        String timestamp = order.getCompletionDate();

        //convert timestamp to dd/mm/yyyy mm:hh am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String completionDate= DateFormat.format("dd-MM-yyyy",calendar).toString();
        holder.dateTimeTextView.setText(completionDate);
        holder.updatedTextView.setText(order.getUpdatedByName());





    }

    @Override
    public int getItemCount() {
        return pastOrderList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder
    {






        private TextView orderIdTextView,customerNameTextView,customerNumberTextView,descriptionTextView,
        quantityTextView,materialTypeTextView,totalAmountTextView,dateTimeTextView
                ,orderStatusTextView,completionText,updatedTextView;


        private LinearLayout orderLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);


            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            customerNameTextView = itemView.findViewById(R.id.customerNameTextView);
            customerNumberTextView = itemView.findViewById(R.id.customerNumberTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            materialTypeTextView = itemView.findViewById(R.id.materialTypetTextView);
            totalAmountTextView = itemView.findViewById(R.id.totalAmountTextView);
            dateTimeTextView = itemView.findViewById(R.id.dateTimeTextView);
            orderStatusTextView = itemView.findViewById(R.id.orderStatusTextView);
            completionText = itemView.findViewById(R.id.completionText);
            updatedTextView = itemView.findViewById(R.id.updatedTextView);
            orderLayout = itemView.findViewById(R.id.orderLayout);






        }
    }



}
