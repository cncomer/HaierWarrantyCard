package com.bestjoy.app.haierwarrantycard.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bestjoy.app.haierwarrantycard.HaierServiceObject;
import com.bestjoy.app.haierwarrantycard.HaierServiceObject.HaierResultObject;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.bestjoy.app.haierwarrantycard.ui.model.ModleSettings;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.bestjoy.app.haierwarrantycard.view.ProCityDisEditPopView;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.NetworkUtils;

public class NewHomeActivity extends BaseActionbarActivity {
	private static final String TAG = "NewHomeActivity";
	private ProCityDisEditPopView mProCityDisEditPopView;
	private EditText mHomeEditText;
	private HomeObject mHomeObject ;
	@Override
	protected boolean checkIntent(Intent intent) {
		mHomeObject = HomeObject.getHomeObject();
		if (mHomeObject == null) {
			DebugUtils.logD(TAG, "mHomeObject is null, so finish it");
		}
		return mHomeObject != null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_home);
		if (mHomeObject.mHomeAid > 0) {
			setTitle(R.string.activity_title_update_home);
		}
		mProCityDisEditPopView = new ProCityDisEditPopView(this);
		mHomeEditText = (EditText) findViewById(R.id.my_home);
		updateView();
	}

	private void updateView() {
		mProCityDisEditPopView.setHomeObject(mHomeObject);
		mHomeEditText.setText(mHomeObject.getHomeTag(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ModleSettings.createActionBarMenu(menu, null);
		MenuItem homeItem = menu.add(R.string.menu_save, R.string.menu_save, 0, mHomeObject.mHomeAid > 0?R.string.button_update:R.string.menu_save);
		homeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.string.menu_save:
			if(valiInput()) {
				createNewHomeAsync();
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private boolean valiInput() {
		HomeObject mHomeObject = mProCityDisEditPopView.getHomeObject();
		if(TextUtils.isEmpty(mHomeObject.mHomeProvince)) {
			MyApplication.getInstance().showMessage(R.string.msg_input_usr_pro);
			return false;
		} else if (TextUtils.isEmpty(mHomeObject.mHomeCity)){
			MyApplication.getInstance().showMessage(R.string.msg_input_usr_city);
			return false;
		} else if (TextUtils.isEmpty(mHomeObject.mHomeDis)){
			MyApplication.getInstance().showMessage(R.string.msg_input_usr_dis);
			return false;
		} else if (TextUtils.isEmpty(mHomeObject.mHomePlaceDetail)){
			MyApplication.getInstance().showMessage(R.string.msg_input_usr_place_detail);
			return false;
		}
		return true;
	}

	public static void startActivity(Context context) {
		Intent intent = new Intent(context, NewHomeActivity.class);
		context.startActivity(intent);
	}


	CreateNewHomeAsyncTask mCreateNewHomeAsyncTask;
	private void createNewHomeAsync() {
		AsyncTaskUtils.cancelTask(mCreateNewHomeAsyncTask);
		showDialog(DIALOG_PROGRESS);
		mCreateNewHomeAsyncTask = new CreateNewHomeAsyncTask();
		mCreateNewHomeAsyncTask.execute();
		
	}
	
	private class CreateNewHomeAsyncTask extends AsyncTask<String, Void, HaierResultObject> {
		@Override
		protected HaierResultObject doInBackground(String... arg0) {
			InputStream is = null;
			HaierResultObject haierResultObject = new HaierResultObject();
			HomeObject mHomeObject = mProCityDisEditPopView.getHomeObject();
			final int LENGTH = 6;
			String[] urls = new String[LENGTH];
			String[] paths = new String[LENGTH];
			urls[0] = HaierServiceObject.SERVICE_URL + "Addaddr.ashx?ShenFen=";
			paths[0] = mHomeObject.mHomeProvince;
			urls[1] = "&City=";
			paths[1] = mHomeObject.mHomeCity;
			urls[2] = "&QuXian=";
			paths[2] = mHomeObject.mHomeDis;
			urls[3] = "&DetailAddr=";
			paths[3] = mHomeObject.mHomePlaceDetail;
			urls[4] = "&UID=";
			paths[4] = String.valueOf(HaierAccountManager.getInstance().getAccountObject().mAccountUid);
			urls[5] = "&Tag=";
			paths[5] = mHomeEditText.getText().toString().trim();
			DebugUtils.logD(TAG, "urls = " + Arrays.toString(urls));
			DebugUtils.logD(TAG, "paths = " + Arrays.toString(paths));
			try {
				is = NetworkUtils.openContectionLocked(urls, paths, MyApplication.getInstance().getSecurityKeyValuesObject());
				if (is != null) {
					String content = NetworkUtils.getContentFromInput(is);
					haierResultObject = HaierResultObject.parse(content);
					if (haierResultObject.isOpSuccessfully()) {
						//更新服务器上的数据成功，我们需要更新本地的
						if (mHomeObject.mHomeAid == -1) {
							//是新建
							mHomeObject.mHomeUid = HaierAccountManager.getInstance().getAccountObject().mAccountUid;
							String data = haierResultObject.mStrData;
							DebugUtils.logD(TAG, "CreateNewHomeAsyncTask return data " + data);
							if (!TextUtils.isEmpty(data)) {
								int index = data.indexOf(":");
								if (index > 0) {
									data = data.substring(index+1);
									DebugUtils.logD(TAG, "CreateNewHomeAsyncTask find aid " + data);
									mHomeObject.mHomeAid = Long.valueOf(data);
								}
							}
						} 
						boolean saved = mHomeObject.saveInDatebase(getContentResolver(), null);
						if (!saved) {
							MyApplication.getInstance().showMessageAsync(R.string.msg_local_save_op_failed);
						}
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				haierResultObject.mStatusMessage = e.getMessage();
			} catch (IOException e) {
				e.printStackTrace();
				haierResultObject.mStatusMessage = e.getMessage();
			} finally {
				NetworkUtils.closeInputStream(is);
			}
			return haierResultObject;
		}

		@Override
		protected void onPostExecute(HaierResultObject result) {
			super.onPostExecute(result);
			dismissDialog(DIALOG_PROGRESS);
			MyApplication.getInstance().showMessageAsync(result.mStatusMessage);
			if(result.isOpSuccessfully()) {
				NewHomeActivity.this.finish();
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dismissDialog(DIALOG_PROGRESS);
		}
	}
}
