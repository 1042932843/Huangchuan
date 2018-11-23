package com.zeller.fastlibrary.huangchuang.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.http.client.HttpRequest;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.ucloud.ucommon.Utils;
import com.ucloud.uvod.UMediaProfile;
import com.ucloud.uvod.UPlayerStateListener;
import com.ucloud.uvod.widget.UVideoView;
import com.umeng.analytics.MobclickAgent;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;

import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.User;
import com.zeller.fastlibrary.huangchuang.permission.PermissionsActivity;
import com.zeller.fastlibrary.huangchuang.permission.PermissionsChecker;
import com.zeller.fastlibrary.huangchuang.util.Constant;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;
import com.zeller.fastlibrary.huangchuang.util.Util;
import com.zeller.fastlibrary.huangchuang.view.CustomWebView;
import com.zeller.fastlibrary.huangchuang.view.PullToRefreshLayout;
import com.zeller.fastlibrary.huangchuang.view.UEasyPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by Administrator on 2017/6/9 0009.
 */
public class NewsdetailActivity extends Activity implements View.OnClickListener, PtrHandler, UPlayerStateListener {
    public static final String REQUEST_ID = "id";
    public static final int REQUEST_CODE = 'n' + 'e' + 'w' + 's' + 'd' + 'e' + 't';
    private NewsdetailApi newsdetailApi;
    private CancelApi cancelApi;
    private ImageView back;
    private PullToRefreshLayout pull;
    private CustomWebView webView;
    private TextView createDate;
    private TextView title;
    private TextView topview_title_txtv;
    private TextView headerTitle;
    private FrameLayout header;
    private TextView complain;
    private String newsDetail;
    private ComplainApi complainApi;
    private String id;
    private String isCollection;
    private PermissionsChecker permissionsChecker; //for android target version >=23
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String TAG = "EasyPlayer";

    private RelativeLayout rela1;

    private ImageButton topview_left_button;
    private boolean mReceiverTag = false;   //广播接受者标识
    @Bind(R.id.video_main_view)
    UEasyPlayer easyPlayer;

    @Bind(R.id.hud_view)
    TableLayout debugInfoHudView;

    @Bind(R.id.button_share)
    TextView button_share;

    private RelativeLayout share_lay;

    private String uri;
    private String videoUrl;
    //    private String uri ="http://113.106.249.196:801/001001/gmzy/zyk/gwyks/CPSIJKG0811170644021.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsdetail);
        ButterKnife.bind(this);
        newsdetailApi = new NewsdetailApi(this);
        complainApi = new ComplainApi(this);
        cancelApi = new CancelApi(this);
        assignViews();
        initViews();
        permissionsChecker = new PermissionsChecker(this);
        if (permissionsChecker.lacksPermissions(permissions)) {
            PermissionsActivity.startActivityForResult(this, REQUEST_CODE, permissions);
        }
     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            setTranslucentStatus(this, true);

        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源

