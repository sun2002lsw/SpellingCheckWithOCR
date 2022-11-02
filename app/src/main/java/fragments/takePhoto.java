package fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.spellingcheckwithocr.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import helper.util;

public class takePhoto extends Fragment {

    private Uri pictureUri;
    private ImageView imageView;
    private Button takePhotoBtn, takePhotoAgainBtn, extractStringBnt;

    // 카메라 사용 허가에 대한 처리. 허가하면 그대로 이어서 카메라 실행
    final private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            permissionGranted -> {
                if (permissionGranted) {
                    TakePicture();
                } else {
                    Toast.makeText(getContext(), "카메라 사용을 허가 해주세요", Toast.LENGTH_SHORT).show();
                }
            });

    // 카메라 호출 후 결과 값을 어떻게 처리할 지. 이미지 뷰에 출력및 후처리
    final private ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    AfterTakePicture();
                }
            });

    // 사진이 있을 때 후처리
    private void AfterTakePicture() {
        // 촬영 이미지
        imageView.setImageURI(pictureUri);

        // 첫 촬영 버튼 사라짐
        takePhotoBtn.setEnabled(false);
        takePhotoBtn.setTextColor(Color.TRANSPARENT);
        takePhotoBtn.setBackgroundColor(Color.TRANSPARENT);
        
        // 촬영 버튼 등장
        takePhotoAgainBtn.setEnabled(true);
        takePhotoAgainBtn.setTextColor(Color.BLACK);
        takePhotoAgainBtn.setBackgroundColor(Color.YELLOW);

        // 다음 탭 진행 버튼 등장
        String languageCode = util.MainActivity(this).GetOcrLanguage();
        String btnText = util.LanguageCodeToKorean(languageCode) + " 읽기";

        extractStringBnt.setEnabled(true);
        extractStringBnt.setText(btnText);
        extractStringBnt.setTextColor(Color.BLACK);
        extractStringBnt.setBackgroundColor(Color.GREEN);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_take_photo, container, false);

        imageView = view.findViewById(R.id.picture);

        // 첫 사진 찍기 버튼. 카메라 권한이 없으면 권한 요청 & 카메라 촬영, 아니면 그냥 바로 촬영
        takePhotoBtn = view.findViewById(R.id.takePhoto);
        takePhotoBtn.setBackgroundColor(Color.GREEN);
        takePhotoBtn.setOnClickListener(v -> {
            if (NeedCameraPermission()) {
                RequestCameraPermission();
            } else {
                TakePicture();
            }
        });

        // 사진 다시 찍기 버튼. 처음에는 비활성화하고 AfterTakePicture 이후 활성화. 작업은 위와 같음
        takePhotoAgainBtn = view.findViewById(R.id.takePhotoAgain);
        takePhotoAgainBtn.setEnabled(false);
        takePhotoAgainBtn.setOnClickListener(v -> {
            if (NeedCameraPermission()) {
                RequestCameraPermission();
            } else {
                TakePicture();
            }
        });
        
        // 글자 읽기 버튼. 처음에는 비활성화하고 AfterTakePicture 이후 활성화. 누르면 다음 탭으로 진행
        extractStringBnt = view.findViewById(R.id.extractString);
        extractStringBnt.setEnabled(false);
        extractStringBnt.setOnClickListener(v -> {
            util.MainActivity(takePhoto.this).SetExtractedString("");
            util.MainActivity(takePhoto.this).EnableTab(1);
        });

        // 혹시 이미 사진을 찍었으면 다 필요없고 그걸로 처리
        File picture = util.MainActivity(this).GetPicture();
        if (picture != null) {
            pictureUri = util.FileToUri(getContext(), picture);
            AfterTakePicture();
        }

        return view;
    }

    // 카메라 권한 확인및 요청
    @NonNull
    private Boolean NeedCameraPermission() {
        Context ctx = getContext();
        if (ctx == null) {
            return Boolean.TRUE;
        }

        int cameraPermissionState = ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA);
        return cameraPermissionState != PackageManager.PERMISSION_GRANTED;
    }

    private void RequestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    // 사진 찍기. 찍은 사진 이미지 뷰에 출력
    private void TakePicture() {
        // 찍은 사진을 저장할 임시 사진 파일 생성및 URI 획득
        File picture;
        try {
            picture = CreateTempPictureFile();
        } catch (IOException ex) {
            Toast.makeText(getContext(), ex.toString(), Toast.LENGTH_LONG).show();
            return;
        }
        pictureUri = util.FileToUri(getContext(), picture);

        // 해당 URI 파일에 사진을 저장하는 것으로 사진 촬영 시작
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        takePictureLauncher.launch(takePictureIntent);
    }
    
    // 임의 이름의 사진 파일 생성및 반환
    @NonNull
    private File CreateTempPictureFile() throws IOException {
        Context ctx = getContext();
        if (ctx == null) {
            throw new IOException("getContext() is null");
        }

        // 해당 앱 전용 사진 저장 폴더
        File[] storageDirs = ContextCompat.getExternalFilesDirs(ctx, Environment.DIRECTORY_PICTURES);
        if (storageDirs.length == 0) {
            throw new IOException("there is no picture storageDir");
        }
        File storageDir = storageDirs[0];

        // 일단 해당 폴더를 깔끔하게 청소
        File[] files = storageDir.listFiles();
        for(File file : files != null ? files : new File[0]) {
            if (!file.delete()) {
                Log.d("warning", "기존 사진 파일 삭제를 실패했습니다");
            }
        }

        // 임의의 파일 이름을 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(new Date());
        String pictureFileName = "JPEG_" + timeStamp + "_";

        // 전용 사진 폴더에 임의 이름의 사진 파일 생성
        File picture = File.createTempFile(pictureFileName, ".jpg", storageDir);

        // 해당 사진 이미지 반환
        util.MainActivity(this).SetPicture(picture);
        return picture;
    }
}