package com.bestjoy.app.haierwarrantycard.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.shwy.bestjoy.utils.AsyncTaskUtils;

public class LoginActivity extends BaseActionbarActivity implements View.OnClickListener{
	private static final String TAG = "NewCardActivity";

	private TextView mRegisterButton;
	private Button mLoginBtn;
	private EditText mTelInput, mPasswordInput;

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
	
	
	private void initViews() {
		mRegisterButton = (TextView) findViewById(R.id.button_register);
		mRegisterButton.setOnClickListener(this);
		
		mLoginBtn = (Button) findViewById(R.id.button_login);
		mLoginBtn.setOnClickListener(this);
		
		mTelInput = (EditText) findViewById(R.id.tel);
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
					loginAsync(tel, pwd);
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
		} else {
			if (!mLoginDialog.isShowing()) {
				mLoginDialog.show();
			}
		}
		mLoginAsyncTask = new LoginAsyncTask();
		mLoginAsyncTask.execute(param);
	}
	private class LoginAsyncTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (mLoginDialog != null) {
				mLoginDialog.hide();
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
	
	
	public static void startIntent(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}
	
}
