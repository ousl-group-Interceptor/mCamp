package com.interceptor.mcamp;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class PageAdapter extends FragmentPagerAdapter {
    int numCount;
    FragmentOverView overView = new FragmentOverView();
    FragmentReviews reviews = new FragmentReviews();
    FragmentComments comments = new FragmentComments();

    public PageAdapter(@NonNull FragmentManager fm, int numCount) {
        super(fm);
        this.numCount = numCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return overView;
            case 1: return reviews;
            case 2: return comments;
        }
        return overView;
    }

    @Override
    public int getCount() {
        return numCount;
    }
}
