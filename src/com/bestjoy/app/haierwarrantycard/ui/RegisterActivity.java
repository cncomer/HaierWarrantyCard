package com.bestjoy.app.haierwarrantycard.ui;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.bestjoy.app.haierwarrantycard.HaierServiceObject;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.Intents;
import com.shwy.bestjoy.utils.NetworkUtils;

public class RegisterActivity extends BaseActionbarActivity implements View.OnClickListener{
	private static final String TAG = "RegisterActivity";
	
	private static final int TIME_COUNDOWN = 60000;
	
	private Button mNextButton;
	private Button mBtnGetyanzhengma;
	private EditText mTelInput;
	private EditText mCodeInput;
	private String mYanZhengCodeFromServer;

	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugUtils.logD(TAG, "onCreate()");
		if (isFinishing()) {
			return ;
		}
		setContentView(R.layout.activity_register_confirm);
		this.initViews();
	}

	private void initViews() {
		mNextButton = (Button) findViewById(R.id.button_next);
		mNextButton.setOnClickListener(this);
		
		mBtnGetyanzhengma = (Button) findViewById(R.id.button_getyanzhengma);
		mBtnGetyanzhengma.setOnClickListener(this);
		
		mTelInput = (EditText) findViewById(R.id.usr_tel);
		mCodeInput = (EditText) findViewById(R.id.usr_validate);
		mCodeInput.setOnClickListener(this);
	}
	
	 public boolean onCreateOptionsMenu(Menu menu) {
		 return false;
	 }

	public static void startIntent(Context context, Bundle modelBundel) {
		Intent intent = new Intent(context, RegisterActivity.class);
		if (modelBundel == null) {
			modelBundel = new Bundle();
		}
		intent.putExtras(modelBundel);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		String tel = mTelInput.getText().toString().trim();
		String code = mCodeInput.getText().toString().trim();
		switch (v.getId()) {
			case R.id.button_next:
			if(TextUtils.isEmpty(tel)) {
				MyApplication.getInstance().showMessage(R.string.msg_input_usrtel);
				return;
			}
			if(TextUtils.isEmpty(code)) {
				MyApplication.getInstance().showMessage(R.string.msg_input_yanzheng_code);
				return;
			}
			if (mYanZhengCodeFromServer != null
					&& mYanZhengCodeFromServer.equals(mCodeInput.getText()
							.toString().trim())) {
				Bundle bundle = getIntent().getExtras();
				if (bundle == null) {
					bundle = new Bundle();
				}
				bundle.putString(Intents.EXTRA_TEL, tel);
				RegisterConfirmActivity.startIntent(this, bundle);
			} else {
				MyApplication.getInstance().showMessage(R.string.msg_input_yanzheng_code_error);
			}
				break;
			case R.id.button_getyanzhengma:
				if(!TextUtils.isEmpty(tel)) {
					mYanZhengCodeFromServer = null;
					mBtnGetyanzhengma.setEnabled(false);
					doTimeCountDown();
					loadYanzhengCodeAsync(tel);
				} else {
					MyApplication.getInstance().showMessage(R.string.msg_input_usrtel);
				}
				break;
		}
	}

	private void doTimeCountDown() {
		new TimeCount(TIME_COUNDOWN, 1000).start();
	}
	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			mBtnGetyanzhengma.setEnabled(false);
		}

		@Override
		public void onFinish() {
			mBtnGetyanzhengma.setText(RegisterActivity.this.getResources()
					.getString(R.string.button_re_vali));
			mBtnGetyanzhengma.setEnabled(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			mBtnGetyanzhengma.setText(RegisterActivity.this.getResources()
					.getString(R.string.second, millisUntilFinished / 1000));
		}
	}
	private GetYanZhengCodeAsyncTask mGetYanZhengCodeAsyncTask;
	private ProgressDialog mGetYanZhengCodeDialog;
	private void loadYanzhengCodeAsync(String... param) {
		AsyncTaskUtils.cancelTask(mGetYanZhengCodeAsyncTask);
		mGetYanZhengCodeDialog = getProgressDialog();
		if (mGetYanZhengCodeDialog == null) {
			showDialog(DIALOG_PROGRESS);
			mGetYanZhengCodeDialog = getProgressDialog();
			mGetYanZhengCodeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					AsyncTaskUtils.cancelTask(mGetYanZhengCodeAsyncTask);
				}
			});
		} else {
			if (!mGetYanZhengCodeDialog.isShowing()) {
				mGetYanZhengCodeDialog.show();
			}
		}
		mGetYanZhengCodeAsyncTask = new GetYanZhengCodeAsyncTask();
		mGetYanZhengCodeAsyncTask.execute(param);
	}
	
	private class GetYanZhengCodeAsyncTask extends AsyncTask<String, Void, String> {
		private String mError;
		private String mRandCode;
		@Override
		protected String doInBackground(String... params) {
			mError = null;
			InputStream is = null;
			String url;
			String path;
			StringBuilder sb = new StringBuilder(HaierServiceObject.SERVICE_URL);
			sb.append("SendMessage.ashx?cell=").append(params[0]);
			url = sb.substring(0, sb.indexOf("=")+1);
			path = params[0];
			DebugUtils.logD(TAG, "url : " + url);
			DebugUtils.logD(TAG, "path : " + path);
			DebugUtils.logD(TAG, "sb : " + sb.toString());
			try {
				is = NetworkUtils.openContectionLocked(url, path, MyApplication.getInstance().getSecurityKeyValuesObject());
				DebugUtils.logD(TAG, "is : " + is.toString());
				try {
					JSONObject jsonObject = new JSONObject(NetworkUtils.getContentFromInput(is));
					mRandCode = jsonObject.getString("randcode");
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
			return mRandCode;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (mGetYanZhengCodeDialog != null) {
				mGetYanZhengCodeDialog.hide();
			}
			mYanZhengCodeFromServer = result;
			DebugUtils.logD(TAG, "result data : " + mYanZhengCodeFromServer);
			if(mYanZhengCodeFromServer == null) {
				MyApplication.getInstance().showMessage(R.string.msg_input_yanzheng_code_check_net);
			} else {
				MyApplication.getInstance().showMessage(R.string.msg_yanzheng_code_msg_send);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (mGetYanZhengCodeDialog != null) {
				mGetYanZhengCodeDialog.hide();
			}
		}
		
	}
}
