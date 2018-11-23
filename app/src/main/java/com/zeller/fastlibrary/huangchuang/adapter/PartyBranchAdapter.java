package com.zeller.fastlibrary.huangchuang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.model.PartyBranch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by lenovo on 2017/2/27.
 */
public class PartyBranchAdapter extends BaseAdapter {
    private final Context context;
    private List<PartyBranch> list;
    private HashSet<Integer> hs;
//    private List<RedFlag> olist;

    public PartyBranchAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<PartyBranch>();
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
        return list.size();
    }

    @Override
    public PartyBranch getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class holder {
        TextView name;

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        holder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.partybranch_list_item, null);
            holder = new holder();

            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (holder) view.getTag();
        }

        PartyBranch item = list.get(position);
        if (null != item) {
            holder.name.setText(item.getName());
        }

        return view;
    }


    public HashSet<Integer> getHs() {
        return hs;
    }

    public void setHs(HashSet<Integer> hs) {
        this.hs = hs;
    }

    public void clear() {
        list.clear();
    }

    public boolean addAll(Collection<? extends PartyBranch> collection) {
        boolean pa = list.addAll(collection);
        return pa;
    }


    public boolean add(PartyBranch object) {
        return list.add(object);
    }

}
