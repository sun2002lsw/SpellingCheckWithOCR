package helper;

import android.content.Context;
import android.net.Uri;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.spellingcheckwithocr.MainActivity;

import java.io.File;

public class util {
    static public Uri FileToUri(Context ctx, File file) {
        return FileProvider.getUriForFile(ctx, "com.example.spellingcheckwithocr.fileprovider", file);
    }
    static public MainActivity MainActivity(Fragment fragment) {
        return (MainActivity)fragment.getActivity();
    }
}