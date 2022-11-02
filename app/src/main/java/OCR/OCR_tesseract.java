package OCR;

import android.content.Context;

import androidx.annotation.NonNull;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

import helper.util;

public class OCR_tesseract {

    private String dataPath;
    private String languageCode;
    private TessBaseAPI tessAPI;

    public void SetLanguageCode(@NonNull Context ctx, String languageCode) {
        this.dataPath = ctx.getFilesDir().getAbsolutePath();
        this.languageCode = languageCode;
        
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
            util.CopyAsset(ctx, assetPath, dataFilePath);
        }
    }
    
    public void SetProgressNotifier(TessBaseAPI.ProgressNotifier pn) {
        if (dataPath.isEmpty() || languageCode.isEmpty()) {
            return;
        }

        tessAPI = new TessBaseAPI(pn);
        tessAPI.init(dataPath, languageCode);
    }

    public String ProcessOCR(File picture) {
        if (tessAPI == null)
            if (dataPath.isEmpty() || languageCode.isEmpty()) {
                return "어떤 언어로 할지 선택을 먼저 해주세요";
            } else {
                tessAPI = new TessBaseAPI();
                tessAPI.init(dataPath, languageCode);
            }
        
        tessAPI.setImage(picture);
        String hocrText = tessAPI.getHOCRText(0);

        return util.ConvertHocrToText(hocrText);
    }
}