package com.interceptor.mcamp;
//Done

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class GoogleAuthentication extends ActivitySignIn {

    DatabaseReference Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        GoogleSignInClient client = GoogleSignIn.getClient(this, options);

        Intent i = client.getSignInIntent();
        startActivityForResult(i, 1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        assert currentUser != null;
                        String email = currentUser.getEmail();
                        String name = currentUser.getDisplayName();
                        Uri userImage = currentUser.getPhotoUrl();
                        assert email != null;

                        CheckRegister(email, name, userImage, new boolean[]{true});
                    } else {
                        Toast.makeText(this, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void CheckRegister(String email, String name, Uri userImage, boolean[] run) {
        String ID;
        ID = Common.emailToID(email);

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        Data.child("Users/" + ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    Data.child("Users/" + ID + "/google").setValue("true");
                    Data.child("Users/" + ID + "/facebook").setValue("false");
                    Data.child("Users/" + ID + "/name").setValue(name);
                    Data.child("Users/" + ID + "/email").setValue(email);
                    Data.child("Users/" + ID + "/password").setValue(ID);
                    Data.child("Users/" + ID + "/image").setValue(String.valueOf(userImage));
                    sharedVariable.setWhileLogin(ID, name, email, String.valueOf(userImage), true, false);
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