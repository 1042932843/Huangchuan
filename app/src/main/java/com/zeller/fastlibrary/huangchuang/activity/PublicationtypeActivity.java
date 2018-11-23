package com.zeller.fastlibrary.huangchuang.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.adapter.PublicationtypeAdapter;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.PublicationType;
import com.zeller.fastlibrary.huangchuang.model.User;
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
 * Created by Administrator on 2017/6/20 0020.
 */
public class PublicationtypeActivity extends BaseActivity implements View.OnClickListener, PtrHandler {
    public static final int REQUEST_CODE = 'p' + 'u' + 'b' + 'l' + 'i' + 'o' + 'n' + 't' + 'y';
    private PullToRefreshLayout pull;
    private ListView listView;
    private ImageView back;
    private PublicationtypeAdapter publicationtypeAdapter;
    private Handler handler = new Handler();
    private  PublicationtypeApi publicationtypeApi;
    private List<PublicationType> home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicationtype);
        publicationtypeApi = new PublicationtypeApi(this);
        assignViews();
        initViews();

    }

    private void assignViews() {

        pull = (PullToRefreshLayout) findViewById(R.id.pull);
        listView = (ListView) findViewById(R.id.list);
        back = (ImageView) findViewById(R.id.back);
        listView.setAdapter(publicationtypeAdapter = new PublicationtypeAdapter(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PublicationType publicationType =publicationtypeAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("id", publicationType.getId());
                intent.putExtra("name", publicationType.getTypeName());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

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
        User user = App.me().getUser();
        if (null != user) {
            publicationtypeApi.Publicationtype(user.getUuid());
        }
        //结束后调用
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pull.refreshComplete();
            }
        }, 1000);
    }

    private class PublicationtypeApi extends HttpUtil {

        private PublicationtypeApi(Context context) {
            super(context);
        }


        public void Publicationtype(String uuid) {

            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?newsTypeList",
                    "uuid", uuid
            );
        }
        @Override
        public void onSuccess(ApiMsg apiMsg) {

            if (publicationtypeAdapter == null) {
                publicationtypeAdapter = new PublicationtypeAdapter(PublicationtypeActivity.this);
            } else {
                publicationtypeAdapter.clear();
            }

                if (home == null) {
                    home = new ArrayList<PublicationType>();
                } else {
                    home.clear();
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
//                        RedFlag redFlag = JSON.parseObject(o, RedFlag.class);
                        home.add(JSON.parseObject(o, PublicationType.class));
                    }
                    publicationtypeAdapter.addAll(home);
                    publicationtypeAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                App.me().toast(apiMsg.getMessage());
            }
        }

    }
}
