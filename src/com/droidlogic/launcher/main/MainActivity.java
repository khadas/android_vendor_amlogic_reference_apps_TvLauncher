
package com.droidlogic.launcher.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.base.LeanbackActivity;
import com.droidlogic.launcher.util.Logger;

public class MainActivity extends LeanbackActivity {

    public static final String TAG = "MainLaunch";
    private static final String TV_USER_SETUP_COMPLETE = "tv_user_setup_complete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        skipUserSetup();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, MainFragment.newInstance())
                    .commit();
        }
    }

    //we have not user setup APK, force skip it , or we can't use home key
    private void skipUserSetup() {
        if (Settings.Secure.getInt(getContentResolver(), TV_USER_SETUP_COMPLETE, 0) == 0) {
            Logger.d(TAG, "force skip user setup");
            Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);
            Settings.Secure.putInt(getContentResolver(), TV_USER_SETUP_COMPLETE, 1);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
