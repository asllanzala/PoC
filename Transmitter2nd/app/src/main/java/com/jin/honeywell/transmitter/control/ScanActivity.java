package com.jin.honeywell.transmitter.control;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.jin.honeywell.transmitter.R;
import com.jin.honeywell.transmitter.manager.BLEManager;
import com.jin.honeywell.transmitter.manager.BLEScanManager;
import com.jin.honeywell.transmitter.manager.HPlusPermission;
import com.jin.honeywell.transmitter.manager.PermissionListener;
import com.jin.honeywell.transmitter.manager.SensorBleDataManager;
import com.jin.honeywell.transmitter.model.LightSensorStatus;
import com.jin.honeywell.transmitter.model.ScanningDeviceListAdapter;
import com.jin.honeywell.transmitter.view.LoadingProgressDialog;
import com.jin.honeywell.transmitter.view.QuitDialogFragment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class ScanActivity extends AppCompatActivity implements PermissionListener {

    private ImageView mScanRotateImageView;
    private ListView mScanListView;
    protected Dialog mDialog;

    protected HPlusPermission mHPlusPermission;
    private BLEScanManager mBleScanManager = BLEScanManager.getInstance();
    private BLEManager mBleManager = BLEManager.getInstance();
    private List<String> mBleNameList = new LinkedList<>();
    private ScanningDeviceListAdapter mScanDeviceListAdapter;
    private HashMap<String, String> mBleMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mHPlusPermission = new HPlusPermission(this);

        mBleManager.startBLEService(ScanActivity.this);
        SensorBleDataManager.getInstance().registerBleReceiver();

        mScanRotateImageView = (ImageView) findViewById(R.id.scan_rotate_iv);
        startRotateAnimation();

        mScanListView = (ListView) findViewById(R.id.scan_device_list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mBleManager.isBLEEnable()) {
            Toast.makeText(ScanActivity.this, "请先打开手机蓝牙功能", Toast.LENGTH_LONG).show();
        }

        mHPlusPermission.checkAndRequestPermission(HPlusPermission.LOCATION_SERVICE_REQUEST_CODE, this);

        mBleScanManager.startBleScan();
        initBleListView();
        updateScanningListView();
        updateConnectListener();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBleScanManager.stopBleScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        quitApp();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            QuitDialogFragment dialog = new QuitDialogFragment();
            dialog.show(getFragmentManager(), "QuitDialogFragment");
            dialog.setListener(new QuitDialogFragment.NoticeDialogListener() {
                @Override
                public void onDialogPositiveClick() {
                    quitApp();
                }
            });
        }
        return false;
    }

    private void initBleListView() {
        mBleNameList.clear();
        mScanDeviceListAdapter = new ScanningDeviceListAdapter(this, mBleNameList);
        mScanListView.setAdapter(mScanDeviceListAdapter);
        mScanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String address = mBleMap.get(mScanDeviceListAdapter.getItem(position));
                        BLEManager.getInstance().connectBle(address);

                        mDialog = LoadingProgressDialog.show(ScanActivity.this, "正在连接设备...");
                    }
                });
            }
        });
    }

    private void updateScanningListView() {

        mBleScanManager.setScanListener(new BLEScanManager.ScanListener() {
            @Override
            public void onScan(final String address, final String deviceName) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isBleNameValid(deviceName)) {
                            mBleMap.put(deviceName, address);
                            mBleNameList.add(deviceName);
                            mScanDeviceListAdapter.notifyDataSetChanged();
                        }
                    }
                });

            }
        });
    }

    private void updateConnectListener() {
        SensorBleDataManager.getInstance().setDeviceStatusListener(new SensorBleDataManager.StatusListener() {
            @Override
            public void onStatusChange(String macAddress, LightSensorStatus status) {
                if (mDialog != null) {
                    mDialog.cancel();
                }

                if (status == LightSensorStatus.CONNECT) {
                    Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                    intent.putExtra("macAddress", macAddress);
                    startActivity(intent);
                } else if (status == LightSensorStatus.DISCONNECT) {
                    Toast.makeText(ScanActivity.this, "连接设备失败", Toast.LENGTH_LONG).show();
                }
            }

        });
    }


    private void startRotateAnimation() {
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.enroll_rescan_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        mScanRotateImageView.startAnimation(operatingAnim);
    }

    private boolean isBleNameValid(String bleName) {

        if (bleName == null || bleName.isEmpty())
            return false;

        if (mBleNameList.contains(bleName))
            return false;

        return true;
    }

    private void quitApp() {
        BLEManager.getInstance().unregisterBleService(ScanActivity.this);
        SensorBleDataManager.getInstance().unregisterBleReceiver();

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    public void onPermissionGranted(int permissionCode) {

    }

    @Override
    public void onPermissionNotGranted(String[] permission, int permissionCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(permission, permissionCode);
        }
    }

    @Override
    public void onPermissionDenied(int permissionCode) {

    }
}
