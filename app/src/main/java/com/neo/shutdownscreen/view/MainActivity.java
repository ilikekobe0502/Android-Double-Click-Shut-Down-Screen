package com.neo.shutdownscreen.view;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.neo.shutdownscreen.R;
import com.neo.shutdownscreen.service.MyService;
import com.neo.shutdownscreen.util.DeviceAdminSampleReceiver;
import com.neo.shutdownscreen.util.PublicDialog;
import com.neo.shutdownscreen.util.Utility;
import com.splunk.mint.Mint;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private Switch mSwitchService;
    private Button mButtonAccess;
    private Button mButtonRate;
    private TextView mTextViewVersion;

    private DevicePolicyManager mManager;
    private ComponentName mComponent;

    private Intent service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setMINT(MainActivity.this);
        setContentView(R.layout.activity_main);
        //固定畫面直立
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = this.getSupportActionBar();

        mSwitchService = (Switch) findViewById(R.id.switch_service_on);
        mButtonAccess = (Button) findViewById(R.id.button_open_access);
        mButtonRate = (Button) findViewById(R.id.button_rate);
        mTextViewVersion = (TextView) findViewById(R.id.textView_version);
        mButtonRate.setOnClickListener(this);

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
            mSwitchService.setChecked(true);
        else
            mSwitchService.setChecked(false);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mButtonAccess.setVisibility(View.VISIBLE);
        }
        mButtonAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent usageAccessIntent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                usageAccessIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (usageAccessIntent.resolveActivity(getPackageManager()) != null)
                    startActivity(usageAccessIntent);
                else {
                    Toast.makeText(MainActivity.this, getString(R.string.toast_no_activity_exception), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSwitchService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!mManager.isAdminActive(mComponent)) {
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponent);
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "");
                        startActivity(intent);
                    } else {
                        //判斷Service 有無開啟
                        if (!isMyServiceRunning(MyService.class)) {
                            startService(service);
                            Log.i(TAG, "Service is start");
                            PublicDialog.showConfirmDialog(MainActivity.this, getString(R.string.main_dialog_title), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        }
                    }
                } else {
                    stopService(service);
                    Log.i(TAG, "Service is stop");
                }
            }
        });

        StringBuilder message = new StringBuilder();
        message.append(getString(R.string.dialog_device_version)).append(Utility.getSDKVersion()).append("\n").
                append(Utility.getAPPVersion(getApplicationContext()));
        mTextViewVersion.setText(message);
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

    /**
     * 宣告 action bar
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * action bar 事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                StringBuilder message = new StringBuilder();
                message.append(getString(R.string.dialog_device_version)).append(Utility.getSDKVersion()).append("\n").
                        append(getString(R.string.dialog_app_version)).append(" ").
                        append(Utility.getAPPVersion(getApplicationContext()));
                PublicDialog.showConfirmDialog(MainActivity.this, message.toString(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                return true;
            case R.id.action_rate:
                Utility.RateMyApp(MainActivity.this);
                return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_rate:
                Utility.RateMyApp(MainActivity.this);
                break;
        }
    }
}
