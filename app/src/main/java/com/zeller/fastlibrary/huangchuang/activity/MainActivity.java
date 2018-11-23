package com.zeller.fastlibrary.huangchuang.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.update.PgyUpdateManager;
import com.squareup.picasso.Picasso;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.Service.WebSocketService;
import com.zeller.fastlibrary.huangchuang.adapter.FragmentPagerAdapter;

import com.zeller.fastlibrary.huangchuang.fragments.HomeFragment;
import com.zeller.fastlibrary.huangchuang.fragments.LoginFragment;
import com.zeller.fastlibrary.huangchuang.fragments.PagerFragment;
import com.zeller.fastlibrary.huangchuang.fragments.PartyFragment;
import com.zeller.fastlibrary.huangchuang.fragments.SpeakFragment;
import com.zeller.fastlibrary.huangchuang.fragments.UserFragment;
import com.zeller.fastlibrary.huangchuang.listener.PgyUpdateManagerListener;
import com.zeller.fastlibrary.huangchuang.listener.SystemBarTintManager;
import com.zeller.fastlibrary.huangchuang.model.GlobalValue;
import com.zeller.fastlibrary.huangchuang.model.User;
import com.zeller.fastlibrary.huangchuang.util.LogUtil;
import com.zeller.fastlibrary.huangchuang.view.FragmentViewPager;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    public static final int REQUEST_CODE = 'm' + 'a' + 'i' + 'n';

    private FragmentViewPager viewPager; // 分页控件
    //    private ImageButton phone;
    private RadioGroup navBar;
    TextView testeveryday;
    private RadioButton mHomeFragment;
    private RadioButton mPartyFragment;
    private RadioButton mUserFragment;
    //    private RadioButton mLoginFragment;
    private RadioButton mSpeakFragment;
    private FragmentPagerAdapter pagerAdapter; // 分页适配器
    //region 已启动过或重新启动时则不再显示启动页面
    // 例:小米打开相机后被activity结束,返回App时为重启
    private boolean started;
    private boolean restarted;
    private long onBackPressedTimeMillis; // 按下返回键的时间戳
    private TextView travel, hui_pu_jin_rong, djcyltv, pests, flood_information, notice_of_program, social_service, huimin_subsidies, sjd;
    // 要申请的权限
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private List<Activity> activityList = new LinkedList<Activity>();
    private ImageView img;
    private static final int PERMISSION_REQUEST_CODE = 1; //权限请求码
    private LinearLayout homepage;
    private LinearLayout shouye;
    private ImageView djbo, yxqcyx, xxsjd, lxyz, wsyzz, djcfp, lddyzc, dwzs, djcyliv;


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                djbo(msg.what);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });

    private boolean falg;
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.zeller.fastlibrary.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public static boolean isForeground = false;

