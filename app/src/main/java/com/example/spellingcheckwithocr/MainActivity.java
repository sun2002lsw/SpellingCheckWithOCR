package com.example.spellingcheckwithocr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.material.tabs.TabLayout;

import java.io.File;

import OCR.OCR_tesseract;
import OCR.OcrEngine;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private File picture;
    private OcrEngine ocrEngine;
    private String extractedString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 내가 만든 툴바를 가져와서 해당 툴바를 액션바로 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 상단 탭. 일단 다른 2개 탭은 비활성화
        tabLayout = findViewById(R.id.tabLayout);
        TabLayout.Tab secondTab = tabLayout.getTabAt(1);
        TabLayout.Tab thirdTab = tabLayout.getTabAt(2);

        if (secondTab != null) {
            secondTab.view.setClickable(false);
        }
        if (thirdTab != null) {
            thirdTab.view.setClickable(false);
        }

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

                TabLayout.Tab tab = tabLayout.getTabAt(position);
                if (tab != null) {
                    tab.select();
                }
            }
        });
        
        // todo. 이거나중에 고를 수 있게 바꾸자
        SetOcrEngine(new OCR_tesseract());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // 해당 탭까지 활성화
    public void EnableTab(int position) {
        for (int i = 0; i <= position; i ++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.view.setClickable(true);
            }
        }
        for (int i = position + 1; i < 3; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.view.setClickable(false);
            }
        }

        viewPagerAdapter.setItemCount(position + 1);

        if (viewPager.getCurrentItem() > position) {
            viewPager.setCurrentItem(position);
            viewPager.setAdapter(viewPagerAdapter);
        } else {
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setCurrentItem(position);
        }
    }

    // 촬영한 사진
    public void SetPicture(File picture) {
        this.picture = picture;
    }
    public File GetPicture() {
        return picture;
    }

    // OCR
    public void SetOcrEngine(OcrEngine ocrEngine) { this.ocrEngine = ocrEngine; }
    public OcrEngine GetOcrEngine() { return ocrEngine; }
    
    // 추출된 문자열
    public void SetExtractedString(String str) { this.extractedString = str; }
    public String GetExtractedString() {
        return this.extractedString;
    }
}