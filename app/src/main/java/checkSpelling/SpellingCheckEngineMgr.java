package checkSpelling;

import java.util.HashMap;

public class SpellingCheckEngineMgr {

    String curLanguageCode;
    HashMap<String, SpellingCheckEngine> engineByLanguage;

    public void Init() {
        engineByLanguage = new HashMap<>();
        curLanguageCode = "kor";

        engineByLanguage.put("kor", new Korean_saramin());
        engineByLanguage.put("eng", new English_test());
    }

    public void SetLanguageCode(String languageCode) { curLanguageCode = languageCode; }

    public SpellingCheckEngine GetEngine() { return engineByLanguage.get(curLanguageCode); }
}