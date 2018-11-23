package com.zeller.fastlibrary.huangchuang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.model.Account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by lenovo on 2017/2/27.
 */
public class Accountdapter extends BaseAdapter {
    private final Context context;
    private List<Account> list;
    private HashSet<Integer> hs;
//    private List<RedFlag> olist;

    public Accountdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<Account>();
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
    public Account getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class holder {
        TextView position;
        TextView identity;
        TextView num;
        TextView sex;
        TextView serviceYear;
        TextView name;
        TextView poor;
        TextView cunName;
        TextView rrsh;

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        holder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.account_list_item, null);
            holder = new holder();

            holder.cunName = (TextView) view.findViewById(R.id.cunName);
            holder.rrsh = (TextView) view.findViewById(R.id.rrsh);
            holder.serviceYear = (TextView) view.findViewById(R.id.serviceYear);
            holder.position = (TextView) view.findViewById(R.id.position);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.poor = (TextView) view.findViewById(R.id.poor);
            holder.identity = (TextView) view.findViewById(R.id.identity);
            holder.num = (TextView) view.findViewById(R.id.num);
            holder.sex = (TextView) view.findViewById(R.id.sex);
            view.setTag(holder);
        } else {
            holder = (holder) view.getTag();
        }

        Account item = list.get(position);
        if (null != item) {
            holder.cunName.setText(item.getCunName());
            holder.rrsh.setText(item.getRrsh());
            holder.serviceYear.setText(item.getServiceYear());
            holder.position.setText(item.getPosition());
            holder.name.setText(item.getCunName());
            holder.poor.setText(item.getPoor());
            holder.identity.setText(item.getIdentity());
            holder.num.setText(item.getNum());
            holder.sex.setText(item.getSex());
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

    public boolean addAll(Collection<? extends Account> collection) {
        boolean pa = list.addAll(collection);
        return pa;
    }


    public boolean add(Account object) {
        return list.add(object);
    }

}
