package com.example.radhe.testandroid;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by radhe on 26/2/17.
 */

public class Connection  {

    public Connection(){

    }
    public JSONObject makeHttpRequest(String url, String method, HashMap<String,String >params) throws IOException{
        JSONObject jobj = new JSONObject();
        StringBuilder response = new StringBuilder();
        try{
            if(method.equals("GET")) {
                URL httpurl = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) httpurl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();

                try {
                    BufferedReader breader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String ans = "";
                    while ((ans=breader.readLine())!=null){
                        response.append(ans);
                    }
                    breader.close();
                    String t= "";
                    t = new String(response);
                    jobj = new JSONObject(t);
                }
                catch (IOException e){
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    if (urlConnection!=null){
                        urlConnection.disconnect();
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
     return jobj;
    }
}
