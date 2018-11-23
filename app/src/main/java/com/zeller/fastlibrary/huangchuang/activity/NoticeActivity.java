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
import com.zeller.fastlibrary.huangchuang.adapter.NoticeAdaptent;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.Notice;
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
 * Created by Administrator on 2017/6/21 0021.
 */
public class NoticeActivity extends BaseActivity implements View.OnClickListener, PtrHandler, OnScrollLastItemListener {
    private PullToRefreshLayout pull;
    private ListView listView;
    private ImageView back;
    private NoticeApi noticeApi;
    private Handler handler = new Handler();
    private List<Notice> home;
    private NoticeAdaptent noticeAdaptent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notice);
        noticeApi = new NoticeApi(this);
        assignViews();
        initViews();

    }

    private void assignViews() {
        pull = (PullToRefreshLayout) findViewById(R.id.pull);
        listView = (ListView) findViewById(R.id.list);
        back = (ImageView) findViewById(R.id.back);
        listView.setAdapter(noticeAdaptent = new NoticeAdaptent(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notice notice = noticeAdaptent.getItem(position);
                if (notice.getType().equals("1")) {
                    Intent intent = new Intent(NoticeActivity.this, NewsdetailActivity.class);
                    intent.putExtra(NewsdetailActivity.REQUEST_ID, notice.getNewsId());
                    startActivityForResult(intent, NewsdetailActivity.REQUEST_CODE);
                }
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
    public void onScrollLastItem(AbsListView view) {
        User user = App.me().getUser();
        if (null != user) {
            noticeApi.getnext(user.getUuid());
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        User user = App.me().getUser();
        if (null != user) {
            noticeApi.Notice(user.getUuid());
        }
        //结束后调用
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pull.refreshComplete();
            }
        }, 1000);
    }

    private class NoticeApi extends HttpUtil {
        private int page;
        private boolean hasMore;

        private NoticeApi(Context context) {
            super(context); // 传递上下文, 初始化进度对话框
        }


        public void Notice(String uuid) {
            page = 1;
            hasMore = false;
            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?noticeListPage",
                    "uuid", uuid,
                    "currentPage", page + ""

            );

        }

        private void getnext(String uuid) {
            if (hasMore) {
                send(HttpRequest.HttpMethod.POST,
                        "hcdj/phoneNewsController.do?noticeListPage",
                        "uuid", uuid,
                        "currentPage", page + ""

                );
            }

        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {

            if (noticeAdaptent == null) {
                noticeAdaptent = new NoticeAdaptent(NoticeActivity.this);
            } else {
                noticeAdaptent.clear();
            }

            if (page == 1) {
                if (home == null) {
                    home = new ArrayList<Notice>();
                } else {
                    home.clear();
                }
            }


            if (apiMsg.isSuccess()) {
            try {
                    JSONObject obj = new JSONObject(apiMsg.getResult());
                    JSONArray jsonArray = obj.getJSONArray("list");
                    if (jsonArray.length() <= 0) {
                        App.me().toast("暂无数据");
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String o = jsonArray.getString(i);
//                        RedFlag redFlag = JSON.parseObject(o, RedFlag.class);
                        home.add(JSON.parseObject(o, Notice.class));
                    }
                    page += 1;
                    hasMore = jsonArray.length() >= 10;
                    noticeAdaptent.addAll(home);
                    noticeAdaptent.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                App.me().toast(apiMsg.getMessage()); // 登录不成功, 根据接口返回提示
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NewsdetailActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            pull.post(new Runnable() {
                @Override
                public void run() {
                    pull.autoRefresh();
                }
            });
        }
    }
}



