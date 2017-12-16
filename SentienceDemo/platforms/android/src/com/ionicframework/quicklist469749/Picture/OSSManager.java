package com.ionicframework.quicklist469749.Picture;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.OSSServiceProvider;
import com.alibaba.sdk.android.oss.callback.DeleteCallback;
import com.alibaba.sdk.android.oss.callback.GetFileCallback;
import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.alibaba.sdk.android.oss.model.AccessControlList;
import com.alibaba.sdk.android.oss.model.AuthenticationType;
import com.alibaba.sdk.android.oss.model.ClientConfiguration;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.model.TokenGenerator;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSData;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.alibaba.sdk.android.oss.util.OSSLog;
import com.alibaba.sdk.android.oss.util.OSSToolKit;
import com.ionicframework.quicklist469749.MyApplication;


public class OSSManager {

    public static final String TAG = "OSSManager";

    static String accessKey;

    static String screctKey;

    static String bucketName;

    private static OSSService mOSSService = null;

    private static OSSManager mOSSManager = null;

    private static OSSBucket mBucket;

    public static String srcFileDir;
    private static Context context;

    public static OSSManager getInstance() {
        if (mOSSManager == null) {
            mOSSManager = new OSSManager();
//            mOSSManager.getBosClientInstance();
            mOSSService = getBosClientInstance();
        }

        return mOSSManager;
    }

    public static OSSService getBosClientInstance() {
        if (mOSSService == null) {
            // 开启Log
            OSSLog.enableLog();

            context = MyApplication.getInstance();

            // KEY init
//            accessKey = "EK1pbRbasr10vFUg";
//            screctKey = "Jp9rzkoJnYYfr0jyT94XAH3q1v9AtB";
//            bucketName = "smartcamera";

            accessKey = "LTAIKlzZV40BL1gO,";
            screctKey = "VYiXpspeqgN82SXHVquVQFacsE3hnX";
            bucketName = "panel2us";

//            bucketName = "panel";

//                accessKey = context.getPackageManager().getApplicationInfo(context.getPackageName(),
//                        PackageManager.GET_META_DATA).metaData.getString("com.alibaba.app.oss.key");

//                screctKey = context.getPackageManager().getApplicationInfo(context.getPackageName(),
//                        PackageManager.GET_META_DATA).metaData
//                        .getString("com.alibaba.app.oss.screctkey");

//                bucketName = MyApplication.getInstance().getPackageManager()
//                        .getApplicationInfo(context.getPackageName(),
//                                PackageManager.GET_META_DATA).metaData
//                        .getString("com.alibaba.app.oss.bucketname");

//                Log.v(TAG, "accessKey=" + accessKey + ", screctKey=" + screctKey + ", bucketName="
//                        + bucketName);

            // 初始化阿里OOS服务
            mOSSService = OSSServiceProvider.getService();

            mOSSService.setApplicationContext(MyApplication.getInstance());
            mOSSService.setGlobalDefaultHostId("oss-cn-shanghai.aliyuncs.com");
//            mOSSService.setGlobalDefaultHostId("panel.oss-us-east-1.aliyuncs.com");
            mOSSService.setGlobalDefaultACL(AccessControlList.PRIVATE); // 默认为private
            mOSSService
                    .setAuthenticationType(AuthenticationType.ORIGIN_AKSK); // 设置加签类型为原始AK/SK加签
            mOSSService.setGlobalDefaultTokenGenerator(new TokenGenerator() { // 设置全局默认加签器
                @Override
                public String generateToken(String httpMethod, String md5, String type,
                                            String date,
                                            String ossHeaders, String resource) {

                    String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n"
                            + ossHeaders
                            + resource;

                    return OSSToolKit.generateToken(accessKey, screctKey, content);
                }
            });
            mOSSService.setCustomStandardTimeWithEpochSec(System.currentTimeMillis() / 1000);

            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectTimeout(30 * 1000); // 设置全局网络连接超时时间，默认30s
            conf.setSocketTimeout(30 * 1000); // 设置全局socket超时时间，默认30s
            conf.setMaxConcurrentTaskNum(5); // 替换设置最大连接数接口，设置全局最大并发任务数，默认为6
            conf.setIsSecurityTunnelRequired(false); // 是否使用https，默认为false
            mOSSService.setClientConfiguration(conf);

            mBucket = mOSSService.getOssBucket(bucketName); // 替换为你的bucketName
            // 可选地进行访问权限或者CDN加速域名的设置
            // mBucket.setBucketACL(AccessControlList.PUBLIC_READ);

            // 设置CDN加速域名时， 权限至少是公共读
            // mBucket.setCdnAccelerateHostId("<cname.to.cdn.domain.com>");

            // 可选地进行Bucket cname的设置
            // mBucket.setBucketHostId("<cname.to.mBucket>");
        }

        return mOSSService;
    }

