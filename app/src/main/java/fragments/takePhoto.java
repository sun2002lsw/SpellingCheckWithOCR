package fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.spellingcheckwithocr.MainActivity;
import com.example.spellingcheckwithocr.R;

public class takePhoto extends Fragment {

    private int CAMERA_PERMISSION_CODE = 0x00AF;

    private ImageView imageView;
    private Button takePhotoBtn, extractStringBnt;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_take_photo, container, false);

        imageView = view.findViewById(R.id.picture);

        // 사진 찍기 버튼. 누르면 카메라 권한 확인및 요청 -> 카메라 호출
        takePhotoBtn = view.findViewById(R.id.takePhoto);
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultPicture.launch(intent);
            }
        });

        // 글자 읽기 버튼. 처음에는 비활성화 & 메인 화면에서 다음 탭으로 진행
        extractStringBnt = view.findViewById(R.id.extractString);
        extractStringBnt.setEnabled(false);
        extractStringBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).EnableTab(1);
            }
        });

        // 혹시 이미 사진을 찍었으면 그걸로 처리
        Bitmap picture = ((MainActivity)getActivity()).GetPicture();
        if (picture != null) {
            AfterTakePicture(picture);
        }

        return view;
    }

    // 카메라 권한 확인및 요청
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    // 카메라 호출 후 결과 값을 어떻게 처리할 지. 이미지 뷰에 출력및 버튼 속성 변경
    ActivityResultLauncher<Intent> activityResultPicture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap picture = (Bitmap) extras.get("data");
                        ((MainActivity)getActivity()).SetPicture(picture);

                        AfterTakePicture(picture);
                    }
                }
            });
    
    // 사진이 있을 때 후처리
    private void AfterTakePicture(Bitmap picture) {
        // 촬영 이미지
        imageView.setImageBitmap(picture);

        // 촬영 버튼
        takePhotoBtn.setText("다시 찍기");
        takePhotoBtn.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.yellow));

        // 다음 탭 진행 버튼
        extractStringBnt.setEnabled(true);
        extractStringBnt.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.green));
    }
}