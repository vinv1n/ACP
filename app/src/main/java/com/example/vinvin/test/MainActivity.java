package com.example.vinvin.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.util.List;

import static android.widget.Toast.LENGTH_LONG;


public class MainActivity extends AppCompatActivity {

    private WifiManager mainWifi;
    private TextView mainText;
    private static final int PERMISSION_TO_WIFI = 2;
    private Menu menu;
    private GoogleMap Gmap;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            StringBuilder sb = new StringBuilder();
            List<ScanResult> networks;
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equalsIgnoreCase(intent.getAction())) {

                networks = mainWifi.getScanResults();
                for (int i = 0; i < networks.size(); i++) {
                    sb.append(Integer.toString(i + 1) + ". ");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log:
                LogView();
                return true;
            case R.id.settings_id:
                SettingsView();
                return true;
            case R.id.turn_off:
                try {
                    unregisterReceiver(receiver);
                    changeMenu(1);
                    return true;
                } catch (IllegalArgumentException e) {
                    createWifi();
                    changeMenu(0);
                    return true;
                }
            default:
                return true;
        }
    }

    public void changeMenu(int i) {
        MenuItem turn_status = menu.findItem(R.id.turn_off);
        if (i == 1) {
            turn_status.setTitle("Turn on");
        } else {
            turn_status.setTitle("Turn off");
        }
    }
    public void LogView(){
        setContentView(R.layout.activity_log);

    }

    public void CheckStatus(CheckBox box0, CheckBox box1, CheckBox box2) {


    }

    public void SettingsView() {
        setContentView(R.layout.activity_settings);
        Button exit_settings = (Button) findViewById(R.id.exit_button);
        Button accept_button = (Button) findViewById(R.id.accept_button);
        exit_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_main);
            }
        });
        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Should change in variables in algorithm
                setContentView(R.layout.activity_main);
            }
        });

    }

    private void createWifi() {
        try {
            //Registers the receiver and starts the network scan
            mainWifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            mainWifi.setWifiEnabled(true);

            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            registerReceiver(receiver, filter);

            mainWifi.startScan();
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!", LENGTH_LONG).show();
            System.exit(1);
        }
    }

    private void ask() {
        int Permission = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        if (Permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_TO_WIFI);
        } else {
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
            }
        }
    }
}
