package com.neo.shutdownscreen.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.neo.shutdownscreen.R;

/**
 * Created by neo on 2016/5/4.
 */
public class Utility {
    private final static int TAG_SDK_VERSION = Build.VERSION.SDK_INT;

    /**
     * 取得裝置的SDK版本
     *
     * @return
     */
    public static int getSDKVersion() {
        return TAG_SDK_VERSION;
    }

    /**
     * 取得APP的版本
     *
     * @param context
     * @return
     */
    public static StringBuilder getAPPVersion(Context context) {
        StringBuilder data = new StringBuilder();
        String versionName;
        int versionCode;
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;

            data.append(context.getString(R.string.dialog_app_version_name)).append(versionName).append(" ").
                    append(context.getString(R.string.dialog_app_version_code)).append(versionCode);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return data;
    }
}
