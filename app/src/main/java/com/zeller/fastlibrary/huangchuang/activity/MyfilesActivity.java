package com.zeller.fastlibrary.huangchuang.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.http.client.HttpRequest;
import com.squareup.picasso.Picasso;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.DateTimePicker;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.User;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;
import com.zeller.fastlibrary.huangchuang.util.LogUtil;
import com.zeller.fastlibrary.huangchuang.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by Administrator on 2017/6/8 0008.
 */
public class MyfilesActivity extends BaseActivity implements View.OnClickListener, DateTimePicker.OnDateSetListener, CompoundButton.OnCheckedChangeListener {
    private ImageView back;
    public static final int REQUEST_CODE = 'm' + 'y' + 'f' + 'i' + 'l' + 'e' + 's';
    private static final int REQUEST_CER = 'm' + 'y' + 'i' + 'm' + 'g';
    private TextView realName;
    private TextView sex_s;
    private EditText height;
    private EditText weight;
    private TextView birthday;
    private EditText nativePlace;
    private EditText homePhone;
    private EditText phone;
    private TextView position;
    private TextView departmentName;
    private TextView complain;
    private TextView rudang_time;
    private RelativeLayout rela_sex;

    private RelativeLayout make_time_lay;
    private RelativeLayout rela_rudang_time;
    private RelativeLayout Myfiles;
    private ImageView Headportrait;

    private int i = -1;
    private DateTimePicker dateTimePicker;
    //    private CheckBox cb_sex;
    private CheckBox cb_height;
    private CheckBox cb_weight;
    private CheckBox cb_birthday;
    private CheckBox cb_nativePlace;
    private CheckBox cb_homePhone;
    private MyfilesApi myfilesApi;
    final int DATE_DIALOG = 1;
    int mYear, mMonth, mDay;
    private UploadPhotoApi uploadPhoto;
    private static final int PERMISSION_REQUEST_CODE = 1; //权限请求码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfiles);
        uploadPhoto = new UploadPhotoApi(this);
        myfilesApi = new MyfilesApi(this);
        assignViews();
        initViews();
        GetUi();
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
    }

    private void GetUi() {
        Bundle bundle = this.getIntent().getExtras();
        String realNameText = bundle.getString("realName");
        String sexText = bundle.getString("sex");
        String heightText = bundle.getString("height");
        String weightText = bundle.getString("weight");
        String birthdayText = bundle.getString("birthday");
        String nativePlaceText = bundle.getString("nativePlace");
        String homePhoneText = bundle.getString("homePhone");
        String phoneText = bundle.getString("phone");
        String positionText = bundle.getString("position");
        String departmentNameText = bundle.getString("departmentName");
        String joinPartyDateText = bundle.getString("joinPartyDate");
        String pictureText = bundle.getString("picture");
        String heightpublicText = bundle.getString("heightpublic");
        String weightpublicText = bundle.getString("weightpublic");
        String idcardpublicText = bundle.getString("idcardpublic");
        String homephonepublicText = bundle.getString("homephonepublic");
        String nativeplacepublicText = bundle.getString("nativeplacepublic");
        String phonepublicText = bundle.getString("phonepublic");
        String birthdaypublicText = bundle.getString("birthdaypublic");
//        private CheckBox cb_height;
//        private CheckBox cb_weight;
//        private CheckBox cb_birthday;
//        private CheckBox cb_nativePlace;
//        private CheckBox cb_homePhone;
        if (null != heightpublicText && heightpublicText.equals("1")) {
            cb_height.setChecked(true);
        } else {
            cb_height.setChecked(false);
        }
        if (null != weightpublicText && weightpublicText.equals("1")) {
            cb_weight.setChecked(true);
        } else {
            cb_weight.setChecked(false);
        }
        if (null != homephonepublicText && homephonepublicText.equals("1")) {
            cb_homePhone.setChecked(true);
        } else {
            cb_homePhone.setChecked(false);
        }
        if (null != nativeplacepublicText && nativeplacepublicText.equals("1")) {
            cb_nativePlace.setChecked(true);
        } else {
            cb_nativePlace.setChecked(false);
        }

        if (null != birthdaypublicText && birthdaypublicText.equals("1")) {
            cb_birthday.setChecked(true);
        } else {
            cb_birthday.setChecked(false);
        }


        if (!pictureText.isEmpty()) {
            Picasso.with(App.me()).load(pictureText).fit().placeholder(R.drawable.icon_default_user).into(Headportrait);
            Headportrait.setTag(pictureText);
        }
        realName.setText(realNameText);
        if (sexText.equals("2")) {
            sex_s.setText("女");
        } else {
            sex_s.setText("男");
        }
        height.setText(heightText);
        weight.setText(weightText);
        birthday.setText(birthdayText);
        nativePlace.setText(nativePlaceText);
        homePhone.setText(homePhoneText);
        phone.setText(phoneText);
        position.setText(departmentNameText);
        departmentName.setText(positionText);
        rudang_time.setText(joinPartyDateText);

        rela_sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyfilesActivity.this);
