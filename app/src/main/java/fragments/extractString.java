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

import com.example.spellingcheckwithocr.R;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

import OCR.OCR_tesseract;
import helper.util;

public class extractString extends Fragment {

    File picture;
    ImageView imageView;
    ProgressBar progressBar;
    TextView textView;
    boolean isLargeTextView;
    Dialog textEditDialog;
    Button abortOCR, editText, checkSpelling;
    
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
        imageView.setOnClickListener(v -> util.MainActivity(extractString.this).MoveTab(0));

        progressBar = view.findViewById(R.id.progressBar);
        
        textView = view.findViewById(R.id.extractedString);
        textView.setOnClickListener(v -> {
            if (isLargeTextView) {
                isLargeTextView = false;
                ConstraintLayout layout = view.findViewById(R.id.textLayout);
                layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            }else {
                isLargeTextView = true;
                ConstraintLayout layout = view.findViewById(R.id.textLayout);
                layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, (float) 0.001));
            }
        });

        textEditDialog = new Dialog(ctx);
        textEditDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        textEditDialog.setCancelable(true);
        textEditDialog.setContentView(R.layout.dialog_edit_text);
        textEditDialog.findViewById(R.id.saveEditedText).setOnClickListener(v -> {
            EditText editTextView = textEditDialog.findViewById(R.id.largeTextViewForEdit);
            String editedText = editTextView.getText().toString();

            util.MainActivity(extractString.this).SetExtractedString(editedText);
            textView.setText(editedText);

            textEditDialog.dismiss();
        });

        abortOCR = view.findViewById(R.id.abortOCR);
        abortOCR.setBackgroundColor(Color.RED);

        editText = view.findViewById(R.id.editText);
        editText.setEnabled(false);
        editText.setOnClickListener(v -> openTextEditDialog());

        checkSpelling = view.findViewById(R.id.checkSpelling);
        checkSpelling.setEnabled(false);
        checkSpelling.setOnClickListener(v -> util.MainActivity(extractString.this).EnableTab(2));

        // 찍은 사진 가져오기. 사진이 없으면 이전 탭으로 복귀
        picture = util.MainActivity(this).GetPicture();
        if (picture == null) {
            Toast.makeText(getContext(), "사진을 찍어주세요", Toast.LENGTH_SHORT).show();
            util.MainActivity(this).EnableTab(0);
            return view;
        }

        // 찍어둔 사진 출력
        new Thread(() -> {
            Uri pictureUri = util.FileToUri(getContext(), picture);
            activity.runOnUiThread(() -> imageView.setImageURI(pictureUri));
        }).start();

        // 이미 문자열을 추출했으면, 그대로 출력
        String extractedString = util.MainActivity(extractString.this).GetExtractedString();
        if (!extractedString.isEmpty()) {
            afterOCR(activity, extractedString);
            return view;
        }
        
        // OCR 비동기 진행
        new Thread(() -> processOCR(activity)).start();

        return view;
    }

    private void processOCR(@NonNull FragmentActivity activity) {
        // OCR 진행에 따른 시각화 처리
        progressBar.setProgress(0);
        TessBaseAPI.ProgressNotifier pn = progress -> progressBar.setProgress(progress.getPercent());

        OCR_tesseract ocrEngine = util.MainActivity(this).GetOcrEngine();
        ocrEngine.SetProgressNotifier(pn);

        // OCR 중단 버튼
        abortOCR.setOnClickListener(v -> ocrEngine.StopOCR());

        // OCR
        String extractedString = null;
        try {
            extractedString = ocrEngine.ProcessOCR(picture);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        // 작업 도중에 뭔가 다른짓해서 현재 Fragment 파괴됨
        if (util.MainActivity(extractString.this) == null) {
            return;
        }

        // 작업이 중단된 경우, 첫 화면으로 복귀
        if (extractedString == null || extractedString.isEmpty()) {
            activity.runOnUiThread(() -> util.MainActivity(extractString.this).EnableTab(0));
            return;
        }

        // 작업 성공
        util.MainActivity(extractString.this).SetExtractedString(extractedString);
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

            progressBar.setProgress(100);
        });
    }
    
    private void openTextEditDialog() {
        EditText editTextView = textEditDialog.findViewById(R.id.largeTextViewForEdit);
        editTextView.setText(textView.getText().toString());

        textEditDialog.show();
        textEditDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}