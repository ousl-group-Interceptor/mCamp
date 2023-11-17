package com.interceptor.mcamp;
//Done

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Objects;

public class FacebookAuthentication extends ActivitySignIn {

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(this, Collections.singletonList("public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(@NonNull FacebookException exception) {
                        // App code
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the FacebookAuthentication SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private FirebaseAuth mAuth;

    private void handleFacebookAccessToken(AccessToken token) {
        mAuth = FirebaseAuth.getInstance();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        updateUI(user, new boolean[]{true});
                    }  // If sign in fails, display a message to the user.

                });
    }

    private void updateUI(FirebaseUser user, boolean[] run) {
        String ID;
        ID = Common.emailToID(Objects.requireNonNull(user.getEmail()));

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        Data.child("Users/" + ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;

                    Data.child("Users/" + ID + "/google").setValue("false");
                    Data.child("Users/" + ID + "/facebook").setValue("true");
                    Data.child("Users/" + ID + "/password").setValue(ID);

                    Data.child("Users/" + ID + "/name").setValue(user.getDisplayName());
                    Data.child("Users/" + ID + "/email").setValue(user.getEmail());
                    String userImage = String.valueOf(user.getPhotoUrl());
                    Data.child("Users/" + ID + "/userImage").setValue(userImage);
                    sharedVariable.setWhileLogin(ID, user.getDisplayName(), user.getEmail(), userImage, false, true);
                    go();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    private void go() {
        startActivity(new Intent(this, ActivityHome.class));
    }
}
