package com.cmrl.maintenance;

import google.zxing.integration.android.IntentIntegrator;
import google.zxing.integration.android.IntentResult;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class AssetMaintActivity extends AppCompatActivity {

    public LinearLayout linearLayout;
    CreateLayout createLayout;
    String auth_key,equipment;
    Response.Listener<String> responseListener, responseListenerforAsset;

    String assetMaintURL = "maintenance-next-dues/?assetCode=";
    String assetDecipherURL = "asset-code/decipher?assetCode=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SaveData.checkedList.clear();
        SaveData.editTextList.clear();

        Intent intent = getIntent();
        auth_key = intent.getStringExtra("auth_key");
        setContentView(R.layout.activity_asset_maint);
        linearLayout = (LinearLayout) findViewById(R.id.maintList);
        createLayout = new CreateLayout(AssetMaintActivity.this,linearLayout);
        //Log.i("value", "Key: "+auth_key);

        //Starting the ZXing Scanner
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
        //Log.i("value1", "starting scanner");

        responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    //Log.i("value1", "Response: "+jsonResponse.toString());
                    jsonResponse = jsonResponse.getJSONObject("data");
                    SaveData.FREQ_ID = jsonResponse.getString("freq_id");
                    //Log.i("value1","FREQ ID: "+ SaveData.FREQ_ID);

                    FetchData fetchData = new FetchData(AssetMaintActivity.this);
                    Map<String, String> param = fetchData.getApiParam(jsonResponse);
                    //Log.i("value1", "Response: " + param);

                    String[][] createdViews = fetchData.createViews(param, linearLayout);

                    createLayout.createSubmitButton(createdViews);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        responseListenerforAsset = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonAssetResponse = new JSONObject(response);
                    equipment = jsonAssetResponse.getJSONObject("data").getString("equipment");
                    //Log.i("value1",jsonAssetResponse.toString());
                    //Log.i("value1","EQUIPMENT: " +equipment);
                    AssetMaintActivity.this.getSupportActionBar().setTitle(equipment);
                    SaveData.EQUIPMENT = equipment;

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //Fetch Data from URL once the QRCode is scanned
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        //Log.i("value1", "Scanning done");
        if (scanningResult != null) {
            // Volley Request
            SaveData.ASSET_CODE = scanningResult.getContents();

            assetMaintURL += SaveData.ASSET_CODE + "&token=" + auth_key;
            assetDecipherURL += SaveData.ASSET_CODE;

            AssetMaintRequest decipherRequest = new AssetMaintRequest(assetDecipherURL, responseListenerforAsset);
            RequestQueue queue = Volley.newRequestQueue(AssetMaintActivity.this);
            //Log.i("value1","AssetDecipher req");
            queue.add(decipherRequest);

            AssetMaintRequest maintRequest = new AssetMaintRequest(assetMaintURL, responseListener);
            //Log.i("value1","AssetMAint req");
            queue.add(maintRequest);

        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}

