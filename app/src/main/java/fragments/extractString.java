package fragments;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.spellingcheckwithocr.R;

import java.io.File;

import OCR.OcrEngine;
import helper.util;

public class extractString extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_extract_string, container, false);

        // 찍은 사진 가져오기. 사진이 없으면 이전 탭으로 복귀
        File picture = helper.util.MainActivity(this).GetPicture();
        if (picture == null) {
            Toast toast = Toast.makeText(getContext(), "사진이 없어요~", Toast.LENGTH_LONG);
            toast.show();
            
            helper.util.MainActivity(this).EnableTab(0);
            return view;
        }

        // 찍어둔 사진 출력
        ImageView imageView = view.findViewById(R.id.picture2);
        Uri pictureUri = util.FileToUri(getContext(), picture);
        imageView.setImageURI(pictureUri);

        // OCR 진행에 따른 시각화 처리
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        OcrEngine ocrEngine = helper.util.MainActivity(this).GetOcrEngine();
        ocrEngine.SetProgressListener(progressBar::setProgress);

        // OCR 비동기로 진행
        EditText editText = view.findViewById(R.id.extractedString);
        new Thread(() -> {
            String extractedString = null;
            try {
                extractedString = ocrEngine.ProcessOCR(picture);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            util.MainActivity(extractString.this).SetExtractedString(extractedString);
            editText.setText(extractedString);
        }).start();

        return view;
    }
}