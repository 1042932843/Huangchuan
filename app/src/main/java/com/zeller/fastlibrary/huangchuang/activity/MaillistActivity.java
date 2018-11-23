/*
 * Copyright (c) 2015 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zeller.fastlibrary.huangchuang.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.http.client.HttpRequest;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.adapter.SortAdapter;
import com.zeller.fastlibrary.huangchuang.listener.SystemBarTintManager;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.User;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;
import com.zeller.fastlibrary.huangchuang.view.PinYinKit;
import com.zeller.fastlibrary.huangchuang.view.PinyinComparator;
import com.zeller.fastlibrary.huangchuang.view.SortModel;
import com.zeller.fastlibrary.huangchuang.view.widget.SideBar;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用户联系人列表
 *
 * @author kymjs (http://www.kymjs.com/) on 7/24/15.
 */
public class MaillistActivity extends BaseActivity implements SideBar
        .OnTouchingLetterChangedListener, TextWatcher {
    public static final String REQUEST_UUID = "uuid"; // 订单id

    @BindView(id = R.id.school_friend_member)
    private ListView mListView;
    private TextView mFooterView;
    public PinyinComparator comparator = new PinyinComparator();
    private KJHttp kjh = null;
    private List<SortModel> datas;
    private SortAdapter mAdapter;
    private ImageView back;
    private  MaillistApi maillistApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maillist);
        maillistApi = new MaillistApi(this);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(this, true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源

        tintManager.setStatusBarTintResource(R.color.colorPrimary);
        User user =App.me().getUser();
        if (null!=user){
            maillistApi.Maillist(user.getUuid());
        }
        initViews();

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
    private  void  initViews(){
        SideBar mSideBar = (SideBar) findViewById(R.id.school_friend_sidrbar);
        TextView mDialog = (TextView) findViewById(R.id.school_friend_dialog);
        EditText mSearchInput = (EditText) findViewById(R.id.school_friend_member_search_input);
        mListView = (ListView) findViewById(R.id.school_friend_member);
        mSideBar.setTextView(mDialog);
        mSideBar.setOnTouchingLetterChangedListener(this);
        mSearchInput.addTextChangedListener(this);

        // 给listView设置adapter
        mFooterView = (TextView) View.inflate(MaillistActivity.this, R.layout.item_list_contact_count, null);
        mListView.addFooterView(mFooterView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id == -1){

                }else {
                    Log.d("reg", "position:"+position+"   id:"+id);
                    SortModel sortModel = datas.get(position);
                    Intent intent = new Intent(MaillistActivity.this, MaillistDetailsActivity.class);
                    intent.putExtra(MaillistDetailsActivity.REQUEST_ID, sortModel.getId());
                    startActivity(intent);
                }
            }
        });

//        doHttp();

    }


