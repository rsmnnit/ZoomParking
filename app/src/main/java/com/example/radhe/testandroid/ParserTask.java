package com.example.radhe.testandroid;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by spark1299 on 6/3/17.
 */

public class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>> {
    public GoogleMap mMap ;
    public static PolylineOptions polylineOptions;
    public static Polyline polyline;
    public static int cntline = 0;
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... params) {

        JSONObject jsonObject;
        List<List<HashMap<String,String>>> routes = null;
        try {
            jsonObject = new JSONObject(params[0]);
            DataParser parser = new DataParser();
            routes = parser.parse(jsonObject);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return routes;
    }
    public void removePolyline(){
        if(cntline !=0){
            polyline.remove();
            cntline= 0 ;
        }

    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
        super.onPostExecute(lists);
        ArrayList<LatLng> points;
         polylineOptions = null;
        for(int i=0;i<lists.size();i++)
        {
            points = new ArrayList<>();
            polylineOptions = new PolylineOptions();
            List<HashMap<String,String>> path = lists.get(i);
            for(int j=0;j<path.size();j++)
            {
                HashMap<String,String>point = path.get(j);
                LatLng position = new LatLng(Double.parseDouble(point.get("lat")),Double.parseDouble(point.get("lng")));
                points.add(position);
            }
            polylineOptions.addAll(points);
            polylineOptions.width(10);
            polylineOptions.color(Color.BLUE);
        }
        if(polylineOptions!=null)
        {
            mMap = new MapsActivity().getmMap();
            removePolyline();
            polyline = mMap.addPolyline(polylineOptions);
            cntline +=1;
        }
        else
        {
            Log.d("PolyLine Error","PolyLine(Static vale me) me fatt gayi");
        }
    }
}
