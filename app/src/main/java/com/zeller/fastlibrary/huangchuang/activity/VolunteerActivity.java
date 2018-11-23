package com.zeller.fastlibrary.huangchuang.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.adapter.VolunteerAdapter;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.Volunteer;
import com.zeller.fastlibrary.huangchuang.ui.OnScrollLastItemListener;
import com.zeller.fastlibrary.huangchuang.ui.OnScrollListener;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;
import com.zeller.fastlibrary.huangchuang.view.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by Administrator on 2017/6/16 0016.
 */
public class VolunteerActivity extends BaseActivity implements View.OnClickListener, PtrHandler, OnScrollLastItemListener,AdapterView.OnItemClickListener {
    private PullToRefreshLayout pull;
    private ListView listView;
    private ImageView back;
    private Handler handler = new Handler();
    private  VolunteerApi volunteerApi;
    private VolunteerAdapter volunteerAdapter;
    private List<Volunteer> home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);
        volunteerApi = new VolunteerApi(this);
        assignViews();
        initViews();
    }


    private void assignViews() {
        pull = (PullToRefreshLayout) findViewById(R.id.pull);
        listView = (ListView) findViewById(R.id.list);
        back = (ImageView) findViewById(R.id.back);
        listView.setAdapter(volunteerAdapter = new VolunteerAdapter(this));
    }
    private void initViews() {
        View[] views = {back};
        for (View view : views) {
            view.setOnClickListener(this);
        }
        listView.setOnScrollListener(new OnScrollListener(this));
        listView.setOnItemClickListener(this);
        pull.setPtrHandler(this);
        pull.post(new Runnable() {
            @Override
            public void run() {
                pull.autoRefresh();
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }


    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        if (volunteerApi != null) {
            volunteerApi.Volunteer();
        }
        //结束后调用
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pull.refreshComplete();
            }
        }, 1000);
    }
    @Override
    public void onScrollLastItem(AbsListView view) {
        if (volunteerApi != null) {
            volunteerApi.getnext();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Volunteer volunteer = home.get(position);
        Intent intent = new Intent(VolunteerActivity.this,EventdetailsActivity.class);
        intent.putExtra(EventdetailsActivity.REQUEST_ID,volunteer.getId());
        startActivity(intent);
    }

    private class VolunteerApi extends HttpUtil {
        private int page;
        private boolean hasMore;

        private VolunteerApi(Context context) {
            super(context); // 传递上下文, 初始化进度对话框
        }


        public void Volunteer() {
            page = 1;
            hasMore = false;
            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?volunteerListPage",
                    "currentPage", page + ""
            );

        }

        private void getnext() {
            if (hasMore) {
                send(HttpRequest.HttpMethod.POST,
                        "hcdj/phoneNewsController.do?volunteerListPage",
                        "currentPage", page + ""

                );
            }

        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {

            if (volunteerAdapter == null) {
                volunteerAdapter = new VolunteerAdapter(VolunteerActivity.this);
            } else {
                volunteerAdapter.clear();
            }

            if (page == 1) {
                if (home == null) {
                    home = new ArrayList<Volunteer>();
                } else {
                    home.clear();
                }
            }


            if (apiMsg.isSuccess()) {
                try {
                    JSONObject obj = new JSONObject(apiMsg.getResult());
                    JSONArray jsonArray = obj.getJSONArray("list");
//                    if (jsonArray.length() <= 0) {
//                        App.me().toast("暂无数据");
//                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String o = jsonArray.getString(i);
                        home.add(JSON.parseObject(o, Volunteer.class));
                    }
                    page += 1;
                    hasMore = jsonArray.length() >= 10;
                    volunteerAdapter.addAll(home);
                    volunteerAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                App.me().toast(apiMsg.getMessage()); // 登录不成功, 根据接口返回提示
            }
        }

    }
}
