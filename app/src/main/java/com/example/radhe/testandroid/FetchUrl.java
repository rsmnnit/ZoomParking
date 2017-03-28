package com.example.radhe.testandroid;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by spark1299 on 6/3/17.
 */

public class FetchUrl extends AsyncTask<String,Void,String> {

    @Override
    protected String doInBackground(String... params) {
        String data = "";
        try
        {
            data = downloadUrl(params[0]);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return data;
    }

    private String downloadUrl(String param) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(param);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        ParserTask parserTask = new ParserTask();
        parserTask.execute(s);
    }
}
