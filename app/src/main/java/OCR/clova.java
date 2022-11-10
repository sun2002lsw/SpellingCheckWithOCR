package OCR;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class clova implements engine {

    final private AtomicBoolean isStopped = new AtomicBoolean();
    private String invokeURL;

    @Override
    public boolean NeedInvokeURL() { return invokeURL.isEmpty(); }

    @Override
    public void SetInvokeURL(@NonNull String url) {
        invokeURL = url;
    }

    @Override
    public boolean IsValidInvokeURL(@NonNull String url) {
        return url.startsWith("https://") && url.endsWith("/general");
    }

    @Override
    public void SetLanguage(@NonNull Context ctx, @NonNull String language) {
        // do nothing
        // 애초에 영어는 기본 지원이고, 도메인 설정에서 한글 지원을 설정함
    }

    @Override
    public void SetProgressbar(@NonNull ProgressBar progressBar) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 90);
        animation.setDuration(5000); // 5 second
        animation.setInterpolator(new LinearInterpolator());
        animation.start();
    }

    @Override
    @NonNull
    public String StartOCR(@NonNull File picture) {
        isStopped.set(false);

        // process ocr

        if (isStopped.get()) {
            return "";
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void StopOCR() {
        isStopped.set(true);
    }
}