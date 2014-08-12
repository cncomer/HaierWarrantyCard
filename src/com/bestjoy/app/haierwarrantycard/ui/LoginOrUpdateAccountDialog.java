package com.bestjoy.app.haierwarrantycard.ui;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.bestjoy.app.haierwarrantycard.HaierServiceObject;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.AccountObject;
import com.bestjoy.app.haierwarrantycard.account.AccountParser;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.update.UpdateService;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.bestjoy.app.haierwarrantycard.utils.YouMengMessageHelper;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.Intents;
import com.shwy.bestjoy.utils.NetworkUtils;
/**
 * 这个类用来更新和登录账户使用。
 * @author chenkai
 *
 */
public class LoginOrUpdateAccountDialog extends Activity{

	private static final String TAG = "LoginOrUpdateAccountDialog";
	private AccountObject mAccountObject;
	private String mTel, mPwd;
	private boolean mIsLogin = false;
	private TextView mStatusView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_or_update_layout);
		mStatusView = (TextView) findViewById(R.id.title);
		Intent intent = getIntent();
		mIsLogin = intent.getBooleanExtra(Intents.EXTRA_TYPE, true);
		mTel = intent.getStringExtra(Intents.EXTRA_TEL);
		mPwd = intent.getStringExtra(Intents.EXTRA_PASSWORD);
		loginAsync();
	}

	private LoginAsyncTask mLoginAsyncTask;
	private void loginAsync() {
		mStatusView.setText(mIsLogin?R.string.msg_login_dialog_title_wait:R.string.msg_update_dialog_title_wait);
		AsyncTaskUtils.cancelTask(mLoginAsyncTask);
		mLoginAsyncTask = new LoginAsyncTask();
		mLoginAsyncTask.execute();
	}
	private class LoginAsyncTask extends AsyncTask<Void, Void, Void> {

		private String _error;
		InputStream _is = null;
		@Override
		protected Void doInBackground(Void... params) {
			_error = null;
			mAccountObject = null;
			_is = null;
			//modify by chenkai, 20140701, 将登录和更新调用的地址抽离出来，以便修改 begin
			//StringBuilder sb = new StringBuilder(HaierServiceObject.SERVICE_URL);
			//sb.append("20140625/login.ashx?cell=").append(mTel)
			//.append("&pwd=");
			try {
				_is = NetworkUtils.openContectionLocked(HaierServiceObject.getLoginOrUpdateUrl(mTel, mPwd), null);
				//modify by chenkai, 20140701, 将登录和更新调用的地址抽离出来，以便修改 end
				mAccountObject = AccountParser.parseJson(_is, mStatusView);
				if (mAccountObject != null && mAccountObject.isLogined()) {
					boolean saveAccountOk = HaierAccountManager.getInstance().saveAccountObject(LoginOrUpdateAccountDialog.this.getContentResolver(), mAccountObject);
					if (!saveAccountOk) {
						//登录成功了，但本地数据保存失败，通常不会走到这里
						_error = LoginOrUpdateAccountDialog.this.getString(R.string.msg_login_save_success);
					}
				} 
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				_error = e.getMessage();
			} catch (IOException e) {
				e.printStackTrace();
				_error = MyApplication.getInstance().getGernalNetworkError();
			} catch (JSONException e) {
				e.printStackTrace();
				_error = e.getMessage();
			} finally {
				NetworkUtils.closeInputStream(_is);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (isCancelled()) {
				//通常不走到这里
				onCancelled();
				return;
			}
			if (_error != null) {
				MyApplication.getInstance().showMessage(_error);
				setResult(Activity.RESULT_CANCELED);
			} else if (mAccountObject != null) {
				//如果登陆成功
				if (mAccountObject.isLogined()) {
					setResult(Activity.RESULT_OK);
					//每次登陆，我们都需要注册设备Token
					YouMengMessageHelper.getInstance().saveDeviceTokenStatus(false);
					//登录成功，我们需要检查是否能够上传设备Token到服务器绑定uid和token
					UpdateService.startCheckDeviceTokenToService(LoginOrUpdateAccountDialog.this);
				} else {
					MyApplication.getInstance().showMessage(mAccountObject.mStatusMessage);
					setResult(Activity.RESULT_CANCELED);
				}
			} else {
				MyApplication.getInstance().showMessage(R.string.msg_login_failed_general);
				setResult(Activity.RESULT_CANCELED);
			}
			finish();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			MyApplication.getInstance().showMessage(R.string.msg_op_canceled);
			setResult(Activity.RESULT_CANCELED);
			finish();
			
		}
		
		public void cancelTask(boolean cancel) {
			super.cancel(cancel);
			//由于IO操作是不可中断的，所以我们这里关闭IO流来终止任务
			NetworkUtils.closeInputStream(_is);
			
		}
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (mLoginAsyncTask != null) {
			mLoginAsyncTask.cancelTask(true);
			DebugUtils.logD(TAG, "login or update is canceled by user");
		}
	}
	
	public static Intent createLoginOrUpdate(Context context, boolean login, String tel, String pwd) {
		Intent intent = new Intent(context, LoginOrUpdateAccountDialog.class);
		intent.putExtra(Intents.EXTRA_TYPE, login);
		intent.putExtra(Intents.EXTRA_TEL, tel);
		intent.putExtra(Intents.EXTRA_PASSWORD, pwd);
		return intent;
	}
	
}
