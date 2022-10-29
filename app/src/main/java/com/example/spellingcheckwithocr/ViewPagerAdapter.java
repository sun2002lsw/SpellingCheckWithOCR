package com.example.spellingcheckwithocr;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import fragments.checkSpelling;
import fragments.extractString;
import fragments.takePhoto;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private int availableTabCnt = 1;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) { super(fragmentActivity); }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new extractString();
            case 2:
                return new checkSpelling();
            default:
                return new takePhoto();
        }
    }

    @Override
    public int getItemCount() {
        return availableTabCnt;
    }

    public void setItemCount(int cnt) { availableTabCnt = cnt; }
}