/*    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i("reg", logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i("reg", logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e("reg", logs);
            }
        }
    };
    private static final int MSG_SET_ALIAS = 1001;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d("reg", "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            (String) msg.obj,
                            null,
                            mAliasCallback);
                    break;
                default:
                    Log.i("reg", "Unhandled msg - " + msg.what);
            }
        }
    };*/


    private void djbo(int position) {
        switch (position) {
            case 0://党建播报
                djbo.setVisibility(View.VISIBLE);
                break;
            case 1://严乡强村育新
                yxqcyx.setVisibility(View.VISIBLE);
                break;
            case 2://学习十九大
                xxsjd.setVisibility(View.VISIBLE);
                break;
            case 3://两学一做
                lxyz.setVisibility(View.VISIBLE);
                break;
            case 4://网上e支部
                wsyzz.setVisibility(View.VISIBLE);
                break;
            case 5://党建促扶贫
                djcfp.setVisibility(View.VISIBLE);
                break;
            case 6://流动党员之窗
                lddyzc.setVisibility(View.VISIBLE);
                break;
            case 7://党务助手
                dwzs.setVisibility(View.VISIBLE);
                break;
            case 8://党建+产业链
                djcyliv.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        User user = App.me().login();
        if (null != user) {
            showlistbean();
        }

    }


    private void showlistbean() {
            SharedPreferences sp = getSharedPreferences("app_msg", MODE_PRIVATE);
            String key0 = "_0";
            String key1 = "_1";
            String key2 = "_2";
            String key3 = "_3";
            String key4 = "_4";
            String key5 = "_5";
            String key6 = "_6";
            String key7 = "_7";
            String key8 = "_8";

            String str0 = sp.getString(key0, "0");
            String str1 = sp.getString(key1, "0");
            String str2 = sp.getString(key2, "0");
            String str3 = sp.getString(key3, "0");
            String str4 = sp.getString(key4, "0");
            String str5 = sp.getString(key5, "0");
            String str6 = sp.getString(key6, "0");
            String str7 = sp.getString(key7, "0");
            String str8 = sp.getString(key8, "0");


            if (!str0.equals("0")) {
                setlistbean(sp, key0);
            } else {
                djbo.setVisibility(View.GONE);
            }

            if (!str1.equals("0")) {
                setlistbean(sp, key1);
            } else {
                yxqcyx.setVisibility(View.GONE);
            }
            if (!str2.equals("0")) {
                setlistbean(sp, key2);
            } else {
                xxsjd.setVisibility(View.GONE);
            }
            if (!str3.equals("0")) {
                setlistbean(sp, key3);
            } else {
                lxyz.setVisibility(View.GONE);
            }
            if (!str4.equals("0")) {
                setlistbean(sp, key4);
            } else {
                wsyzz.setVisibility(View.GONE);
            }
            if (!str5.equals("0")) {
                setlistbean(sp, key5);
            } else {
                djcfp.setVisibility(View.GONE);
            }
            if (!str6.equals("0")) {
                setlistbean(sp, key6);
            } else {
                lddyzc.setVisibility(View.GONE);
            }
            if (!str7.equals("0")) {
                setlistbean(sp, key7);
            } else {
                dwzs.setVisibility(View.GONE);
            }
            if (!str8.equals("0")) {
                setlistbean(sp, key8);
            } else {
                djcyliv.setVisibility(View.GONE);
            }


    }


    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                    Log.d("reg", "content:" + messge);
                    String key ="_" + messge;
                    SharedPreferences sp = getSharedPreferences("app_msg", MODE_PRIVATE);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putInt(key + "_what", Integer.valueOf(messge));
                    edit.putString(key + "_msg", "");
                    edit.putString(key, "1");
                    edit.commit();
                    setlistbean(sp, key);

            }
        }
    }

    private void setCostomMsg(String msg) {

    }

    private void assignViews() {

        djbo = (ImageView) findViewById(R.id.djbo);
        yxqcyx = (ImageView) findViewById(R.id.yxqcyx);
        xxsjd = (ImageView) findViewById(R.id.xxsjd);
        lxyz = (ImageView) findViewById(R.id.lxyz);
        wsyzz = (ImageView) findViewById(R.id.wsyzz);
        djcfp = (ImageView) findViewById(R.id.djcfp);
        lddyzc = (ImageView) findViewById(R.id.lddyzc);
        dwzs = (ImageView) findViewById(R.id.dwzs);
        djcyliv = (ImageView) findViewById(R.id.djcyliv);

        homepage = (LinearLayout) findViewById(R.id.homepage);
        shouye = (LinearLayout) findViewById(R.id.shouye);
        viewPager = (FragmentViewPager) findViewById(R.id.viewPager);
//        phone = (ImageButton)findViewById(R.id.phone);
        navBar = (RadioGroup) findViewById(R.id.navBar);
        mHomeFragment = (RadioButton) findViewById(R.id.mHomeFragment);
        mPartyFragment = (RadioButton) findViewById(R.id.mPartyFragment);
        mUserFragment = (RadioButton) findViewById(R.id.mUserFragment);
        mSpeakFragment = (RadioButton) findViewById(R.id.mSpeakFragment);
        img = (ImageView) findViewById(R.id.img);
        String im = "http://218.29.203.38:8096/hcdj/app/images/banner9.jpg";
        Glide.with(this)                             //配置上下文
                .load(im)//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .error(R.drawable.banner)           //设置错误图片
                .placeholder(R.drawable.banner) //设置占位图片
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .centerCrop()
                .into(img);
        travel = (TextView) findViewById(R.id.travel);
        hui_pu_jin_rong = (TextView) findViewById(R.id.hui_pu_jin_rong);
        djcyltv = (TextView) findViewById(R.id.djcyltv);
        flood_information = (TextView) findViewById(R.id.flood_information);
        notice_of_program = (TextView) findViewById(R.id.notice_of_program);
        social_service = (TextView) findViewById(R.id.social_service);
        huimin_subsidies = (TextView) findViewById(R.id.huimin_subsidies);
        pests = (TextView) findViewById(R.id.pests);
        sjd = (TextView) findViewById(R.id.sjd);
        test_layout=(RelativeLayout)findViewById(R.id.test_layout);
    }
    RelativeLayout test_layout;

    private void initViews() {
        View[] views = {travel, hui_pu_jin_rong, djcyltv, pests, flood_information, notice_of_program, social_service, huimin_subsidies, sjd,test_layout};

        for (View view : views) {
            view.setOnClickListener(this);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        User user = App.me().getUser();
        if (user != null) {
            pagerAdapter = new FragmentPagerAdapter(
                    fragmentManager,
                    HomeFragment.class, // 首页
                    PartyFragment.class, // 党员之家
                    SpeakFragment.class, // 信息发布
                    UserFragment.class // 我的

            );
        } else {
            pagerAdapter = new FragmentPagerAdapter(
                    fragmentManager,
                    HomeFragment.class, // 首页
                    PartyFragment.class, // 党员之家
                    LoginFragment.class, // 登录
                    UserFragment.class // 我的

            );
        }
        viewPager.setOffscreenPageLimit(3);
        viewPager.setDuration(1000);
        viewPager.setScrollflag(false);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);
        navBar.setOnCheckedChangeListener(this);
        viewPager.setCurrentItem(0, false);
        RadioButton navItem = (RadioButton) navBar.getChildAt(0);
        navItem.setChecked(true);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        // 判断是否模拟器打开app软件
        if (AntiEmulator.checkPipes() == true && AntiEmulator.checkQEmuDriverFile() == true
                && AntiEmulator.CheckPhoneNumber(this) == true
                && AntiEmulator.CheckDeviceIDS(this) == true && AntiEmulator.CheckImsiIDS(this) == true
                && AntiEmulator.CheckEmulatorBuild() == true) {
            exit();
        }
        started = savedInstanceState != null && savedInstanceState.getBoolean("started", false); // 是否启动过
        restarted = savedInstanceState != null && savedInstanceState.getBoolean("restarted", false); // 是否为重启
        if (!started) { // 如未启动过
            started = true; // 设置为已启动
            //结束后调用
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    shouye.setVisibility(View.GONE);
                }
            }, 3000);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        setContentView(R.layout.activity_main);
        testeveryday=(TextView) findViewById(R.id.testeveryday);
        //testeveryday.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG );
        setTagAndAlias();// 绑定极光推送别名
        assignViews();
        initViews();
        Service();//开启Websocket服务
        registerMessageReceiver();  // used for receive msg

        final GlobalValue globalValue = new GlobalValue();
        mHomeFragment.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 boolean isCheck = globalValue.isCheck();
                                                 if (falg == true) {
                                                     homepage.setVisibility(View.VISIBLE);
                                                 } else if (falg == false) {
                                                     falg = true;
                                                     if (isCheck) {
                                                         if (v == mHomeFragment) {
                                                             homepage.setVisibility(View.VISIBLE);
                                                         }
                                                     }
                                                 }
                                             }
                                         }
        );
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)

        {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimary);//通知栏所需颜色
        }

        try

        {
            PgyUpdateManager.register(this, new PgyUpdateManagerListener(this));
        } catch (
                Exception e
                )

        {
            PgyCrashManager.reportCaughtException(this, e);
            LogUtil.e(MainActivity.class, "检查更新失败", e);
            App.me().toast("检查更新失败");
        }
        String json = getIntent().getStringExtra("json");
        Log.d("reg", "json:" + json);
        User user = App.me().getUser();
        if (null != json) {
            try {
                JSONObject object = new JSONObject(json);
                String object1 = object.getString("json");
                JSONObject object2 = new JSONObject(object1);
                String type = object2.getString("type");
                if (type.equals("1")) {
                    if (null != user) {
                        String noticeId = object2.getString("noticeId");
//                        Intent intent = new Intent(MainActivity.this, NewsdetailActivity.class);
                        Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
//                        intent.putExtra(NewsdetailActivity.REQUEST_ID, noticeId);
                        startActivity(intent);
                    } else {
                        startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), LoginActivity.REQUEST_CODE);
                    }
                } else if (type.equals("2")) {
                    if (null != user) {
                        String newsId = object2.getString("newsId");
                        Intent intent = new Intent(MainActivity.this, NewsdetailActivity.class);
                        intent.putExtra(NewsdetailActivity.REQUEST_ID, newsId);
                        startActivity(intent);
                    } else {
                        startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), LoginActivity.REQUEST_CODE);
                    }

                } else if (type.equals("3")) {
                    String typeName = object2.getString("typeName");
                    if (null != typeName) {
                        if (typeName.equals("党建播报")) {
                            page(1, "0");
                            App.me().clearmsgcount("0");
                        } else if (typeName.equals("党务助手")) {
                            page(2, "3");
                            App.me().clearmsgcount("7");
                        } else if (typeName.equals("严乡强村育新")) {
                            page(1, "5");
                            App.me().clearmsgcount("1");
                        } else if (typeName.equals("党建+产业链")) {
                            page(1, "0");
                            App.me().clearmsgcount("8");
                        } else if (typeName.equals("两学一做")) {
                            page(1, "3");
                            App.me().clearmsgcount("3");
                        } else if (typeName.equals("网上e支部")) {
                            page(1, "2");
                            App.me().clearmsgcount("4");
                        } else if (typeName.equals("党建促扶贫")) {
                            page(1, "4");
                            App.me().clearmsgcount("5");
                        } else if (typeName.equals("流动党员之窗")) {
                            page(2, "1");
                            App.me().clearmsgcount("6");
                        } else if (typeName.equals("学习十九大")) {
                            page(1, "1");
                            App.me().clearmsgcount("2");
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }


    private void page(int i, String type) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (i == 1) {
                    Homepage(type);
                } else if (i == 2) {
                    Partypage(type);
                }
            }
        }, 3000);
    }

    /**
     * 设置标签与别名
     */
    private void setTagAndAlias() {
        /**
         *这里设置了别名，在这里获取的用户登录的信息
         *并且此时已经获取了用户的userId,然后就可以用用户的userId来设置别名了
         **/
        //false状态为未设置标签与别名成功
        //if (UserUtils.getTagAlias(getHoldingActivity()) == false) {
        Set<String> tags = new HashSet<String>();
        //这里可以设置你要推送的人，一般是用户uid 不为空在设置进去 可同时添加多个
        User user = App.me().getUser();
        if (user != null) {
            Log.d("reg", "user.getUuid():" + user.getUuid());
            if (!TextUtils.isEmpty(user.getUuid())) {
                tags.add(user.getIdcard());//设置tag
            }
            //上下文、别名【Sting行】、标签【Set型】、回调
            JPushInterface.setAliasAndTags(App.me(), user.getUuid(), tags,
                    mAliasCallback);
        }
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "别名设置成功";
                    Log.i("reg", logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i("reg", logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 30);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e("reg", logs);
            }
        }
    };
    private static final int MSG_SET_ALIAS = 1001;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d("reg", "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            (String) msg.obj,
                            null,
                            mAliasCallback);
                    break;
                default:
                    Log.i("reg", "Unhandled msg - " + msg.what);
            }
        }
    };

    private void Service() {
        if (App.me().login() != null) {
            Intent intent = new Intent(this, WebSocketService.class);
            startService(intent);
        }
    }


    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, WebSocketService.class);
        stopService(intent);
        unregisterReceiver(mMessageReceiver);
        PgyUpdateManager.unregister(); // 注销蒲公英更新监听
        super.onDestroy();
