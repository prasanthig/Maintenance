package com.cmrl.maintenance;

/*
* This Activity deciphers the supplied Asset Code. The input asset code is sent to specified URL
* and using Volley module, the deciphered message is obtained
* B. Umesh Rai
 */

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AssetDecipherActivity extends AppCompatActivity {

    private String latitude = "";
    private String longitude = "";
    String assetDecipherURL = "asset-code/decipher?assetCode=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_decipher);

        final EditText etAssetcode = (EditText) findViewById(R.id.etAssetcode);
        final TextView tvLogin = (TextView) findViewById(R.id.tvLogin);
        final Button btDecipher = (Button) findViewById(R.id.btDecipher);

        // Save values from predecessor
        Intent intent = getIntent();
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");

        // User wants to Login instead
        assert tvLogin != null;
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(AssetDecipherActivity.this, UserActivity.class);
                userIntent.putExtra("latitude", latitude); // Saved values returned
                userIntent.putExtra("longitude", longitude);
                AssetDecipherActivity.this.startActivity(userIntent);
                finish();
            }
        });

        // Start Decipher. JSON Request and Response
        assert btDecipher != null;
        btDecipher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String assetCode = etAssetcode.getText().toString();
                // Response received from the server
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            //Log.i("value", jsonResponse.toString());
                            boolean success = jsonResponse.getJSONObject("data").getBoolean("success");
                            String corridor = jsonResponse.getJSONObject("data").getString("corridor");
                            String station = jsonResponse.getJSONObject("data").getString("station");
                            String equipment = jsonResponse.getJSONObject("data").getString("equipment");
                            String equipment_no = jsonResponse.getJSONObject("data").getString("equipment_no");
                            String location = jsonResponse.getJSONObject("data").getString("location");
                            //Log.i("value",  "success: " + String.valueOf(success) + ", equipment: "+ equipment);

                            if (success) {
                                //Pass to successor program
                                Intent intent = new Intent(AssetDecipherActivity.this, AssetDetailActivity.class);
                                intent.putExtra("corridor", corridor);
                                intent.putExtra("station", station);
                                intent.putExtra("equipment", equipment);
                                intent.putExtra("equipment_no", equipment_no);
                                intent.putExtra("location", location);
                                AssetDecipherActivity.this.startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AssetDecipherActivity.this);
                                builder.setMessage("Asset Code Decipher Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Volley", "Error");
                    }
                };

                assetDecipherURL += assetCode;
                // Volley Request
                AssetMaintRequest assetDecipherRequest = new AssetMaintRequest(assetDecipherURL, responseListener, errorListener);
                RequestQueue queue = Volley.newRequestQueue(AssetDecipherActivity.this);
                queue.add(assetDecipherRequest);
            }
        });
    }
}
