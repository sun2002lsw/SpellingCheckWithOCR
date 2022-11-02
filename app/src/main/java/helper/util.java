package helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.spellingcheckwithocr.MainActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

    static public String ConvertHocrToText(String Hocr) {
        Document xml = null;
        try {
            xml = loadXMLFromString(Hocr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (xml == null) {
            return "";
        }

        Element root = xml.getDocumentElement();
        return extractStringFromNodeList(root.getChildNodes());
    }

    private static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    private static String extractStringFromNodeList(@NonNull NodeList childList) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < childList.getLength(); i++) {
            Node item = childList.item(i);
            if(item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            NodeList grandChildList = item.getChildNodes();
            if (grandChildList.getLength() == 1) {
                String oneText = item.getTextContent() + " ";
                sb.append(oneText);
            } else {
                String manyText = extractStringFromNodeList(grandChildList);
                sb.append(manyText);
            }
        }

        return sb.toString().trim();
    }
}