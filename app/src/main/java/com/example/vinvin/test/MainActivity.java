package com.example.vinvin.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private WifiManager mainWifi;
    private TextView mainText;
    private static final int PERMISSION_TO_WIFI = 2;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            StringBuilder sb = new StringBuilder();
            List<ScanResult> networks;
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equalsIgnoreCase(intent.getAction())) {

                networks = mainWifi.getScanResults();
                for(int i = 0; i<networks.size(); i++){
                    sb.append(Integer.toString(i+1) + ". ");
                    sb.append("BSSID ");
                    sb.append(networks.get(i).BSSID + " Level " + networks.get(i).level + " ");
                    sb.append("SSID ");
                    sb.append(networks.get(i).SSID + "\n");
                }
                mainText.setText(sb);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainText = (TextView) findViewById(R.id.text_id);
        ask();
    }


    private void createWifi() {

        mainWifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        mainWifi.setWifiEnabled(true);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiver, filter);

        mainWifi.startScan();
    }
    private void ask(){
        int Permission = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        if(Permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_TO_WIFI);
        }else{
            createWifi();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
            String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_TO_WIFI: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createWifi();
                } else {
                    this.finish();
                    System.exit(0);
                }
                return;
            }
        }
    }
}
