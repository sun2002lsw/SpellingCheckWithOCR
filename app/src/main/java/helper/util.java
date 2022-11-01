package helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.spellingcheckwithocr.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class util {
    static public Uri FileToUri(Context ctx, File file) {
        return FileProvider.getUriForFile(ctx, "com.example.spellingcheckwithocr.fileprovider", file);
    }

    static public MainActivity MainActivity(Fragment fragment) {
        return (MainActivity)fragment.getActivity();
    }

    static public void CopyAsset(@NonNull Context ctx, String assetPath, String copyPath) {
        try{
            // 원본 asset 위치
            AssetManager assetManager = ctx.getAssets();
            InputStream istream = assetManager.open(assetPath);

            // 복사할 파일 위치
            OutputStream ostream = new FileOutputStream(copyPath);

            // 복사
            byte[] buffer = new byte[1024];
            int read;
            while ((read = istream.read(buffer)) != -1) {
                ostream.write(buffer, 0, read);
            }

            // 뒷정리
            ostream.flush();
            ostream.close();
            istream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}