package com.interceptor.mcamp;
//Done
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

public class ActivityCreateNewPassword extends AppCompatActivity {

    TextInputEditText password, confirmPassword;
    private DatabaseReference Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_password);

        password = findViewById(R.id.inPassword);
        confirmPassword = findViewById(R.id.inConfirmPassword);
    }

    public void resetPassword(View view) {
        Common.startLoading(this, "Loading");
        checkFiled();
    }

    private void checkFiled() {
        if (String.valueOf(password.getText()).equals("")
                || String.valueOf(confirmPassword.getText()).equals("")) {
            Common.stopLoading();
            Common.showMessage(this, "Warning!", "Please fill all inputs.");
        } else {
            checkPassword();
        }
    }

    private void checkPassword() {
        if (Objects.requireNonNull(password.getText()).toString().equals(Objects.requireNonNull(confirmPassword.getText()).toString())) {
            checkPassword(new boolean[]{true});
        } else {
            Common.stopLoading();
            Common.showMessage(this, "Password dose not match!", "Please provide same password for Password and Confirm Password");
        }
    }

    private void checkPassword(boolean[] run) {
        String ID = Common.userID;

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        Data.child("Users/" + ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    String pass = Objects.requireNonNull(dataSnapshot.child("password").getValue()).toString();
                    String hashPassword;
                    try {
                        hashPassword = Common.hashString(Objects.requireNonNull(password.getText()).toString());
                        if (pass.equals(hashPassword)) {
                            samePassword();
                        } else
                            changePassword(hashPassword, ID);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    private void changePassword(String hashPassword, String id) {
        Data.child("Users/" + id + "/password").setValue(hashPassword);
        Common.stopLoading();
        new EmailManager(this, Common.email).sendMail();
        new AlertDialog.Builder(this)
                .setTitle("Successful")
                .setMessage("You Password changed. SignIn now.")
                .setPositiveButton("Sign In", (dialog, which) -> startActivity(new Intent(this, ActivitySignIn.class)))
                .show();
    }

    private void samePassword() {
        Common.stopLoading();
        Common.showMessage(this, "Policy error!", "Please provide new " +
                "password. \nYou can not user_icon old Password as new one");
    }
}