package checkSpelling;

import java.util.HashMap;

public class engineMgr {

    String curLanguage;
    HashMap<String, engine> engineByLanguage;

    public void Init(String language) {
        engineByLanguage = new HashMap<>();
        curLanguage = language;

        engineByLanguage.put("kor", new Korean_saramin());
        engineByLanguage.put("eng", new English_quillbot());
    }

    public void SetLanguage(String language) { curLanguage = language; }

    public engine GetEngine() { return engineByLanguage.get(curLanguage); }
}