package com.jin.honeywell.transmitter.control;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewAnimator;

import com.jin.honeywell.transmitter.R;
import com.jin.honeywell.transmitter.manager.BLEManager;
import com.jin.honeywell.transmitter.manager.SensorBleDataManager;
import com.jin.honeywell.transmitter.model.Diagnose;
import com.jin.honeywell.transmitter.model.LightSensorModel;
import com.jin.honeywell.transmitter.model.LightSensorStatus;
import com.jin.honeywell.transmitter.model.SensorUnit;
import com.jin.honeywell.transmitter.util.ByteUtil;
import com.jin.honeywell.transmitter.util.StatusBarUtils;
import com.jin.honeywell.transmitter.view.LoadingProgressDialog;
import com.jin.honeywell.transmitter.view.SimpleDialogFragment;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LightSensor";

    private Spinner mUnitSpinner;
    private TextView mPvValueTextView;

    private EditText mPvMaxEditText;
    private EditText mPvMinEditText;
    private TextView mPvMaxUnitTextView;
    private TextView mPvMinUnitTextView;

    private EditText mDiyMaxEditText;
    private EditText mDiyMinEditText;
    private TextView mDiyMaxUnitTextView;
    private TextView mDiyMinUnitTextView;

    private Button mSetZeroButton;
    private Button mChangeLimitButton;
    private Button mChangeDiyButton;

    private EditText mCircuitEditText;
    private ToggleButton mCircuitButton;
    private TextView mSerialTextView;
    private EditText mTagEditText;
    private Button mTagButton;
    private Button mDiagnoseButton;

    protected Dialog mDialog;

    private BLEManager mBleManager = BLEManager.getInstance();
    private SensorBleDataManager mDataManager = SensorBleDataManager.getInstance();
    private String mMacAddress;

    private String[] mUnits;
    private boolean mLogShown = true;
    private int mCurrentUnit = 0;
    private int mDialogNeedToCancelCount = 0;
    private String mDiagnoseMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMacAddress = getIntent().getStringExtra("macAddress");
        mUnits = getResources().getStringArray(R.array.unit_array);

        mDialog = LoadingProgressDialog.show(MainActivity.this, "正在获取数据...");

        initView();

        startReadPvPollingThread();

        SensorBleDataManager.getInstance().setDeviceStatusListener(new SensorBleDataManager.StatusListener() {
            @Override
            public void onStatusChange(String macAddress, LightSensorStatus status) {
                if (status == LightSensorStatus.DISCONNECT) {
                    finish();
                }
            }

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBleManager.disconnectBle(mMacAddress);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_version:
                showSimpleDialog("蓝牙变送器V1.0  20170818");
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

        mPvValueTextView = (TextView) findViewById(R.id.pv_value_tv);
        mSetZeroButton = (Button) findViewById(R.id.set_zero_btn);
        mChangeLimitButton = (Button) findViewById(R.id.change_limit_btn);
        mChangeDiyButton = (Button) findViewById(R.id.change_diy_btn);
        mPvMaxEditText = (EditText) findViewById(R.id.pv_max_et);
        mPvMinEditText = (EditText) findViewById(R.id.pv_min_et);
        mPvMaxUnitTextView = (TextView) findViewById(R.id.pv_max_unit);
        mPvMinUnitTextView = (TextView) findViewById(R.id.pv_min_unit);
        mDiyMaxEditText = (EditText) findViewById(R.id.diy_max_et);
        mDiyMinEditText = (EditText) findViewById(R.id.diy_min_et);
        mDiyMaxUnitTextView = (TextView) findViewById(R.id.diy_max_unit_tv);
        mDiyMinUnitTextView = (TextView) findViewById(R.id.diy_min_unit_tv);
        mCircuitEditText = (EditText) findViewById(R.id.mA_et);
        mCircuitButton = (ToggleButton) findViewById(R.id.circuit_toggle);
        mSerialTextView = (TextView) findViewById(R.id.serial_tv);
        mTagEditText = (EditText) findViewById(R.id.tag_et);
        mTagButton = (Button) findViewById(R.id.write_tag_btn);
        mDiagnoseButton = (Button) findViewById(R.id.read_diagnose_btn);

        mSetZeroButton.setOnClickListener(this);
        mChangeLimitButton.setOnClickListener(this);
        mChangeDiyButton.setOnClickListener(this);
        mTagButton.setOnClickListener(this);
        mDiagnoseButton.setOnClickListener(this);

        // Circuit test toggle
        mCircuitButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startCircuit();
                } else {
                    startCircuit();
                }
            }
        });

        // Unit spinner
        mUnitSpinner = (Spinner) findViewById(R.id.pv_unit_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.unit_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUnitSpinner.setAdapter(adapter);
        mUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentUnit != position) {
                    mCurrentUnit = position;

                    changeUnit(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        SensorBleDataManager.getInstance().setDeviceStatusListener(new SensorBleDataManager.StatusListener() {
            @Override
            public void onStatusChange(String macAddress, LightSensorStatus status) {
                if (status == LightSensorStatus.CONNECT) {
                    mMacAddress = macAddress;
                } else if (status == LightSensorStatus.DISCONNECT) {

                }
            }
        });

        SensorBleDataManager.getInstance().setDeviceDataListener(new SensorBleDataManager.DataListener() {

            @Override
            public void onSerial(String macAddress, String serial) {
                mSerialTextView.setText(serial);

                readTag();
            }

            @Override
            public void onUnit(String macAddress) {
                readLimit();
            }

            @Override
            public void onTag(String macAddress, String tag) {
                mTagEditText.setText(tag);

                readLimit();
            }

            @Override
            public void onPv(String macAddress, final String pv, final int unit) {
                mCurrentUnit = unit;
                mUnitSpinner.setSelection(unit);
                mPvValueTextView.setText(pv);
            }

            @Override
            public void onLimit(String macAddress, String max, String min, int unit) {
                mPvMaxEditText.setText(max);
                mPvMinEditText.setText(min);
                mPvMaxUnitTextView.setText(mUnits[unit]);
                mPvMinUnitTextView.setText(mUnits[unit]);
            }

            @Override
            public void onDiy(String macAddress, String max, String min) {
                mDiyMaxEditText.setText(min);
                mDiyMinEditText.setText(max);
                mDiyMaxUnitTextView.setText(mUnits[mCurrentUnit]);
                mDiyMinUnitTextView.setText(mUnits[mCurrentUnit]);
            }

            @Override
            public void onDiagnose0(String macAddress, byte[] response) {
                mDiagnoseMessage = Diagnose.parseMessage(response);
                mDataManager.sendCommand(mMacAddress, LightSensorModel.READ_DIAGNOSE1, null, (byte) 0);
            }

            @Override
            public void onDiagnose1(String macAddress, byte[] response) {
                mDiagnoseMessage += Arrays.toString(response) + "\n";
                mDataManager.sendCommand(mMacAddress, LightSensorModel.READ_DIAGNOSE2, null, (byte) 0);
            }

            @Override
            public void onDiagnose2(String macAddress, byte[] response) {
                mDiagnoseMessage += Arrays.toString(response);
                showSimpleDialog("诊断信息：\n\n" + mDiagnoseMessage);
                mDiagnoseMessage = "";
            }

            @Override
            public void onSuccess(String macAddress) {
                showSimpleDialog("设置成功");
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_zero_btn:
                setZero();
                break;
            case R.id.change_limit_btn:
                changeLimit();
                break;
            case R.id.change_diy_btn:
                changeDiy();
                break;
            case R.id.read_diagnose_btn:
                readDiagnose();
                break;
            case R.id.write_tag_btn:
                if (mTagEditText.getText().toString().length() != 6) {
                    showSimpleDialog("位号的长度必须是6位");
                } else {
                    writeTag();
                }
                break;

        }
    }

    private void readPv() {
        mDataManager.sendCommand(mMacAddress, LightSensorModel.READ_PV, null, (byte) 0);
    }

    private void setZero() {
        showLoadingDialog("开始调零...");
        mDataManager.sendCommand(mMacAddress, LightSensorModel.SET_ZERO, null, (byte) 0);
    }

    private void readLimit() {
        mDataManager.sendCommand(mMacAddress, LightSensorModel.READ_LIMIT, null, (byte) 0);
    }

    private void readDiyLimit() {
        mDataManager.sendCommand(mMacAddress, LightSensorModel.READ_DIY,
                new byte[]{(byte) 0xfd, 0x02}, (byte) 2);
    }

    private void readTag() {
        mDataManager.sendCommand(mMacAddress, LightSensorModel.READ_TAG, null, (byte) 0);
    }

    private void startCircuit() {
        try {
            int mA = Integer.parseInt(mCircuitEditText.getText().toString());

            showLoadingDialog("正在设置...");
            mDataManager.sendCommand(mMacAddress, LightSensorModel.START_CIRCUIT,
                    ByteUtil.get4BytesFromInt(mA), (byte) 4);

        } catch (Exception e) {
            showSimpleDialog("格式错误");
            e.printStackTrace();
        }
    }

    private void changeUnit(int position) {
        mDataManager.sendCommand(mMacAddress, LightSensorModel.CHANGE_UNIT,
                new byte[]{SensorUnit.getUnitFromInt(position)}, (byte) 1);
    }

    private void changeLimit() {

        byte[] limit = new byte[9];
        limit[0] = SensorUnit.getUnitFromInt(mCurrentUnit);

        try {
            float max = Float.parseFloat(mPvMaxEditText.getText().toString());
            float min = Float.parseFloat(mPvMinEditText.getText().toString());
            System.arraycopy(ByteUtil.get4BytesFromFloat(max), 0, limit, 1, 4);
            System.arraycopy(ByteUtil.get4BytesFromFloat(min), 0, limit, 5, 4);

            showLoadingDialog("正在设置...");
            mDataManager.sendCommand(mMacAddress, LightSensorModel.CHANGE_LIMIT, limit, (byte) 9);
        } catch (Exception e) {
            showSimpleDialog("格式错误");
            e.printStackTrace();
        }
    }

    private void changeDiy() {
        showSimpleDialog("暂不支持该功能");
    }

    private void writeTag() {
        showLoadingDialog("正在设置...");

        byte[] tag = ByteUtil.getBytesFromString(mTagEditText.getText().toString());
        mDataManager.sendCommand(mMacAddress, LightSensorModel.CHANGE_TAG, tag, (byte) 21);
    }

    private void readDiagnose() {
        showLoadingDialog("正在读取信息...");
        mDataManager.sendCommand(mMacAddress, LightSensorModel.READ_DIAGNOSE0, null, (byte) 0);
    }

    private void showLoadingDialog(String message) {
        mDialogNeedToCancelCount = 2;

        mDialog = LoadingProgressDialog.show(MainActivity.this, message);
    }

    private void showSimpleDialog(String message) {
        if (mDialog != null) {
            mDialogNeedToCancelCount = 0;
            mDialog.cancel();
        }

        SimpleDialogFragment dialog = new SimpleDialogFragment();
        dialog.setMessage(message);
        dialog.show(getFragmentManager(), "SimpleDialogFragment");
    }

    private void startReadPvPollingThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    readPv();

                    if (mDialogNeedToCancelCount == 0) {
                        if (mDialog != null)
                            mDialog.cancel();
                    }

                    if (mDialogNeedToCancelCount > 0) {
                        mDialogNeedToCancelCount--;
                    }
                }

            }
        }).start();
    }

}
