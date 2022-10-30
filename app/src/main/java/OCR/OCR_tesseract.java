package OCR;

import java.io.File;

public class OCR_tesseract extends OcrEngineBase {

    @Override
    void Init() throws InterruptedException {
        for (int i = 0; i <= 100; i++) {
            PrintProgress(i);
            Thread.sleep(50);
        }
    }

    @Override
    String OCR(File picture) throws InterruptedException {
        return "오, 뭔가 엄청나게 긴 문자열이 출력되었다능... 잠도 안 자고 미친거 아니냐능 시발 이건 아닌데 좆됐다;;;";
    }
}

/*
    private String dataPath = "" ; //언어데이터가 있는 경로
    private TessBaseAPI tessAPI;

    private void InitTess() {
        //언어파일 경로
        dataPath = getContext().getFilesDir() + "/tesseract/";

        //트레이닝데이터가 카피되어 있는지 체크
        CheckFile(new File(dataPath + "tessdata/"));

        //Tesseract API 세팅
        tessAPI = new TessBaseAPI();
        tessAPI.init(dataPath, "eng");
    }

    private void CheckFile(@NonNull File dir) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if(!dir.exists() && dir.mkdirs()) {
            CopyFiles();
        }

        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if(dir.exists()) {
            File datafile = new File(dataPath + "/tessdata/eng.traineddata");
            if(!datafile.exists()) {
                CopyFiles();
            }
        }
    }

    private void CopyFiles() {
        try{
            AssetManager assetManager = getContext().getAssets();
            InputStream istream = assetManager.open("eng.traineddata");

            String filepath = dataPath + "/tessdata/eng.traineddata";
            OutputStream ostream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = istream.read(buffer)) != -1) {
                ostream.write(buffer, 0, read);
            }

            ostream.flush();
            ostream.close();
            istream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ProcessOCR(@NonNull View view) {
        File picture = helper.util.MainActivity(this).GetPicture();

        tessAPI.setImage(picture);
        String extractedString = tessAPI.getUTF8Text();
        helper.util.MainActivity(this).SetExtractedString(extractedString);

        EditText editText = view.findViewById(R.id.extractedString);
        editText.setText(extractedString);
    }
 */