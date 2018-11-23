package com.zeller.fastlibrary.huangchuang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zeller.fastlibrary.R;


/**
 * Created by Administrator on 2017/6/15 0015.
 */
public class StudioActivity extends BaseActivity implements View.OnClickListener {

    private ImageView back;
    private RelativeLayout opinion;
    private RelativeLayout studio;
    private RelativeLayout zzxf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio);
        assignViews();
        initViews();
    }

    private void assignViews() {
        back = (ImageView) findViewById(R.id.back);
        opinion = (RelativeLayout) findViewById(R.id.opinion);
        studio = (RelativeLayout) findViewById(R.id.studio);
        zzxf = (RelativeLayout) findViewById(R.id.zzxf);
    }

    private void initViews() {
        View[] views = {back, opinion, studio,zzxf};
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
            case R.id.opinion:
                Opinion("1", "社情民意");
                break;
            case R.id.studio:
                Opinion("2", "约见党代表");
                break;
            case R.id.zzxf:
                Intent intent = new Intent(StudioActivity.this, PetitionActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void Opinion(String type, String title) {
        Intent intent = new Intent(StudioActivity.this, OpinionActivity.class);
        intent.putExtra(OpinionActivity.REQUEST_NAME, title);
        intent.putExtra(OpinionActivity.REQUEST_TYPE, type);
        intent.putExtra("apiType", "DY");
        startActivity(intent);
    }
}
