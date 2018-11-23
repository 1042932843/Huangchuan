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
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.adapter.RedFlagAdaptent;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.RedFlag;
import com.zeller.fastlibrary.huangchuang.model.User;
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
 * Created by Administrator on 2017/6/17 0017.
 */
public class SearchResultActivity extends BaseActivity implements View.OnClickListener, PtrHandler, OnScrollLastItemListener {
    public static final String REQUEST_VALUE = "value";
    public static final String REQUEST_NAME = "opinion";
    private RedFlagApi redFlagApi;
    private PullToRefreshLayout pull;
    private ListView listView;
    private ImageView back;
    private RedFlagAdaptent redFlagAdaptent;
    private List<RedFlag> home;
    private Handler handler = new Handler();
    private String value;
    private TextView headerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redflag);
        redFlagApi = new RedFlagApi(this);
        assignViews();
        initViews();
        value = getIntent().getStringExtra(REQUEST_VALUE);
      String   name  = getIntent().getStringExtra(REQUEST_NAME);
        if (null!=name){
            headerTitle.setText(name);
        }
    }

    private void assignViews() {
        pull = (PullToRefreshLayout) findViewById(R.id.pull);
        listView = (ListView) findViewById(R.id.list);
        back = (ImageView) findViewById(R.id.back);
        headerTitle = (TextView) findViewById(R.id.headerTitle);
        listView.setAdapter(redFlagAdaptent = new RedFlagAdaptent(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RedFlag redFlag = redFlagAdaptent.getItem(position);
                Intent intent = new Intent(SearchResultActivity.this, NewsdetailActivity.class);
                intent.putExtra(NewsdetailActivity.REQUEST_ID, redFlag.getId());
                startActivity(intent);
            }
        });
    }
    private void initViews() {
        View[] views = {back};
        for (View view : views) {
            view.setOnClickListener(this);
        }
        listView.setOnScrollListener(new OnScrollListener(this));
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
        User user = App.me().getUser();
        if (null!=user){
            if (redFlagApi != null) {
                redFlagApi.RedFlag(user.getUuid(),value);
            }
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
        User user = App.me().getUser();
        if (null!=user){
            if (redFlagApi != null) {
                redFlagApi.getnext(user.getUuid(),value);
            }
        }

    }

    private class RedFlagApi extends HttpUtil {
        private int page;
        private boolean hasMore;

        private RedFlagApi(Context context) {
            super(context); // 传递上下文, 初始化进度对话框
        }


        public void RedFlag(String uuid,String title) {
            page = 1;
            hasMore = false;
            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?searchNewsPage",
                    "uuid",uuid,
                    "title",title,
                    "currentPage", page + ""

            );

        }

        private void getnext(String uuid,String title ) {
            if (hasMore) {
                send(HttpRequest.HttpMethod.POST,
                        "hcdj/phoneNewsController.do?searchNewsPage",
                        "uuid",uuid,
                        "title",title,
                        "currentPage", page + ""

                );
            }

        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {

            if (redFlagAdaptent == null) {
                redFlagAdaptent = new RedFlagAdaptent(SearchResultActivity.this);
            } else {
                redFlagAdaptent.clear();
            }

            if (page == 1) {
                if (home == null) {
                    home = new ArrayList<RedFlag>();
                } else {
                    home.clear();
                }
            }


            if (apiMsg.isSuccess()) {
                // 登录成功, 保存用户登录信息, 持久化
                try {
                    JSONObject obj = new JSONObject(apiMsg.getResult());
                    JSONArray jsonArray = obj.getJSONArray("list");
//                    if (jsonArray.length() <= 0) {
//                        App.me().toast("暂无数据");
//                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String o = jsonArray.getString(i);
//                        RedFlag redFlag = JSON.parseObject(o, RedFlag.class);
                        home.add(JSON.parseObject(o, RedFlag.class));
                    }
                    page += 1;
                    hasMore = jsonArray.length() >= 10;
                    redFlagAdaptent.addAll(home);
                    redFlagAdaptent.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                App.me().toast(apiMsg.getMessage()); // 登录不成功, 根据接口返回提示
            }
        }

    }
}
