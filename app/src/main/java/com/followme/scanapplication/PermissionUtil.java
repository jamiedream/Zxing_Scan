/*
 * Copyright (c) 2019.
 * Create by J.Y Yen 29/ 4/ 2019.
 */

package com.followme.scanapplication;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.lang.ref.WeakReference;

public class PermissionUtil {

    public static final int PERMISSIONS_REQUEST_WRITE = 100;
    public static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 101;

    public boolean checkPermossion(WeakReference<Activity> activityWeakReference, String permission, int greaterVersion, int requestCode){

        return checkDangerPermission(activityWeakReference, permission, greaterVersion, requestCode);

    }

    /**
     * @param greaterVersion: not necessary, default 0
     *
     * */
    private boolean checkDangerPermission(WeakReference<Activity> activityWeakReference, String permission, int greaterVersion, int requestCode) {

        int currentAPIVersion = Build.VERSION.SDK_INT;

        if (currentAPIVersion >= greaterVersion) {

            if (ContextCompat.checkSelfPermission(activityWeakReference.get(), permission) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(activityWeakReference.get(), permission)) {

                } else {
                    ActivityCompat
                            .requestPermissions(activityWeakReference.get(), new String[] { permission }, requestCode);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

}
