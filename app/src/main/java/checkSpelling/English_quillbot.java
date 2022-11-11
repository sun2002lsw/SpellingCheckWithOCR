package checkSpelling;

import android.util.Log;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class English_quillbot implements engine {

    @Override
    public ArrayList<WrongWordInfo> CheckSpelling(String sentence) throws Exception {
        Connection conn = Jsoup.connect("https://quillbot.com/api/utils/grammar-check/");

        conn = conn.header("accept", "*/*");
        conn = conn.header("accept-encoding", "gzip, deflate, br");
        conn = conn.header("accept-language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        conn = conn.header("content-type", "application/json");
        conn = conn.header("cookie", "abID=97; anonID=dc2d241db895c9ab; _gid=GA1.2.1516477099.1667922661; authenticated=false; premium=false; acceptedPremiumModesTnc=false; connect.sid=s:dG1lAAVqkHUPPd9_7MbyjyIvPk-l8m4I.YFh/KwVXJsfNErP/oJP3p2uESaYskQoPaMPMiT8iVWI; _gcl_au=1.1.1087573025.1667922661; user_status=not registered; FPID=FPID2.2.7AsA78fqNqIkSeRh2zMY3OtqQo/lDmmFFdxI1kkMfZ4=.1667922661; _fbp=fb.1.1667922662337.1652251853; g_state={\"i_p\":1668027213446,\"i_l\":2}; qdid=5785366433900570005; _clck=1t20tfb|1|f6f|0; __cf_bm=XijSYDBrOwe4EIBvZTQnfxDZG3MhjR7s5vYO3Ij83bM-1668003558-0-AWugL/naHSnPF/BASDLAd2941ejsx0TbnWs0K1mYRpnD4hLinq1mvLtnC1D1Qk+Y4xTFrjY9WH5etyPWOvpOoXs=; OptanonConsent=isGpcEnabled=0&datestamp=Wed+Nov+09+2022+23:19:19+GMT+0900+(한국+표준시)&version=6.38.0&isIABGlobal=false&hosts=&landingPath=https://quillbot.com/grammar-check&groups=C0001:1,C0002:1,C0003:1,C0004:1; _gat=1; __kla_id=eyIkcmVmZXJyZXIiOnsidHMiOjE2Njc5MjI2NjEsInZhbHVlIjoiaHR0cHM6Ly93d3cuZ29vZ2xlLmNvbS8iLCJmaXJzdF9wYWdlIjoiaHR0cHM6Ly9xdWlsbGJvdC5jb20vZ3JhbW1hci1jaGVjayJ9LCIkbGFzdF9yZWZlcnJlciI6eyJ0cyI6MTY2ODAwMzU2MCwidmFsdWUiOiIiLCJmaXJzdF9wYWdlIjoiaHR0cHM6Ly9xdWlsbGJvdC5jb20vZ3JhbW1hci1jaGVjayJ9fQ==; amp_6e403e=sP-5XiGi7c8gz-1wOw3Kit...1ghec4una.1ghec4vis.1c.9.1l; _ga_D39F2PYGLM=GS1.1.1668003561.4.0.1668003561.60.0.0; _ga=GA1.1.1097124299.1667922661; FPLC=nWyajRpKPOLsuIUd/yQzxtx/I7dYUKiOsV0OtsGs3TsMrkz7SBzNlM7ESsFspxFvYCp+fLTR/lLO58MRaZNPBztgjQUBi8kQvMOSnKtnnninlkS2RzCQnJuI18Kj+A==; _clsk=eigi81|1668003562977|1|0|b.clarity.ms/collect; __insp_wid=379258038; __insp_slim=1668003563028; __insp_nv=true; __insp_targlpu=aHR0cHM6Ly9xdWlsbGJvdC5jb20vZ3JhbW1hci1jaGVjaw==; __insp_targlpt=RnJlZSBHcmFtbWFyIENoZWNrZXIgfCBRdWlsbEJvdCBBSQ==; __insp_norec_sess=true; _uetsid=251095505f7d11ed8cd6a1c3a50052c2; _uetvid=2510ba605f7d11ed9a9025d6a750e781; _dd_s=rum=0&expire=1668004461643");

        conn = conn.cookie("abID", "97");
        conn = conn.cookie("anonID", "dc2d241db895c9ab");
        conn = conn.cookie("_gid", "GA1.2.1516477099.1667922661");
        conn = conn.cookie("authenticated", "false");
        conn = conn.cookie("premium", "false");
        conn = conn.cookie("acceptedPremiumModesTnc", "false");
        conn = conn.cookie("connect.sid", "s:dG1lAAVqkHUPPd9_7MbyjyIvPk-l8m4I.YFh/KwVXJsfNErP/oJP3p2uESaYskQoPaMPMiT8iVWI");
        conn = conn.cookie("_gcl_au", "1.1.1087573025.1667922661");
        conn = conn.cookie("user_status", "not registered");
        conn = conn.cookie("FPID", "FPID2.2.7AsA78fqNqIkSeRh2zMY3OtqQo/lDmmFFdxI1kkMfZ4=.1667922661");
        conn = conn.cookie("_fbp", "fb.1.1667922662337.1652251853");
        conn = conn.cookie("g_state", "{\"i_p\":1668027213446,\"i_l\":2}");
        conn = conn.cookie("qdid", "5785366433900570005");
        conn = conn.cookie("_clck", "1t20tfb|1|f6f|0");
        conn = conn.cookie("__cf_bm", "XijSYDBrOwe4EIBvZTQnfxDZG3MhjR7s5vYO3Ij83bM-1668003558-0-AWugL/naHSnPF/BASDLAd2941ejsx0TbnWs0K1mYRpnD4hLinq1mvLtnC1D1Qk+Y4xTFrjY9WH5etyPWOvpOoXs=");
        conn = conn.cookie("OptanonConsent", "isGpcEnabled=0&datestamp=Wed+Nov+09+2022+23:19:19+GMT+0900+(한국+표준시)&version=6.38.0&isIABGlobal=false&hosts=&landingPath=https://quillbot.com/grammar-check&groups=C0001:1,C0002:1,C0003:1,C0004:1");
        conn = conn.cookie("_gat", "1");
        conn = conn.cookie("__kla_id", "eyIkcmVmZXJyZXIiOnsidHMiOjE2Njc5MjI2NjEsInZhbHVlIjoiaHR0cHM6Ly93d3cuZ29vZ2xlLmNvbS8iLCJmaXJzdF9wYWdlIjoiaHR0cHM6Ly9xdWlsbGJvdC5jb20vZ3JhbW1hci1jaGVjayJ9LCIkbGFzdF9yZWZlcnJlciI6eyJ0cyI6MTY2ODAwMzU2MCwidmFsdWUiOiIiLCJmaXJzdF9wYWdlIjoiaHR0cHM6Ly9xdWlsbGJvdC5jb20vZ3JhbW1hci1jaGVjayJ9fQ==");
        conn = conn.cookie("amp_6e403e", "sP-5XiGi7c8gz-1wOw3Kit...1ghec4una.1ghec4vis.1c.9.1l");
        conn = conn.cookie("_ga_D39F2PYGLM", "GS1.1.1668003561.4.0.1668003561.60.0.0");
        conn = conn.cookie("_ga", "GA1.1.1097124299.1667922661");
        conn = conn.cookie("FPLC", "nWyajRpKPOLsuIUd/yQzxtx/I7dYUKiOsV0OtsGs3TsMrkz7SBzNlM7ESsFspxFvYCp+fLTR/lLO58MRaZNPBztgjQUBi8kQvMOSnKtnnninlkS2RzCQnJuI18Kj+A==");
        conn = conn.cookie("_clsk", "eigi81|1668003562977|1|0|b.clarity.ms/collect");
        conn = conn.cookie("__insp_wid", "379258038");
        conn = conn.cookie("__insp_slim", "1668003563028");
        conn = conn.cookie("__insp_nv", "true");
        conn = conn.cookie("__insp_targlpu", "aHR0cHM6Ly9xdWlsbGJvdC5jb20vZ3JhbW1hci1jaGVjaw==");
        conn = conn.cookie("__insp_targlpt", "RnJlZSBHcmFtbWFyIENoZWNrZXIgfCBRdWlsbEJvdCBBSQ==");
        conn = conn.cookie("__insp_norec_sess", "true");
        conn = conn.cookie("_uetsid", "251095505f7d11ed8cd6a1c3a50052c2");
        conn = conn.cookie("_uetvid", "2510ba605f7d11ed9a9025d6a750e781");
        conn = conn.cookie("_dd_s", "rum=0&expire=1668004461643");

        conn = conn.referrer("https://quillbot.com/grammar-check");
        conn = conn.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("text", sentence);
        bodyJson.put("isFluency", true);
        bodyJson.put("checkForComplexity", true);
        bodyJson.put("checkForFormality", false);
        bodyJson.put("explainSuggestions", true);

        Document doc;
        try {
            doc = conn.requestBody(bodyJson.toString()).ignoreContentType(true).post();
        } catch (Exception e) {
            throw new Exception("quillbot 홈페이지 연결에 실패했습니다. 인터넷 연결을 확인해주세요.");
        }

        Log.d("debug", doc.text());

        return null;
    }
}