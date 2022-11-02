package OCR;

import android.content.Context;

import androidx.annotation.NonNull;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public interface OcrEngine {
    void Init(@NonNull Context ctx, String languageCode, TessBaseAPI.ProgressNotifier pn);
    String ProcessOCR(File picture);
}
