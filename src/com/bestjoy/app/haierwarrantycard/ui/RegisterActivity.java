package com.bestjoy.app.haierwarrantycard.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsMessage;
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
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.Intents;
import com.shwy.bestjoy.utils.NetworkUtils;
import com.shwy.bestjoy.utils.SecurityUtils;

public class RegisterActivity extends BaseActionbarActivity implements View.OnClickListener{
	private static final String TAG = "RegisterActivity";
	
	private static final int TIME_COUNDOWN = 120000;
	
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
		// add by chenkai, 开始前先检查网络 begin
		if (!ComConnectivityManager.getInstance().isConnected()) {
			ComConnectivityManager.getInstance().onCreateNoNetworkDialog(this);
			return;
		}
		// add by chekai, 开始前先检查网络 end
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
					&& mYanZhengCodeFromServer.equals(SecurityUtils.MD5.md5(code))) {
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
		mCodeInput.setHint(R.string.usr_validate);
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
		@Override
		protected String doInBackground(String... params) {
			mError = null;
			InputStream is = null;
			String url;
			String path;
			StringBuilder sb = new StringBuilder(HaierServiceObject.SERVICE_URL);
			sb.append("20140514/SendMessage.ashx?cell=").append(params[0]);
			url = sb.substring(0, sb.indexOf("=")+1);
			path = params[0];
			DebugUtils.logD(TAG, "url : " + url);
			DebugUtils.logD(TAG, "path : " + path);
			DebugUtils.logD(TAG, "sb : " + sb.toString());
			try {
				is = NetworkUtils.openContectionLocked(url, path, MyApplication.getInstance().getSecurityKeyValuesObject());
				if (is == null) {
					DebugUtils.logE(TAG, "openContectionLocked return null");
					mError = mContext.getString(R.string.msg_get_yanzhengma_gernal_error);
					return null;
				}
				try {
					JSONObject jsonObject = new JSONObject(NetworkUtils.getContentFromInput(is));
					return jsonObject.getString("randcode");
				} catch (JSONException e) {
					e.printStackTrace();
					mError = mContext.getString(R.string.msg_get_yanzhengma_gernal_error);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				mError = e.getMessage();
			} catch (IOException e) {
				e.printStackTrace();
				mError = MyApplication.getInstance().getGernalNetworkError();
			} finally {
				NetworkUtils.closeInputStream(is);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (mGetYanZhengCodeDialog != null) {
				mGetYanZhengCodeDialog.hide();
			}
			DebugUtils.logD(TAG, "result data : " + result);
			if(mError != null) {
				MyApplication.getInstance().showMessage(mError);
			} else if ("".equals(result)) {
				//提示用户已注册过了
				MyApplication.getInstance().showMessage(R.string.msg_yanzheng_code_msg_has_registered);
			} else if ("2".equals(result)) {
				//提示用户本日获取短信验证码超过限制
				MyApplication.getInstance().showMessage(R.string.msg_get_yanzhengma_overtime);
			} else {
				mYanZhengCodeFromServer = result;
				MyApplication.getInstance().showMessage(R.string.msg_yanzheng_code_msg_send);
				//add by chenkai, 开始监听验证码短信
				if (HaierServiceObject.isSupportReceiveYanZhengMa()) {
					mCodeInput.setHint(R.string.hint_wait_yanzhengma_sms);
					register();
				}
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
	
	//add by chenkai, 增加读取验证码短信，并回填验证码 begin
	private static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private static final Pattern YANZHENGMA_PATTERN = Pattern.compile(".+(\\d{6})");
	private YanZhengMaReceiver mYanZhengMaReceiver;
	private void register() {
		if (mYanZhengMaReceiver == null) {
			mYanZhengMaReceiver = new YanZhengMaReceiver();
			IntentFilter filter = new IntentFilter(SMS_ACTION);
			filter.setPriority(Integer.MAX_VALUE);
			registerReceiver(mYanZhengMaReceiver, filter);
		}
		
	}
	
	private void unregister() {
		if (mYanZhengMaReceiver != null) {
			unregisterReceiver(mYanZhengMaReceiver);
			mYanZhengMaReceiver = null;
		}
	}
	
	private class YanZhengMaReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			DebugUtils.logD(TAG, "onReceive intent " + intent);
			if (SMS_ACTION.equals(intent.getAction())) {
				//回填验证码
				SmsMessage[] smsMessages = Intents.getMessagesFromIntent(intent);
				String message  = smsMessages[0].getMessageBody();
				String address = smsMessages[0].getOriginatingAddress();
				DebugUtils.logD(TAG, "message " + message);
				DebugUtils.logD(TAG, "address " + address);
				if (!TextUtils.isEmpty(address) 
						&& address.length() > 11 
						&& !(address.startsWith("86") || address.startsWith("+86"))
						&& message.contains(context.getString(R.string.haier_yanzhengma_verify2))) {
					Matcher matcher = YANZHENGMA_PATTERN.matcher(message);
					if (matcher.find()) {
						mCodeInput.setText(matcher.group(1));
						DebugUtils.logD(TAG, "find yanzhengma " + matcher.group(1));
						mCodeInput.setHint(R.string.usr_validate);
						//add by chenkai, 移除监听验证码短信
						unregister();
						abortBroadcast();
					}
				}
			}
		}
		
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//add by chenkai, 移除监听验证码短信
		if (HaierServiceObject.isSupportReceiveYanZhengMa()) unregister();
	}
	//add by chenkai, 增加读取验证码短信，并回填验证码 end
}
