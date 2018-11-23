package com.zeller.fastlibrary.huangchuang.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.http.client.HttpRequest;
import com.squareup.picasso.Picasso;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.activity.AboutUsActivity;
import com.zeller.fastlibrary.huangchuang.activity.CollectionActivity;
import com.zeller.fastlibrary.huangchuang.activity.IntercalateActivity;
import com.zeller.fastlibrary.huangchuang.activity.LoginActivity;
import com.zeller.fastlibrary.huangchuang.activity.MyfilesActivity;
import com.zeller.fastlibrary.huangchuang.activity.NewslistActivity;
import com.zeller.fastlibrary.huangchuang.activity.NoticeActivity;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.User;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class UserFragment extends PagerFragment implements View.OnClickListener {

    private RelativeLayout Myfiles;
    private RelativeLayout intercalate;
    private RelativeLayout tongzhi;
    private RelativeLayout collection;
    private RelativeLayout Aboutus;
    private RelativeLayout newslist;
    private LinearLayout info;
    private UserApi userApi;
    private TextView realName;
    private TextView position;
    private User user;
    private Button login;
    private  String string;
    private ImageView img;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        userApi = new UserApi(getActivity());
        User user = App.me().getUser();
        if (null != user) {
            userApi.User(user.getUuid());
        }
        assignViews(view);
        initViews();
        return view;
    }

    @Subscriber(tag = "Login")
    private void Login(String s) {
        if (null != s && s.equals("1")) {
            User user = App.me().getUser();
            if (null != user) {
                userApi.User(user.getUuid());
            }
        }
    }
    private void assignViews(View view) {
        Myfiles = (RelativeLayout) view.findViewById(R.id.Myfiles);
        Aboutus = (RelativeLayout) view.findViewById(R.id.Aboutus);
        intercalate = (RelativeLayout) view.findViewById(R.id.intercalate);
        tongzhi = (RelativeLayout) view.findViewById(R.id.tongzhi);
        collection = (RelativeLayout) view.findViewById(R.id.collection);
        newslist = (RelativeLayout) view.findViewById(R.id.newslist);
        realName = (TextView) view.findViewById(R.id.realName);
        position = (TextView) view.findViewById(R.id.position);
        login = (Button) view.findViewById(R.id.login);
        info= (LinearLayout) view.findViewById(R.id.info);
        img = (ImageView) view.findViewById(R.id.img);
    }

    private void initViews() {
        View[] views = {Myfiles, intercalate,login,tongzhi,collection,Aboutus,newslist};
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }


