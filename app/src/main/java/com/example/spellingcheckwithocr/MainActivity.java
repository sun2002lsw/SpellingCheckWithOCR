package com.example.spellingcheckwithocr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.File;

import OCR.OCR_tesseract;
import helper.util;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private File picture;
    private OCR_tesseract tesseract;
    private String ocrLanguage = "kor";
    private String menuSelectLang;
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

        // 대망의 OCR 엔진
        tesseract = new OCR_tesseract();
        tesseract.SetLanguageCode(MainActivity.this, ocrLanguage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.setting) {
            showOptionMenu();
        } else if (itemId == R.id.info) {
            showInfoMenu();
        } else {
            Toast.makeText(MainActivity.this, "무슨 메뉴를 누른거지?", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showOptionMenu() {
        final String[] languages = { "한글", "영어" };
        menuSelectLang = languages[0];

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("어떤 글자를 읽을지 선택");
        builder.setSingleChoiceItems(languages, 0, (dialog, which) -> menuSelectLang = languages[which]);

        builder.setPositiveButton("선택", (dialog, which) -> {
            String selectLang = util.KoreanToLanguageCode(menuSelectLang);
            if (selectLang.equals(ocrLanguage)) {
                String text = "이미 " + menuSelectLang + " 읽기로 설정중입니다";
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                return;
            }
            
            ocrLanguage = selectLang;
            tesseract.SetLanguageCode(MainActivity.this, ocrLanguage);
            SetExtractedString(""); // 언어 변경으로 문자열 다시 추출해야 함
            
            String text = util.LanguageCodeToKorean(ocrLanguage) + " 읽기로 설정 되었습니다";
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            
            // 사진 촬영 화면으로 복귀
            EnableTab(0);
        });

        builder.setNegativeButton("취소", (dialog, which) -> {
            String text = util.LanguageCodeToKorean(ocrLanguage) + " 읽기가 유지 됩니다";
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    private void showInfoMenu() {
        final Dialog dialog = new Dialog(MainActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_info);

        // 맨위 제목
        Animation blink = new AlphaAnimation(1.0f, 0.0f);
        blink.setDuration(250);
        blink.setStartOffset(1000);
        blink.setRepeatMode(Animation.RESTART);
        blink.setRepeatCount(Animation.INFINITE);

        TextView title = dialog.findViewById(R.id.infoTitle);
        title.startAnimation(blink);

        // 맨 아래 버튼
        Button btn = dialog.findViewById(R.id.infoBtn);
        btn.setOnClickListener(v -> dialog.dismiss());

        int colorFrom = ContextCompat.getColor(this, R.color.purple_200);
        int colorTo = ContextCompat.getColor(this, R.color.teal_200);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(500);
        colorAnimation.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimation.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimation.addUpdateListener(animation -> btn.setBackgroundColor((int)animation.getAnimatedValue()));
        colorAnimation.start();

        dialog.show();
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

    public void MoveTab(int position) {
        if (viewPager.getCurrentItem() <= position) {
            return;
        }

        viewPager.setCurrentItem(position);
    }

    // 촬영한 사진
    public void SetPicture(File picture) {
        this.picture = picture;
    }
    public File GetPicture() {
        return picture;
    }

    // OCR
    public String GetOcrLanguage() { return ocrLanguage; }
    public OCR_tesseract GetOcrEngine() { return tesseract; }
    
    // 추출된 문자열
    public void SetExtractedString(String str) { this.extractedString = str; }
    public String GetExtractedString() { return this.extractedString; }
}