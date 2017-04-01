package com.example.radhe.testandroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Ref;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Details.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Details#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Details extends android.app.Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FloatingActionButton close;
    private FloatingActionButton show;
    private FloatingActionButton direction;
    private FloatingActionButton refresh;
    private FloatingActionButton closedirection;
    private OnFragmentInteractionListener mListener;
    private ProgressDialog pdialog;
    private Connection jParser = new Connection();
    private String available = "";
    private String success = "";
    private String occupied = "";
    private String id;
    private View view;
    private String ans;
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private HashMap<String ,String>details = new HashMap<String, String>();
    private String availabilitystring ;
    private LatLng dest_loc,curr_loc;
    public String url;
    public String parkingname;

    public Details() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Details.
     */
    // TODO: Rename and change types and number of parameters
    public static Details newInstance(String param1, String param2) {
        Details fragment = new Details();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
         view= inflater.inflate(R.layout.fragment_details, container, false);
        view.setBackgroundColor(Color.argb(255,20,160,8));
        new loaddetails().execute();
        id = this.getArguments().getString("id");
        //mParam1 = this.getArguments().getString("name");

        //TextView tv =(TextView)view.findViewById(R.id.show);
        //tv.setText(mParam1);

        refresh = (FloatingActionButton) view.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Refresh ref = new Refresh();
                Thread t = new Thread(ref);
                t.run();

            }
        });

        closedirection = (FloatingActionButton) view.findViewById(R.id.closedirection);
        closedirection.setVisibility(View.GONE);
        closedirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ParserTask().removePolyline();
                closedirection.setVisibility(View.GONE);
                direction.setVisibility(View.VISIBLE);

            }
        });

        show=(FloatingActionButton) view.findViewById(R.id.show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(),Parking_view.class);
                i.putExtra("id",id);
                i.putExtra("string",availabilitystring);
                i.putExtra("name",parkingname);
                startActivity(i);
            }
        });


        close = (FloatingActionButton) view.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getActivity().getFragmentManager().popBackStack();
               // getActivity().getFragmentManager().popBackStack();




            }
        });

         direction = (FloatingActionButton)view.findViewById(R.id.direction);
        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                direction.setVisibility(View.GONE);
                closedirection.setVisibility(View.VISIBLE);
                 curr_loc = new MapsActivity().get_curr_loc();
                //Log.d("Curr_loc",dest_loc.toString());
                //Log.d("dest_loc",curr_loc.toString());
                if(curr_loc!=null) {
                    String url = getUrl(curr_loc, dest_loc);
                    FetchUrl fetchUrl = new FetchUrl();
                    fetchUrl.execute(url);
                }
                else {
                    direction.setVisibility(View.VISIBLE);
                    closedirection.setVisibility(View.GONE);
                }
            }
        });
        show.setVisibility(View.GONE);
        close.setVisibility(View.GONE);
        direction.setVisibility(View.GONE);
        return view;
        // Inflate the layout for this fragment

    }







    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }







    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class loaddetails extends AsyncTask<String,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new ProgressDialog(getActivity().getApplicationContext());
            pdialog.setMessage("Fetch Details");
            pdialog.setIndeterminate(false);
            pdialog.setCancelable(false);



        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pdialog.dismiss();
            Log.d("check",available);
            if (getActivity()==null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    TextView name = (TextView) view.findViewById(R.id.name);
                    TextView address = (TextView) view.findViewById(R.id.address);
                    TextView total = (TextView)view.findViewById(R.id.total_available);
                   // TextView current = (TextView)view.findViewById(R.id.current_available);
                    name.setText(details.get("name"));
                    address.setText(details.get("address"));
                    total.setText(details.get("availability")+" out of "+details.get("total_availability")+" available");
                    //current.setText(details.get("availability"));
                    show.setVisibility(View.VISIBLE);
                    close.setVisibility(View.VISIBLE);
                    direction.setVisibility(View.VISIBLE);
                    dest_loc = new LatLng(Double.parseDouble(details.get("latitude")),Double.parseDouble(details.get("longitude")));
                }
            });


        }

        @Override
        protected Void doInBackground(String... args) {

            String url = "http://192.168.43.136:8000/parkhere/show_particular.php?id="+id;



            HashMap<String,String>params = new HashMap<String, String>();
            JSONObject json = null;
            try {
                json = jParser.makeHttpRequest(url,"GET",params);
                ans = json.toString();
                Log.d("URL",url);
                System.out.println("url is "+url);

                Log.d("JSON1",ans+url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                success = json.getString("success");
                if (success=="1" || success.equals("1")){
                    jsonArray = json.getJSONArray("details");
                    for (int i=0;i<jsonArray.length();i++){
                        jsonObject = jsonArray.getJSONObject(i);
                        details.put("name",jsonObject.getString("name"));
                        details.put("total_availability",jsonObject.getString("total_availability"));
                        details.put("latitude",jsonObject.getString("latitude"));
                        details.put("longitude",jsonObject.getString("longitude"));
                        availabilitystring = jsonObject.getString("availability");
                        parkingname = jsonObject.getString("name");
                        details.put("address",jsonObject.getString("address"));
                        int cc=0;
                        for (int j=0;j<jsonObject.getString("availability").length();j++){
                            if(jsonObject.getString("availability").charAt(j)=='0')
                                cc++;
                        }
                        details.put("availability",String .valueOf(cc));

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class Refresh implements Runnable{
        @Override
        public void run() {
            new loaddetails().execute();

        }
    }





}
