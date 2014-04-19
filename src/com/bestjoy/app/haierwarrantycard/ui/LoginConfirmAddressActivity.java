package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;

public class LoginConfirmAddressActivity extends BaseActionbarActivity {
	private static final String TAG = "RegisterActivity";

	private ProCityDisEditView psdEditView1;
	private ProCityDisEditView psdEditView2;
	private ProCityDisEditView psdEditView3;

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
		setContentView(R.layout.activity_login_confirm);
//		this.initViews();
	}

//	private void initViews() {
//		psdEditView1 = new ProCityDisEditView(this, R.id.edit_province_1,
//				R.id.edit_city_1, R.id.edit_district_1);
//		psdEditView2 = new ProCityDisEditView(this, R.id.edit_province_2,
//				R.id.edit_city_2, R.id.edit_district_2);
//		psdEditView3 = new ProCityDisEditView(this, R.id.edit_province_3,
//				R.id.edit_city_3, R.id.edit_district_3);
//		
//	}
	
	public static void startIntent(Context context) {
		Intent intent = new Intent(context, LoginConfirmAddressActivity.class);
		context.startActivity(intent);
	}

	
	
	 public boolean onCreateOptionsMenu(Menu menu) {
		 return false;
	 }
}
