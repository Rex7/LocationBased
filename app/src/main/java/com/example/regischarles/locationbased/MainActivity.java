package com.example.regischarles.locationbased;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity implements View.OnClickListener, TransferData {
    private static final int FINE_LOCATION = 1;
    private static final int COARSE_LOCATION = 2;
    TextView result, longitudeText, latitudeText;
    String Tag = "Main_Activity";
    SessionManage sessionManage;
    Button check;
    private LocationRequest locationRequest;
    private Location location;
    private LocationCallback locationCallback;
    private int responseCode;
    private FusedLocationProviderClient locationProviderApi;
    private boolean grantedPermission = false;
    RequestQueue requestQueue;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationProviderApi = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        LocationRequestConfiguration();
        result = findViewById(R.id.ResulText);
        latitudeText = findViewById(R.id.latitude);
        longitudeText = findViewById(R.id.longitude);
        check = findViewById(R.id.SubmitButton);
        check.setOnClickListener(this);
        new MyAsync(this).execute();
        sessionManage = new SessionManage(getApplicationContext());
        phoneNumber = sessionManage.getUserDetail().get("phone");
        /*
        LocationCallBack is for repeated calling of our location and store it either in local db
        is internet is not available or in center server.
         */
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {
                    Log.v("Latitude ", "" + location.getLatitude());
                    Log.v("longitude ", " " + location.getLongitude());
                    Toast.makeText(getApplicationContext(), "\nNew location Updating ", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Latitude is " + location.getLatitude() + "\nLongitude is " + location.getLongitude(), Toast.LENGTH_LONG).show();
                    latitudeText.setText(getResources().getText(R.string.lat) + String.valueOf(location.getLatitude()));
                    longitudeText.setText(getResources().getText(R.string.lon) + String.valueOf(location.getLongitude()));
                }

            }
        };

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.SubmitButton)
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
    }


    /*
       CheckConnection checks which network we are connected and whether internet facility is available
     */

    public void CheckConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        try {
            if (networkInfo != null && networkInfo.isConnected()) {
                result.setText("Connected to to " + networkInfo.getTypeName());
                boolean flag = checkInternetConnection();
                Log.v("boolean", "value" + flag);
                if (flag) {
                    Toast.makeText(getApplicationContext(), "We have internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "We Dont have internet connection", Toast.LENGTH_SHORT).show();
                }
            } else
                result.setText("No Network detected");

        } catch (Exception e) {
            Log.v("error ", e.toString());
        }
    }

    /*
    locationRequestUpdate we check if all the required permission for location tracking is enabled
    then we start to requestLocation
     */
    public void locationRequestUpdate() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION);
            }
            Log.v(Tag, "g");

            return;
        }
        locationProviderApi.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    /*
    onRequestPermissionsResult is used to request the runtime permission for our application to perform
    without any interruption
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    grantedPermission = true;
                    Toast.makeText(getApplicationContext(), "granted", Toast.LENGTH_SHORT).show();
                } else {
                    grantedPermission = false;
                    String req = getResources().getString(R.string.request_not_granted);
                    Toast.makeText(getApplicationContext(), req, Toast.LENGTH_SHORT).show();
                }
                break;
            case COARSE_LOCATION:
                break;
        }

    }

    /*
    LocationRequestConfiguration helps us to set the default values of how
    location should be retrieved  and the duration of each reading and its accuracy
     */
    public void LocationRequestConfiguration() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(60 * 1000);
        locationRequest.setFastestInterval(15 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    protected void onStart() {
        super.onStart();
        CheckConnection();

    }

    public void stopTracking() {
        locationProviderApi.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTracking();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationRequestUpdate();
    }

    /*
    @param location the Location
    we will reuse this function later
     */
    public void onLocationChanged(Location location) {
        final Double lon, lat;
        lon = location.getLongitude();
        lat = location.getLatitude();
        StringRequest stringRequest;
        latitudeText.setText(getResources().getText(R.string.lat) + String.valueOf(lat));
        longitudeText.setText(getResources().getText(R.string.lon) + String.valueOf(lon));
        requestQueue = VolleySingle.getInstance().getRequestQueue();
        stringRequest = new StringRequest(Request.Method.POST, "http://mysimplepage.freeiz.com/insertLocation.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                String message;
                try {
                    message = response;
                    Log.v("UniqueTag", "Message " + message);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.v("response ", "data " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> data = new HashMap<>();
                data.put("phone", phoneNumber);
                data.put("lat", lat.toString());
                data.put("long", lon.toString());
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public void setData(int res) {
        responseCode = res;
    }

    /*
    Checking internet availability
     */
    public boolean checkInternetConnection() {
        try {
            URL url = new URL("https://www.google.com/");
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(1000);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
            httpUrlConnection.connect();
            int res = httpUrlConnection.getResponseCode();
            Log.v("Respnseee", "res" + res);
            return true;


        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
            Log.v("MyException", "Exception" + ex.getLocalizedMessage());
            return false;

        }


    }
}
