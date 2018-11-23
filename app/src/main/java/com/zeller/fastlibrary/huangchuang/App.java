package com.zeller.fastlibrary.huangchuang;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.awen.camera.CameraApplication;
import com.lidroid.xutils.DbUtils;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.pgyersdk.Pgy;
import com.pgyersdk.activity.FeedbackActivity;
import com.pgyersdk.crash.PgyCrashManager;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zeller.fastlibrary.BuildConfig;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.greendaoTest.DaoMaster;
import com.zeller.fastlibrary.greendaoTest.DaoSession;
import com.zeller.fastlibrary.huangchuang.model.User;
import com.zeller.fastlibrary.huangchuang.util.LogUtil;

import org.greenrobot.greendao.database.Database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


public class App extends Application {

    private static App me; // 全局公用
    private SharedPreferences sp;
    private SharedPreferences sharedPreferences;
    private static final String db_name = "search_history";
    private static DaoSession daoSession;

    // 全局公用
    public static App me() {
        return me;
    }

    // 数据库
    private DbUtils db;

    // 区域数据库
    private DbUtils dbArea;

    // 城市代码 默认为珠海
    private int cityCode = 440400;


    // 土司提示控件 全局公用
    private Toast toast;

    // 自定义土司文本控件
    private TextView toastText;

    // 输入法管理
    private InputMethodManager input;


    private boolean wxpay_flag;


    // 获取数据库
    public DbUtils getDb() {
        if (db == null) {
            db = DbUtils.create(this, "app.db", 4, null);
        }
        return db;
    }


    // 获取选择的城市代码 用于获取区域资讯
    public int getCityCode() {
        return cityCode;
    }

    // 设置选择的城市代码 切换城市
    public boolean setCityCode(int cityCode) {
        int oldCityCode = this.cityCode;
        this.cityCode = cityCode;
        return oldCityCode != cityCode;
    }

/*    // 设置登录成功后的用户实体
    public void setUser(User user) {
        DbUtils db = getDb();
        try {
            db.deleteAll(User.class); // 清除全部, 仅保存一条记录
            if (user != null) {
                db.save(user); // 持久化到数据库
            }
        } catch (DbException e) {
            LogUtil.e(App.class, e.getMessage(), e);
        }
        this.user = user;
    }

    // 获取登录的用户实体 如未登录则返回空对象
    public User getUser() {
        if (user == null) {
            DbUtils db = getDb();
            try {
                user = db.findFirst(User.class);
            } catch (DbException e) {
                LogUtil.e(App.class, e.getMessage(), e);
            }
        }
        return user;
    }*/


public void clearmsgcount(String type) {
        SharedPreferences sp = me().getSharedPreferences("app_msg", me().MODE_PRIVATE);
        String key ="_" + type;
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(key + "_what", 0);
        edit.putString(key + "_msg", "");
        edit.putString(key, "0");
        edit.commit();

}

    //病人数量
    public int getPatient_size() {
        SharedPreferences sharedPreferences = me.getSharedPreferences("sys_info", 0);
        return sharedPreferences.getInt("patient_size", 0);
    }

    public void setPatient_size(int patient_size) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("sys_info", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putInt("patient_size", patient_size);
        e.commit();

    }


    public void Empty() {
        if (null != sp) {
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.commit();
        }
    }

    // 弹出全局土司, 如提示内容为空则不显示
    public void toast(CharSequence text) {
        if (text == null || text.length() == 0) return;
        if (toast == null) {
            toastText = new TextView(me);
            toastText.setBackgroundResource(R.drawable.toast_background);
            toastText.setTextColor(Color.WHITE);
            toastText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            toast = Toast.makeText(me, null, Toast.LENGTH_LONG);
            toast.setView(toastText);
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toastText.setText(text);
        toast.show();
    }

    // 弹出全局土司, 如提示内容为空则不显示
    public void toast(final CharSequence text, Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                toast(text);
            }
        });
    }


    // 弹出全局土司, 从字符串资源中获取
    public void toast(int resId) {
        if (resId != 0) toast(me.getResources().getString(resId));
    }

    // 获取输入法管理服务
    public InputMethodManager input() {
        if (input == null) {
            input = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        }
        return input;
    }

    //    // 绑定推送服务, 重复调用则重新绑定
