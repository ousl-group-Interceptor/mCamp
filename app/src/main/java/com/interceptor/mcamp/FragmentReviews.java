package com.interceptor.mcamp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DecimalFormat;

public class FragmentReviews extends Fragment {

    private View view;
    private TextView seeMore;
    private ImageView giveRate;
    private RatingBar rating_bar;
    private ProgressBar bar_excellent, bar_good, bar_average, bar_below, bar_poor;
    private DataSnapshot currentLocationReview, userList;
    private StorageReference storageRef;
    private LinearLayout parentContainer;
    private LayoutInflater layoutInflater;
    SharedVariable sharedVariable;
    private int min = 1, max = 10;

    public FragmentReviews() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reviews, container, false);

        rating_bar = view.findViewById(R.id.rating_bar);
        bar_excellent = view.findViewById(R.id.progressBarExcellent);
        bar_good = view.findViewById(R.id.progressBarGood);
        bar_average = view.findViewById(R.id.progressBarAverage);
        bar_below = view.findViewById(R.id.progressBarBelow);
        bar_poor = view.findViewById(R.id.progressBarPoor);
        seeMore = view.findViewById(R.id.see_more);
        giveRate = view.findViewById(R.id.giveRate);

        sharedVariable = new SharedVariable(requireContext());

        seeMore.setOnClickListener(v -> seeMore());
        giveRate.setOnClickListener(v -> giveRate());

        rating_bar.setRating(0.0f);
        bar_excellent.setProgress(0);
        bar_good.setProgress(0);
        bar_average.setProgress(0);
        bar_below.setProgress(0);
        bar_poor.setProgress(0);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        parentContainer = view.findViewById(R.id.users_reviews);
        layoutInflater = LayoutInflater.from(requireContext());

        checkData(new boolean[]{true});


        return view;
    }

    private void giveRate() {
        startActivity(new Intent(requireContext(), com.interceptor.mcamp.ActivityGiveRate.class));
    }

    private void checkData(boolean[] run) {
        Common.startLoading(requireContext(), "Loading...");

        Common.currentLocationData.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    boolean[] secondRun = {true};
                    userList = dataSnapshot;

                    Common.currentLocationData.child("Locations/" + Common.currentLocationCategory + "/" + Common.currentLocationID + "/Reviews").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (secondRun[0]) {
                                secondRun[0] = false;
                                if (dataSnapshot.exists()) {
                                    currentLocationReview = dataSnapshot;
                                    calculateData();
                                    clear();
                                    loadReview(min, max);
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

    private void loadReview(int min, int max) {
        int counter = 0;
        seeMore.setVisibility(View.GONE);
        for (DataSnapshot review : currentLocationReview.getChildren()) {
            counter++;
            if (min > counter) {
                continue;
            }
            if (max < counter) {
                seeMore.setVisibility(View.VISIBLE);
                break;
            }
            reviewAdder(Float.parseFloat(String.valueOf(review.child("Rate").getValue())),
                    String.valueOf(review.child("Description").getValue()),
                    String.valueOf(review.child("Date").getValue()),
                    String.valueOf(review.child("User").getValue()));
        }
        Common.stopLoading();
    }

    private void reviewAdder(float rate, String description, String date, String user) {
        LinearLayout xmlView = (LinearLayout) layoutInflater.inflate(R.layout.single_review, parentContainer, false);

        TextView reviewData = xmlView.findViewById(R.id.review_date);
        TextView reviewDescription = xmlView.findViewById(R.id.user_review_description);
        TextView reviewUser = xmlView.findViewById(R.id.user_name);
        ImageView reviewUserImage = xmlView.findViewById(R.id.review_user);
        RatingBar reviewRating = xmlView.findViewById(R.id.rating_bar);

        reviewData.setText(date);
        reviewDescription.setText(description);
        reviewRating.setRating(rate);
        reviewUser.setText(String.valueOf(userList.child(user + "/name").getValue()));

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

    private void calculateData() {
        int noOfReviews = 0;
        int excellent = 0, good = 0, average = 0, below = 0, poor = 0;
        double sumOfRating = 0.0;
        for (DataSnapshot review : currentLocationReview.getChildren()) {
            noOfReviews++;
            double rating = Double.parseDouble(String.valueOf(review.child("Rate").getValue()));
            sumOfRating += rating;

            if(String.valueOf(review.child("User").getValue()).equals(sharedVariable.getUserID())){
                giveRate.setVisibility(View.GONE);
                break;
            }

            if (rating <= 1)
                poor++;
            else if (rating <= 2)
                below++;
            else if (rating <= 3)
                average++;
            else if (rating <= 4)
                good++;
            else if (rating <= 5)
                excellent++;
        }

        TextView totalReviewString = view.findViewById(R.id.total_number_of_rating);
        TextView ratingRateNumber = view.findViewById(R.id.rating_rate_number);
        String str = "based on " + noOfReviews + " reviews";

        ratingRateNumber.setText(String.valueOf(limitDoubleToOneDecimal(sumOfRating / noOfReviews)));
        rating_bar.setRating(Float.parseFloat(String.valueOf(sumOfRating / noOfReviews)));
        totalReviewString.setText(str);
        bar_excellent.setProgress(excellent * 100 / noOfReviews);
        bar_good.setProgress(good * 100 / noOfReviews);
        bar_average.setProgress(average * 100 / noOfReviews);
        bar_below.setProgress(below * 100 / noOfReviews);
        bar_poor.setProgress(poor * 100 / noOfReviews);
    }

    private double limitDoubleToOneDecimal(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return Double.parseDouble(decimalFormat.format(value));
    }

    private void clear() {
        parentContainer.removeAllViews();
    }

    public void seeMore() {
        min += 10;
        max += 10;
        loadReview(min, max);
    }
}