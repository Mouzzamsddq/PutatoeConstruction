package com.putatoe.putatoeconstructionserviceprovider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

public class MyOTPDialog extends DialogFragment {

    private DialogFragmentListener mDialogFragmentListener;
    private EditText otpEditText1,otpEditText2,otpEditTex3,otpEditText4,otpEditText5,otpEditText6;
    private RelativeLayout relativeLayoutOtp;
    private Button confirmButton,cancelButton;
    Bundle bundle;
    String code;




    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.otp_dialog,null);
        otpEditText1 = view.findViewById(R.id.firstOtpEditText);
        otpEditText2 = view.findViewById(R.id.secondOtpEditText);
        otpEditTex3 = view.findViewById(R.id.thirdOtpEditText);
        otpEditText4 = view.findViewById(R.id.fourthOtpEditText);
        relativeLayoutOtp = getActivity().findViewById(R.id.rootRelativeLayout);
        confirmButton = view.findViewById(R.id.confirmButton);
        cancelButton = view.findViewById(R.id.cancelButton);




        otpEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==1)
                {
                    otpEditText2.requestFocus();
                    otpEditText2.setCursorVisible(true);
                    otpEditText1.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.border));
                    otpEditText2.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.border));

                }
                else {
                    otpEditText1.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.border2));
                }


            }
        });
        otpEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==1) {
                    otpEditTex3.requestFocus();

                    otpEditTex3.setCursorVisible(true);
                    otpEditTex3.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border));
                }
                else
                {
                    otpEditText1.requestFocus();
                    otpEditText1.setCursorVisible(true);
                    otpEditText2.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.border2));
                }


            }
        });

        otpEditTex3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 1) {
                    otpEditText4.requestFocus();
                    otpEditText4.setCursorVisible(true);
                    otpEditText4.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border));
                }
                else
                {
                    otpEditText2.requestFocus();
                    otpEditText2.setCursorVisible(true);
                    otpEditTex3.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.border2));
                }


            }
        });
        builder.setCancelable(false);

        otpEditText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length()==1)
                {


                }
                else
                {
                    otpEditTex3.requestFocus();
                    otpEditTex3.setCursorVisible(true);
                    otpEditText4.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.border2));



                }

            }
        });
        builder.setView(view);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder = new StringBuilder();
                if(otpEditText1.getText().toString().length() ==1 && otpEditText2.getText().toString().length()==1 && otpEditTex3.getText().toString().length()==1
                && otpEditText4.getText().toString().length()==1)
                {
                    stringBuilder.append(otpEditText1.getText().toString());
                    stringBuilder.append(otpEditText2.getText().toString());
                    stringBuilder.append(otpEditTex3.getText().toString());
                    stringBuilder.append(otpEditText4.getText().toString());
                    mDialogFragmentListener.onDialogFragmentFinish(stringBuilder.toString());
                }
                else
                {
                    Snackbar.make(relativeLayoutOtp,"Please Enter the valid OTP", Snackbar.LENGTH_LONG)
                            .show();
                }





            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });





        return  builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof DialogFragmentListener)
        {
            mDialogFragmentListener = (DialogFragmentListener) context;

        }
    }

    public interface DialogFragmentListener
    {
        void onDialogFragmentFinish(String code);
    }

}