    /**
     * 同步上传数据
     *
     * @param fileName 文件名需要包括文件名后缀，比如“upload.txt”
     */
    public void syncUploadText(String textToSend, String fileName) throws OSSException {
        byte[] dataToUpload = textToSend.getBytes();
        OSSData data = mOSSService.getOssData(mBucket, fileName);
        data.setData(dataToUpload, "text/plain");
        data.addXOSSMetaHeader("x-oss-meta-name1", "value1");

        data.upload();
    }

    /**
     * 断点上传文件
     *
     * @param filePath 文件名需要包括文件名后缀，比如“/path/to/file/upload.png”
     * @param fileName 文件名需要包括文件名后缀，比如“upload.png”
     */
    public void resumableUpload(String filePath, final String fileName, final int type, final String name)
            throws FileNotFoundException {
//        Log.i(TAG, "resumableUpload filePath: " + filePath);

        OSSFile bigfFile = mOSSService.getOssFile(mBucket, fileName);
        Log.e(TAG, "bigFile: " + bigfFile);
        bigfFile.setUploadFilePath(filePath, "application/octet-stream");

        bigfFile.ResumableUploadInBackground(new SaveCallback() {

            @Override
            public void onSuccess(String objectKey) {

                String title = "";
                switch (type) {
                    case 0:
                        title = "Here is a motion alarm";
                        break;
                    case 1:
                        title = name;
                        break;
                    case 2:
                        title = "Here is a stranger";
                        break;
                }
                Log.e(TAG, "resumableUpload [onSuccess] - " + objectKey + " upload success! title: " + title);
//                new ThreadTask().new BaiduPushTask(title, fileName, type).execute();
//                UploadBroadcastManager.sendBroadcastUploadSuccess(objectKey);

            }


            @Override
            public void onProgress(String objectKey, int byteCount, int totalSize) {
                Log.e(TAG, "resumableUpload [onProgress] - current upload " + objectKey + " bytes: "
                        + byteCount
                        + " in total: " + totalSize);
//                UploadBroadcastManager.sendBroadcastUploadProgress(byteCount, totalSize);
            }

            @Override
            public void onFailure(String objectKey, OSSException ossException) {
                Log.e(TAG, "resumableUpload [onFailure] - upload " + objectKey + " failed!\n"
                        + ossException
                        .toString());
//                Toast.makeText(context,context.getString(R.string.falied_upload),Toast.LENGTH_SHORT).show();
                ossException.printStackTrace();
                ossException.getException().printStackTrace();
//                UploadBroadcastManager.sendBroadcastUploadFailed();
            }
        });
    }

    /*
        删除文件
     */
    public void deleteFIle(String fileName) {
        OSSFile bigFile = mOSSService.getOssFile(mBucket, fileName);
        bigFile.deleteInBackground(new DeleteCallback() {
            @Override
            public void onSuccess(String objectKey) {
                Log.d(TAG, "deleteFIle [onSuccess] - " + objectKey + " deleteFIle success!");
            }

            @Override
            public void onProgress(String objectKey, int byteCount, int totalSize) {

            }

            @Override
            public void onFailure(String objectKey, OSSException ossException) {
                Log.e(TAG, "deleteFIle [onFailure] - upload " + objectKey + " failed!\n"
                        + ossException
                        .toString());
                ossException.printStackTrace();
                ossException.getException().printStackTrace();
            }

        });
    }

    /**
     * 断点下载
     *
     * @param fileName File name need to be downloaded from server.
     * @return the file path of this file.
     */
    public void resumableDownload(final String fileName) throws IOException {
        initLocalFile();
        OSSFile bigFile = mOSSService.getOssFile(mBucket, fileName);
        bigFile.ResumableDownloadToInBackground(srcFileDir + fileName, new GetFileCallback() {
            @Override
            public void onSuccess(String objectKey, String filePath) {
                Log.d(TAG, "resumableDownload [onSuccess] - " + objectKey + " storage path: "
                        + filePath);
                Intent intent = new Intent();
                intent.setAction(Path.NOTIFY_ACTION_LIST);
                intent.putExtra("msg", fileName);
                context.sendBroadcast(intent);
//                DownloadBroadcastManager.sendBroadcastDownloadSuccess(filePath);
            }

            @Override
            public void onProgress(String objectKey, int byteCount, int totalSize) {
//                Log.d(TAG, "resumableDownload [onProgress] - current download: " + objectKey
//                        + " bytes:" + byteCount
//                        + " in total:" + totalSize);
//                DownloadBroadcastManager.sendBroadcastDownloadProgress(byteCount, totalSize);
            }

            @Override
            public void onFailure(String objectKey, OSSException ossException) {
                Log.e(TAG, "resumableDownload [onFailure] - download " + objectKey + " failed!\n"
                        + ossException
                        .toString());
                ossException.printStackTrace();
//                DownloadBroadcastManager.sendBroadcastDownloadFailed();
            }
        });
    }

    private void initLocalFile() {
//        srcFileDir = Environment.getExternalStorageDirectory().getAbsolutePath()
//                + "/honeywell.SmartCamera/";
        srcFileDir = Path.RECEIVE_PATH;
        File dir = new File(srcFileDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

}
