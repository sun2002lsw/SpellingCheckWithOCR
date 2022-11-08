package checkSpelling;

import java.util.ArrayList;

public interface SpellingCheckEngine {
    ArrayList<WrongWordInfo> CheckSpelling(String sentence) throws Exception;
}