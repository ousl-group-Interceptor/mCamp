package com.interceptor.mcamp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Objects;

public class ActivityProfile extends AppCompatActivity {

    private ImageView profileImage;
    private TextView name, email;
    private SharedVariable sharedVariable;
    private DatabaseReference Data;
    private StorageReference storageRef;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedVariable = new SharedVariable(this);
        profileImage = findViewById(R.id.profile_picture);
        name = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        start();
    }

    private void start() {
        loadImage();
        loadName();
        loadEmail();
        loadCoin(new boolean[]{true});
    }

    private void loadCoin(boolean[] run) {
        String path = "Users/" + sharedVariable.getUserID() + "/Points";
        Data.child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    if (dataSnapshot.exists()) {
                        int totalPoint = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                        TextView coinView = findViewById(R.id.coin_count);
                        coinView.setText(totalPoint);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadEmail() {
        email.setText(sharedVariable.getEmail());
    }

    private void loadName() {
        name.setText(sharedVariable.getName());
    }

    private void loadImage() {
        if (!sharedVariable.getUserImageUri().equals("unknown")) {
            if (Common.userImageBitmap == null) {
                loadImage(new boolean[]{true});
            } else {
                profileImage.setImageBitmap(Common.userImageBitmap);
            }
        }
    }

    private void loadImage(boolean[] run) {
        String ID = sharedVariable.getUserID();

        Data.child("Users/" + ID + "/image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    if (dataSnapshot.exists()) {
                        String imageUrl = String.valueOf(dataSnapshot.getValue());
                        if (imageUrl.startsWith("displayImage/")) {
                            storageRef.child(String.valueOf(dataSnapshot.getValue())).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                                // Decode the byte array to a Bitmap
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                // Display the image in an ImageView
                                Common.userImageBitmap = bitmap;
                                profileImage.setImageBitmap(bitmap);
                            });
                        } else {
                            getImageBitmap(sharedVariable.getUserImageUri());
                            profileImage.setImageBitmap(Common.userImageBitmap);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    public void getImageBitmap(String imageUrl) {
        Picasso.get().load(imageUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // The 'bitmap' variable now contains the image in Bitmap format
                // You can use it as needed, for example, set it to an ImageView
                Common.userImageBitmap = bitmap;
                profileImage.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                // Handle the case where the bitmap couldn't be loaded
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // Do nothing or handle as needed
            }
        });
    }

    public void changeImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String ID = sharedVariable.getUserID();
        Common.startLoading(this, "Loading");
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the image URI
            Uri imageUri = data.getData();

            String link = "displayImage/" + ID + ".jpg";
            StorageReference imageRef = storageRef.child(link);

            // Upload the image to Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image upload successful
                        // You can retrieve the download URL of the uploaded image
                        imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                            // Save the imageUrl to Firebase Realtime Database or perform any other operations
                            sharedVariable.seUserImageUri(link);
                            Data.child("Users/" + ID + "/image").setValue(link);
                            Common.stopLoading();
                            Toast.makeText(this, "Change Successful", Toast.LENGTH_SHORT).show();
                            loadImage(new boolean[]{true});

                        }).addOnFailureListener(e -> {
                            Common.stopLoading();
                            Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Common.stopLoading();
                        Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    });
        } else
            Common.stopLoading();
    }

    public void changeName(View view) {
        PopupWindow popupWindow;
        // Inflate the popup window layout
        @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_change_name, null);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // Find the views within the popup window layout
        EditText NameEditText = popupView.findViewById(R.id.valueEditText);
        Button okButton = popupView.findViewById(R.id.okButton);
        Button cancelButton = popupView.findViewById(R.id.cancelButton);

        okButton.setEnabled(true);

        // Set click listener for the OK button
        okButton.setOnClickListener(v -> {
            Common.startLoading(this, "Loading...");
            String stringNewName = NameEditText.getText().toString();
            // Handle the nickname input as needed
            if (!stringNewName.equals("")) {
                Common.userName = stringNewName;
                sharedVariable.setName(stringNewName);
                Data.child("Users/" + sharedVariable.getUserID() + "/name").setValue(stringNewName).addOnSuccessListener(unused -> {
                    loadName();
                    Common.stopLoading();
                    popupWindow.dismiss();
                }).addOnFailureListener(unused -> {
                    Common.stopLoading();
                    Common.showMessage(this, "Error", "Please Try Again.");
                });
            } else {
                Common.stopLoading();
                Common.showMessage(this, "Error!", "Please enter your name");
            }
        });

        // Set click listener for the Cancel button
        cancelButton.setOnClickListener(v -> popupWindow.dismiss());

        // Create the popup window
        popupWindow.setFocusable(true);

        // Show the popup window at the center of the screen
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    public void openMyReview(View view) {
        startActivity(new Intent(this, ActivityMyReviews.class));
    }

    public void openMyLocation(View view) {
        startActivity(new Intent(this, ActivityMyContrbution.class));
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ActivityHome.class));
    }

}