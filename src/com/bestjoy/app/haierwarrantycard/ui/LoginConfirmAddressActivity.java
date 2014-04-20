package com.bestjoy.app.haierwarrantycard.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.bestjoy.app.haierwarrantycard.view.ProCityDisEditView;
import com.shwy.bestjoy.utils.AsyncTaskUtils;

public class LoginConfirmAddressActivity extends BaseActionbarActivity implements View.OnClickListener{
	private static final String TAG = "RegisterActivity";

	private ProCityDisEditView[] mHomes = new ProCityDisEditView[3];

	@Override
	protected boolean checkIntent(Intent intent) {
		if (LoginActivity.mAccountObject == null) {
			DebugUtils.logD(TAG, "LoginActivity.mAccountObject == null. finish");
			return false;
		}
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugUtils.logD(TAG, "onCreate()");
		if (isFinishing()) {
			return ;
		}
		setContentView(R.layout.activity_login_confirm);
		mHomes[0] = (ProCityDisEditView) findViewById(R.id.home1);
		mHomes[1] = (ProCityDisEditView) findViewById(R.id.home2);
		mHomes[2] = (ProCityDisEditView) findViewById(R.id.home3);
		
		findViewById(R.id.button_save).setOnClickListener(this); 
		populateHome();
		
	}
	
	private void populateHome() {
		//XXX TODO
		int homeLen = 3;
		if (LoginActivity.mAccountObject.mAccountHomes.size() < 3) {
			homeLen = LoginActivity.mAccountObject.mAccountHomes.size();
		}
		for(int index = 0; index < homeLen; index++) {
			mHomes[index].setHomeObject(LoginActivity.mAccountObject.mAccountHomes.get(index));
		}
	}

	public static void startIntent(Context context) {
		Intent intent = new Intent(context, LoginConfirmAddressActivity.class);
		context.startActivity(intent);
	}

	
	
	 public boolean onCreateOptionsMenu(Menu menu) {
		 return false;
	 }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_save:
			saveAsync();
			break;
		}
		
	}
	
	private SaveAsyncTask mSaveAsyncTask;
	private void saveAsync() {
		showDialog(this.DIALOG_PROGRESS);
		AsyncTaskUtils.cancelTask(mSaveAsyncTask);
		mSaveAsyncTask = new SaveAsyncTask();
		mSaveAsyncTask.execute();
	}
	
	private class SaveAsyncTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			//重新更细一下Home数据
			for(ProCityDisEditView view: mHomes) {
				view.updateHomeObject();
			}
			List<HomeObject> list = new ArrayList<HomeObject>(LoginActivity.mAccountObject.mAccountHomes.size());
			for(HomeObject homeObject : LoginActivity.mAccountObject.mAccountHomes) {
				if (!homeObject.hasValidateAddress()) {
					list.add(homeObject);
				}
			}
			if(list.size() > 0) {
				LoginActivity.mAccountObject.mAccountHomes.removeAll(list);
			}
			return HaierAccountManager.getInstance().saveAccountObject(mContext.getContentResolver(), LoginActivity.mAccountObject);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dismissDialog(DIALOG_PROGRESS);
			MyApplication.getInstance().showMessage(R.string.msg_login_confirm_success, Toast.LENGTH_LONG);
			MainActivity.startActivityForTop(mContext);
			finish();
		}
		
	}
	
	
	
}
