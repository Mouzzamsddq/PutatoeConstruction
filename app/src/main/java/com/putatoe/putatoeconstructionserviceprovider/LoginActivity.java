package com.putatoe.putatoeconstructionserviceprovider;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Customer;
import com.putatoe.putatoeconstructionserviceprovider.POJO.Owner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import okhttp3.internal.cache.DiskLruCache;

public class LoginActivity extends AppCompatActivity implements MyOTPDialog.DialogFragmentListener {


    private static String sendOTPURL = "https://control.msg91.com/api/sendotp.php";
    public static String verifyOTPURL = "https://control.msg91.com/api/verifyRequestOTP.php";

    //Edit Text object
    private EditText phoneEditText;

    //continueLoginButton
    private Button continueLoginButton;

    //root layout object
    private RelativeLayout relativeLayout;

    //incorrect text view
    private TextView incorrectTextView;


    //checkbox object
    private CheckBox acceptTermCheckBox;

    //terms text view
    private TextView termsTextView;

    //progress Linear layout object
    private LinearLayout progressLinearLayout;


    //verification id object which contain the otp
    private String verificationId;


    //entered code
    private String enteredCode;

    //file name of share preferences
    public static String FILE_NAME = "username";


    /* access modifiers changed from: private */
    public AppCompatEditText otp1;
    /* access modifiers changed from: private */
    public AppCompatEditText otp2;
    /* access modifiers changed from: private */
    public AppCompatEditText otp3;
    /* access modifiers changed from: private */
    public AppCompatEditText otp4;

    public TextView otpBoxErrorView;

    private AlertDialog alertDialog;


    ProgressDialog progressDialog;


    private MyOTPDialog myOTPDialog;


    List<String> employeeList;

    List<String> materialList;

    List<String> ownersList;


    List<String> buisnessList;





    private Button customerLoginButton;


    private TextView selectBuisnessTextView;




    int position=0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init views method which initialize all the objects

