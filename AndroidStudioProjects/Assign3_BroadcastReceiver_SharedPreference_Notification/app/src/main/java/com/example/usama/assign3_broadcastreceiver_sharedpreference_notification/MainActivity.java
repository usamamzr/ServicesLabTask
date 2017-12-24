package com.example.usama.assign3_broadcastreceiver_sharedpreference_notification;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.usama.assign3_broadcastreceiver_sharedpreference_notification.NetworkReceiver.IS_NETWORK_AVAILABLE;


public class MainActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Name = "nameKey";
    private BroadcastReceiver mReceiver;

    Switch ntwSwitch, apmSwitch;
    TextView tvBS;
    EditText etName;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ntwSwitch = findViewById(R.id.ntwSwitch);
        apmSwitch = findViewById(R.id.apmSwitch);
        tvBS = findViewById(R.id.tvBS);
        etName = findViewById(R.id.etName);
        btnSave = findViewById(R.id.btnSave);
        mReceiver=new BatteryReceiver();

        updateUI(isAirplaneMode());

        ntwSwitch.setChecked(getFromSP("ntwSwitch", true));
        ntwSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    saveInSP("ntwSwitch", true);
                    WifiManager wifiOn = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    if (wifiOn != null) {
                        wifiOn.setWifiEnabled(true);
                        ntwSwitch.setChecked(true);
                    }

                } else {
                    saveInSP("ntwSwitch", false);
                    WifiManager wifiOff = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    if (wifiOff != null) {
                        wifiOff.setWifiEnabled(false);
                        ntwSwitch.setChecked(false);
                    }
                }
                generateNotification();
            }
        });

        final IntentFilter intentFilter = new IntentFilter(NetworkReceiver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);

                if (isNetworkAvailable) {
                    ntwSwitch.setChecked(getFromSP("ntwSwitch", true));
                    saveInSP("ntwSwitch", true);
                    Toast.makeText(context, "Network Status: Connected", Toast.LENGTH_LONG).show();
                } else {
                    ntwSwitch.setChecked(getFromSP("ntwSwitch", false));
                    saveInSP("ntwSwitch", false);
                    Toast.makeText(context, "Network Status: Disconnected", Toast.LENGTH_LONG).show();
                }
                generateNotification();
            }
        }, intentFilter);


        AirplaneModeReceiver airplaneModeReceiver = new AirplaneModeReceiver();
        IntentFilter apIntentFilter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(new AirplaneModeReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isAirplaneModeOn = intent.getBooleanExtra(IS_AIRPLANEMODE_ON, false);

                if (isAirplaneModeOn) {
                    apmSwitch.setChecked(getFromSP("apmSwitch", true));
                    saveInSP("apmSwitch", true);
                    Toast.makeText(context, "Airplane Mode is ON", Toast.LENGTH_LONG).show();
                } else {
                    apmSwitch.setChecked(getFromSP("apmSwitch", false));
                    saveInSP("apmSwitch", false);
                    Toast.makeText(context, "Airplane Mode is OFF", Toast.LENGTH_LONG).show();
                }
            }
        }, apIntentFilter);
        this.registerReceiver(airplaneModeReceiver, apIntentFilter);
//        airplaneModeReceiver = new AirplaneModeReceiver();
//        registerReceiver(airplaneModeReceiver, apIntentFilter);

        BatteryReceiver mReceiver = new BatteryReceiver();
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                String name = etName.getText().toString();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Name, name);
                editor.commit();
                Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String n = prefs.getString("nameKey", "");
        etName.setText(n);
    }

    @Override
    protected void onStart() {
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));
        super.onStart();
    }
    @Override
    protected void onStop() {
        unregisterReceiver(mReceiver);
        super.onStop();
    }

    private boolean getFromSP(String key, boolean b) {
        SharedPreferences preferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    private void saveInSP(String key, boolean value) {
        SharedPreferences preferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void generateNotification() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(),
                (int) System.currentTimeMillis(), intent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("Network Status")
                        .setContentText("Wi-Fi state changed")
                        .setContentIntent(pIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    public void updateUI(final boolean state) {
        // set switch according to state
        if (state) {
            Toast.makeText(MainActivity.this, "ON", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "OFF", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NewApi")
    public boolean isAirplaneMode() {
        return Settings.Global.getInt(getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

}
