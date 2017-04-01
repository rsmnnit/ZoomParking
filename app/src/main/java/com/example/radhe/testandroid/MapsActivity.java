package com.example.radhe.testandroid;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, Details.OnFragmentInteractionListener {

    private static GoogleMap mMap;
    Marker Delhi;
    Marker Home;
    Marker Sydney;
    FloatingActionButton search;
    private ProgressDialog pdialog;
    private Connection jParser = new Connection();
    private String success;
    private String available;
    private JSONObject jobj;
    private JSONArray jarray;
    private String url = "http://192.168.43.136:8000/parkhere/showallmarkers.php";
    private List<Markers> markersList = new ArrayList<Markers>();

    private Double search_latitude,search_longitude;
    private Context mContext;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    double latitude;
    double longitude;
    Location location;
    FloatingActionButton current ;
    private  int cnt=0;
    private LocationManager locationManager;
    private android.location.LocationListener locationListener;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private int fragmentcount=0;
    Marker searchObj;
    private Markers[] array = new Markers[100];
    private static LatLng curr_loc;

    GoogleApiClient client;
    Location mLastLocation;

   public GoogleMap getmMap()
   {
       return mMap;
   }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        cnt = 0;




        search = (FloatingActionButton) findViewById(R.id.search_location);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                         if (!isNetworkAvailable()) {
                             Toast.makeText(getApplicationContext(), " Not Connected to Internet", Toast.LENGTH_LONG).show();
                             Intent i = new Intent(getApplicationContext(), MainActivity.class);
                             startActivity(i);
                             return;

                         }
                    if(searchObj!=null){

                    searchObj.remove();
                    searchObj=null;
                }
                EditText et = (EditText) findViewById(R.id.location_name);
                String location = et.getText().toString();
                et.setText("");

                List<android.location.Address> addressList = null;
                if (location != null && !location.equals("")) {
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("LOCATION--",location);
                    Log.d("LIST",addressList.toString());
                    android.location.Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    searchObj=mMap.addMarker(new MarkerOptions().position(latLng).title(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(6));
                    search_latitude = address.getLatitude();
                    search_longitude = address.getLongitude();
                }
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //Toast.makeText(location.getLatitude()+" "+location.getLongitude(),LENGTH_LONG,getApplicationContext()).show();
                Log.d("CURRENT LOCATION ",location.getLatitude()+" "+location.getLongitude());

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(location.toString());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                curr_loc = latLng;
                mMap.addMarker(markerOptions);

                if(cnt==0){

                    CameraUpdate center=CameraUpdateFactory.newLatLng(latLng);
                    CameraUpdate zoom=CameraUpdateFactory.zoomTo(11);
                    mMap.moveCamera(center);
                    mMap.animateCamera(zoom);
                    //mMap.moveCamera(center);
                    cnt=1;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET}, 10);


                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET}, 10);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0,  locationListener);

            }
        }
        else{

        }

        current = (FloatingActionButton) findViewById(R.id.current_location);
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cnt = 0;

            }
        });

    }




    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) throws SecurityException{
        switch (requestCode){
            case 10:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
                }
                return;
        }



    }


    public LatLng get_curr_loc()
    {
        return curr_loc;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        new loaddetails().execute();


    }




    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        this.finishAffinity();

    }

    @Override
    public boolean onMarkerClick(Marker marker) {


        Bundle bundle = new Bundle();
        String tag = (String) marker.getTag();

        if (marker.getTag()==null)
            return true;

        Log.d("ID",tag);

        bundle.putString("id", tag);
        android.app.FragmentManager fm = getFragmentManager();
        android.app.FragmentTransaction ft = fm.beginTransaction();
        Details details = new Details();
        details.setArguments(bundle);
        while (fragmentcount>0){
            fragmentcount--;
            getFragmentManager().popBackStack();
        }

        ft.addToBackStack("details");
        ft.add(R.id.details, details, "hello");
        fragmentcount++;
        ft.commit();

        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    class Markers {
        String availability;
        String lattitude;
        String longitude;
        String id;
        public Markers(String availability,String lat,String  lon,String id){
            this.availability= availability;
            this.lattitude= lat;
            this.longitude = lon;
            this.id= id;
        }
    }









    class loaddetails extends AsyncTask<String,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new ProgressDialog(getApplicationContext());
            pdialog.setMessage("Fetch Details");
            pdialog.setIndeterminate(false);
            pdialog.setCancelable(false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pdialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0;i<markersList.size();i++){
                        Markers mark = markersList.get(i);
                        LatLng latLng = new LatLng(Double.valueOf(mark.lattitude),Double.valueOf(mark.longitude));
                        int count = 0;
                        for(int j=0;j<mark.availability.length();j++){
                            if(mark.availability.charAt(j)=='1')
                                count++;
                        }
                        Marker temp = mMap.addMarker(new MarkerOptions().position(latLng).title("Available Spaces "+String.valueOf(count)));
                        temp.setTag(mark.id);
                        //temp.showInfoWindow();
                    }
                }
            });
        }

        @Override
        protected Void doInBackground(String... args) {
            HashMap<String,String> params = new HashMap<String, String>();
            JSONObject json = null;
            try {

                json = jParser.makeHttpRequest(url,"GET",params);
                Log.d("json", String.valueOf(json));

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                success = json.getString("success");
                if (success=="1" || success.equals("1")){
                    jarray = json.getJSONArray("details");

                    for (int i=0;i<jarray.length();i++){
                        JSONObject obj = jarray.getJSONObject(i);
                        Markers marker = new Markers(obj.getString("availability"),obj.getString("latitude"),obj.getString("longitude"),obj.getString("id"));
                        markersList.add(marker);

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
