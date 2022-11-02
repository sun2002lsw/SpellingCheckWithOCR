package fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    EditText editText;
    Button ocrBtn;
    
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
        
        imageView = view.findViewById(R.id.picture2);
        progressBar = view.findViewById(R.id.progressBar);
        editText = view.findViewById(R.id.extractedString);
        ocrBtn = view.findViewById(R.id.ocrBtn);

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
        
        // OCR 진행
        new Thread(() -> {
            processOCR(activity);
        }).start();

        return view;
    }

    private void processOCR(@NonNull FragmentActivity activity) {
        activity.runOnUiThread(() -> editText.getText().clear());

        // OCR 진행에 따른 시각화 처리
        progressBar.setProgress(0);
        TessBaseAPI.ProgressNotifier pn = progress -> progressBar.setProgress(progress.getPercent());

        OCR_tesseract ocrEngine = util.MainActivity(this).GetOcrEngine();
        ocrEngine.SetProgressNotifier(pn);

        // OCR 중단 버튼
        ocrBtn.setOnClickListener(v -> ocrEngine.StopOCR());

        // OCR
        String extractedString = null;
        try {
            extractedString = ocrEngine.ProcessOCR(picture);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        // 작업이 중단된 경우, 첫 화면으로 복귀
        if (extractedString == null || extractedString.isEmpty()) {
            util.MainActivity(extractString.this).SetExtractedString("");
            activity.runOnUiThread(() -> util.MainActivity(extractString.this).EnableTab(0));
            return;
        }

        // 작업 성공
        util.MainActivity(extractString.this).SetExtractedString(extractedString);
        afterOCR(activity, extractedString);
    }

    private void afterOCR(@NonNull FragmentActivity activity, String extractedString) {
        activity.runOnUiThread(() -> {
            editText.setText(extractedString);

            // ocr 중단 버튼을 맞춤법 검사 버튼으로 수정
            ocrBtn.setText("맞춤법 검사하기");
            ocrBtn.setTextColor(Color.BLACK);
            ocrBtn.setBackgroundColor(Color.GREEN);
            ocrBtn.setOnClickListener(v -> {
                String check = util.MainActivity(extractString.this).GetExtractedString();
                if (check.isEmpty()) {
                    Toast.makeText(getContext(), "앗! 사진을 다시 찍었네요", Toast.LENGTH_SHORT).show();
                    util.MainActivity(extractString.this).EnableTab(1);
                    return;
                }

                util.MainActivity(extractString.this).EnableTab(2);
            });

            progressBar.setProgress(100);
        });
    }
}