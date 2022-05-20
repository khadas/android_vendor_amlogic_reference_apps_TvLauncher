package com.droidlogic.launcher.util;

import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.TextUtils;

import java.io.File;
import java.util.List;

public class StorageManagerUtil {

    private final StorageManager storageManager;
    private Listener listener;

    public StorageManagerUtil(StorageManager storageManager, Listener listener) {
        this.storageManager = storageManager;
        this.listener = listener;
    }

    private boolean isInteresting(int state) {
        return state == VolumeInfo.STATE_UNMOUNTED || state == VolumeInfo.STATE_MOUNTED || state == VolumeInfo.STATE_EJECTING;
    }

    private void callback(String deviceName, int state) {
        if (TextUtils.isEmpty(deviceName) || listener == null) return;
        boolean mountState = state == VolumeInfo.STATE_MOUNTED;
        if (deviceName.equals("sdcard0") || deviceName.equals("sdcard1") || deviceName.equals("0")) {
            listener.onTFCardMountState(mountState);
        } else {
            listener.onUsbDeviceMountState(mountState);
        }
    }

    private void storageCheck(VolumeInfo volumeInfo) {
        int state = volumeInfo.getState();
        if (volumeInfo.getType() == VolumeInfo.TYPE_PUBLIC && isInteresting(state)) {
            File file = volumeInfo.getPathForUser(0);
            if (file != null) {
                callback(file.getName(), state);
            }
        }
    }

    private void refresh() {
        List<VolumeInfo> volumeInfoList = storageManager.getVolumes();
        if (volumeInfoList == null) return;
        for (VolumeInfo volumeInfo : volumeInfoList)
            storageCheck(volumeInfo);
    }

    private final StorageEventListener storageEventListener = new StorageEventListener() {
        @Override
        public void onVolumeStateChanged(VolumeInfo volumeInfo, int oldState, int newState) {
            super.onVolumeStateChanged(volumeInfo, oldState, newState);
            if ((isInteresting(volumeInfo.getState()))) {
                storageCheck(volumeInfo);
            }
        }
    };

    public void registerListener() {
        storageManager.registerListener(storageEventListener);
        refresh();
    }

    public void unRegisterListener() {
        storageManager.unregisterListener(storageEventListener);
    }

    public interface Listener {
        void onTFCardMountState(boolean isMount);

        void onUsbDeviceMountState(boolean isMount);
    }

}
