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
import java.util.concurrent.atomic.AtomicBoolean;

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

        Button checkSpelling = view.findViewById(R.id.checkSpelling);
        checkSpelling.setBackgroundColor(Color.GRAY);
        checkSpelling.setEnabled(false);

        EditText editText = view.findViewById(R.id.extractedString);

        AtomicBoolean ocrComplete = new AtomicBoolean();

        // 찍은 사진 가져오기. 사진이 없으면 이전 탭으로 복귀
        File picture = util.MainActivity(this).GetPicture();
        if (picture == null) {
            Toast toast = Toast.makeText(getContext(), "사진을 찍어주세요", Toast.LENGTH_SHORT);
            toast.show();
            
            util.MainActivity(this).EnableTab(0);
            return view;
        }

        // 찍어둔 사진 출력
        ImageView imageView = view.findViewById(R.id.picture2);
        Uri pictureUri = util.FileToUri(getContext(), picture);
        imageView.setImageURI(pictureUri);

        // OCR 진행에 따른 시각화 처리
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        TessBaseAPI.ProgressNotifier pn = progress -> progressBar.setProgress(progress.getPercent());

        OCR_tesseract ocrEngine = util.MainActivity(this).GetOcrEngine();
        ocrEngine.SetProgressNotifier(pn);

        // OCR 비동기로 진행
        new Thread(() -> {
            activity.runOnUiThread(() -> editText.getText().clear());

            // OCR
            String extractedString = ocrEngine.ProcessOCR(picture);
            ocrComplete.set(true);

            // 후처리
            util.MainActivity(extractString.this).SetExtractedString(extractedString);
            activity.runOnUiThread(() -> {
                progressBar.setProgress(100);
                editText.setText(extractedString);
                checkSpelling.setBackgroundColor(Color.GREEN);
                checkSpelling.setEnabled(true);
            });
        }).start();

        return view;
    }
}