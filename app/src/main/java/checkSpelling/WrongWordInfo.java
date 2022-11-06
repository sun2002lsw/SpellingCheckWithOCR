package checkSpelling;

public class WrongWordInfo {

    public final String wrongWord, reason;
    public final String[] correctWords;

    public WrongWordInfo(String wrongWord, String reason, String[] correctWords) {
        this.wrongWord = wrongWord;
        this.reason = reason;
        this.correctWords = correctWords;
    }
}