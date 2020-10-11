package com.example.regischarles.locationbased;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SessionManage sessionManage;
    RequestQueue requestQueue;
    private MarkerOptions options=new MarkerOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sessionManage=new SessionManage(getApplicationContext());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(final GoogleMap googleMap) {
        final ArrayList<LatLng> markers=new ArrayList<>();
        mMap = googleMap;
        StringRequest stringRequest;
        requestQueue=VolleySingle.getInstance().getRequestQueue();
        stringRequest= new StringRequest(Request.Method.POST, "http://mysimplepage.freeiz.com/getAllRecord.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONArray jsonarray = new JSONArray(response);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        Double lat = Double.valueOf(jsonobject.getString("latitude"));
                        Double lon = Double.valueOf(jsonobject.getString("longitude"));
                        markers.add(new LatLng(lat,lon));
                        googleMap.addMarker(new MarkerOptions().position(markers.get(i)));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markers.get(i), 17.0f));
                        Log.v("UniqueTag","lat "+lat);
                        Log.v("UniqueTag","lon "+lon);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.v("response ","data "+response);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> data = new HashMap<>();
                data.put("phone", sessionManage.getUserDetail().get("phone"));





                return data;

            }
        };


        requestQueue.add(stringRequest);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        for (LatLng point : markers) {
            Log.v("UniqueTag","point"+point.latitude);
            options.position(point);
            options.title("someTitle");



        }



    }
}
