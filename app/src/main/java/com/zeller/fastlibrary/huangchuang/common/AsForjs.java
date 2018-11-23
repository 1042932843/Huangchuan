package com.zeller.fastlibrary.huangchuang.common;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/6/8 0008.
 */
public class AsForjs {
    private Context context;
    private String name1;
    private String pwd2;

//    private static CallBack callBack;


    public AsForjs(Context context) {
        this.context = context;
    }

    /*
    通过注解@android.webkit.JavascriptInterface
    关联  javaScript 接口
     */
    @android.webkit.JavascriptInterface
    public void show() {
        Toast.makeText(context, "安卓程序调用成功", Toast.LENGTH_SHORT).show();
    }

    @android.webkit.JavascriptInterface
    public void show(String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    @android.webkit.JavascriptInterface
    public boolean showname(String name, String pwd) {
        name1 = name;
        pwd2 = pwd;
        Toast.makeText(context, "用户名：" + name + "密码：" + pwd, Toast.LENGTH_SHORT).show();
//        if (name.equals("123") && pwd.equals("123")) {
//            callBack.changeText(name1, pwd2);
            return true;
//        }
//        return false;

    }

    public String getname() {
        return name1;
    }

    public String getPwd2() {
        return pwd2;
    }


//    public static void setCallBack(Context context) {
//        callBack = (CallBack) context;
//
//    }
//
//    public interface CallBack {
//        public void changeText(String name, String pwd);
//    }

}
