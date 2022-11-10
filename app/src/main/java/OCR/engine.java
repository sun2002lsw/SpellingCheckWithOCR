package OCR;

import android.content.Context;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import java.io.File;

public interface engine {
    boolean NeedInvokeURL();
    void SetInvokeURL(@NonNull String url);
    boolean IsValidInvokeURL(@NonNull String url);

    void SetLanguage(@NonNull Context ctx, @NonNull String language);
    void SetProgressbar(@NonNull ProgressBar progressBar);

    @NonNull
    String StartOCR(@NonNull File picture);
    void StopOCR();
}