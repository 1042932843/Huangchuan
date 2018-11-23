/*
 * Copyright © 2015 珠海云集软件科技有限公司.
 * Website：http://www.YunJi123.com
 * Mail：dev@yunji123.com
 * Tel：+86-0756-8605060
 * QQ：340022641(dove)
 * Author：dove
 */

package com.zeller.fastlibrary.huangchuang.fragments;

import android.support.v4.app.Fragment;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 分页片段
 */
public abstract class PagerFragment extends Fragment {

    private int position;
    private boolean firstTime = true;

    public final int getPosition() {
        return position;
    }

    public final void setPosition(int position) {
        this.position = position;
    }

    public final void onPageSelected() {
        onPageSelected(firstTime);
        firstTime = false;
    }

    protected void onPageSelected(boolean firstTime){

    }
    public final void onSetStr(RelativeLayout re,TextView text) {
        onSetStr(false,re,text);
    }

    protected void onSetStr(boolean firstTime,RelativeLayout re,TextView text){

    }



}
