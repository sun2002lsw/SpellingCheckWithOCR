package OCR;

import android.content.Context;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import java.io.File;

public interface engine {
    void SetLanguageCode(@NonNull Context ctx, String languageCode);
    void SetProgressbar(ProgressBar progressBar);

    String StartOCR(File picture);
    void StopOCR();
}