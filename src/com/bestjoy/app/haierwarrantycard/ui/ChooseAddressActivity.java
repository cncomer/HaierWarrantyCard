package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;

public class ChooseAddressActivity extends BaseActionbarActivity {
	private static final String TAG = "RegisterActivity";

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
		setContentView(R.layout.activity_choose_address);
		this.initViews();
	}

	private void initViews() {
		
	}
	
	public static void startIntent(Context context) {
		Intent intent = new Intent(context, ChooseAddressActivity.class);
		context.startActivity(intent);
	}

	
	
	 public boolean onCreateOptionsMenu(Menu menu) {
		 return false;
	 }
}
