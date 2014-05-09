package com.bestjoy.app.haierwarrantycard.update;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.ui.BaseActionbarActivity;
import com.bestjoy.app.haierwarrantycard.ui.PreferencesActivity;
import com.bestjoy.app.haierwarrantycard.utils.MenuHandlerUtils;
import com.shwy.bestjoy.utils.DebugUtils;

public class AppAboutActivity extends BaseActionbarActivity implements View.OnClickListener{

	private static final String TAG = "AppAboutActivity";
	private static final int DIALOG_RELEASENOTE = 1;
	private static final int DIALOG_INTRODUCE = 2;
	
	private ServiceAppInfo mServiceAppInfo;
	
	private TextView mVersionName, mUpdateStatus;
	private LinearLayout mButtonUpdate;
	
	private Button mBtnHelp, mBtnHome, mBtIntroduce;
	private int mCurrentVersion;
	private String mCurrentVersionCodeName; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.isFinishing()) {
			return;
		}
		setContentView(R.layout.about_app);
		
		SharedPreferences prefs = MyApplication.getInstance().mPreferManager;
		mCurrentVersion = prefs.getInt(PreferencesActivity.KEY_LATEST_VERSION, 0);
		mCurrentVersionCodeName = prefs.getString(PreferencesActivity.KEY_LATEST_VERSION_CODE_NAME, "");
		
		mServiceAppInfo = ServiceAppInfo.read();
		initView();
		UpdateService.startUpdateServiceForce(mContext);
	}
	
	public void initView() {
		if (mButtonUpdate == null) {
			mVersionName = (TextView) findViewById(R.id.app_version_name);
			mUpdateStatus = (TextView) findViewById(R.id.desc_update);
			
			mButtonUpdate = (LinearLayout) findViewById(R.id.button_update);
			mBtIntroduce = (Button) findViewById(R.id.button_introduce);
			
			mBtnHome = (Button) findViewById(R.id.button_home);
			mBtnHelp = (Button) findViewById(R.id.button_help);
			
			mButtonUpdate.setOnClickListener(this);
			mBtIntroduce.setOnClickListener(this);
			mBtnHome.setOnClickListener(this);
			mBtnHelp.setOnClickListener(this);
			
			mBtIntroduce.setVisibility(View.GONE);
			mBtnHome.setVisibility(View.GONE);
			mBtnHelp.setVisibility(View.GONE);
		}
		mVersionName.setText(mCurrentVersionCodeName);
		if (mServiceAppInfo != null && mServiceAppInfo.mVersionCode > mCurrentVersion) {
			//发现新版本
			mButtonUpdate.setEnabled(true);
			mUpdateStatus.setText(getString(R.string.format_latest_version, mServiceAppInfo.mVersionName));
		} else {
			//已经是最新的版本了
			mButtonUpdate.setEnabled(false);
			mUpdateStatus.setText(R.string.msg_app_has_latest);
		}
	}
	
	 @Override
     public boolean onCreateOptionsMenu(Menu menu) {
		 return false;
     }
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_update:
			if (mServiceAppInfo != null) {
				startActivity(UpdateActivity.createIntent(mContext, mServiceAppInfo));
			} else {
				DebugUtils.logE(TAG, "mServiceAppInfo == null, so we ignore update click");
			}
			break;
		case R.id.button_introduce:
			showDialog(DIALOG_INTRODUCE);
			break;
		case R.id.button_home:
			break;
		case R.id.button_help:
			break;
		}
		
	}
	
	@Override
	public Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_INTRODUCE:
		case DIALOG_RELEASENOTE:
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}
	
	public static Intent createIntent(Context context) {
		Intent intent = new Intent(context, AppAboutActivity.class);
		return intent;
	}

}
