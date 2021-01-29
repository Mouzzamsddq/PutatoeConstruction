package com.putatoe.putatoeconstructionserviceprovider.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.putatoe.putatoeconstructionserviceprovider.R;

import io.paperdb.Paper;

public class BusinessSummaryDialog  extends DialogFragment {
    private Button okButton;
    private TextView incomingTextView,outgoingTextView,customerNameTextView;
    String mobileNumber;
    float totalIncoming,totalOutgoing;
    private TextView incomingText,outgoingText;

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
        incomingText = view.findViewById(R.id.text1);
        outgoingText = view.findViewById(R.id.text2);

        incomingText.setText("Total Advance");
        outgoingText.setText("Total Outstanding");




        //set business name to customer text view
        customerNameTextView.setText(Paper.book().read("BuisnessName"));


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();

            }
        });
        builder.setView(view);
        return builder.create();
    }
}
