package com.bestjoy.app.haierwarrantycard.ui;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.ClientProtocolException;

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
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.AccountObject;
import com.bestjoy.app.haierwarrantycard.account.AccountParser;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.NetworkUtils;

public class LoginActivity extends BaseActionbarActivity implements View.OnClickListener{
	private static final String TAG = "NewCardActivity";

	private TextView mRegisterButton;
	private Button mLoginBtn;
	private EditText mTelInput, mPasswordInput;
	public static AccountObject mAccountObject;

	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugUtils.logD(TAG, "onCreate()");
		if (isFinishing()) {
			return ;
		}
		setContentView(R.layout.activity_login_20140415);
		initViews();
	}
	
	public void onResume() {
		super.onResume();
		//每次进来我们都要先清空一下mAccountObject，这个值作为静态变量在各个Activity中传递
		mAccountObject = null;
	}
	
	
	private void initViews() {
		mRegisterButton = (TextView) findViewById(R.id.button_register);
		mRegisterButton.setOnClickListener(this);
		
		mLoginBtn = (Button) findViewById(R.id.button_login);
		mLoginBtn.setOnClickListener(this);
		
		mTelInput = (EditText) findViewById(R.id.tel);
		//显示上一次输入的用户号码
		mTelInput.setText(HaierAccountManager.getInstance().getLastUsrTel());
		
		mPasswordInput = (EditText) findViewById(R.id.pwd);
	}
	
	 public boolean onCreateOptionsMenu(Menu menu) {
		 return false;
	 }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.button_register:
				RegisterActivity.startIntent(this);
				break;
			case R.id.button_login:
				String tel = mTelInput.getText().toString().trim();
				String pwd = mPasswordInput.getText().toString().trim();
				if (!TextUtils.isEmpty(tel) && !TextUtils.isEmpty(pwd)) {
					HaierAccountManager.getInstance().saveLastUsrTel(tel);
					loginAsync(tel, pwd);
				} else {
					MyApplication.getInstance().showMessage(R.string.msg_input_usrtel_password);
				}
				break;
		}
		
	}
	
	private LoginAsyncTask mLoginAsyncTask;
	private ProgressDialog mLoginDialog;
	private void loginAsync(String... param) {
		AsyncTaskUtils.cancelTask(mLoginAsyncTask);
		mLoginDialog = getProgressDialog();
		if (mLoginDialog == null) {
			showDialog(DIALOG_PROGRESS);
			mLoginDialog = getProgressDialog();
			mLoginDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					AsyncTaskUtils.cancelTask(mLoginAsyncTask);
				}
			});
		} else {
			if (!mLoginDialog.isShowing()) {
				mLoginDialog.show();
			}
		}
		mLoginAsyncTask = new LoginAsyncTask();
		mLoginAsyncTask.execute(param);
	}
	private class LoginAsyncTask extends AsyncTask<String, Void, Void> {

		private static final String URL = "http://115.29.231.29/Haier/login.ashx?";
		private String mError;
		@Override
		protected Void doInBackground(String... params) {
			mError = null;
			mAccountObject = null;
			InputStream is = null;
			StringBuilder sb = new StringBuilder(URL);
			sb.append("cell=").append(params[0])
			.append("&pwd=");
			try {
				is = NetworkUtils.openContectionLocked(sb.toString(), params[1], null);
				mAccountObject = AccountParser.parseJson(is);
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
			if (mLoginDialog != null) {
				mLoginDialog.hide();
			}
			
			if (mError != null) {
				MyApplication.getInstance().showMessage(mError);
			} else if (mAccountObject != null) {
				//如果登陆成功
				if (mAccountObject.isLogined()) {
					doContinue();
				} else {
					MyApplication.getInstance().showMessage(mAccountObject.mStatusMessage);
				}
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (mLoginDialog != null) {
				mLoginDialog.hide();
			}
		}
		
	}
	
	/**
	 * 登陆成功后的下一步操作
	 */
	private void doContinue() {
		LoginConfirmAddressActivity.startIntent(this);
	}
	
	
	public static void startIntent(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}
	
}
