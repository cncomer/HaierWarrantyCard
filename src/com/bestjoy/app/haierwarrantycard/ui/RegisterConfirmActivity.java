package com.bestjoy.app.haierwarrantycard.ui;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bestjoy.app.haierwarrantycard.HaierServiceObject;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.AccountObject;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.bestjoy.app.haierwarrantycard.view.ProCityDisEditView;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.Intents;
import com.shwy.bestjoy.utils.NetworkUtils;


public class RegisterConfirmActivity extends BaseActionbarActivity implements View.OnClickListener{
	private static final String TAG = "RegisterActivity";

	private ProCityDisEditView mProCityDisEditView;
	private String mTel;
	private String mName;
	private String mPwd;
	
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
		mTel = pickTelData();
		mAccountObject = new AccountObject();
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
		
		mHomeObject = mProCityDisEditView.getHomeObject();
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
		private String mStatusCode;
		private String mStatusMessage;
		@Override
		protected Void doInBackground(String... params) {
			mError = null;
			mAccountObject = null;
			InputStream is = null;
			StringBuilder sb = new StringBuilder(HaierServiceObject.SERVICE_URL);
			sb.append("Register.ashx?cell=").append(mTel)
			.append("&UserName=")
			.append(mName)
			.append("&Shen=")
			.append(mHomeObject.mHomeProvince)
			.append("&Shi=")
			.append(mHomeObject.mHomeCity)
			.append("&Qu=")
			.append(mHomeObject.mHomeDis)
			.append("&detail=")
			.append(mHomeObject.mHomePlaceDetail)
			.append("&pwd=")
			.append(mPwd);
			String path = sb.substring(sb.indexOf("?"));
			DebugUtils.logD(TAG, "sb = " + sb.toString());
			try {
				is = NetworkUtils.openContectionLocked(sb.toString(), path, null);
				try {
					JSONObject jsonObject = new JSONObject(NetworkUtils.getContentFromInput(is));
					mStatusCode = jsonObject.getString("StatusCode");
					mStatusMessage = jsonObject.getString("StatusMessage");
					DebugUtils.logD(TAG, "StatusCode = " + mStatusCode);
					DebugUtils.logD(TAG, "StatusMessage = " + mStatusMessage);
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
			} else if (mStatusCode.equals("1")) {
				//注册成功
			}
			MyApplication.getInstance().showMessage(mStatusMessage);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dismissDialog(DIALOG_PROGRESS);
		}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_save_reg:
				DebugUtils.logD(TAG, "button_save onClick");
				if(mAccountObject != null)mAccountObject.mAccountName = mUsrNameEditText.getText().toString().trim();
				mName = mUsrNameEditText.getText().toString().trim();
				mPwd = usrPwdEditText.getText().toString().trim();
				if(mAccountObject != null) mAccountObject.mAccountTel = mTel;
				if(mAccountObject != null) mAccountObject.mAccountPwd = usrPwdEditText.getText().toString().trim();
				usrPwdConfirm = usrPwdConfirmEditText.getText().toString().trim();

				mHomeObject = mProCityDisEditView.getHomeObject();
				if(valiInput()) {
					registerAsync();
				}
				break;
		}
	}

	private boolean valiInput() {
		if (mAccountObject != null && TextUtils.isEmpty(mName)) {
			MyApplication.getInstance().showMessage(R.string.msg_input_usr_name);
			return false;
		}
		if (TextUtils.isEmpty(mPwd)) {
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
		if (!usrPwdConfirm.equals(mPwd)) {
			MyApplication.getInstance().showMessage(R.string.msg_input_pwd_not_match_tips);
			return false;
		}
		return true;
	}
}
