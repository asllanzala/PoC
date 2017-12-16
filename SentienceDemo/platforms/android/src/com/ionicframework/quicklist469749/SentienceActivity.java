package com.ionicframework.quicklist469749;

import android.os.Bundle;

import com.honeywell.sentience.client.SentienceIotClientFactory;
import com.honeywell.sentience.tenant.iot.apiclient.ConnectionMonitorApi;
import com.honeywell.sentience.tenant.iot.apiclient.PointHistoryApi;

import org.apache.cordova.CordovaActivity;
import org.joda.time.DateTime;

import java.util.List;

public class SentienceActivity extends CordovaActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentience);

    }

}
