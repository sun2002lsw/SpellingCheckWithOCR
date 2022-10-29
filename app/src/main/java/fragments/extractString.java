package fragments;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spellingcheckwithocr.MainActivity;
import com.example.spellingcheckwithocr.R;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class extractString extends Fragment {

    private String dataPath = "" ; //언어데이터가 있는 경로
    private TessBaseAPI tessAPI;
    private Bitmap picture;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_extract_string, container, false);

        // 찍은 사진 가져오기. 사진이 없으면 이전 탭으로 복귀
        picture = ((MainActivity)getActivity()).GetPicture();
        if (picture == null) {
            ((MainActivity)getActivity()).EnableTab(0);
            return view;
        }

        // 찍어둔 사진 출력
        ImageView imageView = view.findViewById(R.id.picture2);
        imageView.setImageBitmap(picture);

        InitTess();
        ProcessImage(view);

        return view;
    }

    private void InitTess() {
        //언어파일 경로
        dataPath = this.getContext().getFilesDir() + "/tesseract/";

        //트레이닝데이터가 카피되어 있는지 체크
        CheckFile(new File(dataPath + "tessdata/"));

        //Tesseract API 세팅
        tessAPI = new TessBaseAPI();
        tessAPI.init(dataPath, "eng");
    }

    private void CheckFile(File dir) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if(!dir.exists() && dir.mkdirs()) {
            CopyFiles();
        }

        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if(dir.exists()) {
            File datafile = new File(dataPath + "/tessdata/eng.traineddata");
            if(!datafile.exists()) {
                CopyFiles();
            }
        }
    }

    private void CopyFiles() {
        try{
            AssetManager assetManager = this.getContext().getAssets();
            InputStream istream = assetManager.open("eng.traineddata");

            String filepath = dataPath + "/tessdata/eng.traineddata";
            OutputStream ostream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = istream.read(buffer)) != -1) {
                ostream.write(buffer, 0, read);
            }

            ostream.flush();
            ostream.close();
            istream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ProcessImage(View view) {
        tessAPI.setImage(picture);
        String extractedString = tessAPI.getUTF8Text();

        EditText editText = view.findViewById(R.id.extractedString);
        editText.setText(extractedString);
    }
}