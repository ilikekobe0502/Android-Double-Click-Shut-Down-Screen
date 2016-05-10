package com.neo.shutdownscreen.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import com.neo.shutdownscreen.BuildConfig;
import com.neo.shutdownscreen.R;
import com.splunk.mint.Mint;

/**
 * Created by neo on 2016/5/4.
 */
public class Utility {
    private final static int TAG_SDK_VERSION = Build.VERSION.SDK_INT;

    /**
     * 設置MINT
     * @param context
     */
    public static void setMINT(Context context) {
        if (!BuildConfig.DEBUG)
            Mint.initAndStartSession(context, "0b90fb73");
    }

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
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            data.append(context.getString(R.string.dialog_app_version)).append(versionName);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return data;
    }

    /**
     * 導到自己google store 的網址
     */
    public static void RateMyApp(Context context) {

        // 建立一個Intent - 在這個Intent 上使用 Google Play Store 的連結
        // E.G. market://details?id=
        // 之後用 getPackageName 這個功能來取後這個程式的 Namespace.
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName()));

        try {
            // 之後開始一個新的Activity 去這個Intent
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            // 如果有錯誤的話 使用正常的網址來連接到 Google Play Store的網頁
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }
}
