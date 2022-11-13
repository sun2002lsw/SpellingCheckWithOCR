package fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.spellingcheckwithocr.MainActivity;
import com.example.spellingcheckwithocr.R;

import java.io.File;

import helper.util;

public class extractStringFragment extends Fragment {

    ImageView imageView;
    ProgressBar progressBar, progressCircle;
    TextView textView;
    boolean isLargeTextView;
    Button abortOCR, editText, checkSpelling;
    Dialog textEditDialog;

    File picture;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_extract_string, container, false);

        FragmentActivity activity = getActivity();
        if (activity == null) {
            return view;
        }

        Context ctx = getContext();
        if (ctx == null) {
            return view;
        }

        // 각 UI 설정
        imageView = view.findViewById(R.id.pictureForOCR);
        imageView.setOnClickListener(v -> util.MainActivity(this).MoveTab(0));

        progressBar = view.findViewById(R.id.OCRprogressBar);
        progressBar.setProgress(0);
        progressCircle = view.findViewById(R.id.OCRprogress);
        progressCircle.setVisibility(View.VISIBLE);
        
        textView = view.findViewById(R.id.extractedString);
        textView.setOnClickListener(v -> {
            if (isLargeTextView) {
                isLargeTextView = false;
                ConstraintLayout layout = view.findViewById(R.id.textLayout);
                layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 3));
            } else {
                isLargeTextView = true;
                ConstraintLayout layout = view.findViewById(R.id.textLayout);
                layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            util.MainActivity(this).SetSwipeEnable(!isLargeTextView);
        });

        abortOCR = view.findViewById(R.id.abortOCR);
        abortOCR.setBackgroundColor(Color.RED);

        editText = view.findViewById(R.id.editText);
        editText.setEnabled(false);
        editText.setOnClickListener(v -> openTextEditDialog());

        checkSpelling = view.findViewById(R.id.checkSpelling);
        checkSpelling.setEnabled(false);
        checkSpelling.setOnClickListener(v -> util.MainActivity(this).EnableTab(2));

        // 글 수정 팝업창 설정
        textEditDialog = new Dialog(ctx);
        textEditDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        textEditDialog.setContentView(R.layout.dialog_edit_text);
        textEditDialog.findViewById(R.id.cancelEditedText).setOnClickListener(v -> textEditDialog.dismiss());
        textEditDialog.findViewById(R.id.saveEditedText).setOnClickListener(v -> {
            EditText editTextView = textEditDialog.findViewById(R.id.largeTextViewForEdit);
            String editedText = editTextView.getText().toString();

            util.MainActivity(this).SetExtractedString(editedText);
            activity.runOnUiThread(() -> textView.setText(editedText));

            textEditDialog.dismiss();
        });
        
        // 찍은 사진 가져오기. 사진이 없으면 이전 탭으로 복귀
        picture = util.MainActivity(this).GetPicture();
        if (picture == null) {
            util.MainActivity(this).EnableTab(0);
            return view;
        }

        // 찍어둔 사진 출력
        new Thread(() -> {
            Uri pictureUri = util.FileToUri(getContext(), picture);
            activity.runOnUiThread(() -> imageView.setImageURI(pictureUri));
        }).start();

        // 이미 문자열을 추출했으면, 그대로 출력
        String extractedString = util.MainActivity(this).GetExtractedString();
        if (!extractedString.isEmpty()) {
            afterOCR(activity, extractedString);
            return view;
        }
        
        // OCR 비동기 진행
        new Thread(() -> processOCR(activity)).start();

        return view;
    }

    private void processOCR(@NonNull FragmentActivity activity) {
        OCR.engine ocrEngine = util.MainActivity(this).GetOcrEngine();

        // OCR 진행에 따른 시각화 처리
        ocrEngine.SetProgressbar(progressBar);

        // OCR 중단 버튼
        abortOCR.setOnClickListener(v -> {
            ocrEngine.StopOCR();
            activity.runOnUiThread(() -> util.MainActivity(this).EnableTab(0));
        });

        // OCR
        String extractedString = null;
        try {
            extractedString = ocrEngine.StartOCR(picture);
        } catch (Exception e) {
            // 이미 파괴된 Fragment
            if (util.MainActivity(this) == null) {
                return;
            }

            activity.runOnUiThread(() -> {
                util.MainActivity(this).ShowShortToast(e.getMessage());
                util.MainActivity(this).EnableTab(0);
            });
        }

        // 작업 도중에 뭔가 다른짓해서 현재 Fragment 파괴됨
        if (util.MainActivity(this) == null) {
            return;
        }

        // 작업이 중단됨
        if (extractedString == null || extractedString.isEmpty()) {
            return;
        }

        // 작업 성공
        String msg = ocrEngine.getClass().getSimpleName() + " 엔진으로 사진을 읽었습니다";
        activity.runOnUiThread(() -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());
        
        util.MainActivity(this).SetExtractedString(extractedString);
        afterOCR(activity, extractedString);
    }

    private void afterOCR(@NonNull FragmentActivity activity, String extractedString) {
        activity.runOnUiThread(() -> {
            textView.setText(extractedString);

            // ocr 중단 버튼 사라짐
            abortOCR.setEnabled(false);
            abortOCR.setTextColor(Color.TRANSPARENT);
            abortOCR.setBackgroundColor(Color.TRANSPARENT);

            // 수정 버튼 & 맞춤법 버튼 등장
            editText.setEnabled(true);
            editText.setTextColor(Color.BLACK);
            editText.setBackgroundColor(Color.YELLOW);

            checkSpelling.setEnabled(true);
            checkSpelling.setTextColor(Color.BLACK);
            checkSpelling.setBackgroundColor(Color.GREEN);

            // 진행도 완료 처리
            progressBar.setProgress(100);
            progressCircle.setVisibility(View.GONE);
        });
    }
    
    private void openTextEditDialog() {
        EditText editTextView = textEditDialog.findViewById(R.id.largeTextViewForEdit);
        editTextView.setText(textView.getText().toString());

        textEditDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textEditDialog.show();
    }
}