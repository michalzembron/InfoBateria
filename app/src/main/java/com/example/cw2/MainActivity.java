package com.example.cw2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import static android.os.BatteryManager.*;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.os.BatteryManager.EXTRA_HEALTH;
import static android.os.BatteryManager.EXTRA_LEVEL;
import static android.os.BatteryManager.EXTRA_STATUS;
import static android.os.BatteryManager.EXTRA_TECHNOLOGY;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver mBatteryChangedReceived;

    private ProgressBar progressBar_batteryChargeLevel;
    private TextView tv_batteryChargeLevel;
    private TextView tv_batteryCondition;
    private TextView tv_batterySource;
    private TextView tv_batteryAvailable;
    private TextView tv_batteryLevel;
    private TextView tv_batteryStatus;
    private TextView tv_batteryTechnology;
    private TextView tv_batteryTemp;
    private TextView tv_batteryVoltage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar_batteryChargeLevel = findViewById(R.id.progressBar_batteryChargeLevel);
        tv_batteryChargeLevel = findViewById(R.id.tv_batteryChargeLevel);
        tv_batteryCondition = findViewById(R.id.tv_batteryCondition);
        tv_batterySource = findViewById(R.id.tv_batterySource);
        tv_batteryAvailable = findViewById(R.id.tv_batteryAvailable);
        tv_batteryLevel = findViewById(R.id.tv_batteryLevel);
        tv_batteryStatus = findViewById(R.id.tv_batteryStatus);
        tv_batteryTechnology = findViewById(R.id.tv_batteryTechnology);
        tv_batteryTemp = findViewById(R.id.tv_batteryTemp);
        tv_batteryVoltage = findViewById(R.id.tv_batteryVoltage);

        showBatteryInfo();
    }

    private static String healthCodeToString(int health) {
        switch (health) {
            case BATTERY_HEALTH_COLD: return "Cold";
            case BATTERY_HEALTH_DEAD: return "Dead";
            case BATTERY_HEALTH_GOOD: return "Good";
            case BATTERY_HEALTH_OVERHEAT: return "Overheat";
            case BATTERY_HEALTH_OVER_VOLTAGE: return "Over voltage";
            case BATTERY_HEALTH_UNSPECIFIED_FAILURE: return "Unspecified failure";
            case BATTERY_HEALTH_UNKNOWN:
            default: return "Unknown";
        }
    }

    private static String pluggedCodeToString(int plugged) {
        switch (plugged) {
            case 0: return "Battery";
            case BATTERY_PLUGGED_AC: return "AC";
            case BATTERY_PLUGGED_USB: return "USB";
            default: return "Unknown";
        }
    }

    private static String statusCodeToString(int status) {
        switch (status) {
            case BATTERY_STATUS_CHARGING: return "Charging";
            case BATTERY_STATUS_DISCHARGING: return "Discharging";
            case BATTERY_STATUS_FULL: return "Full";
            case BATTERY_STATUS_NOT_CHARGING: return "Not charging";
            case BATTERY_STATUS_UNKNOWN:
            default: return "Unknown";
        }
    }

    private void enableBatteryReceiver(boolean enabled) {
        PackageManager pm = getPackageManager();
        ComponentName receiverName = new ComponentName(this, BatteryReceiver.class);
        int newState;
        if (enabled) {
            newState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        } else {
            newState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        }
        pm.setComponentEnabledSetting(receiverName, newState, PackageManager.DONT_KILL_APP);
    }

    private void showBatteryInfo(Intent intent){
        if (intent != null){
            progressBar_batteryChargeLevel.setProgress(intent.getIntExtra(EXTRA_LEVEL, 0));
            tv_batteryChargeLevel.setText(String.valueOf(intent.getIntExtra(EXTRA_LEVEL, 0)));
            tv_batteryCondition.setText(healthCodeToString(intent.getIntExtra(EXTRA_HEALTH, BATTERY_HEALTH_UNKNOWN)));
            tv_batterySource.setText(pluggedCodeToString(intent.getIntExtra(EXTRA_PLUGGED, 0)));
            tv_batteryAvailable.setText(intent.getBooleanExtra(EXTRA_PRESENT, false) ? "Tak" : "Nie");
            tv_batteryLevel.setText(String.valueOf(intent.getIntExtra(EXTRA_SCALE, 100)));
            tv_batteryTemp.setText(String.valueOf(intent.getIntExtra(EXTRA_STATUS, Integer.MIN_VALUE)));
            tv_batteryStatus.setText(statusCodeToString(intent.getIntExtra(EXTRA_STATUS, BATTERY_STATUS_UNKNOWN)));
            tv_batteryTechnology.setText(String.valueOf(intent.getStringExtra(EXTRA_TECHNOLOGY)));
            tv_batteryVoltage.setText(String.valueOf(intent.getIntExtra(EXTRA_VOLTAGE, Integer.MIN_VALUE)));
        }
    }

    private void showBatteryInfo() {
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        showBatteryInfo(intent);
    }

    private void createBatteryReceiver() {
        mBatteryChangedReceived = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showBatteryInfo(intent);
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBatteryChangedReceived);
        enableBatteryReceiver(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBatteryChangedReceived == null){
            createBatteryReceiver();
        }
        registerReceiver(mBatteryChangedReceived, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        enableBatteryReceiver(true);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        unregisterReceiver(mBatteryChangedReceived);
        mBatteryChangedReceived = null;
    }
}

