package com.jin.honeywell.lightsensor.control;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.jin.honeywell.lightsensor.R;
import com.jin.honeywell.lightsensor.manager.BLEManager;
import com.jin.honeywell.lightsensor.manager.BLEScanManager;
import com.jin.honeywell.lightsensor.manager.SensorBleDataManager;
import com.jin.honeywell.lightsensor.manager.UIManager;
import com.jin.honeywell.lightsensor.model.LightSensorModel;
import com.jin.honeywell.lightsensor.model.LightSensorStatus;
import com.jin.honeywell.lightsensor.model.SensorSharedPreference;
import com.jin.honeywell.lightsensor.util.DensityUtil;
import com.jin.honeywell.lightsensor.util.Logger.Log;
import com.jin.honeywell.lightsensor.util.Logger.LogFragment;
import com.jin.honeywell.lightsensor.util.Logger.LogWrapper;
import com.jin.honeywell.lightsensor.util.Logger.MessageOnlyLogFilter;
import com.jin.honeywell.lightsensor.util.MapScale;
import com.jin.honeywell.lightsensor.util.StatusBarUtils;
import com.jin.honeywell.lightsensor.view.ConfigureDialogFragment;
import com.jin.honeywell.lightsensor.view.QuitDialogFragment;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LightSensor";

    private RelativeLayout mMapLayout;
    private ImageView mPositionImageView;
    private ImageView mLed1ImageView;
    private ImageView mLed2ImageView;
    private ImageView mLed3ImageView;

    private UIManager mUIManager = UIManager.getInstance();
    private BLEScanManager mBLEScanManager = BLEScanManager.getInstance();

    private boolean mLogShown = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapScale.setMapWidth(DensityUtil.getScreenWidth());
        MapScale.seMapHeight((int) (DensityUtil.getScreenWidth() * MapScale.MAP_HEIGHT / MapScale.MAP_WIDTH));

        startBleService();

        mUIManager.initSensor();