//    public void bindPushService() {
//        User user = getUser();
//        if (user != null) {
//            // 调用 Handler 来异步设置别名
//        }
//        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, "123"));
//    }



    @Override
    public void onCreate() {
        super.onCreate();
        createDaoSession();
        me = this;
        initWX();
        try {
//            SDKInitializer.initialize(this); // 初始化百度地图
            Pgy.setDebug(BuildConfig.DEBUG); // 蒲公英
            PgyCrashManager.register(me = this); // 蒲公英异常捕捉
            FeedbackActivity.setBarImmersive(true); // 蒲公英反馈开启沉浸式布局 未用到该功能
            CameraApplication.init(this, true);
            JPushInterface.setDebugMode(true);
            JPushInterface.init(this);
//            bindPushService();
        } catch (Exception e) {
            LogUtil.e(App.class, e.getMessage(), e);
        }


    }

    public static final String APP_ID="wxbc0dd0a431f1f29e";
    public IWXAPI iwxapi;

    public void initWX(){
        iwxapi= WXAPIFactory.createWXAPI(this,APP_ID,true);
        iwxapi.registerApp(APP_ID);

    }


    //首单完成
    public String getFinished_order() {
        SharedPreferences sharedPreferences = me.getSharedPreferences("sys_info", 0);
        return sharedPreferences.getString("finished_order", "0");
    }

    public void setFinished_order(String finished_order) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("sys_info", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        if (null != finished_order) {
            e.putString("finished_order", finished_order);
        } else {
            e.putString("finished_order", "0");
        }
        e.commit();
    }


    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public void createDaoSession() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, db_name);
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }


    public int getNoPrompt() {
        sharedPreferences = me.getSharedPreferences("No_sys_info", 0);
        return sharedPreferences.getInt("Notprompt", 0);
    }

    public void setNoPrompt(int Notprompt) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("No_sys_info", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putInt("Notprompt", Notprompt);
        e.commit();
    }

    public int getNoPrompt1() {
        sharedPreferences = me.getSharedPreferences("No_sys_info1", 0);
        return sharedPreferences.getInt("Notprompt1", 0);
    }

    public void setNoPrompt1(int Notprompt) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("No_sys_info1", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putInt("Notprompt1", Notprompt);
        e.commit();
    }


    public void NoPrompt() {
        if (null != sharedPreferences) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
        }
    }

    LiteOrm orm;

    public LiteOrm orm() {
        if (orm == null) {
            orm = LiteOrm.newSingleInstance(this, "LocalStorage.db");
            orm.setDebugged(BuildConfig.DEBUG);
        }
        return orm;
    }

    public User getUser() {
        if (user == null) {
            List<User> list = orm().query(QueryBuilder.get(User.class).limit(0, 1));
            if (list.size() > 0) {
                user = list.get(0);
            }
        }
        return user;
    }

    public void login(@NonNull User user) {
        orm().deleteAll(User.class);
        orm().save(this.user = user);
    }

    @NonNull
    User user;

    public User login() {
        if (user == null) {
            List<User> list = orm().query(QueryBuilder.get(User.class).limit(0, 1));
            if (list.size() > 0) {
                user = list.get(0);
                Log.d("reg", "user:" + user.getUuid() + user.getRealName() + user.getIdcard());
            }
        }
        return user;
    }

    public void logout() {
        orm().deleteAll(User.class);
        this.user = null;
    }

    //根据毫秒值获取时间差
    public long getDateTime(String dateTime) throws Exception {
        //先把字符串转成Date类型
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date1 = sdf.parse(dateTime);
        Date date2 = sdf.parse(sdf.format(new Date(System.currentTimeMillis())));
        long longDate1 = date1.getTime();
        long longDate2 = date2.getTime();
        return longDate1 - longDate2;
    }

    public void hideInput(Window window) {
        View view = window.getCurrentFocus();
        hideInput(view);
    }

    public void hideInput(View view) {
        if (view != null) {
            view.clearFocus();
            IBinder binder = view.getWindowToken();
            input().hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //根据毫秒值获取时间
    public String getFormatedDateTime(String dateTime) throws Exception {
        long l = getDateTime(dateTime);
        if (l <= 0) {
            return null;
        } else {
            long day1 = l / 1000 / 60 / 60 / 24;
            long day2 = (l % (1000 * 60 * 60 * 24));
            if (day1 <= 0) {
                if (day2 <= 0) {
                    return null;
                } else {
                    long h = day2 / (1000 * 60 * 60);
                    long m = day2 % (1000 * 60 * 60) / (1000 * 60);
                    return "还有 " + h + "小时" + m + "分开始服务";
                }
            } else {
                long h = day2 / (1000 * 60 * 60);
                long m = day2 % (1000 * 60 * 60) / (1000 * 60);
                return "还有 " + day1 + "天" + h + "小时" + m + "分开始服务";
            }
        }
    }

    //首次点击
    public boolean IsFirst(String first) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("sys_info", 0);
        return sharedPreferences.getBoolean("FI_" + first, true);
    }

    public void setFirst(String first) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("sys_info", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean("FI_" + first, false);
        e.commit();
    }

    public void setIsFirst(String first) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("sys_info", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean("FI_" + first, true);
        e.commit();
    }

    //补差价
    public boolean IsPrice(String first) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("sys_info", 0);
        return sharedPreferences.getBoolean(first, true);
    }

    public void setPrice(String first) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("sys_info", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean(first, false);
        e.commit();
    }

    public void setIsPrice(String first) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("sys_info", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean(first, true);
        e.commit();
    }


    public void call(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("联系客服");
        builder.setMessage("您是否现在拨打4006-713-913人工客服？");
        builder.setPositiveButton("现在联系", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 调用系统拨号
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel:400-671-3913")));

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:4006-713-913"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /*
        监听GPS
      */
    public void initGPS(final Activity activity) {
        LocationManager locationManager = (LocationManager) activity
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            App.me().toast("请打开GPS");
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setMessage("请打开GPS");
            dialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivityForResult(intent, 0); // 设置完成后返回到原来的界面

                        }
                    });
            dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {
            // 弹出Toast
//          Toast.makeText(TrainDetailsActivity.this, "GPS is ready",
//                  Toast.LENGTH_LONG).show();
//          // 弹出对话框
//          new AlertDialog.Builder(this).setMessage("GPS is ready")
//                  .setPositiveButton("OK", null).show();
        }
    }

    public boolean isWxpay_flag() {
        return wxpay_flag;
    }

    public void setWxpay_flag(boolean wxpay_flag) {
        this.wxpay_flag = wxpay_flag;
    }


    // 如果是模拟器运行第一次的时候就是保存
    public boolean IsCheckPipes(boolean first) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("sys_info", 0);
        return sharedPreferences.getBoolean("FI", true);
    }

    public void setCheckPipes(boolean first) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("sys_info", 0);
        if (!sharedPreferences.contains("FI")) {
            SharedPreferences.Editor e = sharedPreferences.edit();
            e.putBoolean("FI", first);
            e.commit();
        }

    }

    public void setIsCheckPipes(boolean first) {
        SharedPreferences sharedPreferences = me.getSharedPreferences("sys_info", 0);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean("FI", true);
        e.commit();
    }

    public boolean getCheckPipes() {
        SharedPreferences sharedPreferences = me.getSharedPreferences("sys_info", 0);
        return sharedPreferences.getBoolean("FI", true);
    }
}
