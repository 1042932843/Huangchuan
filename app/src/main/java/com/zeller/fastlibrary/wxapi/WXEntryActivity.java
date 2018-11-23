package com.zeller.fastlibrary.wxapi;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;

import android.app.Activity;
import android.os.Bundle;



public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
		App.me().iwxapi.handleIntent(getIntent(), this);

    }

	@Override
	public void onResp(BaseResp resp) {

		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:

				App.me().toast("分享成功");
				finish();
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:

				App.me().toast("取消了分享");
				finish();
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:

				finish();
				break;
			case BaseResp.ErrCode.ERR_SENT_FAILED:

				finish();
				break;
			case BaseResp.ErrCode.ERR_UNSUPPORT:

				finish();
				break;
			case BaseResp.ErrCode.ERR_COMM:

				finish();
				break;
			default:

				finish();
				break;
		}
	}

	@Override
	public void onReq(BaseReq req) {

	}


}