package checkSpelling;

import java.util.HashMap;

public class engineMgr {

    String curLanguageCode;
    HashMap<String, engine> engineByLanguage;

    public void Init(String languageCode) {
        engineByLanguage = new HashMap<>();
        curLanguageCode = languageCode;

        engineByLanguage.put("kor", new Korean_saramin());
        engineByLanguage.put("eng", new English_quillbot());
    }

    public void SetLanguageCode(String languageCode) { curLanguageCode = languageCode; }

    public engine GetEngine() { return engineByLanguage.get(curLanguageCode); }
}