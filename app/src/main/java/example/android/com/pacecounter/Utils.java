package example.android.com.pacecounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.provider.DocumentsContract;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

public class Utils {

    private Context mContext;

    public Utils() {
        // TODO Auto-generated constructor stub
        //mContext = applicationContext;
    }



    public static String transToKM(float distance) {
        String result = null;
        int temp_distance = (int)distance;

        if (temp_distance >= 1000) {
            temp_distance = temp_distance / 1000;
            result =  String.valueOf(temp_distance) + "km";
        } else {
            result = String.valueOf(temp_distance) + "m";
        }

        return result;
    }


    public static String getReverseGeoCodingInfo(double longitude, double latitude) {
        StringBuffer sb = new StringBuffer();
        String longi = String.valueOf(longitude);
        String lati = String.valueOf(latitude);
        BufferedReader br = null;
        InputStreamReader in = null;
        HttpsURLConnection httpConn = null;
        String address = null;


        // TODO Auto-generated method stub
        //"https://openapi.naver.com/v1/map/reversegeocode?query=127.1141382,37.3599968"
        try {
            URL url = new URL("https://openapi.naver.com/v1/map/reversegeocode?query=" + longi + "," + lati);
            httpConn = (HttpsURLConnection) url.openConnection();
            httpConn.setRequestProperty("X-Naver-Client-Id", "bDBAfTyCHhTCnzNjWBXi");
            httpConn.setRequestProperty("X-Naver-Client-Secret", "KxH3N2Bicc");
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            //int len = conn.getContentLength();
            in = new InputStreamReader(httpConn.getInputStream());
            br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            //return sb.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        JSONObject jsonObject;
        JSONObject jsonObject2;
        JSONObject jsonObject3;
        JSONArray jsonArray;


        try {
            jsonObject = new JSONObject(sb.toString());
            jsonObject = (JSONObject) jsonObject.get("result");
            jsonArray = (JSONArray) jsonObject.get("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject2 = (JSONObject) jsonArray.get(i);
                if (null != jsonObject2.get("address")) {
                    address = (String) jsonObject2.get("address").toString();
                    Log.v("qioip", address);

                }
            }

            br.close();
            in.close();
            httpConn.disconnect();


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (address == null || address.isEmpty()) {
            return "위치 파악 중";
        } else {
            return address;
        }
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
        return sdf.format(new Date());
    }
}