//    private void doHttp() {
//        User user = App.me().getUser();
//        if (null!=user){
//            kjh.get(Constant.DOMAIN+"/hcdj/phoneNewsController.do?partyUserList&uuid="+user.getUuid(), new HttpCallBack() {
//                @Override
//                public void onSuccess(String t) {
//                    super.onSuccess(t);
//                    parser(t);
//                }
//            });
//        }
//
//    }


    private void parser(String json) {

        try {
            JSONObject jsonObect = new JSONObject(json);
//            JSONObject ja = jsonObect.getJSONObject("result");
            JSONArray array = jsonObect.getJSONArray("list");
            datas = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.optJSONObject(i);
                SortModel data = new SortModel();
                data.setName(object.optString("realName"));
//                data.setInfo(object.optString("photo"));
                Log.d("reg", "mmmm"+object.optString("photo"));
                data.setId(object.optString("id"));
                data.setPhone(object.optString("phone"));
                if (object.optString("photo").equals("")){
                    data.setPhoto("1");
                }else {
                    data.setPhoto(object.optString("photo"));
                }
                datas.add(data);

            }
            try {
                datas =  filledData(datas);
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
            mFooterView.setText("搜索到"+datas.size() + "位联系人");
//            userListNumTxt.setText("全部："+"\t"+sortModelList.size()+"个联系人");

            // sort by a-z
            Collections.sort(datas, comparator);
            mAdapter = new SortAdapter(getApplicationContext(), datas);
            mListView.setAdapter(mAdapter);

//            Collections.sort(datas);
//            mAdapter = new ContactAdapter(mListView, datas);
//            mListView.setAdapter(mAdapter);
        } catch (JSONException e) {
            KJLoger.debug("解析异常" + e.getMessage());
        }
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = 0;
        // 该字母首次出现的位置
        if (mAdapter != null) {
            position = mAdapter.getPositionForSection(s.charAt(0));
        }
        if (position != -1) {
            mListView.setSelection(position);
        } else if (s.contains("#")) {
            mListView.setSelection(0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        try {
            filerData(s.toString());
     } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
      }
//        if (city != null && city.length() > 0) {
//            ArrayList<GovernmentForthTypes> area = new ArrayList<GovernmentForthTypes>();
//            for (GovernmentForthTypes a : this.olist) {
//                if (a.getItemsName().indexOf(city) != -1) {
//                    area.add(a);
//                }
//            }
//            return area;
//        } else {
//            return this.olist;
//        }


//        ArrayList<Contact> temp = new ArrayList<Contact>();
//        for (Contact data : datas) {
//            Log.d("reg","s.toString() :"+s.toString() );
//             if (data.getName().contains(s) || data.getPinyin().contains(s)) {
//                 Log.d("reg","s:"+s.toString());
//                 Log.d("reg","data.getName():"+data.getName());
//                 Log.d("reg","data.getPinyin():"+data.getPinyin());
//                 temp.add(data);
//            }
//             else {
//                temp.remove(data);
//            }

//            if (data.getName().indexOf(s.toString()) != -1) {
//                Log.d("reg","data.getName():"+data.getName());
//                temp.add(data);
//            }
//        }
//        Collections.sort(temp);
//        mAdapter.updateListView(temp);
//        if (mAdapter != null) {
//            mAdapter.refresh(temp);
//        }
    }
    private List<SortModel> filledData(List<SortModel> date) throws BadHanyuPinyinOutputFormatCombination {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for(int i=0; i<date.size(); i++){
            SortModel sortModel = new SortModel();
            sortModel.setName(date.get(i).getName());
            sortModel.setPhone(date.get(i).getPhone());
            sortModel.setId(date.get(i).getId());
            sortModel.setPhoto(date.get(i).getPhoto());
            Log.d("reg","photo:"+date.get(i).getName());
            Log.d("reg","photo:"+date.get(i).getPhone());
            Log.d("reg","photo:"+date.get(i).getId());
            Log.d("reg","photo:"+date.get(i).getPhoto());
            //汉字转换成拼音
            String pinyin = PinYinKit.getPingYin(date.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                sortModel.setSortLetters(sortString.toUpperCase());
            }else{
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    private void filerData(String str) throws BadHanyuPinyinOutputFormatCombination {
        List<SortModel> fSortModels = new ArrayList<SortModel>();
        if (TextUtils.isEmpty(str))
            fSortModels =datas ;
        else
        {
            fSortModels.clear();
            for (SortModel sortModel : datas)
            {
                String name = sortModel.getName();
                String phone=sortModel.getPhone();
                if (name.indexOf(str.toString()) != -1 ||phone.indexOf(str.toString())!=-1||
                        PinYinKit.getPingYin(name).startsWith(str.toString()) || PinYinKit.getPingYin(name).startsWith(str.toUpperCase().toString()))
                {
                    fSortModels.add(sortModel);
                }

            }

        }
        Collections.sort(fSortModels, comparator);
        mAdapter.updateListView(fSortModels);
        mFooterView.setText("搜索到"+fSortModels.size() + "位联系人");
    }
    @Override
    public void afterTextChanged(Editable s) {
    }

    private class MaillistApi extends HttpUtil {

        private MaillistApi(Context context) {
            super(context); // 传递上下文, 初始化进度对话框
        }


        public void Maillist(String uuid) {

            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?partyUserList", // 登录接口地址
                    "uuid", uuid

            );
        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                // 登录成功, 保存用户登录信息, 持久化
                String resultInfo = apiMsg.getResult();
                parser(resultInfo);
            }else {
                App.me().toast(apiMsg.getMessage());
            }
        }

    }
}