//        XGPushManager.unregisterPush(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //得到了授权
                    Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                    try {
                        PgyUpdateManager.register(this, new PgyUpdateManagerListener(this));
                    } catch (Exception e) {
                        PgyCrashManager.reportCaughtException(this, e);
                        LogUtil.e(MainActivity.class, "检查更新失败", e);
                        App.me().toast("检查更新失败");
                    }
                } else {
                    //未授权
                    Toast.makeText(this, "您已禁止自动更新", Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse("https://www.pgyer.com/TMOP");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    //结束整个应用程序
    public void exit() {
        //遍历 链表，依次杀掉各个Activity
        for (Activity activity : activityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        //杀掉，这个应用程序的进程，释放 内存
        int id = android.os.Process.myPid();
        if (id != 0) {
            android.os.Process.killProcess(id);
        }
    }


    @Override
    protected void onResume() {
        isForeground = true;
        User user = App.me().login();
        if (null != user) {
            showlistbean();
        }
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }


    @Subscriber(mode = ThreadMode.ASYNC)
    public void getbean(String s) {
        Log.d("reg", "================================" + s);
        try {
//            pp++;
            JSONObject data = new JSONObject(s);
            String contentString = data.getString("content");
            JSONObject content = new JSONObject(contentString);
            User log = App.me().login();
            if (log != null) {
                Log.d("reg", "content:" + content);
                String key = log.getUuid() + "_" + content.getString("position");
                SharedPreferences sp = getSharedPreferences("app_msg", MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putInt(key + "_what", Integer.valueOf(content.getString("position")));
                edit.putString(key + "_msg", content.getString("typeName"));
                edit.putString(key, "1");
                edit.commit();
                setlistbean(sp, key);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setlistbean(SharedPreferences sp, String key) {
        String str = sp.getString(key, "0");
        if (!str.equals("0")) {
            Message msg = new Message();
            msg.what = sp.getInt(key + "_what", 0);
            msg.obj = sp.getString(key + "_msg", "");
            handler.sendMessage(msg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //党建播报
            case R.id.travel:
                falg = true;
                App.me().clearmsgcount("0");
                Homepage("");
//                Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
//                startActivityForResult(intent, MainActivity.REQUEST_CODE);
                break;
            //严乡强村育新
            case R.id.hui_pu_jin_rong:
                falg = true;
                App.me().clearmsgcount("1");
                Homepage("4");
//                Intent intent1 = new Intent(HomePageActivity.this, MainActivity.class);
//                intent1.putExtra("switchPage", "switchPage(4)");
//                startActivity(intent1);
                break;
            //党建+产业链
            case R.id.djcyltv:
                App.me().clearmsgcount("8");
                //Partypage("0");
                Intent it=new Intent(MainActivity.this, DjCylActivity.class);
                startActivity(it);
                break;
            //两学一做
            case R.id.pests:
                falg = true;
                App.me().clearmsgcount("3");
//                if (activity instanceof MainActivity) {
//                    ((MainActivity) activity).onPageSelected(1);
//                }
                Homepage("3");
//                Intent intent3 = new Intent(HomePageActivity.this, MainActivity.class);
//                intent3.putExtra("switchPage", "switchPage(2)");
//                startActivity(intent3);
                break;
            //网上e支部
            case R.id.flood_information:
                falg = true;
                App.me().clearmsgcount("4");
                Homepage("2");
//                Intent intent4 = new Intent(HomePageActivity.this, MainActivity.class);
//                intent4.putExtra("switchPage", "switchPage(1)");
//                startActivity(intent4);
                break;
            //党建促扶贫
            case R.id.notice_of_program:
//                falg = true;
//                Homepage("4");
//                App.me().clearmsgcount("5");

                Intent intent5 = new Intent(MainActivity.this, PovertyAlleviationActivity.class);
//                intent5.putExtra("switchPage", "switchPage(3)");
                startActivity(intent5);

//                Intent intent5 = new Intent(HomePageActivity.this, MainActivity.class);
//                intent5.putExtra("switchPage", "switchPage(3)");
//                startActivity(intent5);
                break;
            //流动党员之窗
            case R.id.social_service:
                Partypage("0");
                App.me().clearmsgcount("6");
//                Intent intent6 = new Intent(HomePageActivity.this, MainActivity.class);
//                intent6.putExtra("switchPage2", "switchPage(1)");
//                intent6.putExtra("mark", "party");
//                startActivity(intent6);
                break;
            //党务助手
            case R.id.huimin_subsidies:
                Partypage("2");
                App.me().clearmsgcount("7");
//                Intent intent7 = new Intent(HomePageActivity.this, MainActivity.class);
//                intent7.putExtra("switchPage2", "switchPage(3)");
//                intent7.putExtra("mark", "party");
//                startActivity(intent7);
                break;
            //学习十九大
            case R.id.sjd:
                falg = true;
                Homepage("1");
                App.me().clearmsgcount("2");
//                Intent intent8 = new Intent(HomePageActivity.this, MainActivity.class);
//                intent8.putExtra("switchPage2", "switchPage(2)");
//                intent8.putExtra("mark", "party");
//                startActivity(intent8);
                break;

            //每日一测
            case R.id.test_layout:
                Partypage("1");
                break;

        }
        showlistbean();
    }

    private void Homepage(String switchPage) {
        homepage.setVisibility(View.GONE);
        HomeFragment homef = (HomeFragment) pagerAdapter.getItem(0);
        homef.Wbe(switchPage);

        FragmentActivity activity = this;
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).onPageSelected(0);
        }

//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        HomeFragment  homeFragment = new HomeFragment().newInstance(switchPage);
//        transaction.add(R.id.lay_frame, homeFragment);
//        transaction.commit();
    }


    private void Partypage(String switchPage) {
        homepage.setVisibility(View.GONE);
        PartyFragment homef = (PartyFragment) pagerAdapter.getItem(1);
        homef.Wbe(switchPage);
//        Bundle bundle = new Bundle();
//        bundle.putString("mes", switchPage);
//        PartyFragment mf = new PartyFragment();
////将bundle绑定到MyFragment的对象上
//        mf.setArguments(bundle);
//        FragmentTransaction shiwu = getSupportFragmentManager().beginTransaction();
//        shiwu.replace(R.id.lay_frame, mf);
//        shiwu.commit();
        FragmentActivity activity = this;
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).onPageSelected(1);
        }
//
//        homepage.setVisibility(View.GONE);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        PartyFragment mpartyFragment = new PartyFragment().newInstance(switchPage);
//        transaction.add(R.id.lay_frame, mpartyFragment);
//        transaction.commit();
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    @Override
    public void onBackPressed() { // 连续按下两次返回键才退出App
        long currentTimeMillis = System.currentTimeMillis();
        if (onBackPressedTimeMillis != 0 && currentTimeMillis - onBackPressedTimeMillis < 3000) {
            super.onBackPressed();
        } else {
            App.me().toast("再按一次返回键退出");
//            boolean oo1 = AntiEmulator.CheckEmulatorBuild();
//            String s1 = String.valueOf(oo1);
//            App.me().toast("是Build："+ s1 );

        }
        App.me().NoPrompt();
        onBackPressedTimeMillis = currentTimeMillis;
    }

    @Subscriber(tag = "UserFragment.onLogoutSuccess")
    private void trasiation(User user) {
        pagerAdapter = new FragmentPagerAdapter(
                getSupportFragmentManager(),
                HomeFragment.class, // 首页
                PartyFragment.class, // 党员之家
                LoginFragment.class, // 登录
                UserFragment.class // 我的
//                LoginFragment.class // 登录
        );
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);
        navBar.setOnCheckedChangeListener(this);
        viewPager.setCurrentItem(3, false);
        RadioButton navItem = (RadioButton) navBar.getChildAt(3);
        navItem.setChecked(true);
        mUserFragment.setChecked(true);
    }


    @Subscriber(tag = "Login")
    private void trasiation1(String user) {
        if (user != null & user.equals("0")) {
            pagerAdapter = new FragmentPagerAdapter(
                    getSupportFragmentManager(),
                    HomeFragment.class, // 首页
                    PartyFragment.class, // 党员之家
                    SpeakFragment.class, // 信息发布
                    UserFragment.class // 我的
//                LoginFragment.class // 登录
            );
            viewPager.setAdapter(pagerAdapter);
            viewPager.addOnPageChangeListener(this);
            navBar.setOnCheckedChangeListener(this);
            viewPager.setCurrentItem(3, false);
            RadioButton navItem = (RadioButton) navBar.getChildAt(2);
            navItem.setChecked(true);
            mSpeakFragment.setChecked(true);
        } else if (user != null) {
            pagerAdapter = new FragmentPagerAdapter(
                    getSupportFragmentManager(),
                    HomeFragment.class, // 首页
                    PartyFragment.class, // 党员之家
                    SpeakFragment.class, // 信息发布
                    UserFragment.class // 我的
//                LoginFragment.class // 登录
            );
            int i = Integer.parseInt(user);
            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(i, false);

        }

    }

    @Subscriber(tag = "speak")
    private void Speak(String s) {
        if (null != s && s.equals("1")) {
            EventBus.getDefault().post("1", "refresh");
//            Bimp.drr.clear();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == LoginActivity.REQUEST_CODE)) {
            if (resultCode == RESULT_OK) {
            }
        }
        // 将请求回调结果传递到子页面(片段)中处理具体业务
//        if (started && !restarted) { // 如果主控制器被结束掉而重启的话就不调用
//            for (int i = 0; i < pagerAdapter.getCount(); i++) {
//                Fragment fragment = pagerAdapter.getItem(i);
//                if (fragment != null) {
//                    fragment.onActivityResult(requestCode & 0xffff, resultCode, data);
//                }
//            }
//        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) { // 页面切换同步选项卡
        RadioButton navItem = (RadioButton) navBar.getChildAt(position);
        navItem.setChecked(true);
        if (position == 1 || position == 2 || position == 3) {
            falg = false;
        }


//        User user = App.me().getUser();
//        if (user == null && position == 4) {
//            startActivityForResult(new Intent(App.me().getApplicationContext(), LoginActivity.class), LoginActivity.REQUEST_CODE);
//        }
        Fragment fragment = pagerAdapter.getItem(position);
        if (fragment instanceof PagerFragment) {
            ((PagerFragment) fragment).onPageSelected();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) { // 点击选项卡切换页面

        View navItem = group.findViewById(checkedId);
        int index = group.indexOfChild(navItem);
        viewPager.setCurrentItem(index, false);

        // 切换页面时自动隐藏输入法
        View view = getWindow().getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            App.me().input().hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }
}
