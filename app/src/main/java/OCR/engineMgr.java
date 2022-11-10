package OCR;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class engineMgr {

    String curEngine;
    HashMap<String, engine> engineByName;

    public void Init(@NonNull String engineName) {
        engineByName = new HashMap<>();
        curEngine = engineName;

        engineByName.put(tesseract.class.getName(), new tesseract());
        engineByName.put(clova.class.getName(), new clova());
    }

    public void SetEngine(@NonNull String engineName) { curEngine = engineName; }

    @NonNull
    public engine GetEngine() { return engineByName.get(curEngine); }
}