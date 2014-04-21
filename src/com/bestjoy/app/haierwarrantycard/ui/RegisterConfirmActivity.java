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
import android.widget.EditText;

import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.AccountObject;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.bestjoy.app.haierwarrantycard.view.ProCityDisEditView;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.NetworkUtils;


public class RegisterConfirmActivity extends BaseActionbarActivity implements View.OnClickListener{
	private static final String TAG = "RegisterActivity";

	private ProCityDisEditView mProCityDisEditView;
	private String mTel;
	
	private EditText mUsrNameEditText;
	private EditText usrPwdEditText;
	private EditText usrPwdConfirmEditText;
	
	private String usrPwdConfirm;
	
	private AccountObject mAccountObject;
	
	private HomeObject mHomeObject;

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
		if(getIntent() == null || getIntent().getExtras() == null) return null;
		return getIntent().getExtras().getString("usr_tel");
	}

	private void initViews() {
		mProCityDisEditView = (ProCityDisEditView) findViewById(R.id.home);
		mProCityDisEditView.setHomeEditVisiable(View.GONE);
		
		mUsrNameEditText = (EditText) findViewById(R.id.usr_name);
		usrPwdEditText = (EditText) findViewById(R.id.usr_pwd);
		usrPwdConfirmEditText = (EditText) findViewById(R.id.usr_repwd);
		
		mHomeObject = mProCityDisEditView.getHomeObject();
	}
	
	public static void startIntent(Context context) {
		Intent intent = new Intent(context, RegisterConfirmActivity.class);
		context.startActivity(intent);
	}
	public static void startIntent(Context context, Bundle bundle) {
		Intent intent = new Intent(context, RegisterConfirmActivity.class);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}
	
	private RegisterAsyncTask mRegisterAsyncTask;
	private ProgressDialog mRegisterDialog;

	private void registerAsync(String... param) {
		AsyncTaskUtils.cancelTask(mRegisterAsyncTask);
		mRegisterDialog = getProgressDialog();
		if (mRegisterDialog == null) {
			showDialog(DIALOG_PROGRESS);
		} else {
			if (!mRegisterDialog.isShowing()) {
				mRegisterDialog.show();
			}
		}
		mRegisterAsyncTask = new RegisterAsyncTask();
		mRegisterAsyncTask.execute(param);
	}

	private class RegisterAsyncTask extends AsyncTask<String, Void, Void> {

		private static final String URL = "http://115.29.231.29/Haier/Register.ashx?";
		private String mError;
		@Override
		protected Void doInBackground(String... params) {
			mError = null;
			mAccountObject = null;
			InputStream is = null;
			StringBuilder sb = new StringBuilder(URL);
			sb.append("cell=").append(mAccountObject.mAccountTel)
			.append("&UserName=")
			.append(mAccountObject.mAccountName)
			.append("&Shen=")
			.append(mHomeObject.mHomeProvince)
			.append("&Shi=")
			.append(mHomeObject.mHomeCity)
			.append("&Qu=")
			.append(mHomeObject.mHomeDis)
			.append("&detail=")
			.append(mHomeObject.mHomePlaceDetail)
			.append("&pwd=")
			.append(mAccountObject.mAccountPwd);
			DebugUtils.logD("huasong", "sb = " + sb.toString());
			try {
				is = NetworkUtils.openContectionLocked(sb.toString(), null, null);
				//mAccountObject = AccountParser.parseJson(is);
				try {
					JSONObject jsonObject = new JSONObject(NetworkUtils.getContentFromInput(is));
					DebugUtils.logD(TAG, "StatusCode = " + jsonObject.getString("StatusCode"));
					DebugUtils.logD(TAG, "StatusMessage = " + jsonObject.getString("StatusMessage"));
					DebugUtils.logD("huasong", "StatusCode = " + jsonObject.getString("StatusCode"));
					DebugUtils.logD("huasong", "StatusMessage = " + jsonObject.getString("StatusMessage"));
					DebugUtils.logD("huasong", "Data = " + jsonObject.getString("Data"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
			if (mRegisterDialog != null) {
				mRegisterDialog.hide();
			}
			if (mError != null) {
				MyApplication.getInstance().showMessage(mError);
			} else if (mAccountObject != null) {
				//如果登陆成功
				if (mAccountObject.isLogined()) {
				} else {
					MyApplication.getInstance().showMessage(mAccountObject.mStatusMessage);
				}
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (mRegisterDialog != null) {
				mRegisterDialog.hide();
			}
		}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_save_reg:
				DebugUtils.logD("huasong", "button_save onClick");
				DebugUtils.logD(TAG, "button_save onClick");
				mAccountObject.mAccountName = mUsrNameEditText.getText().toString().trim();
				mAccountObject.mAccountTel = mTel;
				mAccountObject.mAccountPwd = usrPwdEditText.getText().toString().trim();
				usrPwdConfirm = usrPwdConfirmEditText.getText().toString().trim();
				if(valiInput()) {
					registerAsync();
				}
				break;
		}
	}

	private boolean valiInput() {
		if (TextUtils.isEmpty(mAccountObject.mAccountName)) {
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
