package checkSpelling;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public interface SpellingCheckEngine {
    @NonNull
    public ArrayList<WrongWordInfo> CheckSpelling(String sentence) throws Exception;
}