package com.interceptor.mcamp;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class ActivityFullscreenImage extends AppCompatActivity {

    private final Bitmap[] imageUrls = Common.currentLocationImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        ViewPager viewPager = findViewById(R.id.viewPager);
        FullscreenImageAdapter adapter = new FullscreenImageAdapter(this, imageUrls);
        viewPager.setAdapter(adapter);
    }
}
