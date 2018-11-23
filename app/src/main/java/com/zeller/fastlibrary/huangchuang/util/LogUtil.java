/*
 * Copyright © 2015 珠海云集软件科技有限公司.
 * Website：http://www.YunJi123.com
 * Mail：dev@yunji123.com
 * Tel：+86-0756-8605060
 * QQ：340022641(dove)
 * Author：dove
 */

package com.zeller.fastlibrary.huangchuang.util;

import android.util.Log;

import com.zeller.fastlibrary.BuildConfig;


/**
 * 日志工具
 */
public final class LogUtil {

    private static final String TAG = "潢川";

    public static void d(Class<?> tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG + "-" + tag.getSimpleName(), msg);
        }
    }

    public static void e(Class<?> tag, String msg, Throwable e) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG + "-" + tag.getSimpleName(), msg, e);
        }
    }

    private LogUtil() {
        // 不需要被实例化
    }

}
