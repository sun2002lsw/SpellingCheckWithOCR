package OCR;

import android.content.Context;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import java.io.File;

public interface engine {
    void SetLanguage(@NonNull Context ctx, String language);
    void SetProgressbar(ProgressBar progressBar);

    String StartOCR(File picture);
    void StopOCR();
}