package com.zeller.fastlibrary.huangchuang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.model.PublicationType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by lenovo on 2017/2/27.
 */
public class PublicationtypeAdapter extends BaseAdapter {
    private final Context context;
    private List<PublicationType> list;
    private HashSet<Integer> hs;
    private int prePos=-1;

    public PublicationtypeAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<PublicationType>();
        this.hs = new HashSet<Integer>();
//        this.olist = list;
    }

/*    public void SearchCity(String city) {
        this.list = Search(city);
        notifyDataSetChanged();
    }*/

 /*   private List<Ranks> Search(String city) {
        if (city != null && city.length() > 0) {
            ArrayList<Ranks> area = new ArrayList<Ranks>();
            for (Ranks a : this.olist) {
                if (a.getNewsTitle().indexOf(city) != -1) {
                    area.add(a);
                }
            }
            return area;
        } else {
            return this.olist;
        }

    }*/
    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public PublicationType getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class holder {
        TextView text;
        public CheckBox checkboxOperateData;

    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        holder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.publicationtype_list_itme, null);
            holder = new holder();

            holder.text = (TextView) view.findViewById(R.id.tv_name);
//            holder.checkboxOperateData = (CheckBox) view.findViewById(R.id.checkbox_operate);
            view.setTag(holder);
        } else {
            holder = (holder) view.getTag();
        }

        final PublicationType item = list.get(position);
        if (null != item) {
            holder.text.setText(item.getTypeName());
        }
            return view;
    }



    // 设置数据
    public void setData(List<PublicationType> data) {
        this.list = data;
    }

    // 添加数据
    public void addData(PublicationType bean) {
        // 下标 数据
        list.add(0, bean);
    }


    public void setHs(HashSet<Integer> hs) {
        this.hs = hs;
    }

    public void clear() {
        list.clear();
    }

    public boolean addAll(Collection<? extends PublicationType> collection) {
        boolean pa = list.addAll(collection);
        return pa;
    }


    public boolean add(PublicationType object) {
        return list.add(object);
    }


    // 删除一个数据
    public void removeData(int position) {
        list.remove(position);
    }


}
