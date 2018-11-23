package com.zeller.fastlibrary.huangchuang.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.listener.SystemBarTintManager;

import org.simple.eventbus.EventBus;

import butterknife.OnClick;

/**
 * Created by Administrator on 2016/11/17 0017.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        PgyCrashManager.register(this);
        EventBus.getDefault().register(this);
        PgyCrashManager.register(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimary);//通知栏所需颜色
        }
    }


    private void setCostomMsg(String msg){
        Log.d("reg","msg:"+msg);
    }
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        App.me().hideInput(getWindow());
        return super.onTouchEvent(event);
    }

    @Override
    @Nullable
    @OnClick(R.id.back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PgyFeedbackShakeManager.register(this, false);
    }

    @Override
    protected void onPause() {
        PgyFeedbackShakeManager.unregister();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        EventBus.getDefault().unregister(this);
        PgyFeedbackShakeManager.unregister();
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


}
