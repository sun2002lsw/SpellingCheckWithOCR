package checkSpelling;

import java.util.ArrayList;

public interface SpellingCheckEngine {
    public ArrayList<WrongWordInfo> CheckSpelling(String sentence) throws Exception;
}