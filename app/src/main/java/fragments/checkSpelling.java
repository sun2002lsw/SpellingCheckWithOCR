package fragments;

import android.os.Bundle;
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
        textView.setText(extractedString);
        textView.setOnClickListener(v -> util.MainActivity(checkSpelling.this).MoveTab(1));

        spellingCheckView = view.findViewById(R.id.spellingCheckTextView);
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
        });

        // 맞춤법 검사
        new Thread(() -> {
            ArrayList<WrongWordInfo> wrongWordInfos;
            SpellingCheckEngine checkEngine = util.MainActivity(checkSpelling.this).GetSpellingCheckEngine();

            String checkedStr = new String();
            try {
                wrongWordInfos = checkEngine.CheckSpelling(extractedString);
                for (int i = 0; i < wrongWordInfos.size(); i ++) {
                    checkedStr += makeWrongWordCheckStr(i, wrongWordInfos.get(i));
                }
            } catch (Exception e) {
                checkedStr = e.toString();
            }

            afterSpellingCheck(view, activity, checkedStr);

        }).start();

        return view;
    }

    private void afterSpellingCheck(@NonNull View view, @NonNull FragmentActivity activity, String checkedStr) {
        ProgressBar progressCircle = view.findViewById(R.id.checkSpellingProgress);

        activity.runOnUiThread(() -> {
            progressCircle.setVisibility(View.GONE);
            spellingCheckView.setText(checkedStr);
        });
    }

    private String makeWrongWordCheckStr(int index, WrongWordInfo wrongWordInfo) {
        String numbering = Integer.toString(index + 1) + ". ";
        String correction = wrongWordInfo.wrongWord + " -> ";
        correction += wrongWordInfo.correctWords[0];
        for (int i = 1; i < wrongWordInfo.correctWords.length; i++) {
            correction += ", " + wrongWordInfo.correctWords[i];
        }
        String reason = "\n(" + wrongWordInfo.reason + ")\n";
        
        return numbering + correction + reason;
    }
}