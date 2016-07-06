package com.cmrl.maintenance;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Response.Listener<String> responseListener;
    String getLocationURL = "";
    JSONObject jsonResponse;
    JSONArray dataArray = null;
    double lat1 = 13.07,lng1 = 80.19;
    IconGenerator generator;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        generator = new IconGenerator(getApplicationContext());
        textView = new TextView(this);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setPadding(10, 10, 10, 10);
        textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        generator.setBackground(getResources().getDrawable(R.drawable.bubble_mask));

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            getLocationURL = extras.getString("getLocationURL");
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    jsonResponse = new JSONObject(response);
                    dataArray = jsonResponse.getJSONArray("data");
                    //Log.i("value1", "Response: " + jsonResponse.toString());
                    mapFragment.getMapAsync(MapsActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if( error instanceof TimeoutError) {
                    Log.i("Volley","Error: TimeoutError  " + error.toString());
                } else if( error instanceof ServerError) {
                    Log.i("Volley","Error: Server Error " + error.getMessage());
                } else if( error instanceof AuthFailureError) {
                    Log.i("Volley","Error: Auth Failure Error " + error.getMessage());
                } else if( error instanceof ParseError) {
                    Log.i("Volley","Error: Parse Error " + error.getMessage());
                } else if( error instanceof NoConnectionError) {
                    Log.i("Volley","Error: No Connection Error " + error.getMessage());
                } else if( error instanceof NetworkError) {
                    Log.i("Volley","Error: NetworkError " + error.getMessage());
                }

            }
        };


        AssetMaintRequest locationRequest = new AssetMaintRequest(getLocationURL, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
        //Log.i("value1", "Location req");
        queue.add(locationRequest);

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


        try {
            if(dataArray!=null)
            for(int i = 0 ; i < dataArray.length(); i++){
                JSONObject locationObject = dataArray.getJSONObject(i);
                if(!locationObject.getString("username").equals("null") &&
                        !locationObject.getString("last_latitude").equals("null") &&
                        !locationObject.getString("last_longitude").equals("null")) {
                    //username[i] = Double.valueOf(locationObject.getString("username"));
                    lat1 = Double.parseDouble(locationObject.getString("last_latitude"));
                    lng1 = Double.parseDouble(locationObject.getString("last_longitude"));
                   // mMap.addMarker(new MarkerOptions().title(locationObject.getString("username")).position(new LatLng(lat1, lng1))).showInfoWindow();
                    //Log.i("value1", " lat=" + lat1 + " longi=" + lng1);


                    textView.setText(locationObject.getString("username"));
                    generator.setContentView(textView);
                    Bitmap icon = generator.makeIcon();

                    MarkerOptions tp = new MarkerOptions().position(new LatLng(lat1, lng1)).icon(BitmapDescriptorFactory.fromBitmap(icon));
                    mMap.addMarker(tp);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
         mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat1,lng1),15.0f));
    }
}
