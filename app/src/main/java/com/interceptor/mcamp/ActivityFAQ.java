package com.interceptor.mcamp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ActivityFAQ extends AppCompatActivity {

    private LinearLayout parentContainer;
    private LayoutInflater layoutInflater;
    private DatabaseReference Data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);

        parentContainer = findViewById(R.id.faqs);
        layoutInflater = LayoutInflater.from(this);

        loadFAQs(new boolean[]{true});
    }

    private void loadFAQs(boolean[] run) {
        Common.startLoading(this, "Loading...");
        Data.child("FAQ").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    if(dataSnapshot.exists()){
                        List<DataSnapshot> snapshotList = new ArrayList<>();
                        for (DataSnapshot faq : dataSnapshot.getChildren()) {
                            snapshotList.add(faq);
                        }

                        Collections.reverse(snapshotList);

                        for (DataSnapshot reversedFAQ : snapshotList) {
                            faqAdder(Objects.requireNonNull(reversedFAQ.child("Question").getValue()).toString(),
                                    Objects.requireNonNull(reversedFAQ.child("Answer").getValue()).toString());
                        }
                        Common.stopLoading();
                    }else
                        Common.stopLoading();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    private void faqAdder(String question, String answer) {
        LinearLayout xmlView = (LinearLayout) layoutInflater.inflate(R.layout.individual_faq, parentContainer, false);

        TextView ques = xmlView.findViewById(R.id.question);
        TextView ans = xmlView.findViewById(R.id.answer);

        ques.setText(question);
        ans.setText(answer);

        parentContainer.addView(xmlView);
    }

    public void addQuestion(View view) {
        PopupWindow popupWindow;
        // Inflate the popup window layout
        @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_ask_question, null);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // Find the views within the popup window layout
        EditText questionEditText = popupView.findViewById(R.id.valueEditText);
        Button okButton = popupView.findViewById(R.id.okButton);
        Button cancelButton = popupView.findViewById(R.id.cancelButton);

        okButton.setEnabled(true);

        // Set click listener for the OK button
        okButton.setOnClickListener(v -> {
            Common.startLoading(this, "Loading...");
            String stringNewQuestion = questionEditText.getText().toString();
            // Handle the nickname input as needed
            if (!stringNewQuestion.equals("")) {
                int i = Integer.parseInt(Common.generateOTP());
                @SuppressLint("DefaultLocale")
                String path = "UserFAQ/" + Common.getCDateTimeString() + String.format("%04d", i);
                Data.child(path).child("Question").setValue(stringNewQuestion);
                Data.child(path).child("User").setValue(new SharedVariable(this).getUserID()).addOnSuccessListener(unused -> {
                    Common.stopLoading();
                    popupWindow.dismiss();
                    Toast.makeText(this, "Question Submit successfully.", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(unused -> {
                    Common.stopLoading();
                    Common.showMessage(this, "Error", "Please Try Again.");
                });
            } else {
                Common.stopLoading();
                Common.showMessage(this, "Error!", "Please enter your question.");
            }
        });

        // Set click listener for the Cancel button
        cancelButton.setOnClickListener(v -> popupWindow.dismiss());

        // Create the popup window
        popupWindow.setFocusable(true);

        // Show the popup window at the center of the screen
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }
}