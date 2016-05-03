package com.neo.shutdownscreen;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.splunk.mint.Mint;


public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private Switch mSwitch_Service;

    private DevicePolicyManager mManager;
    private ComponentName mComponent;

    private Intent service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.initAndStartSession(MainActivity.this, "0b90fb73");
        setContentView(R.layout.activity_main);
        mSwitch_Service = (Switch) findViewById(R.id.switch_service_on);

        // Enable admin
        mManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponent = new ComponentName(this, DeviceAdminSampleReceiver.class);

        service = new Intent(this, MyService.class);


    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onResume() {
        super.onResume();
        if (isMyServiceRunning(MyService.class) && mManager.isAdminActive(mComponent))
            mSwitch_Service.setChecked(true);
        else
            mSwitch_Service.setChecked(false);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mSwitch_Service.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!mManager.isAdminActive(mComponent)) {
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponent);
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "");
                        startActivity(intent);
                    } else {
                        startService(service);
                        Log.i(TAG, "Service is start");

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.main_dialog_title)
                                .setPositiveButton(R.string.main_dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                    }
                } else {
                    stopService(service);
                    Log.i(TAG, "Service is stop");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 確認service有無執行
     *
     * @param serviceClass
     * @return
     */
    protected boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
