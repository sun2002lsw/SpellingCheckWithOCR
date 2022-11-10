package OCR;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

import helper.util;

public class tesseract implements engine {

    private String dataPath;
    private String language;
    private TessBaseAPI tessAPI;

    @Override
    public void SetLanguage(@NonNull Context ctx, String language) {
        this.dataPath = ctx.getFilesDir().getAbsolutePath();
        this.language = language;
        
        // 데이터 가져오기
        String dataDirPath = ctx.getFilesDir() + "/tessdata/";
        String dataFilePath = dataDirPath + language + ".traineddata";

        File tessDataDir = new File(dataDirPath);
        if(!tessDataDir.exists()) {
            if (!tessDataDir.mkdirs()) {
                Toast.makeText(ctx, "tesseract 폴더 생성을 실패했습니다", Toast.LENGTH_LONG).show();
            }
        }

        File dataFile = new File(dataFilePath);
        if(!dataFile.exists()) {
            String assetPath = "tessdata/" + language + ".traineddata";
            util.CopyAsset(ctx, assetPath, dataFilePath);
        }
    }

    @Override
    public void SetProgressbar(ProgressBar progressBar) {
        if (dataPath.isEmpty() || language.isEmpty()) {
            return;
        }

        TessBaseAPI.ProgressNotifier pn = progress -> progressBar.setProgress(progress.getPercent());

        tessAPI = new TessBaseAPI(pn);
        tessAPI.init(dataPath, language);
    }

    @Override
    public String StartOCR(File picture) {
        if (tessAPI == null)
            if (dataPath.isEmpty() || language.isEmpty()) {
                return "어떤 언어로 할지 선택 해주세요";
            } else {
                tessAPI = new TessBaseAPI();
                tessAPI.init(dataPath, language);
            }

        tessAPI.setImage(picture);

        String hocrText = tessAPI.getHOCRText(0);

        return util.ConvertHocrToText(hocrText);
    }

    @Override
    public void StopOCR() {
        if (tessAPI != null) {
            tessAPI.stop();
        }
    }
}