package OCR;

import android.content.Context;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import java.io.File;

public interface engine {
    boolean NeedSecretKey();
    void SetSecretKey(@NonNull String key);

    void SetLanguage(@NonNull Context ctx, @NonNull String language);
    void SetProgressbar(@NonNull ProgressBar progressBar);

    @NonNull
    String StartOCR(@NonNull File picture);
    void StopOCR();
}