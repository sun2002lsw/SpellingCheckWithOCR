package checkSpelling;

import java.util.ArrayList;

public interface engine {
    ArrayList<WrongWordInfo> CheckSpelling(String sentence) throws Exception;
}