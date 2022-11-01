package fragments;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.spellingcheckwithocr.R;

import java.io.File;

import OCR.OcrEngine;
import OCR.OcrProgressListener;
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

        // 문자열 출력 구간 설정
        EditText editText = view.findViewById(R.id.extractedString);
        editText.setText("");

        // OCR 진행에 따른 시각화 처리
        OcrEngine ocrEngine = helper.util.MainActivity(this).GetOcrEngine();
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        ocrEngine.SetProgressListener(new OcrProgressListener() {
            @Override
            public void onProgress(int from, int to) {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", from, to);
                            animation.setDuration(3000); // 3 second
                            animation.setInterpolator(new LinearInterpolator());
                            animation.start();
                        }
                    });
                }
            }
        });

        // OCR 비동기로 진행
        new Thread(() -> {
            ocrEngine.Init(getContext(), "kor");
            String extractedString = ocrEngine.ProcessOCR(picture);
            util.MainActivity(extractString.this).SetExtractedString(extractedString);

            // 문자열 화면에 출력
            FragmentActivity activity = getActivity();
            if (activity != null) {
                String stringForTextView = extractedString;
                activity.runOnUiThread(() -> editText.setText(stringForTextView));
            }
        }).start();

        return view;
    }
}