package com.interceptor.mcamp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ActivitySubComment extends AppCompatActivity {

    private DataSnapshot currentLocationSubComments, userList;
    private LinearLayout parentContainer;
    private LayoutInflater layoutInflater;
    private TextView seeMore;
    private StorageReference storageRef;
    private SharedVariable sharedVariable;
    private DatabaseReference Data;
    private boolean isFunctionBlocked = false;
    private int min = 1, max = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_comment);

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        sharedVariable = new SharedVariable(this);

        parentContainer = findViewById(R.id.users_comment);
        layoutInflater = LayoutInflater.from(this);

        seeMore = findViewById(R.id.see_more);
        LinearLayout addComment = findViewById(R.id.addComment);

        seeMore.setOnClickListener(v -> seeMore());
        addComment.setOnClickListener(v -> addComment());

        loadMainComment(new boolean[]{true});

        checkData(new boolean[]{true});
    }

    private void addComment() {
        PopupWindow popupWindow;
        // Inflate the popup window layout
        @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_add_comment, null);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // Find the views within the popup window layout
        EditText commentEditText = popupView.findViewById(R.id.valueEditText);
        Button okButton = popupView.findViewById(R.id.okButton);
        Button cancelButton = popupView.findViewById(R.id.cancelButton);

        okButton.setEnabled(true);

        // Set click listener for the OK button
        okButton.setOnClickListener(v -> {
            Common.startLoading(this, "Loading...");
            String stringNewComment = commentEditText.getText().toString();
            // Handle the nickname input as needed
            if (!stringNewComment.equals("")) {
                int i = Integer.parseInt(Common.generateOTP());
                @SuppressLint("DefaultLocale")
                String path = "Locations/" + Common.currentLocationCategory + "/" + Common.currentLocationID + "/Comments/" + Common.currentLocationComment + "/SubComment/" + Common.getCDateTimeString() + String.format("%04d", i);
                Data.child(path).child("Comment").setValue(stringNewComment);
                Data.child(path).child("User").setValue(sharedVariable.getUserID()).addOnSuccessListener(unused -> {
                    Common.stopLoading();
                    popupWindow.dismiss();
                    Toast.makeText(this, "Comment create successfully.", Toast.LENGTH_SHORT).show();
                    clear();
                    checkData(new boolean[]{true});
                }).addOnFailureListener(unused -> {
                    Common.stopLoading();
                    Common.showMessage(this, "Error", "Please Try Again.");
                });
            } else {
                Common.stopLoading();
                Common.showMessage(this, "Error!", "Please enter your comment");
            }
        });

        // Set click listener for the Cancel button
        cancelButton.setOnClickListener(v -> popupWindow.dismiss());

        // Create the popup window
        popupWindow.setFocusable(true);

        // Show the popup window at the center of the screen
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    private void loadMainComment(boolean[] run) {
        TextView commentUser = findViewById(R.id.user_name);
        TextView commentDescription = findViewById(R.id.user_comment_description);
        ImageView reviewUserImage = findViewById(R.id.comment_user_image);

        commentUser.setText(Common.mainCommentUserName);
        commentDescription.setText(Common.mainCommentUserDescription);

        Data.child("Users/" + Common.mainCommentUserID + "/image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    if (dataSnapshot.exists()) {
                        String imageUrl = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                        if (imageUrl.startsWith("displayImage/")) {
                            storageRef.child(imageUrl).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                reviewUserImage.setImageBitmap(bitmap);
                            });
                        } else {
                            Picasso.get().load(imageUrl).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    reviewUserImage.setImageBitmap(bitmap);
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
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkData(boolean[] run) {
        Common.startLoading(this, "Loading...");
        Data.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    boolean[] secondRun = {true};
                    userList = dataSnapshot;

                    String path = "Locations/" + Common.currentLocationCategory + "/" + Common.currentLocationID + "/Comments/" + Common.currentLocationComment + "/SubComment";
                    Data.child(path).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (secondRun[0]) {
                                secondRun[0] = false;
                                if (dataSnapshot.exists()) {
                                    currentLocationSubComments = dataSnapshot;
                                    clear();
                                    loadComment(min, max);
                                } else
                                    Common.stopLoading();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    private void loadComment(int min, int max) {
        List<DataSnapshot> snapshotList = new ArrayList<>();
        for (DataSnapshot review : currentLocationSubComments.getChildren()) {
            snapshotList.add(review);
        }

        Collections.reverse(snapshotList);

        int counter = 0;
        seeMore.setVisibility(View.GONE);
        for (DataSnapshot reversedSubComment : snapshotList) {
            counter++;
            if (min > counter) {
                continue;
            }
            if (max < counter) {
                seeMore.setVisibility(View.VISIBLE);
                break;
            }
            commentAdder(reversedSubComment.getKey(),
                    Objects.requireNonNull(reversedSubComment.child("User").getValue()).toString(),
                    Objects.requireNonNull(reversedSubComment.child("Comment").getValue()).toString());
        }
        Common.stopLoading();
    }

    private void commentAdder(String id, String user, String comment) {
        LinearLayout xmlView = (LinearLayout) layoutInflater.inflate(R.layout.single_comment, parentContainer, false);

        TextView commentUser = xmlView.findViewById(R.id.user_name);
        TextView commentDescription = xmlView.findViewById(R.id.user_comment_description);
        ImageView reviewUserImage = xmlView.findViewById(R.id.comment_user_image);

        sharedVariable = new SharedVariable(this);

        commentUser.setText(String.valueOf(userList.child(user + "/name").getValue()));
        setPastTime(id, xmlView);
        commentDescription.setText(comment);
        setLike(id, xmlView);
        LinearLayout commentButton = xmlView.findViewById(R.id.comment);
        commentButton.setVisibility(View.GONE);

        if (userList.child(user + "/image").exists()) {
            String imageUrl = String.valueOf(userList.child(user + "/image").getValue());
            if (imageUrl.startsWith("displayImage/")) {
                storageRef.child(imageUrl).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    reviewUserImage.setImageBitmap(bitmap);
                });
            } else {
                Picasso.get().load(imageUrl).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        reviewUserImage.setImageBitmap(bitmap);
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
        }

        parentContainer.addView(xmlView);
    }

    @SuppressLint({"NewApi", "LocalSuppress", "SetTextI18n"})
    private void setPastTime(String id, LinearLayout xmlView) {
        String dateString = id.substring(0, Math.min(id.length(), 14));

        // Parse the string to LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime exampleDateTime = LocalDateTime.parse(dateString, formatter);

        // Get the current date and time
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));

        // Calculate the difference between two dates
        long minutesDifference = ChronoUnit.MINUTES.between(exampleDateTime, now);
        long secondsDifference = ChronoUnit.SECONDS.between(exampleDateTime, now);
        long hoursDifference = ChronoUnit.HOURS.between(exampleDateTime, now);
        long daysDifference = ChronoUnit.DAYS.between(exampleDateTime, now);
        long monthsDifference = ChronoUnit.MONTHS.between(exampleDateTime, now);
        long yearsDifference = ChronoUnit.YEARS.between(exampleDateTime, now);


        TextView commentPastTime = xmlView.findViewById(R.id.past_time);

        if (yearsDifference > 1) {
            commentPastTime.setText(yearsDifference + " years ago");
        } else if (yearsDifference > 0) {
            commentPastTime.setText(yearsDifference + " year ago");
        } else if (monthsDifference > 1) {
            commentPastTime.setText(monthsDifference + " months ago");
        } else if (monthsDifference > 0) {
            commentPastTime.setText(monthsDifference + " month ago");
        } else if (daysDifference > 1) {
            commentPastTime.setText(daysDifference + " days ago");
        } else if (daysDifference > 0) {
            commentPastTime.setText(daysDifference + " day ago");
        } else if (hoursDifference > 1) {
            commentPastTime.setText(hoursDifference + " hrs ago");
        } else if (hoursDifference > 0) {
            commentPastTime.setText(hoursDifference + " hour ago");
        } else if (minutesDifference > 1) {
            commentPastTime.setText(minutesDifference + " mins ago");
        } else if (minutesDifference > 0) {
            commentPastTime.setText(minutesDifference + " min ago");
        } else if (secondsDifference > 1) {
            commentPastTime.setText(secondsDifference + " secs ago");
        } else if (secondsDifference > 0) {
            commentPastTime.setText(secondsDifference + " sec ago");
        }
    }

    @SuppressLint("SetTextI18n")
    private void setLike(String id, LinearLayout xmlView) {
        TextView likeCount = xmlView.findViewById(R.id.like_count);
        LinearLayout likeButton = xmlView.findViewById(R.id.like);
        ImageView hart = xmlView.findViewById(R.id.hart);

        if (currentLocationSubComments.child(id).child("Likes").exists()) {
            int count = 0;
            for (DataSnapshot user : currentLocationSubComments.child(id).child("Likes").getChildren()) {
                count++;
                if (Objects.requireNonNull(user.getValue()).toString().equals(sharedVariable.getUserID())) {
                    hart.setVisibility(View.VISIBLE);
                }
            }
            likeCount.setText(count + " liked");
        }
        likeButton.setOnClickListener(v -> {
            if (!isFunctionBlocked) {
                isFunctionBlocked = true;
                String path = "Locations/" + Common.currentLocationCategory + "/" + Common.currentLocationID + "/Comments/" + Common.currentLocationComment + "/SubComment" + id + "/Likes";
                if (hart.getVisibility() == View.GONE) {
                    hart.setVisibility(View.VISIBLE);
                    Data.child(path).child(sharedVariable.getUserID()).setValue(sharedVariable.getUserID())
                            .addOnSuccessListener(unused -> isFunctionBlocked = false);
                } else {
                    hart.setVisibility(View.GONE);
                    Data.child(path).child(sharedVariable.getUserID()).removeValue()
                            .addOnSuccessListener(unused -> isFunctionBlocked = false);
                }
            } else
                Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();
        });
    }

    private void seeMore() {
        min += 10;
        max += 10;
        loadComment(min, max);
    }

    private void clear() {
        parentContainer.removeAllViews();
    }
}