//                builder.setIcon(R.drawable.ic_launcher);
//                builder.setTitle("请选择性别");
                final String[] sex = {"男", "女"};
                //    设置一个单项选择下拉框
                /**
                 * 第一个参数指定我们要显示的一组下拉单选框的数据集合
                 * 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认'女' 会被勾选上
                 * 第三个参数给每一个单选项绑定一个监听器
                 */

                if (sex_s.getText().toString().equals("男")) {
                    i = 0;
                } else {
                    i = 1;
                }

                builder.setSingleChoiceItems(sex, i, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sex_s.setText(sex[which]);
                        dialog.dismiss();
                    }
                });
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//
//                    }
//                });
//                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
                builder.show();
            }
        });
    }

    private void assignViews() {

        back = (ImageView) findViewById(R.id.back);
        Headportrait = (ImageView) findViewById(R.id.Headportrait);
        realName = (TextView) findViewById(R.id.realName);
        sex_s = (TextView) findViewById(R.id.sex);
        height = (EditText) findViewById(R.id.height);
        weight = (EditText) findViewById(R.id.weight);
        birthday = (TextView) findViewById(R.id.birthday);
        nativePlace = (EditText) findViewById(R.id.nativePlace);
        homePhone = (EditText) findViewById(R.id.homePhone);
        phone = (EditText) findViewById(R.id.phone);
        position = (TextView) findViewById(R.id.position);
        departmentName = (TextView) findViewById(R.id.departmentName);
        complain = (TextView) findViewById(R.id.complain);
        rudang_time = (TextView) findViewById(R.id.rudang_time);
        rela_sex = (RelativeLayout) findViewById(R.id.rela_sex);
        make_time_lay = (RelativeLayout) findViewById(R.id.make_time_lay);
        Myfiles = (RelativeLayout) findViewById(R.id.Myfiles);
        rela_rudang_time = (RelativeLayout) findViewById(R.id.rela_rudang_time);
//        cb_sex = (CheckBox) findViewById(R.id.cb_sex);
        cb_height = (CheckBox) findViewById(R.id.cb_height);
        cb_weight = (CheckBox) findViewById(R.id.cb_weight);
        cb_birthday = (CheckBox) findViewById(R.id.cb_birthday);
        cb_nativePlace = (CheckBox) findViewById(R.id.cb_nativePlace);
        cb_homePhone = (CheckBox) findViewById(R.id.cb_homePhone);
//        cb_sex.setOnCheckedChangeListener(this);
        cb_height.setOnCheckedChangeListener(this);
        cb_weight.setOnCheckedChangeListener(this);
        cb_birthday.setOnCheckedChangeListener(this);
        cb_nativePlace.setOnCheckedChangeListener(this);
        cb_homePhone.setOnCheckedChangeListener(this);

//        cb_sex.setTag(1);
        cb_height.setTag(2);
        cb_weight.setTag(2);
        cb_birthday.setTag(2);
        cb_nativePlace.setTag(2);
        cb_homePhone.setTag(2);

//        //通过设置checkbox的监听事件，判断checkbox是否被选中
//        cb_sex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                // 通过这个方法，来监听当前的checkbox是否被选中
//                if (isChecked) {
//                    Log.d("reg","1");
//                }else {
//                    Log.d("reg","2");
//                }
//
//            }
//        });
    }

    private void initViews() {
        View[] views = {back, make_time_lay, complain, rela_rudang_time, Myfiles};
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
            case R.id.make_time_lay:
                MakeTime();
                break;
            case R.id.rela_rudang_time:
                showDialog(DATE_DIALOG);
                break;
            case R.id.complain:
                Complain();
                break;
            case R.id.Myfiles:
                // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkPermission(MyfilesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //获取权限后的操作。读取文件
                        MyFiles();
                    } else {
                        //请求权限
                        ActivityCompat.requestPermissions(MyfilesActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_CODE);
                    }
                }else {
                    MyFiles();
                }

                break;
        }
    }
    /**
     * 检测权限是否授权
     *
     * @return
     */
    private boolean checkPermission(Context context, String permission) {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyFiles();
                } else {
                    //未授权
                    Toast.makeText(this, "您已禁止打开相机权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    private void MyFiles() {
        Intent intent = new Intent(MyfilesActivity.this, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        startActivityForResult(intent, REQUEST_CER);
    }

    /**
     * 设置日期 利用StringBuffer追加
     */
    public void display() {
        rudang_time.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay).append(""));
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            display();
        }
    };

    private void Complain() {


        String sexText = sex_s.getText().toString();
        String heightText = height.getText().toString();
        String weightText = weight.getText().toString();
        String birthdayText = birthday.getText().toString();
        String nativePlaceText = nativePlace.getText().toString();
        String homePhoneText = homePhone.getText().toString();
        String phoneText = phone.getText().toString();
        String rudang_timeText = rudang_time.getText().toString();

        if(!StringUtil.matchesPhone(phoneText)){
            App.me().toast("请输入正确的手机号");
            return;
        }
        User user = App.me().getUser();
        if (null != user) {
            myfilesApi.Myfiles(user.getUuid(), cb_height.getTag().toString(), cb_weight.
                            getTag().toString(), cb_birthday.getTag().toString(),
                    cb_nativePlace.getTag().toString(), cb_homePhone.getTag().toString(), sexText, heightText, weightText,
                    birthdayText, nativePlaceText, homePhoneText, phoneText, rudang_timeText, String.valueOf(Headportrait.getTag()));


        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CER && resultCode == RESULT_OK) {
            List<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            ByteArrayOutputStream stream = null;
            List<String> list = new ArrayList<>();
            if (paths != null && paths.size() > 0) {
                for (int i = 0; i < paths.size(); i++) {
                    String o = paths.get(i);
//                    String path = paths.get(0);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(o, options);
                    if (options.outWidth > 512 || options.outHeight > 512) {
                        options.inSampleSize = Math.max(options.outWidth, options.outHeight) / 512;
                    }
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    options.inJustDecodeBounds = false;
                    Bitmap bitmap = BitmapFactory.decodeFile(o, options);
                    stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                    bitmap.recycle();
                    list.add(Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT));
                }
                int length = list.size();
                StringBuffer buf = new StringBuffer("");
                if (length > 0) {
                    buf.append(list.get(0));
                }
                for (int i = 1; i < length; i++) {
                    buf.append(',');
                    buf.append(list.get(i));
                }
            /*    for (int i = 0; i < list.size(); i++) {
                    Log.d("reg","str:"+list.get(i));
                }*/

//                Str  ansistring = Joiner.on(",").join(list);

                uploadPhoto.uploadPhoto(list);

                try {
                    stream.close();
                } catch (IOException e) {
                    LogUtil.e(MyfilesActivity.class, e.getMessage(), e);
                }
//                Intent intent = new Intent(this, CropImageActivity.class);
//                intent.putExtra(CropImageActivity.REQUEST_PATH, paths.get(0));
//                startActivityForResult(intent, CropImageActivity.REQUEST_CODE);
            }


        }
    }

    //调用图片保存接口
    private class UploadPhotoApi extends HttpUtil {

        private UploadPhotoApi(Context context) {
            super(context);
        }

        private void uploadPhoto(List<String> order_photo) {
            send(
                    HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?newsPhotoUpload",
                    "photo", order_photo
            );
        }


        @Override
        public void onSuccess(final ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                String resultInfo = apiMsg.getResult();
                if (resultInfo != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultInfo);
                        String str1 = jsonObject.getString("1");

                        if (null != str1) {
                            Picasso.with(App.me()).load(str1).fit().placeholder(R.drawable.icon_default_user).into(Headportrait);
                            Headportrait.setTag(str1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }
    }

    private void MakeTime() {
        BtnTime();
    }

    private void BtnTime() {

        if (dateTimePicker == null) {
            dateTimePicker = new DateTimePicker(this, this);
        }
        dateTimePicker.show();
    }

    @Override
    public void onDateSet(DateTimePicker dialog, int year, int monthOfYear, int dayOfMonth) {

        String month;
        String day;
        int mon = monthOfYear + 1;
        if (mon < 10) {
            month = "0" + mon;
        } else {
            month = mon + "";
        }
        if (dayOfMonth < 10) {
            day = "0" + dayOfMonth;
        } else {
            day = dayOfMonth + "";
        }


        String date = String.format("%s-%s-%s", year, month, day);
        birthday.setText(date);


    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_weight:
                if (isChecked) {
                    cb_weight.setTag(1);
                } else {
                    cb_weight.setTag(2);
                }
                break;
            case R.id.cb_height:
                if (isChecked) {
                    cb_height.setTag(1);
                } else {
                    cb_height.setTag(2);
                }
                break;
            case R.id.cb_birthday:
                if (isChecked) {
                    cb_birthday.setTag(1);
                } else {
                    cb_birthday.setTag(2);
                }
                break;
            case R.id.cb_nativePlace:
                if (isChecked) {
                    cb_nativePlace.setTag(1);
                } else {
                    cb_nativePlace.setTag(2);
                }
                break;
            case R.id.cb_homePhone:
                if (isChecked) {
                    cb_homePhone.setTag(1);
                } else {
                    cb_homePhone.setTag(2);
                }
                break;
        }
    }

    private class MyfilesApi extends HttpUtil {

        private MyfilesApi(Context context) {
            super(context);
        }
//        Log.d("reg", "sexText：" + sexText);
//        Log.d("reg", "heightText：" + heightText);
//        Log.d("reg", "birthdayText：" + birthdayText);
//        Log.d("reg", "nativePlaceText：" + nativePlaceText);
//        Log.d("reg", "homePhoneText：" + homePhoneText);
//        Log.d("reg", "phoneText：" + phoneText);
//        Log.d("reg", "rudang_timeText：" + rudang_timeText);

        public void Myfiles(String uuid, String heightPublic, String weightPublic,
                            String birthdayPublic, String nativePlacePublic, String homePhonePublic,
                            String sexText, String heightText, String weightText, String birthdayText,
                            String nativePlaceText, String homePhoneText, String phoneText, String rudang_timeText, String picture) {

            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneUserOnlineController.do?updateUser",
                    "uuid", uuid,
                    "heightPublic", heightPublic,
                    "weightPublic", weightPublic,
                    "birthdayPublic", birthdayPublic,
                    "nativePlacePublic", nativePlacePublic,
                    "homePhonePublic", homePhonePublic,
                    "sex", sexText,
                    "height", heightText,
                    "weight", weightText,
                    "birthday", birthdayText,
                    "nativePlace", nativePlaceText,
                    "homePhone", homePhoneText,
                    "phone", phoneText,
                    "joinPartyDate", rudang_timeText,
                    "picture", picture


            );
        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                App.me().toast(apiMsg.getMessage());
                setResult(RESULT_OK);
                finish();
            } else {
                App.me().toast(apiMsg.getMessage());
            }
        }

    }

}

