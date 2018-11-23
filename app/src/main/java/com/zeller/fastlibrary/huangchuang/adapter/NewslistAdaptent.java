package com.zeller.fastlibrary.huangchuang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.model.Newslist;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by lenovo on 2017/2/27.
 */
public class NewslistAdaptent extends BaseAdapter {
    private final Context context;
    private List<Newslist> list;
    private HashSet<Integer> hs;

    public NewslistAdaptent(Context context) {
        this.context = context;
        this.list = new ArrayList<Newslist>();
        this.hs = new HashSet<Integer>();
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Newslist getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class holder {
        TextView tv_name;
        ImageView img;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        holder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.list_itme_newslist, null);
            holder = new holder();

            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            holder.img = (ImageView) view.findViewById(R.id.img);
            view.setTag(holder);
        } else {
            holder = (holder) view.getTag();
        }

        final Newslist item = list.get(position);

        String url = HttpUtil.getImageUrl(item.getNewsImgUrl());

        if (url != null) {
            Picasso.with(context).load(url).fit().placeholder(R.drawable.ic_launcher).into(holder.img);
        }

        if (null != item) {
            holder.tv_name.setText(item.getNewsTitle());
        }
        return view;


    }


    public void setHs(HashSet<Integer> hs) {
        this.hs = hs;
    }

    public void clear() {
        list.clear();
    }

    public boolean addAll(Collection<? extends Newslist> collection) {
        boolean pa = list.addAll(collection);
        return pa;
    }


    public boolean add(Newslist object) {
        return list.add(object);
    }


    // 删除一个数据
    public void removeData(int position) {
        list.remove(position);
    }


}
