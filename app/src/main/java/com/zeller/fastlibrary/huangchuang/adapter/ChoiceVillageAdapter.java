package com.zeller.fastlibrary.huangchuang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.model.Notice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by lenovo on 2017/2/27.
 */
public class ChoiceVillageAdapter extends BaseAdapter {
    private final Context context;
    private List<Notice> list;
    private HashSet<Integer> hs;

    public ChoiceVillageAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<Notice>();
        this.hs = new HashSet<Integer>();
    }
    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Notice getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class holder {
        TextView tv_name;
    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        holder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.list_itme_notice, null);
            holder = new holder();

            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(holder);
        } else {
            holder = (holder) view.getTag();
        }

        final Notice item = list.get(position);
        if (null != item) {
            holder.tv_name.setText(item.getAreaName());
        }
            return view;


    }



    public void setHs(HashSet<Integer> hs) {
        this.hs = hs;
    }

    public void clear() {
        list.clear();
    }

    public boolean addAll(Collection<? extends Notice> collection) {
        boolean pa = list.addAll(collection);
        return pa;
    }


    public boolean add(Notice object) {
        return list.add(object);
    }


    // 删除一个数据
    public void removeData(int position) {
        list.remove(position);
    }


}
