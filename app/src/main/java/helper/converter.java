package helper;

import android.content.Context;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

public class converter {
    static public Uri FileToUri(Context ctx, File file) {
        return FileProvider.getUriForFile(ctx, "com.example.spellingcheckwithocr.fileprovider", file);
    }
}
