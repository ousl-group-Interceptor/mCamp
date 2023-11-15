package com.interceptor.mcamp;
//Done
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

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

public class ActivitySignIn extends AppCompatActivity {

    TextInputEditText email, password;
    SharedVariable sharedVariable;
    DatabaseReference Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email = findViewById(R.id.inEmail);
        password = findViewById(R.id.inPassword);
        sharedVariable = new SharedVariable(this);

        rememberMe();
    }
    private void rememberMe() {
        if (!sharedVariable.getRememberEmail().equals("unknown")) {
            email.setText(sharedVariable.getRememberEmail());
            password.setText(sharedVariable.getRememberPassword());
        }
    }

    public void signIn(View view) {
        Common.startLoading(this, "Loading...");
        if (Objects.requireNonNull(email.getText()).toString().equals("")
                || Objects.requireNonNull(password.getText()).toString().equals("")) {
            Common.stopLoading();
            Common.showMessage(this, "Warning!", "Please fill all the details.");
        } else {
            if (new EmailValidator().isValidEmail(email.getText().toString())) {
                String stringEmail = Objects.requireNonNull(email.getText()).toString();
                checkAccount(new boolean[]{true}, stringEmail);
            } else {
                Common.stopLoading();
                Common.showMessage(this, "Invalid Inputs!", "Please check email you entered.");
            }
        }
    }

    private void checkAccount(boolean[] run, String stringEmail) {
        String ID;
        ID = Common.emailToID(stringEmail);

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        Data.child("Users/" + ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    if (dataSnapshot.exists()) {
                        try {
                            signIn(Objects.requireNonNull(dataSnapshot.child("password")
                                    .getValue()).toString(), ID);
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    } else
                        accountNotExit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    private void signIn(String password, String ID) throws NoSuchAlgorithmException {
        if (password.equals(Common.hashString(Objects.requireNonNull(this.password.getText()).toString()))) {
            saveData(ID, new boolean[]{true});
        } else {
            Common.stopLoading();
            Common.showMessage(this, "Invalid!", "Wrong password. Try again");
        }
    }

    private void accountNotExit() {
        Common.stopLoading();
        Common.showMessage(this, "No Account found", "Please create an account.");
    }

    private void saveData(String ID, boolean[] run) {
        Data.child("Users/" + ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    String name, email;
                    name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                    email = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                    sharedVariable.setWhileLogin(ID, name, email, false, false);
                    complete(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    private void complete(String email) {
        CheckBox rememberMeCheckBox = findViewById(R.id.rememberMe);
        if (rememberMeCheckBox.isChecked()) {
            String pass = Objects.requireNonNull(this.password.getText()).toString();
            sharedVariable.setRememberMe(email, pass);
        } else {
            sharedVariable.setRememberMe("unknown", "unknown");
        }
        Common.stopLoading();
        startActivity(new Intent(this, ActivityHome.class));
    }

    public void signInWithGoogle(View view) {
        startActivity(new Intent(this, GoogleAuthentication.class));
    }

    public void signInWithFacebook(View view) {
        startActivity(new Intent(this, FacebookAuthentication.class));
    }
}