//        mUIManager.startAutoConnectThread();
//        mUIManager.disconnectAllDevices();

        initView();

        initLog();

    }

    @Override
    protected void onResume() {
        super.onResume();

        mBLEScanManager.startBleScan();
        mBLEScanManager.setScanListener(new BLEScanManager.ScanListener() {
            @Override
            public void onScan(String macId, int rssi) {
                if (LightSensorModel.isSensor1(macId)) {
                    mLed1ImageView.setBackgroundResource(R.mipmap.led_on);
                    mUIManager.calculateCoordinate(macId, rssi);
                } else if  (LightSensorModel.isSensor2(macId)) {
                    mLed2ImageView.setBackgroundResource(R.mipmap.led_on);
                    mUIManager.calculateCoordinate(macId, rssi);
                } else if  (LightSensorModel.isSensor3(macId)) {
                    mLed3ImageView.setBackgroundResource(R.mipmap.led_on);
                    mUIManager.calculateCoordinate(macId, rssi);
                }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBLEScanManager.stopBleScan();
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_configure:
                ConfigureDialogFragment configureDialog = new ConfigureDialogFragment();
                configureDialog.show(getFragmentManager(), "ConfigureDialogFragment");
                configureDialog.setListener(new ConfigureDialogFragment.NoticeDialogListener() {
                    @Override
                    public void onDialogPositiveClick(int x1, int y1, int x2, int y2, int x3, int y3,
                                                      int a1, int a2, int a3, float n1, float n2, float n3) {
                        SensorSharedPreference.savePositions(x1, y1, x2, y2, x3, y3);
                        SensorSharedPreference.setA1(a1);
                        SensorSharedPreference.setA2(a2);
                        SensorSharedPreference.setA3(a3);
                        SensorSharedPreference.setN1(n1);
                        SensorSharedPreference.setN2(n2);
                        SensorSharedPreference.setN3(n3);
                    }

                    @Override
                    public void onDialogErrorCallback() {
                        Toast.makeText(getApplicationContext(), getString(R.string.format_error),
                                Toast.LENGTH_LONG).show();
                    }
                });
                return true;

//            case R.id.action_disconnect:
//                DialogFragment disconnectDialog = new DisconnectDialogFragment();
//                disconnectDialog.show(getFragmentManager(), "DisconnectDialogFragment");
//                return true;

            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
                if (mLogShown) {
                    output.setVisibility(View.VISIBLE);
                } else {
                    output.setVisibility(View.INVISIBLE);
                }
                supportInvalidateOptionsMenu();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        logToggle.setTitle(mLogShown ? R.string.hide_log : R.string.show_log);

        return super.onPrepareOptionsMenu(menu);
    }

    private void initView() {
        StatusBarUtils.setWindowStatusBarColor(this, R.color.colorPrimaryDark);

        mMapLayout = (RelativeLayout) findViewById(R.id.map_rl);
        mPositionImageView = (ImageView) findViewById(R.id.location_iv);
        mLed1ImageView = (ImageView) findViewById(R.id.led1_iv);
        mLed2ImageView = (ImageView) findViewById(R.id.led2_iv);
        mLed3ImageView = (ImageView) findViewById(R.id.led3_iv);

        mMapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {

                RelativeLayout.LayoutParams layoutParams
                        = new RelativeLayout.LayoutParams(mMapLayout.getLayoutParams());

                layoutParams.width = DensityUtil.getScreenWidth();
                layoutParams.height = (int) (DensityUtil.getScreenWidth() * MapScale.MAP_HEIGHT / MapScale.MAP_WIDTH);

                mMapLayout.setLayoutParams(layoutParams);
            }
        });

        SensorBleDataManager.getInstance().setDeviceStatusListener(new SensorBleDataManager.DeviceStatusListener() {
            @Override
            public void onChange(String macAddress, LightSensorStatus status) {
                if (status == LightSensorStatus.CONNECT) {
                    if (LightSensorModel.isSensor1(macAddress)) {
                        mLed1ImageView.setBackgroundResource(R.mipmap.led_on);
                    } else if (LightSensorModel.isSensor2(macAddress)) {
                        mLed2ImageView.setBackgroundResource(R.mipmap.led_on);
                    } else if (LightSensorModel.isSensor3(macAddress)) {
                        mLed3ImageView.setBackgroundResource(R.mipmap.led_on);
                    }
                } else if (status == LightSensorStatus.DISCONNECT) {
                    if (LightSensorModel.isSensor1(macAddress)) {
                        mLed1ImageView.setBackgroundResource(R.mipmap.led_off);
                    } else if (LightSensorModel.isSensor2(macAddress)) {
                        mLed2ImageView.setBackgroundResource(R.mipmap.led_off);
                    } else if (LightSensorModel.isSensor3(macAddress)) {
                        mLed3ImageView.setBackgroundResource(R.mipmap.led_off);
                    }
                }
            }
        });

        mUIManager.setLocationListener(new UIManager.LocationListener() {
            @Override
            public void onUpdate(int px, int py) {
                mPositionImageView.setX(px);
                mPositionImageView.setY(py);
            }
        });

        setupSensorPosition();
    }

    private void startBleService() {
        BLEManager.getInstance().startBLEService(MainActivity.this);
        SensorBleDataManager.getInstance().registerBleReceiver();
    }


    private void setupSensorPosition() {

        mLed1ImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                LightSensorModel device = SensorSharedPreference.getDevice(LightSensorModel.SENSOR_1_ADDRESS);
                if (device != null && device.getPosition() != null) {
                    mLed1ImageView.setX(device.getPosition()[0]);
                    mLed1ImageView.setY(device.getPosition()[1]);
                    mUIManager.setLed1X(mLed1ImageView.getX());
                    mUIManager.setLed1Y(mLed1ImageView.getY());
                }
            }
        });

        mLed2ImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                LightSensorModel device = SensorSharedPreference.getDevice(LightSensorModel.SENSOR_2_ADDRESS);
                if (device != null && device.getPosition() != null) {
                    mLed2ImageView.setX(device.getPosition()[0]);
                    mLed2ImageView.setY(device.getPosition()[1]);
                    mUIManager.setLed2X(mLed2ImageView.getX());
                    mUIManager.setLed2Y(mLed2ImageView.getY());
                }
            }
        });

        mLed3ImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                LightSensorModel device = SensorSharedPreference.getDevice(LightSensorModel.SENSOR_3_ADDRESS);
                if (device != null && device.getPosition() != null) {
                    mLed3ImageView.setX(device.getPosition()[0]);
                    mLed3ImageView.setY(device.getPosition()[1]);
                    mUIManager.setLed3X(mLed3ImageView.getX());
                    mUIManager.setLed3Y(mLed3ImageView.getY());
                }
            }
        });

    }

    private void initLog() {

        ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
        output.setDisplayedChild(0);

        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);

        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());

    }

    private void quitApp() {
//        mUIManager.disconnectAllDevices();

        BLEManager.getInstance().unregisterBleService(MainActivity.this);
        SensorBleDataManager.getInstance().unregisterBleReceiver();

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}
