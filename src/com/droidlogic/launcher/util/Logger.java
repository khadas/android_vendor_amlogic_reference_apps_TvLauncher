package com.droidlogic.launcher.util;

import android.util.Log;

public class Logger {

    private static final boolean DEBUG = true;

    private static final String TAG = "TvLauncher";

    public static void i(String TAG, String msg) {
        if (DEBUG) Log.i(TAG, msg);
    }

    public static void i(String msg) {
        if (DEBUG) Log.i(TAG, msg);
    }

    public static void d(String TAG, String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }

    public static void d(String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }

    public static void w(String TAG, String msg) {
        if (DEBUG) Log.w(TAG, msg);
    }

    public static void w(String TAG, String msg, Throwable tr) {
        if (DEBUG) Log.w(TAG, msg, tr);
    }

    public static void w(String msg) {
        if (DEBUG) Log.w(TAG, msg);
    }

    public static void e(String msg) {
        if (DEBUG) Log.e(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        if (DEBUG) Log.e(TAG, msg);
    }

    public static void e(String TAG, String msg, Throwable tr) {
        if (DEBUG) Log.e(TAG, msg, tr);
    }

}
