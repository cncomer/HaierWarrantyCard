package com.bestjoy.app.haierwarrantycard.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.view.Menu;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.shwy.bestjoy.utils.AsyncTaskUtils;

public class RegisterActivity extends BaseActionbarActivity implements View.OnClickListener{
	private static final String TAG = "RegisterActivity";
	
	private Button mNextButton;

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
		switch (v.getId()) {
			case R.id.button_next:
				RegisterConfirmActivity.startIntent(this);
				break;
		}
	}
	
	private RegisterAsyncTask mRegisterAsyncTask;
	private ProgressDialog mRegisterDialog;
	private void loginAsync(String... param) {
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
	private class RegisterAsyncTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (mRegisterDialog != null) {
				mRegisterDialog.hide();
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
}
