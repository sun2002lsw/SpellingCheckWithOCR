package OCR;

import android.content.Context;

import androidx.annotation.NonNull;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class OCR_tesseract implements OcrEngine {

    private TessBaseAPI tessAPI;

    @Override
    public void Init(@NonNull Context ctx, String languageCode, TessBaseAPI.ProgressNotifier pn) {
        // 데이터 가져오기
        String dataDirPath = ctx.getFilesDir() + "/tessdata/";
        String dataFilePath = dataDirPath + languageCode + ".traineddata";

        File tessDataDir = new File(dataDirPath);
        if(!tessDataDir.exists()) {
            tessDataDir.mkdirs();
        }

        File dataFile = new File(dataFilePath);
        if(!dataFile.exists()) {
            String assetPath = "tessdata/" + languageCode + ".traineddata";
            helper.util.CopyAsset(ctx, assetPath, dataFilePath);
        }

        //Tesseract API 세팅
        tessAPI = new TessBaseAPI(pn);
        tessAPI.init(ctx.getFilesDir().getAbsolutePath(), languageCode);
    }

    @Override
    public String ProcessOCR(File picture) {
        tessAPI.setImage(picture);
        return tessAPI.getHOCRText(0);
    }
}