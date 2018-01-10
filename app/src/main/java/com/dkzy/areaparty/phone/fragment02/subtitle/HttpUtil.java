package com.dkzy.areaparty.phone.fragment02.subtitle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ervincm on 2017/10/26.
 */

public class HttpUtil {

    public static  String sendHttpRequest(String address){
        HttpURLConnection connection=null;
        try {
            URL url  = new URL(address);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
         //   connection.setRequestProperty("Charsert","utf-8");

            InputStream in = connection.getInputStream();
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            StringBuilder response=new StringBuilder();
            String line;
            while ((line=reader.readLine())!=null){
                response.append(line);
            }
            return  response.toString();


        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally{
            if(connection!=null){
                connection.disconnect();
            }
        }
    }
}
