/*
 * Copyright © 2015 珠海云集软件科技有限公司.
 * Website：http://www.YunJi123.com
 * Mail：dev@yunji123.com
 * Tel：+86-0756-8605060
 * QQ：340022641(dove)
 * Author：dove
 */

package com.zeller.fastlibrary.huangchuang.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.zeller.fastlibrary.R;


/**
 * 启动页
 */
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_start, null);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
        setContentView(view);
    }

}
