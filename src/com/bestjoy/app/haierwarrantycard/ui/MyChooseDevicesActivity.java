package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;

public class MyChooseDevicesActivity extends BaseActionbarActivity {
	private static final String TAG = "MyChooseDevicesActivity";

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
		setContentView(R.layout.activity_choose_devices);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		String title = getIntent().getStringExtra(Intents.EXTRA_NAME);
		if (!TextUtils.isEmpty(title)) {
			setTitle(title);
		}
//		DebugChooseDevicesAdapter.addAdapter(this, (ListView) findViewById(R.id.listview), getIntent().getIntExtra(Intents.EXTRA_TYPE, R.id.model_my_card));
	}
	
	public static void startIntent(Context context, Bundle bundle) {
		Intent intent = new Intent(context, MyChooseDevicesActivity.class);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		context.startActivity(intent);
	}
	
}
