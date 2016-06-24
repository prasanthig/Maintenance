package com.cmrl.maintenance;

/*
* Displays the Response and terminates the app use. Should be changed for something more useful
* B. Umesh Rai
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AssetDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_detail);

        Intent intent = getIntent();
        String equipment = intent.getStringExtra("equipment");
        String station = intent.getStringExtra("station");
        String location = intent.getStringExtra("location");
        String equipment_no = intent.getStringExtra("equipment_no");
        String corridor = intent.getStringExtra("corridor");

        TextView tvEquipment = (TextView) findViewById(R.id.tvEquipment);
        TextView tvStation = (TextView) findViewById(R.id.tvStation);
        TextView tvLocation = (TextView) findViewById(R.id.tvLocation);
        TextView tvNumber = (TextView) findViewById(R.id.tvNumber);
        TextView tvCorridor = (TextView) findViewById(R.id.tvCorridor);

        tvEquipment.setText("Equipment: "+ equipment);
        tvStation.setText("Station: "+ station);
        tvLocation.setText("Located at: "+ location);
        tvNumber.setText("Equipment Number: "+ equipment_no);
        tvCorridor.setText("Corridor: "+ corridor);
    }
}