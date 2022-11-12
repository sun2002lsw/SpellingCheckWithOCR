package OCR;

import android.content.Context;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.concurrent.atomic.AtomicBoolean;

import helper.util;

public class clova implements engine {

    final private String invokeURL = "https://qnob8o6ets.apigw.ntruss.com/custom/v1/19142/4d7927deced46e4f867246e5881c3d08694f9735df2d955cea18b5e47d5acdbe/general";
    private String secretKey = "";

    private ProgressBar progressBar;
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
    public void SetProgressbar(@NonNull ProgressBar pb) { progressBar = pb; }

    @Override
    public String StartOCR(@NonNull File picture) throws Exception {
        progressBar.setProgress(10);
        isStopped.set(false);

        String result = processOCR(picture);
        if (isStopped.get()) {
            return "";
        }

        return result;
    }

    @NonNull
    private String processOCR(@NonNull File picture) throws Exception {
        // API 호출하기
        progressBar.setProgress(10);
        Document doc;
        try {
            doc = requestAPI(picture.getName(), util.GetBase64EncodingFromJPG(picture));
        } catch (HttpStatusException e) {
            if (e.getStatusCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                throw new Exception("유효하지 않은 API 비밀키입니다\n비밀키를 다시 설정해주세요");
            } else {
                throw e;
            }
        }

        // 응답값 파싱하기
        progressBar.setProgress(95);
        return extractResultFromDoc(doc);
    }

    private Document requestAPI(@NonNull String fileName, @NonNull String base64EncodingImageBytes) throws Exception {
        progressBar.setProgress(30);

        // 가장 중요한 사진 파일 정보
        JSONObject image = new JSONObject();
        image.put("format", "png");
        image.put("name", fileName);
        image.put("data", base64EncodingImageBytes);

        // 사진 파일을 묶어서 보낼 수 있지만, 1장만 보낸다
        JSONArray images = new JSONArray();
        images.put(image);
        
        // 사진 정보를 비롯한 기타 정보로 body 구성
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("version", "V2");
        bodyJson.put("requestId", clova.class.getName());
        bodyJson.put("timestamp", System.currentTimeMillis() / 1000);
        bodyJson.put("images", images);

        progressBar.setProgress(50);

        // request 전송
        Connection conn = Jsoup.connect(invokeURL);
        conn = conn.header("X-OCR-SECRET", secretKey);
        conn = conn.header("Content-Type", "application/json");
        conn = conn.requestBody(bodyJson.toString());

        progressBar.setProgress(80);
        return conn.ignoreContentType(true).post();
    }

    @NonNull
    private String extractResultFromDoc(@NonNull Document doc) throws JSONException {
        Element body = doc.getElementsByTag("body").first();
        String jsonStr = body.toString().replaceAll("<.*?>", "");

        JSONObject fullJson = new JSONObject(jsonStr);
        JSONArray images = fullJson.getJSONArray("images");

        // 항상 사진을 1장만 요청함
        JSONObject pictureJson = images.getJSONObject(0);
        JSONArray fields = pictureJson.getJSONArray("fields");

        // 해당 사진의 모든 출력값 추출
        StringBuilder allText = new StringBuilder();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);

            String text = field.getString("inferText");
            text += field.getBoolean("lineBreak") ? "\n" : " ";

            if (field.getDouble("inferConfidence") < 0.7) {
                text = "۞ ";
            }

            allText.append(text);
        }

        return allText.toString();
    }

    @Override
    public void StopOCR() {
        isStopped.set(true);
    }
}