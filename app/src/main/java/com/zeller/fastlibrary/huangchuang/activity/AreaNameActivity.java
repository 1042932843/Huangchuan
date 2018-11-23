package com.zeller.fastlibrary.huangchuang.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.adapter.TreeViewAdapter;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.Element;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;
import com.zeller.fastlibrary.huangchuang.view.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by Administrator on 2018/3/6 0006.
 */
public class AreaNameActivity extends BaseActivity implements PtrHandler {

    public static final int REQUEST_CODE = 'a' + 'r' + 'e' + 'a';

    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.pull)
    PullToRefreshLayout pull;
    private ListView treeview;
    private PartyBranchApi partyBranchApi;
    /**
     * 树中的元素集合
     */
    private ArrayList<Element> elements;
    /**
     * 数据源元素集合
     */
    private ArrayList<Element> elementsData;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itme_list_areaname);
        ButterKnife.bind(this);
        partyBranchApi = new PartyBranchApi(this);
        init();
        treeview = (ListView) findViewById(R.id.treeview);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            partyBranchApi.PartyBranch();
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

        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                elements = new ArrayList<Element>();
                elementsData = new ArrayList<Element>();
                if (apiMsg.isSuccess()) {
                    Log.d("reg", "AreaNameActivity:" + apiMsg.getResult());
                    try {
                        JSONArray jsonArray = new JSONArray(apiMsg.getResult());
                        Log.d("reg", "jsonArray:" + jsonArray.length());
                        if (jsonArray.length() <= 0) {
                            App.me().toast("暂无数据");
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            String o = jsonArray.getString(i);
                            JSONObject object = new JSONObject(o);
                            if (object.getString("level").equals("1") || object.getString("level").equals("2")) {
//                        Element e = new Element(object.getString("contentText"), object.getString("level"), object.getString("id"), object.getString("parendId"), ), Boolean.getBoolean(object.getString("isExpanded")));
//                        Log.d("reg", "o:" + e.toString());
                                elements.add(JSON.parseObject(o, Element.class));
                            }
                            //添加最外层节点
                            elementsData.add(JSON.parseObject(o, Element.class));
//                    }

//                     Element e1 = new Element(object.getString("contentText"), object.getInt("level"), object.getInt("id"), object.getInt("parendId"), Boolean.getBoolean(object.getString("hasChildren")), Boolean.getBoolean(object.getString("isExpanded")));
//                    elementsData.add(e);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    final TreeViewAdapter treeViewAdapter = new TreeViewAdapter(elements, elementsData, inflater, 0);
//            TreeViewItemClickListener treeViewItemClickListener = new TreeViewItemClickListener(treeViewAdapter);
                    treeview.setAdapter(treeViewAdapter);
                    treeview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Element element = (Element) treeViewAdapter.getItem(i);
                            Intent intent = new Intent();
                            intent.putExtra("content", element.getContentText());
                            intent.putExtra("id", element.getId());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                }
            } else {
                App.me().toast(apiMsg.getMessage());
            }
        }

    }

}
