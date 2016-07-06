package com.cmrl.maintenance;

import google.zxing.integration.android.IntentIntegrator;
import google.zxing.integration.android.IntentResult;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class AssetMaintActivity extends AppCompatActivity {

    public LinearLayout linearLayout;
    CreateLayout createLayout;
    String auth_key,equipment;
    Response.Listener<String> responseListener, responseListenerforAsset;
    Response.ErrorListener errorListener;

    String assetMaintURL = "maintenance-next-dues/?assetCode=";
    String assetDecipherURL = "asset-code/decipher?assetCode=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SaveData.checkedList.clear();
        SaveData.editTextList.clear();
        SaveData.FREQ_ID = "";

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
                    JSONObject dataResponse = jsonResponse.getJSONObject("data");
                    //Log.i("value", "Response: " + dataResponse.toString());
                    SaveData.FREQ_ID = dataResponse.getString("freq_id");
                    //Log.i("value1","FREQ ID: "+ SaveData.FREQ_ID);

                    //if(dataResponse.toString().equals("null"))


                    FetchData fetchData = new FetchData(AssetMaintActivity.this);
                    Map<String, String> param = fetchData.getApiParam(dataResponse);
                    //Log.i("value1", "Response: " + param);

                    String[][] createdViews = fetchData.createViews(param, linearLayout);

                    createLayout.createSubmitButton(createdViews);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(SaveData.FREQ_ID.equals(""))
                    Toast.makeText(AssetMaintActivity.this, "No maintenance Due", Toast.LENGTH_LONG).show();
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
                    if(!(AssetMaintActivity.this.getSupportActionBar() == null))
                        AssetMaintActivity.this.getSupportActionBar().setTitle(equipment);
                    SaveData.EQUIPMENT = equipment;

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

        errorListener = new Response.ErrorListener() {
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


    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //Fetch Data from URL once the QRCode is scanned
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        //Log.i("value1", "Scanning done");
        // Loop for network
        /*if (!isNetworkAvailable(AssetMaintActivity.this)) {
            while(true){
                Log.i("Volley", "Network Not Available");
                if (isNetworkAvailable(AssetMaintActivity.this)) break;
            }
        }*/
        Log.i("Volley", "Network Available");

        if (scanningResult != null) {
            // Volley Request
            SaveData.ASSET_CODE = scanningResult.getContents();

            assetMaintURL += SaveData.ASSET_CODE + "&token=" + auth_key;
            assetDecipherURL += SaveData.ASSET_CODE;

            AssetMaintRequest decipherRequest = new AssetMaintRequest(assetDecipherURL, responseListenerforAsset,errorListener);
            RequestQueue queue = Volley.newRequestQueue(AssetMaintActivity.this);
            //Log.i("value1","AssetDecipher req");
            queue.add(decipherRequest);

            AssetMaintRequest maintRequest = new AssetMaintRequest(assetMaintURL, responseListener, errorListener);
            //Log.i("value1","AssetMaint req");
            queue.add(maintRequest);

        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // Checks for Network connectivity
    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
