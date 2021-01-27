package com.putatoe.putatoeconstructionserviceprovider.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.putatoe.putatoeconstructionserviceprovider.EditDialog;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Order;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Owner;
import com.putatoe.putatoeconstructionserviceprovider.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class PendingOrdersAdapter  extends  RecyclerView.Adapter<PendingOrdersAdapter.MyHolder> {


    private Context context;
    private List<Order> pastOrderList;
    int position = 0;


    public PendingOrdersAdapter(Context context, List<Order> pastOrderList) {
        this.context = context;
        this.pastOrderList = pastOrderList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pending_order_item,parent , false);

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
        holder.orderStatusTextView.setText(order.getOrderStatus());

        if(order.getTransactionType().equals("Incoming"))
        {
            holder.orderLayout.setBackgroundColor(ContextCompat.getColor(context , R.color.light_green));
        }
        else
        {
            holder.orderLayout.setBackgroundColor(ContextCompat.getColor(context , R.color.light_red));
        }





        String timestamp = order.getTimestamp();

        //convert timestamp to dd/mm/yyyy mm:hh am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String postedDate= DateFormat.format("dd-MM-yyyy hh:mm a",calendar).toString();

        String  completionTimeStamp = order.getCompletionDate();
        Calendar calendar1 = Calendar.getInstance(Locale.getDefault());
        calendar1.setTimeInMillis(Long.parseLong(completionTimeStamp));
        String completionDate = DateFormat.format("dd-MM-yyyy",calendar1).toString();
        holder.completionDateTextView.setText(completionDate);
        holder.updateTextView.setText(order.getUpdatedByName());










        holder.changeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                showDialog(order);
                FragmentActivity activity = (FragmentActivity)(context);
                FragmentManager fm = activity.getSupportFragmentManager();
                EditDialog editDialog = new EditDialog(order,context);
                editDialog.show(fm, "fragment_alert");



            }
        });








    }

    @Override
    public int getItemCount() {
        return pastOrderList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder
    {






        private TextView orderIdTextView,customerNameTextView,customerNumberTextView,descriptionTextView,
                quantityTextView,materialTypeTextView,totalAmountTextView
                ,orderStatusTextView,completionDateTextView,updateTextView;

        private RelativeLayout changeLayout;
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
            orderStatusTextView = itemView.findViewById(R.id.orderStatusTextView);
            changeLayout = itemView.findViewById(R.id.changeLayout);
            completionDateTextView = itemView.findViewById(R.id.completionDateTextView);
            updateTextView = itemView.findViewById(R.id.updatedTextView);
            orderLayout = itemView.findViewById(R.id.orderLayout);







        }
    }


    private void showDialog(Order order)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Change Order Status");
        List<String> orderStatusList = new ArrayList<>();
        orderStatusList.add("Pending");
        orderStatusList.add("Completed");
        orderStatusList.add("Declined");

        ArrayAdapter arrayAdapter  = new ArrayAdapter(context, android.R.layout.select_dialog_singlechoice,orderStatusList);
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
                 updateOrder(order,orderStatusList.get(position));

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




    private void  updateOrder(Order order,String orderStatus)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                context.getResources().getString(R.string.database_url)+ Paper.book().read("BuisnessName")+
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
        hashMap.put("orderStatus",orderStatus);
        databaseReference.child(updatedOrderId).updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "Order status updated", Toast.LENGTH_SHORT).show();
                    }
                });

    }







}
