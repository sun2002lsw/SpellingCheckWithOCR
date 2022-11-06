package checkSpelling;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class Korean_saramin {

    private String url = "https://www.saramin.co.kr/zf_user/tools/character-counter";
    private String textAreaId = "character_counter_content";

    public String CheckSpelling(String sentence) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element elem = doc.getElementById(textAreaId);
        String str = elem.toString();

        Log.d("debug", str);
        return str;
    }
}
