package OCR;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.File;

public interface OcrEngine {
    void SetProgressListener(OcrProgressListener l);
    void Init(@NonNull Context ctx, String languageCode);
    String ProcessOCR(File picture);
}

abstract class OcrEngineBase implements OcrEngine {

    private OcrProgressListener progressListener;

    @Override
    public final void SetProgressListener(OcrProgressListener l) {
        progressListener = l;
    }

    protected final void PrintProgress(int from, int to) {
        if (progressListener != null) {
            progressListener.onProgress(from, to);
        }
    }
}