        tintManager.setStatusBarTintResource(R.color.zhuti);*/
        id = getIntent().getStringExtra(REQUEST_ID);
        User(id);
    }

    private void pay(String uri) {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        String intentAction = getIntent().getAction();
        if (!TextUtils.isEmpty(intentAction) && intentAction.equals(Intent.ACTION_VIEW)) {
            Uri data = getIntent().getData();
            if (data != null && "content".equals(getIntent().getScheme())) {
                Cursor cursor = this.getContentResolver().query(data, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    uri = cursor.getString(0);
                    cursor.close();
                }
            } else {
                uri = Uri.decode(uri);
            }
        }
        easyPlayer.init(this);
        UMediaProfile profile = new UMediaProfile();
        profile.setInteger(UMediaProfile.KEY_LIVE_STREAMING, 0); //标识播放的流为直播源，还是点播源(0点播，1直播)
        profile.setInteger(UMediaProfile.KEY_START_ON_PREPARED, 0); //当prepread成功后自动开始播放，(无须自己监听prepared消息调用start方法) 直播推荐开启(1开启，0不开启)
        profile.setInteger(UMediaProfile.KEY_MEDIACODEC, 0); //视频解码方式，推荐软解
        profile.setInteger(UMediaProfile.KEY_RENDER_TEXTURE, 1); //视频渲染方式，推荐
        profile.setInteger(UMediaProfile.KEY_PREPARE_TIMEOUT, 1000 * 15); //设置第一次播放流地址时，prepared超时时间(超过设置的值，sdk内部会做重连动作，单位ms)
        profile.setInteger(UMediaProfile.KEY_READ_FRAME_TIMEOUT, 1000 * 15); //设置播放过程中，网络卡顿出现读取数据超时(超过设置的值，sdk内部会做重连动作，单位ms)
        profile.setInteger(UMediaProfile.KEY_ENABLE_BACKGROUND_PLAY, 1); //设置切换到后台是否继续播放，直播推荐开启，(默认为0不开启)
        profile.setInteger(UMediaProfile.KEY_MAX_RECONNECT_COUNT, 5); //当发生IOERROR PREPARE_TIMEOUT READ_FRAME_TIMEOUT 最大重连次数，默认5次

        //若需要区分4G是否继续播放等与用户确认相关的操作，设置为0，自行根据Android API监听网络状态调用setVideoPath做重连控制操作。
        profile.setInteger(UMediaProfile.KEY_ENABLE_NETWORK_RECOVERY_RECONNECT, 1); //当发生网络切换恢复时SDK内部会做重连（默认为0 不开启 1不开启)
        profile.setInteger(UMediaProfile.KEY_IS_MUSIC_PLAYER, 0); //如果播放的是纯音频流，设置为1，默认为0

        if (uri != null && uri.endsWith("m3u8")) {
            profile.setInteger(UMediaProfile.KEY_MAX_CACHED_DURATION, 0); // m3u8 默认不开启延时丢帧策略
        }

        easyPlayer.setMediaProfile(profile);
        easyPlayer.setScreenOriention(UEasyPlayer.SCREEN_ORIENTATION_SENSOR);
        easyPlayer.setPlayerStateLisnter(this);

//        easyPlayer.setMenuItemSelectedListener(this);

//        if (getIntent().getIntExtra(MainActivity.KEY_SHOW_DEBUG_INFO, 1) == 1) {
//            easyPlayer.setHudView(debugInfoHudView);
//        }
        easyPlayer.initAspectRatio(UVideoView.VIDEO_RATIO_FIT_PARENT);
        easyPlayer.setVideoPath(uri);
        if (!mReceiverTag) {
            IntentFilter filter = new IntentFilter();
            mReceiverTag = true;    //标识值 赋值为 true 表示广播已被注册
            filter.setPriority(1000);
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkStateListener, filter);
        }

    }

    private void initViews() {
        back.setOnClickListener(this);
        button_share.setOnClickListener(this);
        headerTitle.setOnClickListener(this);

        pull.setPtrHandler(this);
        complain.setOnClickListener(this);
        WebSettings settings = webView.getSettings();
        if (android.os.Build.VERSION.SDK_INT < 18) {
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pull.refreshComplete();
            }
        });


    }

    /**
     * 屏幕旋转时调用此方法
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //newConfig.orientation获得当前屏幕状态是横向或者竖向
        //Configuration.ORIENTATION_PORTRAIT 表示竖向
        //Configuration.ORIENTATION_LANDSCAPE 表示横屏
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Toast.makeText(NewsdetailActivity.this, "现在是竖屏", Toast.LENGTH_SHORT).show();
            header.setVisibility(View.VISIBLE);
            topview_left_button.setVisibility(View.GONE);
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(NewsdetailActivity.this, "现在是横屏", Toast.LENGTH_SHORT).show();
            header.setVisibility(View.GONE);
            topview_left_button.setVisibility(View.VISIBLE);
        }
    }

    private void assignViews() {
        topview_left_button = (ImageButton) findViewById(R.id.topview_left_button);
        header = (FrameLayout) findViewById(R.id.header);
        title = (TextView) findViewById(R.id.title);
        topview_title_txtv = (TextView) findViewById(R.id.topview_title_txtv);
        headerTitle = (TextView) findViewById(R.id.headerTitle);
        createDate = (TextView) findViewById(R.id.createDate);
        complain = (TextView) findViewById(R.id.complain);
        back = (ImageView) findViewById(R.id.back);
        pull = (PullToRefreshLayout) findViewById(R.id.pull);
        webView = (CustomWebView) findViewById(R.id.webView);
        rela1 = (RelativeLayout) findViewById(R.id.rela1);
        //共享页面
        share_lay = (RelativeLayout) findViewById(R.id.share_lay);

        share_lay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
        RelativeLayout share_btn1 = (RelativeLayout) findViewById(R.id.share_btn1);
        RelativeLayout share_btn2 = (RelativeLayout) findViewById(R.id.share_btn2);
//        RelativeLayout share_btn3 = (RelativeLayout) findViewById(R.id.share_btn3);
   /*     RelativeLayout share_btn5 = (RelativeLayout) findViewById(R.id.share_btn5);*/
