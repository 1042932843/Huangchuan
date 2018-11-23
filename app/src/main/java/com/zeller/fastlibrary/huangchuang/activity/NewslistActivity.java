package com.zeller.fastlibrary.huangchuang.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.adapter.NewslistAdaptent;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.Newslist;
import com.zeller.fastlibrary.huangchuang.model.User;
import com.zeller.fastlibrary.huangchuang.ui.OnScrollLastItemListener;
import com.zeller.fastlibrary.huangchuang.ui.OnScrollListener;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;
import com.zeller.fastlibrary.huangchuang.view.PullToRefreshLayout;
import com.zeller.fastlibrary.huangchuang.view.SwipeMenu;
import com.zeller.fastlibrary.huangchuang.view.SwipeMenuCreator;
import com.zeller.fastlibrary.huangchuang.view.SwipeMenuItem;
import com.zeller.fastlibrary.huangchuang.view.SwipeMenuListView;

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
public class NewslistActivity extends BaseActivity implements View.OnClickListener, PtrHandler, OnScrollLastItemListener {
    private PullToRefreshLayout pull;
    private SwipeMenuListView listView;
    private ImageView back;
    private NoticeApi noticeApi;
    private Handler handler = new Handler();
    private List<Newslist> home;
    private NewslistAdaptent noticeAdaptent;
    private AlertDialog dialog;
    private DeleteApi deleteApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newslist);
        noticeApi = new NoticeApi(this);
        deleteApi = new DeleteApi(this);
        assignViews();
        initViews();

    }

    private void assignViews() {
        pull = (PullToRefreshLayout) findViewById(R.id.pull);
        listView = (SwipeMenuListView) findViewById(R.id.list);
        back = (ImageView) findViewById(R.id.back);
        listView.setAdapter(noticeAdaptent = new NewslistAdaptent(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  Newslist notice = noticeAdaptent.getItem(position);
                   Intent intent = new Intent(NewslistActivity.this, NewsdetailActivity.class);
                   intent.putExtra(NewsdetailActivity.REQUEST_ID, notice.getId());
                   startActivityForResult(intent, NewsdetailActivity.REQUEST_CODE);

            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                openItem.setWidth(dp2px(90));
                openItem.setTitle("删除");
                // set item title fontsize
                openItem.setTitleSize(16);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" itemdd
//                SwipeMenuItem deleteItem = new SwipeMenuItem(
//                        getApplicationContext());
//                // set item background
//                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
//                        0x3F, 0x25)));
//                // set item width
//                deleteItem.setWidth(dp2px(90));
//                // set a icon
//                deleteItem.setTitle("删除");
//                deleteItem.setTitleSize(16);
//                // set item title font color
//                deleteItem.setTitleColor(Color.WHITE);
//                // add to menu
//                menu.addMenuItem(deleteItem);
            }
        };


        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Newslist theservant = noticeAdaptent.getItem(position);
                User user = App.me().getUser();
                switch (index) {
                    case 0:
                        if (null != user) {
                            if (null != theservant.getId()) {
                                showAlertDialog("是否确定删除此新闻?", theservant.getId());
                            }
                        }
                        break;
                }
                return false;
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public void showAlertDialog(final String s, final String User_name) {

        AlertDialog.Builder builder = new AlertDialog.Builder(NewslistActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_normal_layo, null);
        TextView message = (TextView) view.findViewById(R.id.title);
        message.setText(s);
        TextView positiveButton = (TextView) view.findViewById(R.id.positiveButton);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView negativeButton = (TextView) view.findViewById(R.id.negativeButton);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = App.me().getUser();
                if (null != user) {
                    if (null != User_name) {
                        if (null != deleteApi) {
                            deleteApi.delete(user.getUuid(), User_name);
                        }
                    }
                }
                dialog.dismiss();
            }
        });
        builder.setView(view);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
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
                    "hcdj/phoneNewsController.do?userNewsListPage",
                    "uuid", uuid,
                    "currentPage", page + ""

            );

        }

        private void getnext(String uuid) {
            if (hasMore) {
                send(HttpRequest.HttpMethod.POST,
                        "hcdj/phoneNewsController.do?userNewsListPage",
                        "uuid", uuid,
                        "currentPage", page + ""

                );
            }

        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {

            if (noticeAdaptent == null) {
                noticeAdaptent = new NewslistAdaptent(NewslistActivity.this);
            } else {
                noticeAdaptent.clear();
            }

            if (page == 1) {
                if (home == null) {
                    home = new ArrayList<Newslist>();
                } else {
                    home.clear();
                }
            }


            if (apiMsg.isSuccess()) {
                try {
                    JSONObject obj = new JSONObject(apiMsg.getResult());
                    Log.d("reg", "新闻管理:" + obj);

                    JSONArray jsonArray = obj.getJSONArray("list");
                    if (jsonArray.length() <= 0) {
                        App.me().toast("暂无数据");
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String o = jsonArray.getString(i);
//                        RedFlag redFlag = JSON.parseObject(o, RedFlag.class);
                        home.add(JSON.parseObject(o, Newslist.class));
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

    private class DeleteApi extends HttpUtil {


        private DeleteApi(Context context) {
            super(context);
        }

        public void delete(String userId, String nurseId) {

            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?deleteNewsByUser",
                    "uuid", userId,
                    "newsId", nurseId

            );
        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                App.me().toast(apiMsg.getMessage());
                pull.post(new Runnable() {
                    @Override
                    public void run() {
                        pull.autoRefresh();
                    }
                });
            } else {
                App.me().toast(apiMsg.getMessage());
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}



