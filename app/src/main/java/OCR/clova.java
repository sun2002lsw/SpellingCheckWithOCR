package OCR;

import android.content.Context;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import helper.util;

public class clova implements engine {

    final private String invokeURL = "https://qnob8o6ets.apigw.ntruss.com/custom/v1/19142/4d7927deced46e4f867246e5881c3d08694f9735df2d955cea18b5e47d5acdbe/general";
    private String secretKey = "";
    final private AtomicBoolean isStopped = new AtomicBoolean();

    @Override
    public boolean NeedSecretKey() { return true; }

    @Override
    public void SetSecretKey(@NonNull String key) { secretKey = key; }

    @Override
    public void SetLanguage(@NonNull Context ctx, @NonNull String language) {
        // do nothing
        // 애초에 영어는 기본 지원이고, 도메인 설정에서 한글 지원을 설정함
    }

    @Override
    public void SetProgressbar(@NonNull ProgressBar progressBar) {
        new Thread(() -> {
            for (int fakeProgress = 0; fakeProgress < 90; fakeProgress++) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                progressBar.setProgress(fakeProgress);
            }
        }).start();
    }

    @Override
    @NonNull
    public String StartOCR(@NonNull File picture) {
        isStopped.set(false);

        String result = processOCR(picture);
        if (isStopped.get()) {
            return "";
        }

        return result;
    }

    @NonNull
    private String processOCR(@NonNull File picture) {
        Document doc;
        try {
            doc = requestAPI(picture.getName(), util.GetBase64EncodingFromJPG(picture));
        } catch (Exception e) {
            return e.toString();
        }

        return doc.toString();
    }

    private Document requestAPI(@NonNull String fileName, @NonNull String base64EncodingImageBytes) throws Exception {
        // 가장 중요한 사진 파일 정보
        JSONObject image = new JSONObject();
        image.put("format", "jpg");
        image.put("name", fileName);
        image.put("data", base64EncodingImageBytes);

        // 사진 파일을 묶어서 보낼 수 있지만, 1장만 보낸다
        JSONArray images = new JSONArray();
        images.put(image);
        
        // 사진 정보를 비롯한 기타 정보로 body 구성
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("version", "V2");
        bodyJson.put("requestId", clova.class.getName());
        bodyJson.put("timestamp", util.GetTimeStamp());
        bodyJson.put("images", images);
        bodyJson.put("enableTableDetection", true);

        // request 전송
        Connection conn = Jsoup.connect(invokeURL);

        conn = conn.header("X-OCR-SECRET", secretKey);
        conn = conn.header("Content-Type", "application/json");
        conn = conn.requestBody(bodyJson.toString());

        return conn.post();
    }

    @Override
    public void StopOCR() {
        isStopped.set(true);
    }
}