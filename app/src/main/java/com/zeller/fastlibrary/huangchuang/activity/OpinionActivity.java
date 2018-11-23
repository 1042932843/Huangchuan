package com.zeller.fastlibrary.huangchuang.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.http.client.HttpRequest;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.User;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;
import com.zeller.fastlibrary.huangchuang.util.StringUtil;

/**
 * 社情民意
 */
public class OpinionActivity extends BaseActivity implements View.OnClickListener {
    public static final String REQUEST_NAME = "opinion";
    public static final String REQUEST_TYPE = "type";
    public static final String REQUEST_ID = "id";
    private Button complain;
    private TextView headerTitle;
    private TextView areaName;
    private TextView partyrepresentative;
    private ImageView back;
    private EditText feedback;
    private EditText name;
    private EditText phone;
    private OpinionApi opinionApi;
    private String apiType;
    private RelativeLayout partyrepresentative_rela;
    private String dy_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion);
        opinionApi = new OpinionApi(this);
        assignViews();
        initViews();
        apiType = getIntent().getStringExtra("apiType");
        dy_type = getIntent().getStringExtra(REQUEST_TYPE);
        if (apiType.equals("DY")) {
            if (dy_type.equals("1")) {
                partyrepresentative_rela.setVisibility(View.GONE);
            }
        } else if (apiType.equals("QZ")) {
            if (dy_type.equals("1")) {
                partyrepresentative_rela.setVisibility(View.GONE);
            }
        }
        String titText = getIntent().getStringExtra(REQUEST_NAME);
        if (null != titText) {
            headerTitle.setText(titText);
        }
    }

    private void assignViews() {
        back = (ImageView) findViewById(R.id.back);
        complain = (Button) findViewById(R.id.complain);
        headerTitle = (TextView) findViewById(R.id.headerTitle);
        areaName = (TextView) findViewById(R.id.areaName);
        partyrepresentative = (TextView) findViewById(R.id.partyrepresentative);
        feedback = (EditText) findViewById(R.id.feedback);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        partyrepresentative_rela = (RelativeLayout) findViewById(R.id.partyrepresentative_rela);
    }

    private void initViews() {
        View[] views = {complain, back, areaName, partyrepresentative};
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
            case R.id.complain:
                Complain();
                break;
            case R.id.areaName:
                AreaName();
                break;
            case R.id.partyrepresentative:
                Partyrepresentative();
                break;

        }
    }

    private void Partyrepresentative() {
        Intent intent = new Intent(this, PartyBranchActivity.class);
        intent.putExtra(PartyBranchActivity.REQUEST_ID, areaName.getTag().toString());
        startActivityForResult(intent, PartyBranchActivity.REQUEST_CODE);
    }

    private void AreaName() {
        Intent intent = new Intent(this, AreaNameActivity.class);
        startActivityForResult(intent, AreaNameActivity.REQUEST_CODE);
    }

    private void Complain() {
        String type = getIntent().getStringExtra(REQUEST_TYPE);
        String id = getIntent().getStringExtra(REQUEST_ID);
        String feedbackText = feedback.getText().toString();
        String nameText = name.getText().toString();
        String phoneText = phone.getText().toString();
        String areaNameText = areaName.getText().toString();
        Log.d("reg", "areaNameText:" + areaNameText);
        Log.d("reg", "areaNameText:" + areaNameText.length());
        Log.d("reg", "apiType:" + apiType);

        if (feedbackText.length() == 0) {
            App.me().toast("请输入反馈内容");
            feedback.requestFocus();
            return;
        }

        if (nameText.length() == 0) {
            App.me().toast("请输入名字");
            name.requestFocus();
            return;
        }

        if (phoneText.length() == 0) {
            App.me().toast("请输入手机号码");
            phone.requestFocus();
            return;
        }

        if (!StringUtil.matchesPhone(phoneText)) {
            App.me().toast("手机号码格式不正确");
            phone.requestFocus();
            return;
        }

        if (areaNameText.length() == 0) {
            App.me().toast("请选择党支部");
            AreaName();
            return;
        }

        User user = App.me().getUser();
        if (opinionApi != null) {
            String userid;
            if(user==null){
                userid="";
            }else{
                userid=user.getUuid();
            }

            if (apiType.equals("DY")) {
                if (type.equals("1")) {
                    opinionApi.Opinion(userid, nameText, phoneText, feedbackText, type, "", "", areaName.getTag().toString());
                } else if (type.equals("2")) {
                    String partyrepresentativeText = partyrepresentative.getText().toString();
                    if (partyrepresentativeText.length() == 0) {
                        App.me().toast("请选择党代表");
                        Partyrepresentative();
                        return;
                    }
                    opinionApi.Opinion(userid, nameText, phoneText, feedbackText, type, partyrepresentative.getTag().toString(), partyrepresentativeText, areaName.getTag().toString());
                }
            } else if (apiType.equals("QZ")) {
                if (type.equals("1")) {
                    opinionApi.Opinionwsezb(userid, nameText, phoneText, feedbackText, type, "", "", areaName.getTag().toString());
                } else if (type.equals("2")) {
                    String partyrepresentativeText = partyrepresentative.getText().toString();
                    if (partyrepresentativeText.length() == 0) {
                        App.me().toast("请选择党代表");
                        Partyrepresentative();
                        return;
                    }
                    opinionApi.Opinionwsezb(userid, nameText, phoneText, feedbackText, type, partyrepresentative.getTag().toString(), partyrepresentativeText, areaName.getTag().toString());
                }
//                opinionApi.Opinionwsezb(id, nameText, phoneText, feedbackText, type, areaName.getTag().toString());
            }
        }

    }

    private class OpinionApi extends HttpUtil {

        private OpinionApi(Context context) {
            super(context); // 传递上下文, 初始化进度对话框
        }


        public void Opinion(String uuid, String name, String phone, String detail, String type, String ddbId, String ddbName, String areaId) {
            if (type.equals("1")) {
                send(HttpRequest.HttpMethod.POST,
                        "hcdj/phoneNewsController.do?addDyDdbgzs", // 社情民意
                        "uuid", uuid,
                        "name", name,
                        "phone", phone,
                        "detail", detail,
                        "type", type,
                        "areaId", areaId

                );
            } else if (type.equals("2")) {
                send(HttpRequest.HttpMethod.POST,
                        "hcdj/phoneNewsController.do?addDyDdbgzs", // 党员约见
                        "uuid", uuid,
                        "name", name,
                        "phone", phone,
                        "detail", detail,
                        "type", type,
                        "ddbId", ddbId,
                        "ddbName", ddbName,
                        "areaId", areaId
                );
            }


        }

        public void Opinionwsezb(String id, String name, String phone, String detail, String type,  String ddbId, String ddbName, String areaId) {
            if (type.equals("1")) {
                send(HttpRequest.HttpMethod.POST,
                        "hcdj/phoneNewsController.do?addQzDdbgzs",//群众
//                        "areaId", id,
                        "name", name,
                        "phone", phone,
                        "detail", detail,
                        "type", type,
                        "areaId", areaId
                );
            } else if (type.equals("2")) {
                send(HttpRequest.HttpMethod.POST,
                        "hcdj/phoneNewsController.do?addQzDdbgzs",//群众
//                        "areaId", id,
                        "name", name,
                        "phone", phone,
                        "detail", detail,
                        "type", type,
                        "ddbId", ddbId,
                        "ddbName", ddbName,
                        "areaId", areaId
                );
            }

        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                App.me().toast(apiMsg.getMessage());
                finish();
            } else {
                App.me().toast(apiMsg.getMessage());
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AreaNameActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            if (null != data) {
                Log.d("reg", "data.getStringExtra(\"id\"):" + data.getStringExtra("id"));
                partyrepresentative.setText("");
                partyrepresentative.setTag("");
                areaName.setText(data.getStringExtra("content"));
                areaName.setTag(data.getStringExtra("id"));
                if (null != areaName.getTag().toString()) {
                    if (dy_type.equals("1")) {
                        partyrepresentative_rela.setVisibility(View.GONE);
                    } else if (dy_type.equals("2")) {
                        partyrepresentative_rela.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else if (requestCode == PartyBranchActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            if (null != data) {
                partyrepresentative.setText(data.getStringExtra("content"));
                partyrepresentative.setTag(data.getStringExtra("id"));
            }
        }
    }
}
