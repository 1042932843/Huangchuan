package com.zeller.fastlibrary.huangchuang.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.util.Constant;

import org.java_websocket.drafts.Draft_17;

import java.net.URI;

/**
 * how to create a websocket connection to a server. Only the most important callbacks are overloaded.
 */
public class WebSocketService extends Service {
    WebSockConnet webSockConnet;

    //服务开始是
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            webSockConnet = new WebSockConnet(new URI(Constant.WebConnect + "?userId=" + App.me().login().getUuid()), new Draft_17());
            webSockConnet.connectBlocking();
//            webSockConnet.send("Android端已连接");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //服务摧毁时
    @Override
    public void onDestroy() {
        webSockConnet.close(1);
        super.onDestroy();
    }
}