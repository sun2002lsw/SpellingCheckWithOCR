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

        // 찍은 사진 가져오기. 사진이 없으면 이전 탭으로 복귀
        File picture = util.MainActivity(this).GetPicture();
        if (picture == null) {
            Toast.makeText(getContext(), "사진을 찍어주세요", Toast.LENGTH_SHORT).show();
            util.MainActivity(this).EnableTab(0);
            return view;
        }

        // 찍어둔 사진 출력
        new Thread(() -> {
            ImageView imageView = view.findViewById(R.id.picture2);
            Uri pictureUri = util.FileToUri(getContext(), picture);

            activity.runOnUiThread(() -> imageView.setImageURI(pictureUri));
        }).start();

        // OCR 진행에 따른 시각화 처리
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        TessBaseAPI.ProgressNotifier pn = progress -> progressBar.setProgress(progress.getPercent());

        OCR_tesseract ocrEngine = util.MainActivity(this).GetOcrEngine();
        ocrEngine.SetProgressNotifier(pn);

        // OCR 중단에 따른 처리
        Button stopOCR = view.findViewById(R.id.stopOCR);
        stopOCR.setOnClickListener(v -> ocrEngine.StopOCR());

        // OCR 진행
        new Thread(() -> {
            EditText editText = view.findViewById(R.id.extractedString);
            activity.runOnUiThread(() -> editText.getText().clear());

            // OCR
            String extractedString = null;
            try {
                extractedString = ocrEngine.ProcessOCR(picture);
            } catch (Exception e) {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }

            // 작업이 중단한 경우, 첫 화면으로 복귀
            if (extractedString == null || extractedString.isEmpty()) {
                activity.runOnUiThread(() -> util.MainActivity(extractString.this).EnableTab(0));
                return;
            }

            // 후처리
            String finalExtractedString = extractedString;
            Button checkSpelling = view.findViewById(R.id.checkSpelling);

            activity.runOnUiThread(() -> {
                editText.setText(finalExtractedString);

                stopOCR.setEnabled(false);
                stopOCR.setTextColor(Color.TRANSPARENT);
                stopOCR.setBackgroundColor(Color.TRANSPARENT);

                checkSpelling.setEnabled(true);
                checkSpelling.setTextColor(Color.BLACK);
                checkSpelling.setBackgroundColor(Color.GREEN);

                progressBar.setProgress(100);
            });

            util.MainActivity(extractString.this).SetExtractedString(extractedString);
        }).start();

        return view;
    }
}