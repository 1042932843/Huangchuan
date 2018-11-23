package com.zeller.fastlibrary.huangchuang.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.http.client.HttpRequest;
import com.squareup.picasso.Picasso;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.User;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;
import com.zeller.fastlibrary.huangchuang.view.SortModel;

/**
 * Created by Administrator on 2017/6/14 0014.
 */
public class MaillistDetailsActivity extends BaseActivity implements View.OnClickListener {
    public static final String REQUEST_ID = "id";
    private ImageView back;
    private ImageView tx;
    private MaillistDetailsApi maillistDetailsApi;
    private SortModel user;
    private TextView realName;
    private TextView sex_s;
    private TextView height;
    private TextView weight;
    private TextView birthday;
    private TextView nativePlace;
    private TextView homePhone;
    private TextView phone;
    private TextView position;
    private TextView rudang_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maillistdetails);
        maillistDetailsApi = new MaillistDetailsApi(this);
        String id = getIntent().getStringExtra(REQUEST_ID);
        User user = App.me().getUser();
        if (null != user || null != id) {
            maillistDetailsApi.MaillistDetails(user.getUuid(), id);
        } else {
            startActivityForResult(new Intent(MaillistDetailsActivity.this, LoginActivity.class), LoginActivity.REQUEST_CODE);
        }
        assignViews();
        initViews();
    }


    private void InfoUser(SortModel user) {
        realName.setText(user.getRealName());
        height.setText(user.getHeight());
        weight.setText(user.getWeight());
        birthday.setText(user.getBirthday());
        nativePlace.setText(user.getNativePlace());
        homePhone.setText(user.getHomePhone());
        phone.setText(user.getPhone());
        position.setText(user.getPosition());
        rudang_time.setText(user.getJoinPartyDate());


        String url = HttpUtil.getImageUrl(user.getPhoto());
        if (url != null) {
            Picasso.with(App.me()).load(url).fit().placeholder(R.drawable.icon_default_user).into(tx);
        }

//        if (null!=user.getPhoto()||!user.getPhoto().equals("")){
//
//                Picasso.with(MaillistDetailsActivity.this).load(user.getPhoto()).into(tx);
//
//        }else {
//            .setImageResource(R.drawable.icon_default_user);
//        }


        if (user.getSex().equals("1")) {
            sex_s.setText("男");
        } else {
            sex_s.setText("女");
        }
    }


    private void assignViews() {
        back = (ImageView) findViewById(R.id.back);
        tx = (ImageView) findViewById(R.id.tx);
        realName = (TextView) findViewById(R.id.realName);
        sex_s = (TextView) findViewById(R.id.sex);
        height = (TextView) findViewById(R.id.height);
        weight = (TextView) findViewById(R.id.weight);
        birthday = (TextView) findViewById(R.id.birthday);
        nativePlace = (TextView) findViewById(R.id.nativePlace);
        homePhone = (TextView) findViewById(R.id.homePhone);
        phone = (TextView) findViewById(R.id.phone);
        position = (TextView) findViewById(R.id.position);
        rudang_time = (TextView) findViewById(R.id.rudang_time);
    }

    private void initViews() {
        View[] views = {back, phone, homePhone};
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.phone:
                if (null != user) {
                    call(user.getPhone());
                }
                break;
            case R.id.homePhone:
                if (null != user) {
                    call(user.getHomePhone());
                }
                break;
        }
    }

    private void call(String phone) {
        if (!phone.isEmpty()){
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private class MaillistDetailsApi extends HttpUtil {


        private MaillistDetailsApi(Context context) {
            super(context);

        }

        private void MaillistDetails(String uuid, String userId) {

            send(
                    HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?partyUserInfo", // 接口地址
                    "uuid", uuid,
                    "userId", userId
            );
        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                user = JSON.parseObject(apiMsg.getResult(), SortModel.class);
                InfoUser(user);
            }
            App.me().toast(apiMsg.getMessage());
        }

    }
}
