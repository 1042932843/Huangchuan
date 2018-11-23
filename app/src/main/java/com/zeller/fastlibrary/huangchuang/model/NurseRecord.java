package com.zeller.fastlibrary.huangchuang.model;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2015/12/1.
 */
public class NurseRecord {
    private String url;
    private Bitmap bm;

    public NurseRecord(String url, Bitmap bm) {
        this.url = url;
        this.bm = bm;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getBm() {
        return bm;
    }

    public void setBm(Bitmap bm) {
        this.bm = bm;
    }
}