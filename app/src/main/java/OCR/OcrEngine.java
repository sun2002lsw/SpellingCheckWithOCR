package OCR;

import java.io.File;

public interface OcrEngine {
    void SetProgressListener(OcrProgressListener l);
    String ProcessOCR(File picture) throws InterruptedException;
}

abstract class OcrEngineBase implements OcrEngine {

    private OcrProgressListener progressListener;

    @Override
    public final void SetProgressListener(OcrProgressListener l) {
        progressListener = l;
    }

    protected final void PrintProgress(int progress) {
        if (progressListener != null) {
            progressListener.onProgress(progress);
        }
    }

    @Override
    public final String ProcessOCR(File picture) throws InterruptedException {
        Init();

        return OCR(picture);
    }

    abstract void Init() throws InterruptedException;
    abstract String OCR(File picture) throws InterruptedException;
}