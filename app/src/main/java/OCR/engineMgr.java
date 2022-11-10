package OCR;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class engineMgr {

    String curEngine;
    HashMap<String, engine> engineByName;

    public void Init(@NonNull String engineName) {
        curEngine = engineName;

        engineByName = new HashMap<>();
        engineByName.put(tesseract.class.getSimpleName(), new tesseract());
        engineByName.put(clova.class.getSimpleName(), new clova());
    }

    public void SetEngine(@NonNull String engineName) { curEngine = engineName; }

    @NonNull
    public engine GetEngine() { return engineByName.get(curEngine); }
}