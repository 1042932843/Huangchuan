package com.zeller.fastlibrary.huangchuang.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.http.client.HttpRequest;
import com.squareup.picasso.Picasso;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.Volunteer;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;

/**
 * Created by Administrator on 2017/6/16 0016.
 */
public class EventdetailsActivity extends BaseActivity implements View.OnClickListener {
    public static final String REQUEST_ID = "id";
    private ImageView back;
    private EventdetailsApi eventdetailsApi;
    private ImageView tit_img;
    private TextView title;
    private TextView end_time;
    private TextView beginDate;
    private TextView address;
    private TextView introduce;
    private TextView author;
    private Button submit;
    private Volunteer child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventdetails);
        eventdetailsApi = new EventdetailsApi(this);
        assignViews();
        initViews();
        String id = getIntent().getStringExtra(REQUEST_ID);
        if (null != id) {
            eventdetailsApi.Eventdetails(id);
        }
    }

    private void assignViews() {
        back = (ImageView) findViewById(R.id.back);
        tit_img = (ImageView) findViewById(R.id.tit_img);
        title = (TextView) findViewById(R.id.title);
        end_time = (TextView) findViewById(R.id.end_time);
        beginDate = (TextView) findViewById(R.id.beginDate);
        address = (TextView) findViewById(R.id.address);
        introduce = (TextView) findViewById(R.id.introduce);
        author = (TextView) findViewById(R.id.author);
        submit = (Button) findViewById(R.id.submit);
    }

    private void initViews() {
        View[] views = {back, submit};
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

            case R.id.submit:
                Submit();
                break;
        }
    }

    private void Submit() {
        Intent intent = new Intent(EventdetailsActivity.this, EnlistActivity.class);
        intent.putExtra(EnlistActivity.REQUEST_ID,child.getId());
        startActivityForResult(intent, EnlistActivity.REQUEST_CODE);
    }

    private class EventdetailsApi extends HttpUtil {
        private EventdetailsApi(Context context) {
            super(context); // 传递上下文, 初始化进度对话框
        }


        public void Eventdetails(String id) {
            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?volunteerDetail",
                    "id", id
            );
        }


        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                child = JSON.parseObject(apiMsg.getResult(), Volunteer.class);
                if (null != child) {
                    title.setText(child.getTitle());
                    end_time.setText("开始时间：" + child.getBeginDate());
                    beginDate.setText("结束时间：" +child.getEndDate() );
                    address.setText(child.getAddress());
                    introduce.setText(child.getIntroduce());
                    author.setText(child.getAuthor());
                    if (!child.getNewsImgUrl().equals("null")||null!=child.getNewsImgUrl()){
                        if (child.getNewsImgUrl().isEmpty()) {

                        } else{
                            Picasso.with(EventdetailsActivity.this).load(child.getNewsImgUrl()).into(tit_img);
                        }
                    }
                    if (!child.getStatus().equals("1")){
                        submit.setText("报名已截止");
                        submit.setEnabled(false);
                        submit.setBackgroundColor(Color.GRAY);
                    }
                }
            } else {
                App.me().toast(apiMsg.getMessage());
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EnlistActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
