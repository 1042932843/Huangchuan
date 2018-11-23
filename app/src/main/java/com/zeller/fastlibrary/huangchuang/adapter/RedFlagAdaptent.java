package com.zeller.fastlibrary.huangchuang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.model.RedFlag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by lenovo on 2017/2/27.
 */
public class RedFlagAdaptent extends BaseAdapter {
    private final Context context;
    private List<RedFlag> list;
    private HashSet<Integer> hs;
//    private List<RedFlag> olist;

    public RedFlagAdaptent(Context context) {
        this.context = context;
        this.list = new ArrayList<RedFlag>();
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
    public RedFlag getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    class holder{
        TextView ranks_tiele;
        TextView ranks_date;
//        TextView clickRate;
         ImageView img;

    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        holder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.itme_list_redflag, null);
            holder=new holder();

            holder.ranks_tiele = (TextView) view.findViewById(R.id.text);
            holder.ranks_date = (TextView) view.findViewById(R.id.text_1);
//            holder.clickRate = (TextView) view.findViewById(R.id.clickRate);
            holder.img = (ImageView) view.findViewById(R.id.img);
            view.setTag(holder);
        }else {
            holder=(holder)view.getTag();
        }

        RedFlag item = list.get(position);
        holder.ranks_tiele.setText(item.getNewsTitle());
        holder.ranks_date.setText(item.getCreateDate());
//        holder.clickRate.setText("浏览数 : "+item.getClickRate());

        if (null!=item.getNewsImgUrl()){
            if (item.getNewsImgUrl().isEmpty()) {
//                holder.rela.setVisibility(View.GONE);
//                holder.img.setImageResource(R.drawable.ic_launcher);
            } else{
//                holder.rela.setVisibility(View.VISIBLE);
                Picasso.with(context).load(item.getNewsImgUrl()).into(holder.img);
            }
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

    public boolean addAll(Collection<? extends RedFlag> collection) {
        boolean pa = list.addAll(collection);
        return pa;
    }


    public boolean add(RedFlag object) {
        return list.add(object);
    }

}
