package com.interceptor.mcamp;
//Done

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class ActivitySignUp extends AppCompatActivity {

    private TextInputEditText name, email, password, cPassword;
    private DatabaseReference Data;
    private EmailManager emailManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.inName);
        email = findViewById(R.id.inEmail);
        password = findViewById(R.id.inPassword);
        cPassword = findViewById(R.id.inConfirmPassword);

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
    }

    public void signUp(View view) {
        Common.startLoading(this, "Loading");
        if(Objects.requireNonNull(name.getText()).toString().equals("")
                || Objects.requireNonNull(email.getText()).toString().equals("")
                || Objects.requireNonNull(password.getText()).toString().equals("")
                || Objects.requireNonNull(cPassword.getText()).toString().equals("")) {
            Common.stopLoading();
            Common.showMessage(this, "Warning!", "Please fill all the details.");
        }
        else {
            if (new EmailValidator().isValidEmail(email.getText().toString())) {
                checkAccount(new boolean[]{true});
            } else {
                Common.stopLoading();
                Common.showMessage(this, "Invalid Inputs!", "Please check email you entered.");
            }
        }
    }

    private void checkAccount(boolean[] run) {
        String ID;
        ID = Common.emailToID(Objects.requireNonNull(email.getText()).toString());

        Data.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    if (dataSnapshot.child(ID).exists())
                        alreadyHaveAccount();
                    else
                        checkPassword();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });

    }

    private void checkPassword() {
        if(Objects.requireNonNull(password.getText()).toString().equals(Objects.requireNonNull(cPassword.getText()).toString())) {
            emailVerify();
        }
        else {
            Common.stopLoading();
            Common.showMessage(this, "Password dose not match!", "Please provide same password for Password and Confirm Password");
        }
    }

    private void emailVerify() {
        Common.stopLoading();
        int OTP = Integer.parseInt(Common.generateOTP());
        emailManager = new EmailManager(this, Objects.requireNonNull(email.getText()).toString());
        if(emailManager.sendMail(OTP)){
            getOTP(OTP);
        }
    }

    private void getOTP(int OTP) {
        Context context = this;
        PopupWindow popupWindow;
        // Inflate the popup window layout
        @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_opt, null);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // Find the views within the popup window layout
        EditText OTPEditText = popupView.findViewById(R.id.valueEditText);
        Button okButton = popupView.findViewById(R.id.okButton);
        Button cancelButton = popupView.findViewById(R.id.cancelButton);

        okButton.setEnabled(true);
//        OTPEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                okButton.setEnabled(OTPEditText.getText().toString().length() == 6);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        // Set click listener for the OK button
        okButton.setOnClickListener(v -> {
            String stringOTP = OTPEditText.getText().toString();
            // Handle the nickname input as needed
            if (stringOTP.equals(String.valueOf(OTP))) {
                try {
                    signUp();
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }else {
                Common.stopLoading();
                Common.showMessage(context, "Error!", "Invalid OTP");
            }
        });

        // Set click listener for the Cancel button
        cancelButton.setOnClickListener(v -> popupWindow.dismiss());

        // Create the popup window
        popupWindow.setFocusable(true);

        // Show the popup window at the center of the screen
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    public void signUp() throws NoSuchAlgorithmException {
        Common.startLoading(this, "Registration in progress...");
        String ID, stringName, stringEmail, stringPassword;
        stringName = Objects.requireNonNull(name.getText()).toString();
        stringEmail = Objects.requireNonNull(email.getText()).toString();
        ID = Common.emailToID(stringEmail);
        stringPassword = Common.hashString(Objects.requireNonNull(password.getText()).toString());
        Data.child("Users/" + ID + "/name").setValue(stringName);
        Data.child("Users/" + ID + "/email").setValue(stringEmail);
        Data.child("Users/" + ID + "/password").setValue(stringPassword);
        Data.child("Users/" + ID + "/google").setValue("false");
        Data.child("Users/" + ID + "/facebook").setValue("false");

        new SharedVariable(this).setWhileLogin(ID, stringName, stringEmail, null, false, false);

        emailManager.sendMail(stringName);

        startActivity(new Intent(this, ActivityHome.class));
    }

    private void alreadyHaveAccount() {
        Common.stopLoading();
        Common.showMessage(this, "Account found!", "You already have an account. \nPlease sign in");
    }
}