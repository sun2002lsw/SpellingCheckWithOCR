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
import java.util.Arrays;

import helper.util;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private File picture;
    
    private OCR.engine ocrEngine;
    private String ocrLanguage = "kor";
    
    private String extractedString;
    private checkSpelling.engineMgr checkEngineMgr;

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

        // 탭이 스와이프 됐을 때
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

        // OCR 엔진. 일단 무료인 tesseract 사용
        ocrEngine = new OCR.tesseract();
        ocrEngine.SetLanguage(MainActivity.this, ocrLanguage);

        // 맞춤법 검사기 등록
        checkEngineMgr = new checkSpelling.engineMgr();
        checkEngineMgr.Init(ocrLanguage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_setting) {
            showLanguageMenu();
        } else if (itemId == R.id.menu_ocrEngine) {
            showOcrMenu();
        } else if (itemId == R.id.menu_info) {
            showInfoMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    // 사진 언어 고르기
    private void showLanguageMenu() {
        final String[] languages = { "한글", "영어" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("어떤 글자를 읽을지 선택");

        String curLanguage = util.LanguageCodeToKorean(ocrLanguage);
        int idx = Arrays.asList(languages).indexOf(curLanguage);
        builder.setSingleChoiceItems(languages, idx, (dialog, which) -> {});

        builder.setPositiveButton("선택", (dialog, which) -> {
            if (languages[which].equals(curLanguage)) {
                String text = "이미 " + curLanguage + " 읽기로 설정중입니다";
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                return;
            }

            String selectLang = util.KoreanToLanguageCode(languages[which]);
            ocrLanguage = selectLang;
            checkEngineMgr.SetLanguage(selectLang);
            ocrEngine.SetLanguage(MainActivity.this, selectLang);
            SetExtractedString(""); // 언어 변경으로 문자열 다시 추출해야 함

            String text = languages[which] + " 읽기로 설정 되었습니다";
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            
            // 사진 촬영 화면으로 복귀
            EnableTab(0);
        });

        builder.setNegativeButton("취소", (dialog, which) -> {
            String text = curLanguage + " 읽기가 유지 됩니다";
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    // OCR 엔진 고르기
    private void showOcrMenu() {
        final String[] ocrEngines = {
            OCR.tesseract.class.getSimpleName(),
            OCR.clova.class.getSimpleName()
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("OCR 엔진 선택");

        String curOCR = ocrEngine.getClass().getSimpleName();
        int idx = Arrays.asList(ocrEngines).indexOf(curOCR);
        builder.setSingleChoiceItems(ocrEngines, idx, (dialog, which) -> {});

        builder.setPositiveButton("선택", (dialog, which) -> {
            if (ocrEngines[which].equals(curOCR)) {
                String text = "이미 " + curOCR + " 엔진을 사용중입니다";
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                return;
            }

            OCR.engine newOcrEngine = util.GetOcrEngineByName(ocrEngines[which]);
            if (newOcrEngine == null) {
                Toast.makeText(MainActivity.this, "엥? 무슨 엔진을 고른거지?", Toast.LENGTH_LONG).show();
                return;
            }

            ocrEngine = newOcrEngine;
            ocrEngine.SetLanguage(MainActivity.this, ocrLanguage);
            SetExtractedString(""); // 엔진 변경으로 문자열 다시 추출해야 함

            String text = ocrEngines[which] + " 엔진으로 설정 되었습니다";
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();

            // 사진 촬영 화면으로 복귀
            EnableTab(0);
        });

        builder.setNegativeButton("취소", (dialog, which) -> {
            String text = curOCR + " 엔진이 유지 됩니다";
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    // 개발자 정보
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
    
    // 손으로 밀어서 화면 넘기는거 설정 or 해제
    public void SetSwipeEnable(boolean enabled) {
        viewPager.setUserInputEnabled(enabled);
    }

    // 촬영한 사진
    public void SetPicture(File newPicture) { picture = newPicture; }
    public File GetPicture() { return picture; }

    // OCR
    public String GetOcrLanguage() { return ocrLanguage; }
    public OCR.engine GetOcrEngine() { return ocrEngine; }
    
    // 추출된 문자열
    public void SetExtractedString(String str) { extractedString = str; }
    public String GetExtractedString() { return extractedString; }

    // 맞춤법 검사기
    public checkSpelling.engine GetSpellingCheckEngine() { return checkEngineMgr.GetEngine(); }
}