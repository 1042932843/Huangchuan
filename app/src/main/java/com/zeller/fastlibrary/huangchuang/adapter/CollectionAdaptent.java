package com.zeller.fastlibrary.huangchuang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.model.Collectio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2017/2/27.
 */
public class CollectionAdaptent extends BaseAdapter {
    private final Context context;
    private List<Collectio> list;
    private HashSet<Integer> hs;
//    private List<RedFlag> olist;
    // 存储勾选框状态的map集合
    private Map<Integer, Boolean> isCheck = new HashMap<Integer, Boolean>();
    public boolean flage = false;


    public CollectionAdaptent(Context context) {
        this.context = context;
        this.list = new ArrayList<Collectio>();
        this.hs = new HashSet<Integer>();
        // 默认为不选中
        initCheck(false);
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
    public Collectio getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class holder {
        TextView ranks_tiele;
        TextView ranks_date;
//        TextView clickRate;
         ImageView img;
        public CheckBox checkboxOperateData;
        private RelativeLayout rela;
    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        holder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.itme_list_collection, null);
            holder = new holder();

            holder.ranks_tiele = (TextView) view.findViewById(R.id.text);
            holder.rela = (RelativeLayout) view.findViewById(R.id.rela);
            holder.ranks_date = (TextView) view.findViewById(R.id.text_1);
//            holder.clickRate = (TextView) view.findViewById(R.id.clickRate);
            holder.img = (ImageView) view.findViewById(R.id.img);
            holder.checkboxOperateData = (CheckBox) view.findViewById(R.id.checkbox_operate_data);
            view.setTag(holder);
        } else {
            holder = (holder) view.getTag();
        }

        final Collectio item = list.get(position);
        if (null != item) {
            holder.ranks_tiele.setText(item.getTitle());
            holder.ranks_date.setText(item.getCreateDate());

            if (item.getNewsImgUrl().isEmpty()) {
//                holder.rela.setVisibility(View.GONE);
//                holder.img.setImageResource(R.drawable.ic_launcher);
            } else{
//                holder.rela.setVisibility(View.VISIBLE);
                Picasso.with(context).load(item.getNewsImgUrl()).into(holder.img);
            }

            if (flage) {
                holder.checkboxOperateData.setVisibility(View.VISIBLE);
            } else {
                holder.checkboxOperateData.setVisibility(View.GONE);
            }
// 勾选框的点击事件
            holder.checkboxOperateData .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            // 用map集合保存
                            isCheck.put(position, isChecked);
                            if (Collectio.isCheck) {
                                Collectio.isCheck = false;
                            } else {
                                Collectio.isCheck = true;
                            }
                        }
                    });
            // 设置状态
            if (isCheck.get(position) == null) {
                isCheck.put(position, false);
            }
            holder.checkboxOperateData.setChecked(isCheck.get(position));
        }
            return view;


    }

    // 初始化map集合
    public void initCheck(boolean flag) {
        // map集合的数量和list的数量是一致的
        for (int i = 0; i < list.size(); i++) {
            // 设置默认的显示
            isCheck.put(i, flag);
        }
    }

    // 设置数据
    public void setData(List<Collectio> data) {
        this.list = data;
    }

    // 添加数据
    public void addData(Collectio bean) {
        // 下标 数据
        list.add(0, bean);
    }


    public void setHs(HashSet<Integer> hs) {
        this.hs = hs;
    }

    public void clear() {
        list.clear();
    }

    public boolean addAll(Collection<? extends Collectio> collection) {
        boolean pa = list.addAll(collection);
        return pa;
    }


    public boolean add(Collectio object) {
        return list.add(object);
    }
    // 全选按钮获取状态
    public Map<Integer, Boolean> getMap() {
        // 返回状态
        return isCheck;
    }

    // 删除一个数据
    public void removeData(int position) {
        list.remove(position);
    }


}
