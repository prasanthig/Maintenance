package com.cmrl.maintenance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.drive.events.ChangeEvent;
import com.google.android.gms.drive.events.ChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateActivity extends AppCompatActivity {

    public JSONObject parent = null;
    String equipment;
    String smsMessage = "";
    String smsListURL = "sms-recipient/?equip=";
    RequestQueue queue;
    Response.Listener<String> smsResponseListener;
    Response.ErrorListener errorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        final TextView tvMessage = (TextView) findViewById(R.id.tvMessage);
        final TextView tvEngineer = (TextView) findViewById(R.id.tvEngineer);
        final TextView tvContractor = (TextView) findViewById(R.id.tvContractor);

        Intent intent = getIntent();
        String parentString = intent.getStringExtra("parent");

        equipment = SaveData.EQUIPMENT.toLowerCase();
        equipment = Character.toUpperCase(equipment.charAt(0)) + equipment.substring(1);

        try {
            this.parent = new JSONObject(parentString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.i("value","At Create, Parent: "+parent);
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                try {
                    //Log.i("value", "Response: "+jsonResponse.toString());
                    jsonResponse = jsonResponse.getJSONObject("data");
                    //Log.i("value", "Response: "+jsonResponse.toString());
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        String message = jsonResponse.getString("message");
                        String username = jsonResponse.getString("username");
                        String organisation = jsonResponse.getString("organisation");

                        smsMessage = "Maintenance done by " + username + " of " + organisation + " for equipment: " + SaveData.EQUIPMENT;
                        Log.i("value1", "Message:" + smsMessage);
                        addNextRequest();
                        tvMessage.setText(message);
                        tvEngineer.setText("Engineer: " + username);
                        tvContractor.setText("Contractor: " + organisation);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        smsResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int j = 0;
                String phno = "";
                SmsManager smsManager = SmsManager.getDefault();
                List<String> recipientList = new ArrayList<>();
                recipientList.clear();
                try {
                    Log.i("value1", "SMS List Response: " + response);
                    JSONObject smsListObject = new JSONObject(response);
                    JSONArray dataArray = smsListObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject smsRecipientObject = dataArray.getJSONObject(i);
                        recipientList.add(smsRecipientObject.getString("mobile"));
                    }
                    for (int i = 0; i < recipientList.size(); i++) {
                        phno = recipientList.get(i);
                        Log.i("value1","Phone number: "+phno);
                        try {
                            smsManager.sendTextMessage(phno, null, smsMessage, null, null);
                            //Log.i("value", "Sending message");
                            Toast.makeText(getApplicationContext(), "SMS Sent Successfully!",
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            //Log.i("value1", e.toString());
                            Toast.makeText(getApplicationContext(), "SMS not sent!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Failed to retrieve SMS List, please try again later ! ",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.i("Volley", "Error");
            }
        };

        // Volley Request
        WorksheetRequest worksheetRequest = new WorksheetRequest(parent, responseListener, errorListener);
        queue = Volley.newRequestQueue(CreateActivity.this);
        queue.add(worksheetRequest);
        //Log.i("here","Method Called");
        /*smsListURL += equipment;
        AssetMaintRequest smsListRequest = new AssetMaintRequest(smsListURL, smsResponseListener, errorListener);
        queue.add(smsListRequest);*/

    }

    private void addNextRequest() {
        smsListURL += equipment;
        AssetMaintRequest smsListRequest = new AssetMaintRequest(smsListURL, smsResponseListener, errorListener);
        queue.add(smsListRequest);
    }
}
