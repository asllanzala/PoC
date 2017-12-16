package com.jin.honeywell.transmitter.manager;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Jin on 14/08/2017.
 */

public class HPlusPermission {

    public static final String LOCATION_SERVICE_FINE = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String LOCATION_SERVICE_CORSE = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String BLUETOOTH = Manifest.permission.BLUETOOTH;

    public static final int LOCATION_SERVICE_REQUEST_CODE = 3;
    public static final int BLUETOOTH_REQUEST_CODE = 4;


    private PermissionListener mPermissionListener;

    public HPlusPermission(PermissionListener permissionListener) {
        this.mPermissionListener = permissionListener;
    }

    public HPlusPermission() {
    }

    private int checkPermission(Activity thisActivity, String permission) {
        int permissionCheck = ContextCompat.checkSelfPermission(thisActivity,
                permission);
        return permissionCheck;
    }

    public void requestLocationPermission(Activity permissionActivity) {
        int finePermission = checkPermission(permissionActivity, LOCATION_SERVICE_FINE);
        int corsePermission = checkPermission(permissionActivity, LOCATION_SERVICE_CORSE);
        boolean isPermissionDialogAlreadyShown = ActivityCompat.shouldShowRequestPermissionRationale(permissionActivity, LOCATION_SERVICE_FINE);
        boolean isPermissionDialogAlreadyShown2 = ActivityCompat.shouldShowRequestPermissionRationale(permissionActivity, LOCATION_SERVICE_CORSE);

        if (finePermission == PackageManager.PERMISSION_GRANTED && corsePermission == PackageManager.PERMISSION_GRANTED) {

            mPermissionListener.onPermissionGranted(LOCATION_SERVICE_REQUEST_CODE);
        } else {
            mPermissionListener.onPermissionNotGranted(new String[]{LOCATION_SERVICE_FINE, LOCATION_SERVICE_CORSE}, LOCATION_SERVICE_REQUEST_CODE);

        }

        if ((finePermission == PackageManager.PERMISSION_DENIED && !isPermissionDialogAlreadyShown) ||
                (corsePermission == PackageManager.PERMISSION_DENIED && !isPermissionDialogAlreadyShown2)) {
            mPermissionListener.onPermissionDenied(LOCATION_SERVICE_REQUEST_CODE);
        }

    }

    public void requestBluetoothPermission(Activity permissionActivity) {
        int permission = checkPermission(permissionActivity, BLUETOOTH);
        boolean isPermissionDialogAlreadyShown = ActivityCompat.shouldShowRequestPermissionRationale(permissionActivity, BLUETOOTH);
        if (PackageManager.PERMISSION_GRANTED == permission) {
            mPermissionListener.onPermissionGranted(BLUETOOTH_REQUEST_CODE);

        } else {
            mPermissionListener.onPermissionNotGranted(new String[]{BLUETOOTH}, BLUETOOTH_REQUEST_CODE);
        }

        if (permission == PackageManager.PERMISSION_DENIED && !isPermissionDialogAlreadyShown) {
            mPermissionListener.onPermissionDenied(BLUETOOTH_REQUEST_CODE);
        }
    }

    public void checkAndRequestPermission(int permissionCodes, Activity permissionActivity) {
        switch (permissionCodes) {
            case LOCATION_SERVICE_REQUEST_CODE:
                requestLocationPermission(permissionActivity);
                break;

            case BLUETOOTH_REQUEST_CODE:
                requestBluetoothPermission(permissionActivity);
                break;

        }

    }


}
