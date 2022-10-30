package OCR;

import java.io.File;

public class OCR_tesseract extends OcrEngineBase {

    @Override
    void Init() throws InterruptedException {
        PrintProgress(10);
        Thread.sleep(1000);
        PrintProgress(20);
        Thread.sleep(1000);
        PrintProgress(30);
        Thread.sleep(1000);
        PrintProgress(40);
        Thread.sleep(1000);
        PrintProgress(50);
        Thread.sleep(1000);
        PrintProgress(60);
        Thread.sleep(1000);
        PrintProgress(70);
        Thread.sleep(1000);
    }

    @Override
    String OCR(File picture) throws InterruptedException {
        PrintProgress(10);
        Thread.sleep(1000);
        PrintProgress(20);
        Thread.sleep(1000);
        PrintProgress(30);
        Thread.sleep(1000);
        PrintProgress(40);
        Thread.sleep(1000);
        PrintProgress(50);
        Thread.sleep(1000);
        PrintProgress(60);
        Thread.sleep(1000);
        PrintProgress(70);
        Thread.sleep(1000);

        return null;
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