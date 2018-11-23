package com.zeller.fastlibrary.huangchuang.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.adapter.CollectionAdaptent;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.Collectio;
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
import java.util.Map;
import java.util.regex.Pattern;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by Administrator on 2017/6/15 0015.
 */
public class CollectionActivity extends BaseActivity implements View.OnClickListener, PtrHandler, OnScrollLastItemListener, AdapterView.OnItemClickListener {
    private PullToRefreshLayout pull;
    private ListView listView;
    private ImageView back;
    private CollectionAdaptent collectionAdaptent;
    private List<Collectio> home;
    private List<Collectio> selectedList;
    private Handler handler = new Handler();
    private CollectionApi collectionApi;
    private TextView complain;
    private TextView select;
    private Button submit;
    private  CancelApi cancelApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collection);
        collectionApi = new CollectionApi(this);
        cancelApi = new CancelApi(this);
        assignViews();
        initViews();

    }


    private void assignViews() {
        complain = (TextView) findViewById(R.id.complain);
        select = (TextView) findViewById(R.id.select);
        pull = (PullToRefreshLayout) findViewById(R.id.pull);
        listView = (ListView) findViewById(R.id.list);
        back = (ImageView) findViewById(R.id.back);
        submit = (Button) findViewById(R.id.submit);
        listView.setAdapter(collectionAdaptent = new CollectionAdaptent(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Collectio co = collectionAdaptent.getItem(position);
                    Intent intent = new Intent(CollectionActivity.this, NewsdetailActivity.class);
                    intent.putExtra(NewsdetailActivity.REQUEST_ID, co.getNewsId());
                startActivityForResult(intent, NewsdetailActivity.REQUEST_CODE);
            }
        });
    }
    private void initViews() {
        View[] views = {back, complain, submit, select};
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
            case R.id.complain:
                btnEditList(v);
                break;
            case R.id.submit:
                Submit();
                break;
            case R.id.select:
                Selest();
                break;
        }
    }

    private void Submit() {

        // 拿到所有数据
        Map<Integer, Boolean> isCheck_delete = collectionAdaptent.getMap();
        // 获取到条目数量，map.size = list.size,所以
        int count = collectionAdaptent.getCount();
        // 遍历
        List<String> ids = new ArrayList<>();
        for (int j = 0; j < collectionAdaptent.getCount(); j++) {
            if (isCheck_delete.get(j)) {
                ids.add(collectionAdaptent.getItem(j).getNewsId());
            }
        }

        String s = Pattern.compile("\\b([\\w\\W])\\b").matcher(ids.toString().substring(1,ids.toString().length()-1)).replaceAll("'$1'");

        String oo = s.replace(" ", "");
//        Log.d("reg","oooo:"+oo);
        User user = App.me().getUser();
        if (null!=user){
            cancelApi.Cancel(user.getUuid(),oo);
        }
    }

//    private void Complain() {
//        select.setVisibility(View.VISIBLE);
//        complain.setText("取消");
//    }

    /**
     * 编辑、取消编辑
     *
     * @param view
     */

    public void btnEditList(View view) {

        collectionAdaptent.flage = !collectionAdaptent.flage;

        if (collectionAdaptent.flage) {
            complain.setText("取消");
            select.setVisibility(View.VISIBLE);
        } else {
            complain.setText("编辑");
            select.setVisibility(View.GONE);
        }

        collectionAdaptent.notifyDataSetChanged();
    }

    private void Selest() {
// 全选——全不选
        Map<Integer, Boolean> isCheck = collectionAdaptent.getMap();
        if (select.getText().equals("全选")) {
            collectionAdaptent.initCheck(true);
            // 通知刷新适配器
            collectionAdaptent.notifyDataSetChanged();
            select.setText("全不选");
            select.setTextColor(Color.YELLOW);
        } else if (select.getText().equals("全不选")) {
            collectionAdaptent.initCheck(false);
            // 通知刷新适配器
            collectionAdaptent.notifyDataSetChanged();
            select.setText("全选");
            select.setTextColor(Color.YELLOW);
        }
    }

    // listview的点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // 判断view是否相同
        if (view.getTag() instanceof CollectionAdaptent.holder) {
            // 如果是的话，重用
            CollectionAdaptent.holder holder = (CollectionAdaptent.holder) view.getTag();
            // 自动触发
            holder.checkboxOperateData.toggle();
        }
    }

    @Override
    public void onScrollLastItem(AbsListView view) {
        User user = App.me().getUser();
        if (null != user) {
            collectionApi.getnext(user.getUuid());
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
            collectionApi.Collection(user.getUuid());
        }
        //结束后调用
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pull.refreshComplete();
            }
        }, 1000);
    }

    private class CollectionApi extends HttpUtil {
        private int page;
        private boolean hasMore;

        private CollectionApi(Context context) {
            super(context); // 传递上下文, 初始化进度对话框
        }


        public void Collection(String uuid) {
            page = 1;
            hasMore = false;
            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?collectionListPage",
                    "uuid", uuid,
                    "currentPage", page + ""

            );

        }

        private void getnext(String uuid) {
            if (hasMore) {
                send(HttpRequest.HttpMethod.POST,
                        "hcdj/phoneNewsController.do?collectionListPage",
                        "uuid", uuid,
                        "currentPage", page + ""

                );
            }

        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {

            if (collectionAdaptent == null) {
                collectionAdaptent = new CollectionAdaptent(CollectionActivity.this);
            } else {
                collectionAdaptent.clear();
            }

            if (page == 1) {
                if (home == null) {
                    home = new ArrayList<Collectio>();
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
//                        RedFlag redFlag = JSON.parseObject(o, RedFlag.class);
                        home.add(JSON.parseObject(o, Collectio.class));
                    }
                    page += 1;
                    hasMore = jsonArray.length() >= 10;
                    collectionAdaptent.addAll(home);
                    collectionAdaptent.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                App.me().toast(apiMsg.getMessage()); // 登录不成功, 根据接口返回提示
            }
        }

    }
    private class CancelApi extends HttpUtil {

        private CancelApi(Context context) {
            super(context);
        }


        public void Cancel(String uuid, String newsId) {

            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?cancelCollection",
                    "uuid", uuid,
                    "newsId", newsId,
                    "type", "1"
            );
        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                // 拿到所有数据
                Map<Integer, Boolean> isCheck_delete = collectionAdaptent.getMap();
                // 获取到条目数量，map.size = list.size,所以
                int count = collectionAdaptent.getCount();
                for (int i = 0; i < count; i++) {
                    // 删除有两个map和list都要删除 ,计算方式
                    int position = i - (count - collectionAdaptent.getCount());
                    // 判断状态 true为删除
                    if (isCheck_delete.get(i) != null && isCheck_delete.get(i)) {
                        isCheck_delete.remove(i);
                        collectionAdaptent.removeData(position);
                    }

                    select.setText("全选");
                    select.setTextColor(Color.WHITE);
                    collectionAdaptent.notifyDataSetChanged();
                    App.me().toast(apiMsg.getMessage());
                }
            } else {
                App.me().toast(apiMsg.getMessage());
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
