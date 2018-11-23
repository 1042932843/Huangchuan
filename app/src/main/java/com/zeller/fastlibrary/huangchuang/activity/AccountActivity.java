package com.zeller.fastlibrary.huangchuang.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.adapter.Accountdapter;
import com.zeller.fastlibrary.huangchuang.model.Account;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;
import com.zeller.fastlibrary.huangchuang.view.ListViewForScrollView;
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
public class AccountActivity extends BaseActivity implements View.OnClickListener, PtrHandler {
    public static final String REQUEST_ID = "id";
    private PullToRefreshLayout pull;
    private ListViewForScrollView listView;
    private ImageView back;
    private Handler handler = new Handler();
    private AccountApi accountApi;
    private TextView introduce;
    private List<Account> home;
    private Accountdapter accountdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        accountApi = new AccountApi(this);
        assignViews();
        initViews();
        String id = getIntent().getStringExtra(REQUEST_ID);
        if (null != id) {
            if (null != accountApi) {
                accountApi.Account(id);
            }
        }
    }

    private void assignViews() {
        pull = (PullToRefreshLayout) findViewById(R.id.pull);
        listView = (ListViewForScrollView) findViewById(R.id.list);
        back = (ImageView) findViewById(R.id.back);
        introduce = (TextView) findViewById(R.id.introduce);
        listView.setAdapter(accountdapter = new Accountdapter(this));
    }

    private void initViews() {
        View[] views = {back};
        for (View view : views) {
            view.setOnClickListener(this);
        }
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


        //结束后调用
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pull.refreshComplete();
            }
        }, 1000);
    }

    private class AccountApi extends HttpUtil {


        private AccountApi(Context context) {
            super(context); // 传递上下文, 初始化进度对话框
        }


        public void Account(String areaId) {

            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?dysjtzByArea",
                    "areaId", areaId

            );


        }


        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (accountdapter == null) {
                accountdapter = new Accountdapter(AccountActivity.this);
            } else {
                accountdapter.clear();
            }

            if (home == null) {
                home = new ArrayList<Account>();
            } else {
                home.clear();
            }

            if (apiMsg.isSuccess()) {
                try {
                    JSONObject jsonObject = new JSONObject(apiMsg.getResult());
                    String introduceText = jsonObject.getString("introduce");

                    String imgUrl = jsonObject.getString("imgUrl");

                    if (null != introduceText) {
                        introduce.setText(introduceText);
                    }
                    JSONArray jsonArray = jsonObject.getJSONArray("dysjtzList");


                    for (int i = 0; i < jsonArray.length(); i++) {
                        String o = jsonArray.getString(i);
//                        RedFlag redFlag = JSON.parseObject(o, RedFlag.class);
                        home.add(JSON.parseObject(o, Account.class));
                    }
                    accountdapter.addAll(home);
                    accountdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                App.me().toast(apiMsg.getMessage());
            }
        }

    }
}
