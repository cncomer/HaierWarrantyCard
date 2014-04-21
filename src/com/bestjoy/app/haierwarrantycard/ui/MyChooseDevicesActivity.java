package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;

public class MyChooseDevicesActivity extends BaseActionbarActivity {
	private static final String TAG = "MyChooseDevicesActivity";
	private ViewPager mViewPager;

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
		setContentView(R.layout.activity_choose_devices_main);
//		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		String title = getIntent().getStringExtra(Intents.EXTRA_NAME);
		if (!TextUtils.isEmpty(title)) {
			setTitle(title);
		}
		mViewPager = (ViewPager) findViewById(R.id.pagerview);
		mViewPager.setAdapter(new MyPagerAdapter(this.getSupportFragmentManager()));
//		DebugChooseDevicesAdapter.addAdapter(this, (ListView) findViewById(R.id.listview), getIntent().getIntExtra(Intents.EXTRA_TYPE, R.id.model_my_card));
	}
	
	
	
	public static void startIntent(Context context, Bundle bundle) {
		Intent intent = new Intent(context, MyChooseDevicesActivity.class);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		context.startActivity(intent);
	}
	
	class MyPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {
		
		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		

		@Override
		public CharSequence getPageTitle(int position) {
			return HaierAccountManager.getInstance().getAccountObject().mAccountHomes.get(position).mHomeName;
		}



		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int arg0) {
			
		}

		@Override
		public Fragment getItem(int arg0) {
			return new SherlockFragment();
		}

		@Override
		public int getCount() {
			return HaierAccountManager.getInstance().getAccountObject().mAccountHomeCount;
		}

	}
	
}
