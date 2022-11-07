package fragments;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.spellingcheckwithocr.R;

import java.util.ArrayList;

import checkSpelling.SpellingCheckEngine;
import checkSpelling.WrongWordInfo;
import helper.util;

public class checkSpelling extends Fragment {

    boolean isLargeOriginalView;
    boolean isLargeCheckView;
    TextView spellingCheckView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_spelling, container, false);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return view;
        }

        String extractedString = util.MainActivity(checkSpelling.this).GetExtractedString();
        
        // UI 설정
        TextView textView = view.findViewById(R.id.originalTextView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(extractedString);
        textView.setOnClickListener(v -> {
            if (isLargeOriginalView) {
                isLargeOriginalView = false;
                ConstraintLayout layout = view.findViewById(R.id.originalViewLayout);
                layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            }else {
                isLargeOriginalView = true;
                ConstraintLayout layout = view.findViewById(R.id.originalViewLayout);
                layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, (float) 0.001));
            }

            util.MainActivity(checkSpelling.this).SetSwipeEnable(!isLargeOriginalView);
        });

        spellingCheckView = view.findViewById(R.id.spellingCheckTextView);
        spellingCheckView.setMovementMethod(new ScrollingMovementMethod());
        spellingCheckView.setOnClickListener(v -> {
            if (isLargeCheckView) {
                isLargeCheckView = false;
                ConstraintLayout layout = view.findViewById(R.id.spellingCheckLayout);
                layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            }else {
                isLargeCheckView = true;
                ConstraintLayout layout = view.findViewById(R.id.spellingCheckLayout);
                layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, (float) 0.001));
            }

            util.MainActivity(checkSpelling.this).SetSwipeEnable(!isLargeCheckView);
        });

        // 맞춤법 검사
        new Thread(() -> {
            ArrayList<WrongWordInfo> wrongWordInfos;
            SpellingCheckEngine checkEngine = util.MainActivity(checkSpelling.this).GetSpellingCheckEngine();

            StringBuilder checkedStr = new StringBuilder();
            try {
                wrongWordInfos = checkEngine.CheckSpelling(extractedString);
                for (int i = 0; i < wrongWordInfos.size(); i ++) {
                    checkedStr.append(makeWrongWordCheckStr(i, wrongWordInfos.get(i)));
                }
            } catch (Exception e) {
                checkedStr = new StringBuilder(e.toString());
            }

            afterSpellingCheck(view, activity, checkedStr.toString());

        }).start();

        return view;
    }

    private void afterSpellingCheck(@NonNull View view, @NonNull FragmentActivity activity, @NonNull String checkedStr) {
        ProgressBar progressCircle = view.findViewById(R.id.checkSpellingProgress);
        if (checkedStr.isEmpty()) {
            checkedStr = "고칠 부분이 없습니다~ 대단합니다!";
        }

        String finalCheckedStr = checkedStr;
        activity.runOnUiThread(() -> {
            progressCircle.setVisibility(View.GONE);
            spellingCheckView.setText(finalCheckedStr);
        });
    }

    @NonNull
    private String makeWrongWordCheckStr(int index, @NonNull WrongWordInfo wrongWordInfo) {
        String numbering = (index + 1) + ". ";
        StringBuilder correction = new StringBuilder(wrongWordInfo.wrongWord + " -> ");
        correction.append(wrongWordInfo.correctWords[0]);
        for (int i = 1; i < wrongWordInfo.correctWords.length; i++) {
            correction.append(", ").append(wrongWordInfo.correctWords[i]);
        }
        String reason = "\n※ " + wrongWordInfo.reason + "\n\n";
        
        return numbering + correction + reason;
    }
}