        initViews();






        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                incorrectTextView.setVisibility(View.INVISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });









        customerLoginButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {



                if(!TextUtils.isEmpty(selectBuisnessTextView.getText().toString())) {

                    if (validatePhone()) {
                        if (!acceptTermCheckBox.isChecked()) {
                            Snackbar.make(relativeLayout, "Please Select Term and Condition", Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.putatoeGreenColor)).show();
                        } else {
                            continueLoginButton.setVisibility(View.INVISIBLE);
                            customerLoginButton.setVisibility(View.INVISIBLE);
                            progressLinearLayout.setVisibility(View.VISIBLE);
//
                            String phoneNumber = phoneEditText.getText().toString();
                            sendCampaign(phoneNumber, "user");


                        }
                    }
                }
                else
                {
                    Snackbar.make(relativeLayout, "Please select buisness name", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getColor(R.color.putatoeGreenColor)).show();
                }

            }
        });


        termsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acceptTermCheckBox.isChecked()) {
                    acceptTermCheckBox.setChecked(false);
                } else {
                    acceptTermCheckBox.setChecked(true);
                }
            }
        });


        selectBuisnessTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s.toString())) {
                    getEmployeeList(s.toString());
                    getOwnersList(s.toString());;



                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //when click on continueLogin button
        continueLoginButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //perform login



                if(!TextUtils.isEmpty(selectBuisnessTextView.getText().toString())) {

                    if (validatePhone()) {
                        if (!acceptTermCheckBox.isChecked()) {
                            Snackbar.make(relativeLayout, "Please Select Term and Condition", Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(getColor(R.color.putatoeGreenColor)).show();
                        } else {
                            String phoneNumber = phoneEditText.getText().toString();
                            if (employeeList.contains(phoneNumber)) {

                                continueLoginButton.setVisibility(View.INVISIBLE);
                                customerLoginButton.setVisibility(View.INVISIBLE);
                                progressLinearLayout.setVisibility(View.VISIBLE);

                                sendCampaign(phoneNumber, "owner");


                            }
                            else if(ownersList.contains(phoneNumber))
                            {
                                continueLoginButton.setVisibility(View.INVISIBLE);
                                customerLoginButton.setVisibility(View.INVISIBLE);
                                progressLinearLayout.setVisibility(View.VISIBLE);

                                sendCampaign(phoneNumber, "owners");

                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.custom_dialog, relativeLayout, false);
                                builder.setView(view);
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }


                        }
                    }
                }
                else
                {
                    Snackbar.make(relativeLayout, "Please select buisness name", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getColor(R.color.putatoeGreenColor)).show();
                }

            }
        });


    }


    private void initViews() {
        phoneEditText = findViewById(R.id.mobileInputEditText);
        continueLoginButton = findViewById(R.id.continueLoginButton);
        relativeLayout = findViewById(R.id.rootRelativeLayout);
        incorrectTextView = findViewById(R.id.incorrectTextView);
        acceptTermCheckBox = findViewById(R.id.checkTermCheckBox);
        termsTextView = findViewById(R.id.termsAndConditionTextView);
        progressLinearLayout = findViewById(R.id.progressLinearLayout);
        Paper.init(this);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Login Complete");

        employeeList = new ArrayList<>();
        //initializd the owners list
        ownersList = new ArrayList();
        customerLoginButton = findViewById(R.id.customerLoginButton);
        selectBuisnessTextView = findViewById(R.id.selectBuisnessTextView);

       buisnessList = new ArrayList<>();
       if(Paper.book().read("ListBusiness") == null)
       {
           Log.d("kkk","List");
           getBuisnessList();
       }
       else
       {

           buisnessList =Paper.book().read("ListBusiness");
       }





        materialList = new ArrayList<>();


//        getEmployeeList();

    }


    public boolean validatePhone() {
        String phoneInput = phoneEditText.getText().toString().trim();
        if (phoneInput.isEmpty()) {
            Snackbar.make(relativeLayout, "Please Enter your Phone Number", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.putatoeGreenColor)).show();
            incorrectTextView.setVisibility(View.VISIBLE);
            return false;
        } else if (!Patterns.PHONE.matcher(phoneInput).matches()) {
            Snackbar.make(relativeLayout, "Please enter a valid Phone Number", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.putatoeGreenColor)).show();
            incorrectTextView.setVisibility(View.VISIBLE);
            return false;
        } else if (phoneInput.length() < 10) {
            Snackbar.make(relativeLayout, "Please enter a valid Phone Number", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.putatoeGreenColor)).show();
            incorrectTextView.setVisibility(View.VISIBLE);
            return false;
        } else {
            return true;
        }

    }


    public void sendCampaign(String phone, final String userorservice) {


        //save the mobile number as username in sharedprefrences

        SharedPreferences.Editor editor = getSharedPreferences(FILE_NAME, MODE_PRIVATE).edit();
        editor.putString("username", phone);
        editor.apply();
        Paper.book().write("userOrService",userorservice);


        StringBuilder sb = new StringBuilder();
        sb.append("mobile=");
        sb.append(phone);
        String mobile = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(sendOTPURL);
        sb2.append("?");
        sb2.append("sender=PUTATOE");
        String str = "&";
        sb2.append(str);
        sb2.append(mobile);
        sb2.append(str);
        sb2.append("authkey=263311ACpjglj8LNfy5c682aba");
        sb2.append(str);
        sb2.append("otp_length=4");
        sendOTPURL = sb2.toString();
        Log.e("Send OTP URL ", sendOTPURL);
        AndroidNetworking.post(sendOTPURL).setPriority(Priority.MEDIUM).build().getAsJSONObject(new JSONObjectRequestListener() {
            public void onResponse(JSONObject response) {
                String str = "Error Occured";
                try {
                    if (response.getString("type").equalsIgnoreCase("success")) {
                        progressLinearLayout.setVisibility(View.INVISIBLE);
                        continueLoginButton.setVisibility(View.VISIBLE);
                        customerLoginButton.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
//                        LoginActivity.this.verifyOTP(userorservice);

                        LoginActivity.this.verifyOTP2(userorservice);
                        return;
                    }

                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, str, Toast.LENGTH_SHORT).show();


                }
            }

            public void onError(ANError anError) {
                Toast.makeText(LoginActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();

            }
        });
    }




    private void performRegistration() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.database_url)+ buisnessList.get(position))
                .child("LOGIN").child("CUSTOMER").child(phoneEditText.getText().toString());


        String userType = Paper.book().read("userOrService");

        Paper.book().write("BuisnessName",buisnessList.get(position));
        if(userType != null && userType.equals("user")) {

            Customer customer = new Customer(phoneEditText.getText().toString());
            databaseReference.setValue(customer).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Paper.book().write("customer", customer);
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Complete", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, AllOrdersActivity.class));
                        finish();

                    }
                }
            });
        }
        else if(userType != null && userType.equals("owners"))
        {

            Owner owner = new Owner(phoneEditText.getText().toString());
            Paper.book().write("owners",owner);
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "Login Complete", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
        else
        {
            Owner owner = new Owner(phoneEditText.getText().toString());
            Paper.book().write("owner",owner);
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "Login Complete", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }


    }


    private void verifyOTP2(String userorservice) {
        myOTPDialog = new MyOTPDialog();
        myOTPDialog.setCancelable(false);
        myOTPDialog.show(getSupportFragmentManager(), "fragmentTag");


    }


    @Override
    public void onDialogFragmentFinish(String code) {
        enteredCode = code;
        myOTPDialog = (MyOTPDialog) getSupportFragmentManager().findFragmentByTag("fragmentTag");
        LoginActivity.this.myOTPDialog.dismiss();


        StringBuilder sb2 = new StringBuilder();
        sb2.append("mobile=");
        sb2.append(LoginActivity.this.phoneEditText.getText().toString());
        String phone = sb2.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append("otp=");
        sb3.append(enteredCode);
        String userInputOTP2 = sb3.toString();
        StringBuilder sb4 = new StringBuilder();
        sb4.append(LoginActivity.verifyOTPURL);
        sb4.append("?");
        sb4.append("authkey=263311ACpjglj8LNfy5c682aba");
        String str = "&";
        sb4.append(str);
        sb4.append(phone);
        sb4.append(str);
        sb4.append(userInputOTP2);
        LoginActivity.verifyOTPURL = sb4.toString();
        AndroidNetworking.post(LoginActivity.verifyOTPURL).setPriority(Priority.MEDIUM).build().getAsJSONObject(new JSONObjectRequestListener() {
            public void onResponse(JSONObject response) {
                try {
                    Log.e("response", response.toString());
                    if (response.getString("type").equalsIgnoreCase("success")) {
                        Toast.makeText(LoginActivity.this, "OTP Verified", Toast.LENGTH_SHORT).show();
                        progressDialog.setMessage("Welcome to "+buisnessList.get(position));
                        progressDialog.show();
                        LoginActivity.this.performRegistration();
                        return;
                    } else {


                        Snackbar.make((View) LoginActivity.this.relativeLayout, "Wrong OTP", Snackbar.LENGTH_SHORT).
                                setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.putatoeGreenColor)).show();


                        return;
                    }

                } catch (JSONException e) {
                }
            }

            public void onError(ANError anError) {
                Toast.makeText(LoginActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                Snackbar.make((View) LoginActivity.this.relativeLayout, (CharSequence) anError.getErrorDetail(), Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.putatoeGreenColor)).show();
            }
        });


    }



    private void getEmployeeList(String buisnessName)
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(

                getResources().getString(R.string.database_url)+ buisnessName +"/CompanyValue/Employees"
        );

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    employeeList.add(snapshot1.getKey());
                }

                Paper.book().write("EmployeeList",employeeList);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getOwnersList(String buisnessName)
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(

                getResources().getString(R.string.database_url)+ buisnessName +"/CompanyValue/Owners"
        );

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    ownersList.add(snapshot1.getKey());
                }

                Paper.book().write("OwnerList",ownersList);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





    public void selectBuisness(View v) {

        position =0;
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Select Buisness Name:");




        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.select_dialog_singlechoice,buisnessList);
        builder.setSingleChoiceItems(arrayAdapter, position, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                position = which;

            }
        });


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(!buisnessList.isEmpty())
                {
                    selectBuisnessTextView.setText(buisnessList.get(position));

                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        builder.create().show();


    }


    private void getBuisnessList()
    {



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://putatoeapp.firebaseio.com/PUTATOECONSTRUCTION");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                buisnessList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    buisnessList.add(snapshot1.getKey());
                }


                //write a business list in paper for further usage
                Paper.book().write("ListBusiness",buisnessList);








            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }















}



