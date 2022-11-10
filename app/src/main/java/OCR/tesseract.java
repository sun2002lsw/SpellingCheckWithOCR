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
    private String languageCode;
    private TessBaseAPI tessAPI;

    @Override
    public void SetLanguageCode(@NonNull Context ctx, String languageCode) {
        this.dataPath = ctx.getFilesDir().getAbsolutePath();
        this.languageCode = languageCode;
        
        // 데이터 가져오기
        String dataDirPath = ctx.getFilesDir() + "/tessdata/";
        String dataFilePath = dataDirPath + languageCode + ".traineddata";

        File tessDataDir = new File(dataDirPath);
        if(!tessDataDir.exists()) {
            if (!tessDataDir.mkdirs()) {
                Toast.makeText(ctx, "tesseract 폴더 생성을 실패했습니다", Toast.LENGTH_LONG).show();
            }
        }

        File dataFile = new File(dataFilePath);
        if(!dataFile.exists()) {
            String assetPath = "tessdata/" + languageCode + ".traineddata";
            util.CopyAsset(ctx, assetPath, dataFilePath);
        }
    }

    @Override
    public void SetProgressbar(ProgressBar progressBar) {
        if (dataPath.isEmpty() || languageCode.isEmpty()) {
            return;
        }

        TessBaseAPI.ProgressNotifier pn = progress -> progressBar.setProgress(progress.getPercent());

        tessAPI = new TessBaseAPI(pn);
        tessAPI.init(dataPath, languageCode);
    }

    @Override
    public String StartOCR(File picture) {
        if (tessAPI == null)
            if (dataPath.isEmpty() || languageCode.isEmpty()) {
                return "어떤 언어로 할지 선택 해주세요";
            } else {
                tessAPI = new TessBaseAPI();
                tessAPI.init(dataPath, languageCode);
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