package OCR;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class OCR_tesseract extends OcrEngineBase {

    private Boolean initialized = false;
    private TessBaseAPI tessAPI = new TessBaseAPI();

    @Override
    public void Init(@NonNull Context ctx, String languageCode) {
        PrintProgress(0, 50);
        if (initialized) {
            return;
        }

        // 데이터 경로
        String dataDirPath = ctx.getFilesDir() + "/tessdata/";
        String dataFilePath = dataDirPath + languageCode + ".traineddata";

        File tessDataDir = new File(dataDirPath);
        if(!tessDataDir.exists()) {
            tessDataDir.mkdirs();
        }

        File dataFile = new File(dataFilePath);
        if(!dataFile.exists()) {
            CopyData(ctx, languageCode, dataFilePath);
        }

        //Tesseract API 세팅
        tessAPI.init(ctx.getFilesDir().getAbsolutePath(), languageCode);
        initialized = true;
    }

    private void CopyData(@NonNull Context ctx, String languageCode, String dataFilePath) {
        String assetPath = "tessdata/" + languageCode + ".traineddata";

        helper.util.CopyAsset(ctx, assetPath, dataFilePath);
    }

    @Override
    public String ProcessOCR(File picture) {
        PrintProgress(50, 100);

        tessAPI.setImage(picture);
        return tessAPI.getUTF8Text();
    }

    @Override
    public String ProcessOCR(Bitmap picture) {
        PrintProgress(50, 100);

        tessAPI.setImage(picture);
        return tessAPI.getUTF8Text();
    }
}