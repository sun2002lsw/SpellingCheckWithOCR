package com.example.spellingcheckwithocr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Menu;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    Toolbar mToolbar;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 내가 만든 툴바를 가져와서 해당 툴바를 액션바로 설정
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // 상단 탭. 일단 다른 2개 탭은 비활성화
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.getTabAt(1).view.setClickable(false);
        tabLayout.getTabAt(2).view.setClickable(false);
        
        // 탭 아래의 프레그먼트 화면
        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        
        // 탭을 직접 클릭 했을 때
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        // 탭을 스와이프 했을 때
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // 해당 탭까지 활성화
    public void enableTab(int position) {
        tabLayout.getTabAt(position).view.setClickable(true);
        viewPagerAdapter.setItemCount(position + 1);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(position);
    }
}