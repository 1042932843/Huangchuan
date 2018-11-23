package com.zeller.fastlibrary.huangchuang.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.http.client.HttpRequest;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.activity.MainActivity;
import com.zeller.fastlibrary.huangchuang.activity.PublicationtypeActivity;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.User;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;
import com.zeller.fastlibrary.huangchuang.util.LogUtil;
import com.zeller.fastlibrary.huangchuang.wx.GlideImageLoader;
import com.zeller.fastlibrary.huangchuang.wx.ImagePickerAdapter;
import com.zeller.fastlibrary.huangchuang.wx.SelectDialog;

import org.simple.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/19 0019.
 */
public class SpeakFragment extends BaseFragment implements View.OnClickListener , ImagePickerAdapter.OnRecyclerViewItemClickListener{
    private LinearLayout lien;
    private TextView type;
    private String patient_id;
    private EditText title;
    private EditText capacity;
    private SpeakApi speakApi;
    private View view;
    private TextView wancheng;
    // 请求加载系统照相机
    private static final int REQUEST_CAMERA = 100;
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;

    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 9;               //允许选择图片最大数


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_speak, container, false);
        assignViews(view);
        initViews();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        speakApi = new SpeakApi(getActivity());
        //最好放到 Application oncreate执行
        initImagePicker();

    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }



    public static SpeakFragment newInstance(String arg1) {
        SpeakFragment speakFragment = new SpeakFragment();
        Bundle bundle = new Bundle();
        bundle.putString("arg1", arg1);
        speakFragment.setArguments(bundle);
        return speakFragment;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void assignViews(View view) {
        type = (TextView) view.findViewById(R.id.type);
        lien = (LinearLayout) view.findViewById(R.id.lien);
        title = (EditText) view.findViewById(R.id.title);
        capacity = (EditText) view.findViewById(R.id.capacity);
        wancheng = (TextView) view.findViewById(R.id.wancheng);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(getActivity(), selImageList, maxImgCount);
        adapter.setOnItemClickListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }
    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(getActivity(), R.style
                .transparentFrameWindowStyle,
                listener, names);
        if (!getActivity().isFinishing()) {
            dialog.show();
        }
        return dialog;
    }
    private void initViews() {
        View[] views = {lien, wancheng};
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
//                getActivity().finish();
                break;
            case R.id.lien:
                Lien();
                break;
            case R.id.wancheng:
                Complete();
                break;
        }
    }

    private void Complete() {




        // 高清的压缩图片全部就在  list 路径里面了
        // 完成上传服务器后 .........
        ByteArrayOutputStream stream = null;

        List<String> list = new ArrayList<String>();

        for (int i = 0; i < selImageList.size(); i++) {
            String Str = selImageList.get(i).path;
            Log.d("reg","Str:"+Str);
//                    list.add(FileUtils.SDPATH+Str+".jpg");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(Str, options);
            if (options.outWidth > 512 || options.outHeight > 512) {
                options.inSampleSize = Math.max(options.outWidth, options.outHeight) / 512;
            }
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(Str, options);
            stream = new ByteArrayOutputStream();
            if (null != bitmap) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                bitmap.recycle();
                list.add(Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT));
            }
        }
        if (null != stream) {
            try {
                stream.close();
            } catch (IOException e) {
                LogUtil.e(SpeakFragment.class, e.getMessage(), e);
            }
        }


        if (null == patient_id || patient_id.equals("")) {
            App.me().toast("请选择发布类型");
            return;
        }

        String titleText = title.getText().toString();
        String capacityText = capacity.getText().toString();

        if (titleText.length() == 0) {
            App.me().toast("请输入标题");
            title.requestFocus();
            return;
        }


        /*if (titleText.length() >= 20) {
            App.me().toast("标题过长");
            title.requestFocus();
            return;
        }*/

        if (capacityText.length() == 0) {
            App.me().toast("请输入内容");
            capacity.requestFocus();
            return;
        }

        User user = App.me().getUser();
        if (null != user) {
            Log.d("reg","list:"+list.size());
            speakApi.speak(user.getUuid(), patient_id, titleText, capacityText, list);
        }
    }


    private void Lien() {
        Intent intent = new Intent(getActivity(), PublicationtypeActivity.class);
        startActivityForResult(intent, PublicationtypeActivity.REQUEST_CODE);
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }





    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                List<String> names = new ArrayList<>();
                names.add("拍照");
                names.add("相册");
                showDialog(new SelectDialog.SelectDialogListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0: // 直接调起相机
                                /**
                                 * 0.4.7 目前直接调起相机不支持裁剪，如果开启裁剪后不会返回图片，请注意，后续版本会解决
                                 *
                                 * 但是当前直接依赖的版本已经解决，考虑到版本改动很少，所以这次没有上传到远程仓库
                                 *
                                 * 如果实在有所需要，请直接下载源码引用。
                                 */
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                                Intent intent = new Intent(getActivity(), ImageGridActivity.class);
                                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                                startActivityForResult(intent, REQUEST_CODE_SELECT);
                                break;
                            case 1:
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                                Intent intent1 = new Intent(getActivity(), ImageGridActivity.class);
                                /* 如果需要进入选择的时候显示已经选中的图片，
                                 * 详情请查看ImagePickerActivity
                                 * */
//                                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
                                startActivityForResult(intent1, REQUEST_CODE_SELECT);
                                break;
                            default:
                                break;
                        }

                    }
                }, names);


                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(getActivity(), ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                break;
        }
    }




    ArrayList<ImageItem> images = null;

    //选择照片返回
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PublicationtypeActivity.REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            if (null != data) {
                patient_id = data.getStringExtra("id");
                String make_name = data.getStringExtra("name");
                if (null != make_name) {
                    type.setText(make_name);
                }
            }
        }
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    selImageList.clear();
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                }
            }
        }
    }


    //图片保存服务器接口
    class SpeakApi extends HttpUtil {
        private List<String> photo;

        public SpeakApi(Context context) {
            super(context);
        }


        private void speak(String uuid, String newsTypeId, String title, String detail, List<String> photo) {
            this.photo = photo;
            send(
                    HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?addNews",
                    "uuid", uuid,
                    "newsTypeId", newsTypeId,
                    "title", title,
                    "detail", detail,
                    "photo", this.photo
            );
        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                assignViews(view);
                initViews();
                photo.clear();
//                if (getActivity() instanceof MainActivity) {
//                    ((MainActivity) getActivity()).onTabSelected(0);
//                    EventBus.getDefault().post("1", "speak");
//                }
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).onPageSelected(0);
                    EventBus.getDefault().post("1", "speak");
                }
                type.setText("点击选择发布类型");
                title.setText("");
                capacity.setText("");
                patient_id = "";
                App.me().toast(apiMsg.getMessage());

            } else {
                App.me().toast(apiMsg.getMessage());
            }
        }
    }
}