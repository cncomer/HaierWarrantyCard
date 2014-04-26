package com.bestjoy.app.haierwarrantycard.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.bestjoy.app.haierwarrantycard.HaierServiceObject;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.AccountObject;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.bestjoy.app.haierwarrantycard.ui.model.ModleSettings;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.bestjoy.app.haierwarrantycard.view.ProCityDisEditView;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.Intents;
import com.shwy.bestjoy.utils.NetworkUtils;


public class RegisterConfirmActivity extends BaseActionbarActivity implements View.OnClickListener{
	private static final String TAG = "RegisterActivity";

	private ProCityDisEditView mProCityDisEditView;
	
	private EditText mUsrNameEditText;
	private EditText usrPwdEditText;
	private EditText usrPwdConfirmEditText;
	
	private String usrPwdConfirm;
	
	private AccountObject mAccountObject;
	
	private HomeObject mHomeObject;
	private Button mConfrimReg;

	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugUtils.logD(TAG, "onCreate()");
		if (isFinishing()) {
			return;
		}
		setContentView(R.layout.activity_register);
		this.initViews();
		this.initData();
	}

	private void initData() {
		mAccountObject = new AccountObject();
		mAccountObject.mAccountTel = pickTelData();
		mHomeObject = mProCityDisEditView.getHomeObject();
	}

	private String pickTelData() {
		return getIntent() == null ? null : getIntent().getStringExtra(Intents.EXTRA_TEL);
	}

	private void initViews() {
		mProCityDisEditView = (ProCityDisEditView) findViewById(R.id.home);
		mProCityDisEditView.setHomeEditVisiable(View.GONE);
		
		mUsrNameEditText = (EditText) findViewById(R.id.usr_name);
		usrPwdEditText = (EditText) findViewById(R.id.usr_pwd);
		usrPwdConfirmEditText = (EditText) findViewById(R.id.usr_repwd);
		
		mConfrimReg = (Button) findViewById(R.id.button_save_reg);
		mConfrimReg.setOnClickListener(this);
	}
	
	public static void startIntent(Context context) {
		Intent intent = new Intent(context, RegisterConfirmActivity.class);
		context.startActivity(intent);
	}
	public static void startIntent(Context context, String tel) {
		Intent intent = new Intent(context, RegisterConfirmActivity.class);
		intent.putExtra(Intents.EXTRA_TEL, tel);
		context.startActivity(intent);
	}
	
	private RegisterAsyncTask mRegisterAsyncTask;
	private void registerAsync(String... param) {
		AsyncTaskUtils.cancelTask(mRegisterAsyncTask);
		showDialog(DIALOG_PROGRESS);
		mRegisterAsyncTask = new RegisterAsyncTask();
		mRegisterAsyncTask.execute(param);
	}

	private class RegisterAsyncTask extends AsyncTask<String, Void, Void> {
		private String mError;
		@Override
		protected Void doInBackground(String... params) {
			mError = null;
			InputStream is = null;
			final int LENGTH = 7;
			String[] urls = new String[LENGTH];
			String[] paths = new String[LENGTH];
			urls[0] = HaierServiceObject.SERVICE_URL + "Register.ashx?cell=";
			paths[0] = mAccountObject.mAccountTel;
			urls[1] = "&UserName=";
			paths[1] = mAccountObject.mAccountName;
			urls[2] = "&Shen=";
			paths[2] = mHomeObject.mHomeProvince;
			urls[3] = "&Shi=";
			paths[3] = mHomeObject.mHomeCity;
			urls[4] = "&Qu=";
			paths[4] = mHomeObject.mHomeDis;
			urls[5] = "&detail=";
			paths[5] = mHomeObject.mHomePlaceDetail;
			urls[6] = "&pwd=";
			paths[6] = mAccountObject.mAccountPwd;
			DebugUtils.logD(TAG, "urls = " + Arrays.toString(urls));
			DebugUtils.logD(TAG, "paths = " + Arrays.toString(paths));
			try {
				is = NetworkUtils.openContectionLocked(urls, paths, null);
				try {
					JSONObject jsonObject = new JSONObject(NetworkUtils.getContentFromInput(is));
					mAccountObject.mStatusCode = Integer.parseInt(jsonObject.getString("StatusCode"));
					mAccountObject.mStatusMessage = jsonObject.getString("StatusMessage");
					String data = jsonObject.getString("Data");
					DebugUtils.logD(TAG, "Data = " + data);
					if(data == null || data.trim().equals("")) return null;
					mAccountObject.mAccountUid = Long.parseLong(data.substring(data.indexOf(":")+1));
					DebugUtils.logD(TAG, "StatusCode = " + mAccountObject.mStatusCode);
					DebugUtils.logD(TAG, "StatusMessage = " + mAccountObject.mStatusMessage);
					DebugUtils.logD(TAG, "Data = " + data);
					DebugUtils.logD(TAG, "Uid = " + mAccountObject.mAccountUid);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				mError = e.getMessage();
			} catch (IOException e) {
				e.printStackTrace();
				mError = e.getMessage();
			} finally {
				NetworkUtils.closeInputStream(is);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dismissDialog(DIALOG_PROGRESS);
			if (mError != null) {
				MyApplication.getInstance().showMessage(mError);
			} else if (mAccountObject.mStatusCode == 1) {
				//注册成功
				mAccountObject.mAccountHomes.add(mHomeObject);
				boolean saveResult;
				try {
					saveResult = HaierAccountManager.getInstance().saveAccountObject(getContentResolver(), mAccountObject);
				} catch (Exception e) {
					e.printStackTrace();
					mError = e.getMessage();
					saveResult = false;
				}
				if(saveResult) {
					MyChooseDevicesActivity.startIntent(mContext, ModleSettings.createMyCardDefaultBundle(mContext));
				} else {
					//保存数据库失败
					new AlertDialog.Builder(mContext)
					.setTitle(R.string.msg_register_title)
		   			.setMessage(R.string.msg_register_save_fail)
		   			.setCancelable(false)
		   			.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
		   				@Override
		   				public void onClick(DialogInterface dialog, int which) {
		   					LoginActivity.startIntent(mContext);
		   				}
		   			})
		   			.create()
		   			.show();
				}
			}
			MyApplication.getInstance().showMessage(mAccountObject.mStatusMessage);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dismissDialog(DIALOG_PROGRESS);
		}
	}
	
	 public boolean onCreateOptionsMenu(Menu menu) {
		 return false;
	 }
	 
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_save_reg:
				DebugUtils.logD(TAG, "button_save onClick");
				mAccountObject.mAccountName = mUsrNameEditText.getText().toString().trim();
				mAccountObject.mAccountName = mUsrNameEditText.getText().toString().trim();
				mAccountObject.mAccountPwd = usrPwdEditText.getText().toString().trim();
				mAccountObject.mAccountPwd = usrPwdEditText.getText().toString().trim();
				usrPwdConfirm = usrPwdConfirmEditText.getText().toString().trim();

				mProCityDisEditView.updateHomeObject();
				if(valiInput()) {
					registerAsync();
				}
				break;
		}
	}

	private boolean valiInput() {
		if (mAccountObject != null && TextUtils.isEmpty(mAccountObject.mAccountName)) {
			MyApplication.getInstance().showMessage(R.string.msg_input_usr_name);
			return false;
		}
		if (TextUtils.isEmpty(mAccountObject.mAccountPwd)) {
			MyApplication.getInstance().showMessage(R.string.msg_input_usr_pwd);
			return false;
		}
		if (TextUtils.isEmpty(mHomeObject.mHomeProvince)) {
			MyApplication.getInstance().showMessage(R.string.msg_input_usr_pro);
			return false;
		}
		if (TextUtils.isEmpty(mHomeObject.mHomeCity)) {
			MyApplication.getInstance().showMessage(R.string.msg_input_usr_city);
			return false;
		}
		if (TextUtils.isEmpty(mHomeObject.mHomeDis)) {
			MyApplication.getInstance().showMessage(R.string.msg_input_usr_dis);
			return false;
		}
		if (TextUtils.isEmpty(mHomeObject.mHomePlaceDetail)) {
			MyApplication.getInstance().showMessage(R.string.msg_input_usr_place_detail);
			return false;
		}
		if (!usrPwdConfirm.equals(mAccountObject.mAccountPwd)) {
			MyApplication.getInstance().showMessage(R.string.msg_input_pwd_not_match_tips);
			return false;
		}
		return true;
	}
}
