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
import com.zeller.fastlibrary.huangchuang.adapter.PartyBranchAdapter;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.PartyBranch;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;
import com.zeller.fastlibrary.huangchuang.view.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by Administrator on 2018/3/20 0020.
 */
public class PartyBranchActivity extends BaseActivity implements PtrHandler {
    public static final String REQUEST_ID = "id";
    public static final int REQUEST_CODE = 'p' + 'a' + 'r' + 't' + 'y';
    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.pull)
    PullToRefreshLayout pull;
    private ListView list;
    private PartyBranchApi partyBranchApi;
    private Handler handler = new Handler();
    private PartyBranchAdapter partyBranchAdapter;
    private List<PartyBranch> home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partybranch);
        ButterKnife.bind(this);
        partyBranchApi = new PartyBranchApi(this);
        init();
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(partyBranchAdapter = new PartyBranchAdapter(this));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PartyBranch notice = partyBranchAdapter.getItem(i);
                Intent intent = new Intent();
                intent.putExtra("content", notice.getName());
                intent.putExtra("id", notice.getId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }



    private void init() {
        pull.setPtrHandler(this);
        pull.post(new Runnable() {
            @Override
            public void run() {
                pull.autoRefresh();
            }
        });

    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        if (null != partyBranchApi) {
            String id = getIntent().getStringExtra(REQUEST_ID);
            if (null != id) {
                partyBranchApi.Partyrepresentative(id);
            } else {
                partyBranchApi.PartyBranch();
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

    private class PartyBranchApi extends HttpUtil {

        private PartyBranchApi(Context context) {
            super(context); // 传递上下文, 初始化进度对话框
        }


        public void PartyBranch() {

            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?areaTreeType" // 党员
            );
        }

        public void Partyrepresentative(String areaId) {

            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?ddbUserList", // 党员
                    "areaId", areaId
            );
        }


        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (partyBranchAdapter == null) {
                partyBranchAdapter = new PartyBranchAdapter(PartyBranchActivity.this);
            } else {
                partyBranchAdapter.clear();
            }

            if (home == null) {
                home = new ArrayList<PartyBranch>();
            } else {
                home.clear();
            }


            if (apiMsg.isSuccess()) {
                try {
                    JSONObject jsonObect = new JSONObject(apiMsg.getResult());
                    JSONArray array = jsonObect.getJSONArray("list");
                    if (array.length() <= 0) {
                        App.me().toast("暂无数据");
                    }
                    for (int i = 0; i < array.length(); i++) {
                        String o = array.getString(i);
                        home.add(JSON.parseObject(o, PartyBranch.class));
                    }
                    partyBranchAdapter.addAll(home);
                    partyBranchAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                App.me().toast(apiMsg.getMessage());
            }
        }

    }

}
