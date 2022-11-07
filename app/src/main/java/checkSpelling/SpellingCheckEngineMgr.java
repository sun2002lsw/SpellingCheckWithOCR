package checkSpelling;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class SpellingCheckEngineMgr {

    HashMap<String, SpellingCheckEngine> engineByLanguage;
    String curLanguageCode;

    public void SetLanguageCode(String languageCode) {
        curLanguageCode = languageCode;
    }

    public SpellingCheckEngine GetEngine(@NonNull String language) {
        return engineByLanguage.get(language);
    }

    public void Init() {
        engineByLanguage = new HashMap<String, SpellingCheckEngine>();
        curLanguageCode = "kor";
        SetKoreanCheckEngine("기본값");
    }

    public void SetKoreanCheckEngine(@NonNull String engineName) {
        switch (engineName) {
            case "사람인":
                engineByLanguage.put("kor", new Korean_saramin());
                break;
            default:
                engineByLanguage.put("kor", new Korean_saramin());
        }
    }

    public void SetEnglishCheckEngine(@NonNull String engineName) {
        switch (engineName) {
            case "테스트":
                engineByLanguage.put("eng", new English_test());
                break;
            default:
                engineByLanguage.put("eng", new English_test());
        }
    }
}