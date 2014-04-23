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
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.bestjoy.app.haierwarrantycard.HaierServiceObject;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.NetworkUtils;

public class RegisterActivity extends BaseActionbarActivity implements View.OnClickListener{
	private static final String TAG = "RegisterActivity";
	
	private static final int TIME_COUNDOWN = 60000;
	
	private Button mNextButton;
	private Button mBtnGetyanzhengma;
	private EditText mTelInput;

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
	}
	
	 public boolean onCreateOptionsMenu(Menu menu) {
		 return false;
	 }

	public static void startIntent(Context context) {
		Intent intent = new Intent(context, RegisterActivity.class);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		String tel = mTelInput.getText().toString().trim();
		switch (v.getId()) {
			case R.id.button_next:
				if(!TextUtils.isEmpty(tel)) {
					RegisterConfirmActivity.startIntent(this, tel);
				} else {
					MyApplication.getInstance().showMessage(R.string.msg_input_usrtel);
				}
				break;
			case R.id.button_getyanzhengma:
				if(!TextUtils.isEmpty(tel)) {
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
		}

		@Override
		public void onFinish() {
			mBtnGetyanzhengma.setText(RegisterActivity.this.getResources()
					.getString(R.string.button_re_vali));
			mBtnGetyanzhengma.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			mBtnGetyanzhengma.setClickable(false);
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
			StringBuilder sb = new StringBuilder(HaierServiceObject.SERVICE_URL);
			sb.append("GetCode.ashx?cell=").append(params[0]);
			String path = sb.substring(sb.indexOf("?"));
			DebugUtils.logD(TAG, "sb = " + sb.toString());
			try {
				is = NetworkUtils.openContectionLocked(sb.toString(), path, null);
				try {
					JSONObject jsonObject = new JSONObject(NetworkUtils.getContentFromInput(is));
					mRandCode = jsonObject.getString("randCode");
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
			mTelInput.setText(result);
			DebugUtils.logD(TAG, "result = " + result);
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
