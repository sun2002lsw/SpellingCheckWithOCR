package checkSpelling;

import androidx.annotation.NonNull;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class Korean_saramin implements engine {

    @Override
    public ArrayList<WrongWordInfo> CheckSpelling(String sentence) throws Exception {
        Document doc;
        try {
            String url = "https://www.saramin.co.kr/zf_user/tools/spell-check";
            doc = Jsoup.connect(url).ignoreContentType(true).data("content", sentence).post();
        } catch (IOException e) {
            throw new Exception("사람인 홈페이지 연결에 실패했습니다. 인터넷 연결을 확인해주세요.");
        }

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(extractJsonStrFromDoc(doc));
        } catch (JSONException e) {
            throw new Exception("body -> Json 변환에 실패했습니다. 응답 양식이 변경되었습니다.");
        }

        JSONArray jsonArray;
        try {
            jsonArray = jsonObject.getJSONArray("word_list");
        } catch (JSONException e) {
            throw new Exception("word_list 추출에 실패했습니다. 응답 양식이 변경되었습니다.");
        }

        ArrayList<WrongWordInfo> wrongWordInfos = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject wrongWordJsonInfo = jsonArray.getJSONObject(i);
            WrongWordInfo wrongWordInfo = jsonObjectToWrongWordInfo(wrongWordJsonInfo);
            wrongWordInfos.add(wrongWordInfo);
        }

        return wrongWordInfos;
    }

    @NonNull
    private String extractJsonStrFromDoc(@NonNull Document doc) {
        Element body = doc.getElementsByTag("body").first();

        // 디코딩 하기
        String decodedStr = body.toString();
        decodedStr = StringEscapeUtils.unescapeJava(decodedStr);
        decodedStr = StringEscapeUtils.unescapeHtml4(decodedStr);

        // 쓸데 없는 문자들 지우기
        String jsonStr = decodedStr.replaceAll("<.*?>", "");
        jsonStr = jsonStr.replaceAll("\\n ", "");
        jsonStr = jsonStr.replaceAll("\\r\\n", "");

        int cutIdx = jsonStr.indexOf("speller_list") - 2;
        return jsonStr.substring(0, cutIdx) + "}";
    }

    @NonNull
    private WrongWordInfo jsonObjectToWrongWordInfo(@NonNull JSONObject wrongWordJsonInfo) throws Exception {
        String wrongWord;
        try {
            wrongWord = wrongWordJsonInfo.getString("errorWord");
        } catch (JSONException e) {
            throw new Exception("errorWord 추출에 실패했습니다. 응답 양식이 변경되었습니다.");
        }

        String reason;
        try {
            reason = wrongWordJsonInfo.getString("helpMessage");
        } catch (JSONException e) {
            throw new Exception("helpMessage 추출에 실패했습니다. 응답 양식이 변경되었습니다.");
        }

        String correctWords;
        try {
            correctWords = wrongWordJsonInfo.getString("candWordList");
        } catch (JSONException e) {
            throw new Exception("candWordList 추출에 실패했습니다. 응답 양식이 변경되었습니다.");
        }

        return new WrongWordInfo(wrongWord, reason, correctWords.split(","));
    }
}