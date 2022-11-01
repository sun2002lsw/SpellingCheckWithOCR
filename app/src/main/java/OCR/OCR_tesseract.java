package OCR;

import android.content.Context;

import androidx.annotation.NonNull;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class OCR_tesseract extends OcrEngineBase {

    private Boolean initialized = false;
    private final TessBaseAPI tessAPI = new TessBaseAPI();

    @Override
    public void Init(@NonNull Context ctx, String languageCode) {
        PrintProgress(0, 20);
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
        PrintProgress(20, 50);
        tessAPI.setImage(picture);

        PrintProgress(50, 90);
        String text = tessAPI.getUTF8Text();

        PrintProgress(100, 100);
        return text;
    }
}