/*    public static UserFragment newInstance(String arg1) {
        UserFragment homeFragment = new UserFragment();
        Bundle bundle = new Bundle();
        bundle.putString("arg1", arg1);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }*/

    private void InfoUser(User user) {
        if (null != user) {
            info.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            if (null != user.getRealName()) {
                realName.setText(user.getRealName());
            }
            if (null != user.getPosition()) {
                position.setText(user.getPosition());
            }
            if (!user.getPicture().isEmpty()){
                Picasso.with(App.me()).load(user.getPicture()).fit().placeholder(R.drawable.icon_user).into(img);
            }
        }else {
            info.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            realName.setText(null);
            position.setText(null);
        }
    }
    @Subscriber(tag = "Main")
    private void ssss(String string) {
        this.string =string;
        if (null!=string){
            User user = App.me().getUser();
            if (null != user) {
                userApi.User(user.getUuid());
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        User user = App.me().getUser();

        if (null == user) {
            switch (v.getId()) {
                case R.id.Myfiles:
                case R.id.intercalate:
                case R.id.login:
                case R.id.tongzhi:
                case R.id.collection:
                case R.id.newslist:
                    login();
                    break;
                case R.id.Aboutus:
                    Aboutu();
                    break;
            }
        } else {
            switch (v.getId()) {
                case R.id.Myfiles:
                    Myfiles();
                    break;
                case R.id.intercalate:
                    Intercalate();
                    break;
                case R.id.tongzhi:
                    Tongzhi();
                    break;
                case R.id.collection:
                    Collection();
                    break;
                case R.id.Aboutus:
                    Aboutu();
                    break;
                case R.id.newslist:
                    Newslist();
                    break;
            }
        }

    }

private  void Newslist(){
    Intent intent = new Intent(getActivity(), NewslistActivity.class);
    startActivity(intent);
}

    private void Collection() {
        Intent intent = new Intent(getActivity(), CollectionActivity.class);
        startActivity(intent);
    }
    private void Aboutu() {
        Intent intent = new Intent(getActivity(), AboutUsActivity.class);
        startActivity(intent);
    }

    private void Tongzhi() {

        Intent intent = new Intent(getActivity(), NoticeActivity.class);
//        Intent intent = new Intent(getActivity(), ChoiceVillageActivity.class);
//        Intent intent = new Intent(getActivity(), AccountActivity.class);
//        Intent intent = new Intent(getActivity(), SpeakFragment.class);
        startActivity(intent);

    }

    private void login() {
        startActivityForResult(new Intent(getActivity(), LoginActivity.class), LoginActivity.REQUEST_CODE);
    }

    private void Intercalate() {
        Intent intent = new Intent(getActivity(), IntercalateActivity.class);
        startActivityForResult(intent, IntercalateActivity.REQUEST_CODE);
    }

    private void Myfiles() {
        if (null != user) {
            Intent intent = new Intent(getActivity(), MyfilesActivity.class);
            intent.putExtra("realName", user.getRealName());
            intent.putExtra("sex", user.getSex());
            intent.putExtra("height", user.getHeight());
            intent.putExtra("weight", user.getWeight());
            intent.putExtra("birthday", user.getBirthday());
            intent.putExtra("nativePlace", user.getNativePlace());
            intent.putExtra("homePhone", user.getHomePhone());
            intent.putExtra("phone", user.getPhone());
            intent.putExtra("position", user.getPosition());
            intent.putExtra("joinPartyDate", user.getJoinPartyDate());
            intent.putExtra("picture", user.getPicture());
            intent.putExtra("departmentName", user.getDepartmentName());
            intent.putExtra("heightpublic", user.getHeightPublic());
            intent.putExtra("weightpublic", user.getWeightPublic());
            intent.putExtra("idcardpublic", user.getIdcardPublic());
            intent.putExtra("homephonepublic", user.getHomePhonePublic());
            intent.putExtra("nativeplacepublic", user.getNativePlacePublic());
            intent.putExtra("phonepublic", user.getPhonePublic());
            intent.putExtra("birthdaypublic", user.getBirthdayPublic());


            startActivityForResult(intent, MyfilesActivity.REQUEST_CODE);
        } else {
            login();
        }
    }

    private class UserApi extends HttpUtil {

        private UserApi(Context context) {
            super(context); // 传递上下文, 初始化进度对话框
        }


        public void User(String uuid) {

            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneUserOnlineController.do?loginUserInfo", // 登录接口地址
                    "uuid", uuid

            );
        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                // 登录成功, 保存用户登录信息, 持久化
                hideDialog(); // 立即隐藏进度对话框
                String resultInfo = apiMsg.getResult();
                user = JSON.parseObject(resultInfo, User.class);
                InfoUser(user);
                if (null!=string&&string.equals("1")){
                    hideDialog(); // 立即隐藏进度对话框
                }
            } else if (apiMsg.getState().equals("-1")){
                App.me().logout();
//                DataCleanManager.cleanApplicationData(getActivity());
            }else {
                App.me().toast(apiMsg.getMessage());
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        User user = App.me().getUser();
        if (requestCode == LoginActivity.REQUEST_CODE && resultCode == LoginActivity.RESULT_OK) {
            if (null != user) {
                userApi.User(user.getUuid());
                EventBus.getDefault().post("3", "Login");
            }else {
                InfoUser(null);
                Picasso.with(getActivity()).load(R.drawable.icon_user).into(img);
            }
        } else if (requestCode == IntercalateActivity.REQUEST_CODE && resultCode == IntercalateActivity.RESULT_OK) {
            if (null == user) {
                InfoUser(null);
                Picasso.with(getActivity()).load(R.drawable.icon_user).into(img);
            }
        } else if (requestCode == MyfilesActivity.REQUEST_CODE && resultCode == MyfilesActivity.RESULT_OK) {
            if (null != user) {
                userApi.User(user.getUuid());
            }
        }
    }
}
