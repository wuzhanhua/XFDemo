package com.example.tufei.xfdemo;

//import android.app.Application;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by tufei on 2017/11/3.
 */

public class App extends Application {

    static App instance;
    @Override
    public void onCreate() {
        super.onCreate();

        instance=this;

        //讯飞初始化
        // 请勿在“=”与appid之间添加任何空字符或者转义符
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=59f44605");
    }

    public static App getApplication() {
        return instance;
    }
}
