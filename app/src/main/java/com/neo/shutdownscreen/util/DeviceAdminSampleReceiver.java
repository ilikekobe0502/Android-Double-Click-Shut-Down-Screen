package com.neo.shutdownscreen.util;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by neo on 2016/5/2.
 */
public class DeviceAdminSampleReceiver extends DeviceAdminReceiver {

    void showToast(Context context, String msg) {
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return null;
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
    }

    @Override
    public void onPasswordExpiring(Context context, Intent intent) {
    }
}
