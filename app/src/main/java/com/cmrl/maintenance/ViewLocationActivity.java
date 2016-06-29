package com.cmrl.maintenance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by prasa on 27-Jun-16.
 */
public class ViewLocationActivity extends AppCompatActivity {
    EditText etContractor;
    Button btView;
    Response.Listener<String> responseListener;
    String getLocationURL = "location/?contractor=";
    String[] username, latitudePoints, longitudePoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);
        username = new String[20];
        latitudePoints = new String[20];
        longitudePoints = new String[20];

        etContractor = (EditText) findViewById(R.id.etContractor);
        btView = (Button) findViewById(R.id.btView);


        btView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contractor = etContractor.getText().toString();
                getLocationURL += contractor;
                Intent mapIntent = new Intent(ViewLocationActivity.this,MapsActivity.class);
                mapIntent.putExtra("getLocationURL",getLocationURL);
                startActivity(mapIntent);
            }
        });
    }
}