//        RelativeLayout share_btn7 = (RelativeLayout) findViewById(R.id.share_btn7);
//        RelativeLayout share_btn8 = (RelativeLayout) findViewById(R.id.share_btn8);
//        RelativeLayout share_btn9 = (RelativeLayout) findViewById(R.id.share_btn9);
        Button share_cen = (Button) findViewById(R.id.share_cen);
        share_btn1.setOnClickListener(share);
        share_btn2.setOnClickListener(share);
//        share_btn3.setOnClickListener(share);
//        share_btn5.setOnClickListener(share);
//        share_btn7.setOnClickListener(share);
//        share_btn8.setOnClickListener(share);
//        share_btn9.setOnClickListener(share);
        share_cen.setOnClickListener(share);
    }

    View.OnClickListener share = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.share_btn1:
                    shareWeb(mTargetScene);

                    break;
                case R.id.share_btn2:
                    shareWeb(mTargetScenePY);
                    break;

                case R.id.share_cen:
                    share_lay.setVisibility(share_lay.GONE);
                    break;

                default:
                    break;
            }

        }
    };

    public void shareWeb(int Target){

        boolean ins=App.me().iwxapi.isWXAppInstalled();
        if(!ins){
            App.me().toast("请安装微信后使用");
            return;
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = Constant.DOMAIN+"/hcdj/webpage/wxNewsDetail.html?id="+id;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = getString(R.string.app_name);
        msg.description = title.getText().toString();
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = Target;
        App.me().iwxapi.sendReq(req);
    }
    private static final int THUMB_SIZE = 150;
    private int mTargetScene = SendMessageToWX.Req.WXSceneSession;
    private int mTargetScenePY = SendMessageToWX.Req.WXSceneTimeline;    //设置发送到朋友圈
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
    //分享
    public void wechat(String name) {


        /*if (!WXPayEntryActivity.isWXAppInstalled(App.me())) {
            App.me().toast("没有安装微信，请下载安装");
        }else {
            try {
                boolean b = getlogo();
                // 获取单个平台
                Platform weixin = ShareSDK.getPlatform(name);
                Platform.ShareParams sp = new Platform.ShareParams();
                sp.setShareType(Platform.SHARE_WEBPAGE);
                sp.setTitle(getString(R.string.app_name));//分享标题
                sp.setText(title.getText().toString());//分享文本
                if (b) {
                    sp.setImagePath("/mnt/sdcard/share1.png");
                } else {
                    sp.setImageUrl(Constant.DOMAIN + "/images/ic_launcher.png");
                }
                sp.setUrl("http://218.29.203.38:8096/hcdj/webpage/wxNewsDetail.html?id="+id);
                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                sp.setTitleUrl("http://218.29.203.38:8096/hcdj/webpage/wxNewsDetail.html?id="+id);
                // comment是我对这条分享的评论，仅在人人网和QQ空间使用
                sp.setComment(title.getText().toString());//分享文本
                // site是分享此内容的网站名称，仅在QQ空间使用
                sp.setSite(getString(R.string.app_name));
                // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                sp.setSiteUrl("http://218.29.203.38:8096/hcdj/webpage/wxNewsDetail.html?id="+id);
                weixin.setPlatformActionListener(NewsdetailActivity.this);
                weixin.share(sp);


            } catch (Exception e) {
                Log.e("reg", "err", e);
            }
        }*/


    }

    private boolean getlogo() {
        File f = new File("/mnt/sdcard/share1.png");
        if (!f.exists() || f.length() == 0) {

            AssetManager assetManager = getResources().getAssets();
            InputStream input;
            OutputStream os = null;
            BufferedOutputStream stream = null;
            try {
                os = new FileOutputStream(f);
                if (!f.exists()) {
                    new File(f.getParent()).mkdirs();
                }
                stream = new BufferedOutputStream(os);
                input = assetManager.open("share.png");
                byte buffer[] = new byte[4 * 1024];
                int len = 0;
                while ((len = input.read(buffer)) != -1) {
                    stream.write(buffer, 0, len);
                }
                stream.flush();
                stream.close();
                os.flush();
                os.close();
                input.close();
                return true;
            } catch (Exception e) {
                Log.e("reg", "ERR", e);
                return false;
            }
        } else {
            //Logo已经存在
            return true;
        }
    }

    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {

        Window win = activity.getWindow();

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
    public void finish() {
        if (webView != null) {
            webView.loadUrl("about:blank");
        }
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.isCustomViewShowing()) {
            webView.hideCustomView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {

        if (null != videoUrl && !videoUrl.equals("")) {
            if (!Utils.isNetworkConnected(this)) {
                Toast.makeText(this, R.string.info1, Toast.LENGTH_SHORT).show();
                return;
            }
            switch (Utils.getConnectedType(this)) {
                case ConnectivityManager.TYPE_MOBILE:
                    Toast.makeText(this, R.string.info2, Toast.LENGTH_SHORT).show();
                    break;
                case ConnectivityManager.TYPE_ETHERNET:
                    Toast.makeText(this, R.string.info3, Toast.LENGTH_SHORT).show();
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    Toast.makeText(this, R.string.info4, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.complain:
                if (null != isCollection) {
                    if (isCollection.equals("是")) {
//                        App.me().toast("已收藏");
                        Cancel();
                    } else if (isCollection.equals("否")) {
//                        complain.setText("收藏");
                        Complain();
                    }
                }
                break;
            case R.id.headerTitle:
               /* Intent intent = new Intent(NewsdetailActivity.this, UEasyPlayerActivity.class);
                startActivity(intent);*/
                break;

            case R.id.button_share:
                share_lay.setVisibility(share_lay.VISIBLE);
                break;


        }
    }

    private void User(String id) {
        Log.d("reg", "id:" + id);
        User user = App.me().getUser();
        if (null != user) {
            if (null != id) {
                newsdetailApi.Newsdetail(user.getUuid(), id);
            }
            complain.setVisibility(View.VISIBLE);
        } else {
            if (null != id) {
                newsdetailApi.Newsdetail("", id);
            }
            complain.setVisibility(View.GONE);
        }

    }

    private void Complain() {
        User user = App.me().getUser();
        if (null != user) {
            complainApi.Complain(id, user.getUuid());
        } else {
            startActivityForResult(new Intent(NewsdetailActivity.this, LoginActivity.class), LoginActivity.REQUEST_CODE);
        }
    }

    private void Cancel() {
        User user = App.me().getUser();
        if (null != user) {
            cancelApi.Cancel(user.getUuid(), id);
        } else {
            startActivityForResult(new Intent(NewsdetailActivity.this, LoginActivity.class), LoginActivity.REQUEST_CODE);
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View content, View header) {
        return webView.getScrollY() == 0 && PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {


        webView.loadDataWithBaseURL(null, newsDetail, "text/html", "utf-8", null);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient());


        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        webView.setHorizontalScrollBarEnabled(false);

        webView.getSettings().setSupportZoom(false);

        webView.getSettings().setBuiltInZoomControls(false);

        webView.setHorizontalScrollbarOverlay(true);

        WebSettings settings = webView.getSettings();
        settings.setTextSize(WebSettings.TextSize.LARGEST);

    }



    private class NewsdetailApi extends HttpUtil {

        private NewsdetailApi(Context context) {
            super(context); // 传递上下文, 初始化进度对话框
        }


        public void Newsdetail(String uuid, String newsId) {

            if (null != uuid) {
                send(HttpRequest.HttpMethod.POST,
                        "hcdj/phoneNewsController.do?newsDetails",
                        "uuid", uuid,
                        "newsId", newsId
                );
            } else {
                send(HttpRequest.HttpMethod.POST,
                        "hcdj/phoneNewsController.do?newsDetails",
                        "newsId", newsId
                );
            }

        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                // 登录成功, 保存用户登录信息, 持久化
                String resultInfo = apiMsg.getResult();
                try {
                    JSONObject jsonObect = new JSONObject(resultInfo);
                    String createDateText = jsonObect.getString("createDate");
                    String newsTitle = jsonObect.getString("newsTitle");
                    newsDetail = jsonObect.getString("newsDetail");
                    isCollection = jsonObect.getString("isCollection");
                    videoUrl = jsonObect.getString("videoUrl");
                    String videoName = jsonObect.getString("videoName");
                    if (null != videoUrl && !videoUrl.equals("")) {
                        rela1.setVisibility(View.VISIBLE);
                        pay(videoUrl);
                    } else {
                        rela1.setVisibility(View.GONE);
                    }

                    if (null != videoName && !videoName.equals("")) {
                        topview_title_txtv.setText(videoName);
                    }

                    if (null != isCollection) {
                        if (isCollection.equals("是")) {
                            complain.setText("已收藏");
                        } else if (isCollection.equals("否")) {
                            complain.setText("收藏");
                        }
                    }

                    if (null != createDateText) {
                        createDate.setText(createDateText);
                    }
                    if (null != newsTitle) {
                        title.setText(newsTitle);
                    }


                    pull.post(new Runnable() {
                        @Override
                        public void run() {
                            pull.autoRefresh();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                if (apiMsg.getState().equals("-1")) {
                    setResult(RESULT_OK);
                    finish();
                    App.me().toast(apiMsg.getMessage());
                } else {
                    App.me().toast(apiMsg.getMessage());
                }
            }
        }

    }

    private class ComplainApi extends HttpUtil {

        private ComplainApi(Context context) {
            super(context);
        }


        public void Complain(String newsId, String uuid) {

            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?addCollection",
                    "newsId", newsId,
                    "uuid", uuid,
                    "type", "1"

            );
        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                // 登录成功, 保存用户登录信息, 持久化
//                String resultInfo = apiMsg.getResult();
                App.me().toast(apiMsg.getMessage());
                User(id);
                complain.setText("已收藏");
            } else {
                App.me().toast(apiMsg.getMessage());
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
                App.me().toast(apiMsg.getMessage());
                User(id);
                complain.setText("收藏");
            } else {
                App.me().toast(apiMsg.getMessage());
            }


        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        easyPlayer.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        easyPlayer.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (easyPlayer != null) {
            easyPlayer.onDestroy();
        }
        if (mReceiverTag) {
            if (null != networkStateListener) {
                unregisterReceiver(networkStateListener);
                mReceiverTag = false;   //Tag值 赋值为false 表示该广播已被注销
            }
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (easyPlayer.isFullscreen()) {
                easyPlayer.toggleScreenOrientation();
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public boolean onSettingMenuSelected(UMenuItem item) {
//        return false;
//    }

    private BroadcastReceiver networkStateListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = manager.getActiveNetworkInfo();
                if (activeInfo == null) {
                    Toast.makeText(context, R.string.info1, Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    public void onPlayerStateChanged(State state, int extra1, Object extra2) {
        Log.i(TAG, "lifecycle->EasyPlayer->demo-> onPlayerStateChanged " + state.name());
        switch (state) {
            case PREPARING:
                break;
            case PREPARED:
                break;
            case START:
                break;
            case PAUSE:
                break;
            case STOP:
                break;
            case VIDEO_SIZE_CHANGED:
                break;
            case COMPLETED:
                break;
            case RECONNECT:
                if (extra1 < 0) {
                    Log.e(TAG, "lifecycle->EasyPlayer->demo->RECONNECT reconnect failed.");
                } else if (extra1 == 0) {
                    Log.e(TAG, "lifecycle->EasyPlayer->demo->RECONNECT reconnect failed & info = " + extra2);
                } else {
                    Log.e(TAG, "lifecycle->EasyPlayer->demo->RECONNECT reconnect count = " + extra1 + ", info = " + extra2);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlayerInfo(Info info, int extra1, Object extra2) {
        switch (info) {
            case BUFFERING_START:
                Log.i(TAG, "lifecycle->EasyPlayer->demo-> onPlayerInfo BUFFERING_START.");
                break;
            case BUFFERING_END:
                Log.i(TAG, "lifecycle->EasyPlayer->demo-> onPlayerInfo BUFFERING_END.");
                break;
            case BUFFERING_UPDATE:
                break;
            case BUFFERING_PERCENT:
                //extra2
                break;
            case VIDEO_RENDERING_START:
                break;
            case AUDIO_RENDERING_START:
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlayerError(Error error, int extra1, Object extra2) {
        switch (error) {
            case IOERROR:
                Log.i(TAG, "lifecycle->EasyPlayer->demo-> onPlayerError IOERROR.");
                break;
            case PREPARE_TIMEOUT://just a warn
                Log.i(TAG, "lifecycle->EasyPlayer->demo-> onPlayerError PREPARE_TIMEOUT.");
                break;
            case READ_FRAME_TIMEOUT://just a warn
                Log.i(TAG, "lifecycle->EasyPlayer->demo-> onPlayerError READ_FRAME_TIMEOUT.");
                break;
            case UNKNOWN:
                Log.i(TAG, "lifecycle->EasyPlayer->demo-> onPlayerError UNKNOWN.");
                break;
            default:
                break;
        }
    }



}
