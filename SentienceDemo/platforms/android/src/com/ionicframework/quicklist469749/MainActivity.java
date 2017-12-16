/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.ionicframework.quicklist469749;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.ionicframework.quicklist469749.Picture.OSSManager;
import com.ionicframework.quicklist469749.Picture.Path;

import org.apache.cordova.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import test_plugin.TestPlugin;

public class MainActivity extends CordovaActivity {

    public static final String ACTION_TAKE_PICTURE = "ACTION_TAKE_PICTURE";

    private String mPicFileName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // enable Cordova apps to be started in the background
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }

        // Set by <content src="index.html" /> in config.xml
        loadUrl(launchUrl);


        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_TAKE_PICTURE);
        registerReceiver(mReceiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_TAKE_PICTURE.equals(action)) {

                Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // 创建图片
                mPicFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
                File file = new File(Path.SAVE_DIR);
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File(Path.SAVE_DIR, mPicFileName);

                // 开始拍照
//                Uri uri = Uri.fromFile(file);
                Uri uri = FileProvider.getUriForFile(MainActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider", file);

                imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(imageCaptureIntent, 1);

            }
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "拍摄成功", Toast.LENGTH_SHORT).show();

                    // 上传图片
                    try {
                        OSSManager.getInstance().resumableUpload(Path.SAVE_DIR + mPicFileName, mPicFileName, 0, "");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    // 发送消息给SentienceDemoBoard
                    try {
                        TestPlugin.playMusic(Path.SAVE_DIR + mPicFileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            default:
                break;
        }
    }



}
