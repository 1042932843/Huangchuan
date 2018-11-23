package com.zeller.fastlibrary.huangchuang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.model.Volunteer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by lenovo on 2017/2/27.
 */
public class VolunteerAdapter extends BaseAdapter {
    private final Context context;
    private List<Volunteer> list;
    private HashSet<Integer> hs;
//    private List<RedFlag> olist;

    public VolunteerAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<Volunteer>();
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
    public Volunteer getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    class holder{
        TextView ranks_tiele;
        TextView date;
        TextView addr;
         ImageView img;

    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        holder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.itme_list_volunteer, null);
            holder=new holder();

            holder.ranks_tiele = (TextView) view.findViewById(R.id.text);
            holder.date = (TextView) view.findViewById(R.id.date);
            holder.addr = (TextView) view.findViewById(R.id.addr);
            holder.img = (ImageView) view.findViewById(R.id.img);
            view.setTag(holder);
        }else {
            holder=(holder)view.getTag();
        }

        Volunteer  item = list.get(position);
        holder.addr.setText(item.getAddress());
        holder.date.setText(item.getBeginDate());
        holder.ranks_tiele.setText(item.getTitle());
        if (null!=item.getNewsImgUrl()||!item.getNewsImgUrl().equals("null")){
            if (item.getNewsImgUrl().isEmpty()) {
                holder.img.setVisibility(View.GONE);
//                holder.rela.setVisibility(View.GONE);
//                holder.img.setImageResource(R.drawable.ic_launcher);
            } else{
                holder.img.setVisibility(View.VISIBLE);
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

    public boolean addAll(Collection<? extends Volunteer> collection) {
        boolean pa = list.addAll(collection);
        return pa;
    }


    public boolean add(Volunteer object) {
        return list.add(object);
    }

}
