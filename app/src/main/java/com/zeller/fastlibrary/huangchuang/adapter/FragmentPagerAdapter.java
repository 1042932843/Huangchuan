/*
 * Copyright © 2015 珠海云集软件科技有限公司.
 * Website：http://www.YunJi123.com
 * Mail：dev@yunji123.com
 * Tel：+86-0756-8605060
 * QQ：340022641(dove)
 * Author：dove
 */

package com.zeller.fastlibrary.huangchuang.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * 片段分页适配器 保存和恢复状态
 */
public class FragmentPagerAdapter extends FragmentStatePagerAdapter {

    private final Class<?>[] classes;
    private final Fragment[] fragments;

    public FragmentPagerAdapter(FragmentManager fm, Class<?>... classes) {
        super(fm);
        this.classes = classes;
        this.fragments = new Fragment[classes.length];
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragments[position];
        if (fragment == null && classes != null) {
            try {
                fragments[position] = fragment = (Fragment) classes[position].newInstance();
            } catch (InstantiationException e) {
                Log.e("FragmentPagerAdapter", e.getMessage(), e);
            } catch (IllegalAccessException e) {
                Log.e("FragmentPagerAdapter", e.getMessage(), e);
            }
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return fragments.length;
    }


}
