package com.neo.shutdownscreen.service;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.neo.shutdownscreen.util.DeviceAdminSampleReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by neo on 2016/5/2.
 */
public class MyService extends Service implements View.OnTouchListener {
    private String TAG = this.getClass().getSimpleName();

    // window manager
    private WindowManager mWindowManager;
    // linear layout will use to detect touch event
    private LinearLayout touchLayout;

    private DevicePolicyManager mManager;
    private ComponentName mComponent;
    private long mOldTime = 0;
    private long mNewTime = 0;

    /**
     * 倒數計時
     */
    private CountDownTimer mCountDownTimer = new CountDownTimer(250, 250) {

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            mOldTime = 0;
            mNewTime = 0;
            Log.d(TAG, "old time : " + mOldTime + "\n new time : " + mNewTime);
            mCountDownTimer.cancel();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createPixel();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponent = new ComponentName(this, DeviceAdminSampleReceiver.class);
    }


    /**
     * 判斷是否為在桌面
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean isHome() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String mPackageName = "";
        // android系統是支持多任務的，下面的意思就是：找到所有正在運行的任務，
        /* * 找到正在運行的任務後，還得找出前台運行的任務，最前面的就是前台正在運行的任務 * RunningTaskInfo info = runningTask Infos.get(0); */
        // 如果當前獲取的桌面應用程序的Package名裡面，包含有當前正在前台運行的桌面應用的包名，則表示桌面顯示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//Android 6.0以上
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 1000 * 60;
            // We get usage stats for the last minute
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, endTime);
            // Sort the stats by the last time used
            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    mPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//Android 5.0以上
            mPackageName = mActivityManager.getRunningAppProcesses().get(0).processName;
        } else {//Android5.0以下
            mPackageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        }

        Log.d(TAG,"Top Activity :" + mPackageName);
        return getHomes().contains(mPackageName);
    }

    /**
     * 取得屬於桌面的app的package名稱
     *
     * @return 包含所有package名的字串列表 屬於桌面應用必須要滿竹以下兩個條件：
     * 1.action为android.intent.action.MAIN
     * 2.category包含android.intent.category.Home
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        Log.d(TAG, "Home Package " + names);
        return names;
    }

    /**
     * 創建 1 Pixel 視圖
     */
    private void createPixel() {
        // create linear layout
        touchLayout = new LinearLayout(this);
        // set layout width 30 px and height is equal to full screen
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(1, 1);
        touchLayout.setLayoutParams(lp);
        // set color if you want layout visible on screen
//		touchLayout.setBackgroundColor(Color.CYAN);
        // set on touch listener
        touchLayout.setOnTouchListener(this);
        // fetch window manager object
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // set layout parameter of window manager
//		 WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
//				 	30, // width of layout 30 px
//	        		WindowManager.LayoutParams.MATCH_PARENT, // height is equal to full screen
//	                WindowManager.LayoutParams.TYPE_PHONE, // Type Ohone, These are non-application windows providing user interaction with the phone (in particular incoming calls).
//	                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // this window won't ever get key input focus
//	                PixelFormat.TRANSLUCENT);
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
                1, /* width */
                1, /* height */
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT
        );
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        Log.i(TAG, "add View");

        mWindowManager.addView(touchLayout, mParams);
    }

    @Override
    public void onDestroy() {
        if (mWindowManager != null) {
            if (touchLayout != null) mWindowManager.removeView(touchLayout);
        }
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Log.i(TAG, "Action :" + event.getAction() + "\t X :" + event.getRawX() + "\t Y :" + event.getRawY());

        Rect rect = new Rect();
        v.getHitRect(rect);
        if (rect.contains((int) event.getX(), (int) event.getY())) {
            Log.d(TAG, "is Home ? " + isHome());
            if (isHome()) {
                if (mOldTime == 0) {
                    mOldTime = Calendar.getInstance().getTimeInMillis();

                    mCountDownTimer.start();

                    Log.d(TAG, "old time :" + mOldTime);
                } else {
                    mNewTime = Calendar.getInstance().getTimeInMillis();
                    Log.d(TAG, "new time :" + mNewTime);
                    if ((mNewTime - mOldTime) <= 250) {//1 SEC
                        Log.d(TAG, "Count time :" + (mNewTime - mOldTime));
                        Log.d(TAG, "Double");

                        if (mManager.isAdminActive(mComponent))
                            mManager.lockNow();

                        mCountDownTimer.cancel();

                        mOldTime = 0;
                        mNewTime = 0;
                    } else {
                        Log.d(TAG, "nothing :" + (mOldTime - mNewTime));
                        mOldTime = 0;
                        mNewTime = 0;
                    }
                }
            }
        }

//        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP)
//            Log.i(TAG, "Action :" + event.getAction() + "\t X :" + event.getRawX() + "\t Y :" + event.getRawY());
        return true;
    }
}
