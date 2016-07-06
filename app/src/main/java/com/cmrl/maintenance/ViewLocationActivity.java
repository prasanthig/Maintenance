package com.cmrl.maintenance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by prasa on 27-Jun-16.
 */
public class ViewLocationActivity extends AppCompatActivity {
    String getContractorURL = "location/?contractor=all";
    String getLocationURL = "location/?contractor=";
    LinearLayout viewLocationLayout;
    RadioGroup radioGroup;
    Button btView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);
        viewLocationLayout = (LinearLayout) findViewById(R.id.viewLocationLayout);
        radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        radioGroup.setPadding(30, 30, 30, 30);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(0, 50, 0, 20);
        buttonParams.gravity = Gravity.CENTER_HORIZONTAL;
        btView = new Button(this);
        btView.setText("View");
        btView.setTextColor(getResources().getColor(R.color.white));
        btView.setBackgroundResource(R.drawable.rounded_button);

        btView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(id);
                if(rb!=null){
                    String contractor = rb.getText().toString();
                    getLocationURL += contractor;
                    Intent mapIntent = new Intent(ViewLocationActivity.this, MapsActivity.class);
                    mapIntent.putExtra("getLocationURL", getLocationURL);
                    startActivity(mapIntent);
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(),"Select a contractor!!",Toast.LENGTH_LONG).show();
            }
        });
        btView.setLayoutParams(buttonParams);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    //Log.i("value1", jsonResponse.toString());
                    JSONArray dataArray = jsonResponse.getJSONArray("data");
                    for(int i = 0 ; i < dataArray.length(); i++){
                        JSONObject userObj = dataArray.getJSONObject(i);
                        String org = userObj.getString("organisation");
                        createRadioButton(org);
                        //Log.i("value1",org);
                    }
                    viewLocationLayout.addView(radioGroup);
                    viewLocationLayout.addView(btView);
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
        // Volley Request
        AssetMaintRequest contractorRequest = new AssetMaintRequest(getContractorURL, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(ViewLocationActivity.this);
        queue.add(contractorRequest);
        //Log.i("values", "username: "+username+"/"+password+" location: "+latitude+"/"+longitude );
    }

    private void createRadioButton(String org) {
        RadioButton btn1 = new RadioButton(this);
        btn1.setText(org);
        btn1.setPadding(20, 20, 20, 20);
        btn1.setTextSize(18);
        btn1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        radioGroup.addView(btn1);
    }
}
