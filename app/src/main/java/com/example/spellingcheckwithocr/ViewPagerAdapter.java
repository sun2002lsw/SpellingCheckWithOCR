package com.example.spellingcheckwithocr;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import fragments.checkSpellingFragment;
import fragments.extractStringFragment;
import fragments.takePhotoFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private int availableTabCnt = 1;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) { super(fragmentActivity); }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new extractStringFragment();
            case 2:
                return new checkSpellingFragment();
            default:
                return new takePhotoFragment();
        }
    }

    @Override
    public int getItemCount() {
        return availableTabCnt;
    }

    public void setItemCount(int cnt) { availableTabCnt = cnt; }
}