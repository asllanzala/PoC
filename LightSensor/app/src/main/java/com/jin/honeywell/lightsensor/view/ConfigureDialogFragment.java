package com.jin.honeywell.lightsensor.view;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jin.honeywell.lightsensor.R;
import com.jin.honeywell.lightsensor.model.LightSensorModel;
import com.jin.honeywell.lightsensor.model.SensorSharedPreference;
import com.jin.honeywell.lightsensor.util.MapScale;

import java.util.Map;

public class ConfigureDialogFragment extends DialogFragment {
    private EditText mX1et, mY1et, mX2et, mY2et, mX3et, mY3et, mN1et, mN2et, mN3et, mA1et, mA2et, mA3et;
    private int mX1, mY1, mX2, mY2, mX3, mY3, mA1, mA2, mA3;
    private float mN1, mN2, mN3;


    NoticeDialogListener mListener;

    public interface NoticeDialogListener {
        void onDialogPositiveClick(int x1, int y1, int x2, int y2, int x3, int y3,
                                   int a1, int a2, int a3, float n1, float n2, float n3);
        void onDialogErrorCallback();
    }

    public void setListener(NoticeDialogListener listener) {
        this.mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_configure, null);
        initView(view);

        builder.setView(view).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        getData();
                        mListener.onDialogPositiveClick(mX1, mY1, mX2, mY2, mX3, mY3, mA1, mA2, mA3, mN1, mN2, mN3);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ConfigureDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void initView(View view) {
        mX1et = (EditText) view.findViewById(R.id.sensor1x_et);
        mY1et = (EditText) view.findViewById(R.id.sensor1y_et);
        mX2et = (EditText) view.findViewById(R.id.sensor2x_et);
        mY2et = (EditText) view.findViewById(R.id.sensor2y_et);
        mX3et = (EditText) view.findViewById(R.id.sensor3x_et);
        mY3et = (EditText) view.findViewById(R.id.sensor3y_et);
        mA1et = (EditText) view.findViewById(R.id.formula_a1);
        mA2et = (EditText) view.findViewById(R.id.formula_a2);
        mA3et = (EditText) view.findViewById(R.id.formula_a3);
        mN1et = (EditText) view.findViewById(R.id.formula_n1);
        mN2et = (EditText) view.findViewById(R.id.formula_n2);
        mN3et = (EditText) view.findViewById(R.id.formula_n3);

        Map lightMap = SensorSharedPreference.getPositions();

        int px1 = ((Integer[]) lightMap.get(LightSensorModel.SENSOR_1_ADDRESS))[0];
        int py1 = ((Integer[]) lightMap.get(LightSensorModel.SENSOR_1_ADDRESS))[1];
        int px2 = ((Integer[]) lightMap.get(LightSensorModel.SENSOR_2_ADDRESS))[0];
        int py2 = ((Integer[]) lightMap.get(LightSensorModel.SENSOR_2_ADDRESS))[1];
        int px3 = ((Integer[]) lightMap.get(LightSensorModel.SENSOR_3_ADDRESS))[0];
        int py3 = ((Integer[]) lightMap.get(LightSensorModel.SENSOR_3_ADDRESS))[1];
        int a1 = SensorSharedPreference.getA1();
        int a2 = SensorSharedPreference.getA2();
        int a3 = SensorSharedPreference.getA3();
        float n1 = SensorSharedPreference.getN1();
        float n2 = SensorSharedPreference.getN2();
        float n3 = SensorSharedPreference.getN3();

        try {
            mX1et.setText(String.format("%s", MapScale.getPortionX(px1)));
            mY1et.setText(String.format("%s", MapScale.getPortionY(py1)));
            mX2et.setText(String.format("%s", MapScale.getPortionX(px2)));
            mY2et.setText(String.format("%s", MapScale.getPortionY(py2)));
            mX3et.setText(String.format("%s", MapScale.getPortionX(px3)));
            mY3et.setText(String.format("%s", MapScale.getPortionY(py3)));
            mA1et.setText(String.format("%s", a1));
            mA2et.setText(String.format("%s", a2));
            mA3et.setText(String.format("%s", a3));
            mN1et.setText(String.format("%s", n1));
            mN2et.setText(String.format("%s", n2));
            mN3et.setText(String.format("%s", n3));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getData() {
        try{
            mX1 = Integer.parseInt(mX1et.getText().toString());
            mY1 = Integer.parseInt(mY1et.getText().toString());
            mX2 = Integer.parseInt(mX2et.getText().toString());
            mY2 = Integer.parseInt(mY2et.getText().toString());
            mX3 = Integer.parseInt(mX3et.getText().toString());
            mY3 = Integer.parseInt(mY3et.getText().toString());
            mA1 = Integer.parseInt(mA1et.getText().toString());
            mA2 = Integer.parseInt(mA2et.getText().toString());
            mA3 = Integer.parseInt(mA3et.getText().toString());
            mN1 = Float.parseFloat(mN1et.getText().toString());
            mN2 = Float.parseFloat(mN2et.getText().toString());
            mN3 = Float.parseFloat(mN3et.getText().toString());
        } catch (Exception e){
            mListener.onDialogErrorCallback();
        }
    }

}