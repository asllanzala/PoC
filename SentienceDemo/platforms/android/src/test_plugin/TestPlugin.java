package test_plugin;

import android.content.Intent;

import com.honeywell.sentience.client.model.ApiObjectWrapper;
import com.honeywell.sentience.tenant.iot.apiclient.PointHistoryApi;
import com.honeywell.sentience.tenant.iot.apiclient.SystemPointsApi;
import com.honeywell.sentience.tenant.iot.apiclient.models.PointHistoryQueryParams;
import com.honeywell.sentience.tenant.iot.apiclient.models.PointHistorySample;
import com.honeywell.sentience.tenant.iot.apiclient.models.WebApiPointWriteRequest;

import com.honeywell.sentience.client.SentienceIotClientFactory;
import com.ionicframework.quicklist469749.MainActivity;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * This class echoes a string called from JavaScript.
 */
public class TestPlugin extends CordovaPlugin {

    private static final String mSystemGuid = "7d78d010-d6db-476c-92e0-ff57740c6d01";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        } else if (action.equals("subscribleEvent")) {
            subscribleEvent(args.toString(), callbackContext);
            return true;
        } else if (action.equals("backEvent")) {
            backEvent(callbackContext);
            return true;
        } else if (action.equals("getData")) {
            getData(callbackContext);
            return true;
        } else if (action.equals("playMusic")) {
            if (args.length() > 0) {
                playMusic(args.getString(0));
            }
            return true;
        } else if (action.equals("polling")) {
            polling(callbackContext);
            return true;
        } else if (action.equals("takePhoto")) {
            takePhoto(callbackContext);
            return true;
        }
        return false;
    }


    private void coolMethod(String message, CallbackContext callbackContext) {
//        int deviceId = ((StartActivity)cordova.getActivity()).getHomeDeviceId();
//        callbackContext.success(deviceId);

//        Intent i = new Intent();
//        i.setClass(cordova.getActivity(), TryDemoMainActivity.class);
//        cordova.getActivity().startActivity(i);
//        ((StartActivity)cordova.getActivity()).goToTryDemoActivity();
    }

    private void subscribleEvent(String actionName, CallbackContext callbackContext) {
//        ((StartActivity)cordova.getActivity()).setCallbackContextMap(actionName,callbackContext);


    }

    private void backEvent(CallbackContext callbackContext) {
//        Intent i = new Intent();
//        i.setClass(cordova.getActivity(), SentienceActivity.class);
//        cordova.getActivity().startActivity(i);
    }

    private void getData(final CallbackContext callbackContext) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getDataThread(callbackContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void getDataThread(CallbackContext callbackContext) throws Exception {

        // Initialize client factory
        SentienceIotClientFactory clientFactory = new SentienceIotClientFactory();
        clientFactory.setAuthTokenEndpoint("https://login.windows.net/f0269474-ac95-4f5b-95a9-f7a8abe779b3/oauth2/token");
        clientFactory.setAppClientSecret("S4V![r7v2#gKC-4.iQB=>t7E$k9bj=kCF[5EJ!bM");
        clientFactory.setAppClientId("5ad000b7-2afc-4316-b5da-aa84dc41eaac");
        clientFactory.setGlobalApiEndpoint("https://gatqaregui.testqa-cbp.honeywell.com/");
        clientFactory.setGlobalApiResourceId("http://cbpgatqareguiweb.azurewebsites.net");
        clientFactory.setSystemGuid("7d78d010-d6db-476c-92e0-ff57740c6d01");
        clientFactory.setSystemAuthEndpoint("https://gatqasystemauthentication.testqa-cbp.honeywell.com/");

//        ConnectionMonitorApi connectionMonitorApi = clientFactory.createTenantApiClient(ConnectionMonitorApi.class);
//        DateTime lastHeartbeatTime = connectionMonitorApi
//                .connectionMonitorGetLastHeartBeat(clientFactory.getSystemGuid()).getSystemTime();
//        callbackContext.success("lastHeartbeatTime:" + lastHeartbeatTime);
//        System.out.println("=========lastHeartbeatTime: " + lastHeartbeatTime);

        PointHistoryApi pointHistoryApi = clientFactory.createTenantApiClient(PointHistoryApi.class);
        List<String> pointIds = pointHistoryApi.pointHistoryGetRecentlyUpdatedPointIds(clientFactory.getSystemGuid()).getPointIds();

        DateTime lastTimestamp = DateTime.now().minusMinutes(3);
        DateTime now = DateTime.now().minusSeconds(100);

        try {
            for (String pointId : pointIds) {
                PointHistoryQueryParams params = new PointHistoryQueryParams()
                        .systemGuid(clientFactory.getSystemGuid())
                        .addPointIdsItem(pointId)
                        .startTime(lastTimestamp).isStartDateTimeInclusive(false)
                        .endTime(now).isEndDateTimeInclusive(true);
                List<PointHistorySample> history = pointHistoryApi.pointHistoryPostHistoryQuery(clientFactory.getSystemGuid(), params);
                for (PointHistorySample sample : history) {
                    String data[] = sample.getItemStringValue().split(":");
                    JSONObject object = new JSONObject();
                    object.put("temperature", data[0]);
                    object.put("humidity", data[1]);
                    object.put("pm25", data[2]);
                    object.put("pm10", data[3]);
                    object.put("co2", data[4]);
                    object.put("tvoc", data[5]);
                    callbackContext.success(object);
                    System.out.println("=========data: " + sample.getItemStringValue());
                }
            }
        } catch (Exception e) {

        }

    }


    private void polling(final CallbackContext callbackContext) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        Thread.sleep(30000);
                        getDataThread(callbackContext);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    public static void playMusic(final String mySong) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    playMusicThread(mySong);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void playMusicThread(String mySong) throws Exception {

        // Initialize client factory
        SentienceIotClientFactory clientFactory = new SentienceIotClientFactory();
        clientFactory.setAuthTokenEndpoint("https://login.windows.net/f0269474-ac95-4f5b-95a9-f7a8abe779b3/oauth2/token");
        clientFactory.setAppClientSecret("S4V![r7v2#gKC-4.iQB=>t7E$k9bj=kCF[5EJ!bM");
        clientFactory.setAppClientId("5ad000b7-2afc-4316-b5da-aa84dc41eaac");
        clientFactory.setGlobalApiEndpoint("https://gatqaregui.testqa-cbp.honeywell.com/");
        clientFactory.setGlobalApiResourceId("http://cbpgatqareguiweb.azurewebsites.net");
        clientFactory.setSystemGuid("7d78d010-d6db-476c-92e0-ff57740c6d01");
        clientFactory.setSystemAuthEndpoint("https://gatqasystemauthentication.testqa-cbp.honeywell.com/");

        WebApiPointWriteRequest req = new WebApiPointWriteRequest();
        req.setValue(ApiObjectWrapper.of(mySong));
        SystemPointsApi systemPointsApi = clientFactory.createTenantApiClient(SystemPointsApi.class);
        systemPointsApi.systemPointsWritePointWithHttpInfo(clientFactory.getSystemGuid(), "point1", req);

    }

    private void takePhoto(final CallbackContext callbackContext) {

        Intent sendDataIntent = new Intent(MainActivity.ACTION_TAKE_PICTURE);
        cordova.getActivity().sendBroadcast(sendDataIntent);

    }

}
