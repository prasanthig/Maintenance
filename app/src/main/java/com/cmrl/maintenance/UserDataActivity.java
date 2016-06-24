package com.cmrl.maintenance;

/*
* Displays the Response and terminates the app use. Should be changed for something more useful
* B. Umesh Rai
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class UserDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        Intent intent = getIntent();
        String userid = intent.getStringExtra("userid");
        String username = intent.getStringExtra("username");
        String organisation = intent.getStringExtra("organisation");
        String latitude = intent.getStringExtra("latitude");
        String longitude = intent.getStringExtra("longitude");
        String auth_key = intent.getStringExtra("auth_key");

        TextView tvUsername = (TextView) findViewById(R.id.tvUsername);
        TextView tvUserid = (TextView) findViewById(R.id.tvUserid);
        TextView tvContractor = (TextView) findViewById(R.id.tvContractor);
        TextView tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        TextView tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        TextView tvAccesskey = (TextView) findViewById(R.id.tvAccesskey);

        tvUsername.setText("Username: "+ username);
        tvUserid.setText("User ID: "+ userid);
        tvContractor.setText("Contractor: "+ organisation);
        tvLatitude.setText("Latitude: "+latitude);
        tvLongitude.setText("Longitude: "+longitude);
        tvAccesskey.setText("Key: "+ auth_key);

    }
}
