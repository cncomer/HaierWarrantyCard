package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.BaoxiuCardObject;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.bestjoy.app.haierwarrantycard.ui.model.ModleSettings;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;
/**
 * 我的家UI，需要选择设备的调用，都可以直接进入该Activity进行选择
 * @author chenkai
 *
 */
public class MyChooseDevicesActivity extends BaseActionbarActivity implements HomeBaoxiuCardFragment.OnBaoxiuCardItemClickListener {
	/**用来发起选择设备的Action*/
	public static final String ACTION_CHOOSE_DEVICE = "com.bestjoy.app.haierwarrantycard.Intent.ACTION_CHOOSE_DEVICE";
	private static final String TAG = "MyChooseDevicesActivity";
	private ViewPager mViewPager;
	private boolean mIsChooseDevice = false;
	
	private Bundle mBundle = null;
	
	private int mHomeSelected = 0;
	private MyPagerAdapter mMyPagerAdapter;

	@Override
	protected boolean checkIntent(Intent intent) {
		mBundle = intent.getExtras();
		if (mBundle == null) {
			DebugUtils.logD(TAG, "you must pass Bundle object in createChooseDevice()");
			return false;
		}
		return true;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//每次进来我们先重置这个静态成员
		BaoxiuCardObject.setBaoxiuCardObject(null);
		DebugUtils.logD(TAG, "onCreate()");
		if (isFinishing()) {
			return ;
		}
		if (ACTION_CHOOSE_DEVICE.equals(getIntent().getAction())) {
			DebugUtils.logD(TAG, "want to choose device");
			mIsChooseDevice = true;
		}
		setContentView(R.layout.activity_choose_devices_main);
		String title = mBundle.getString(Intents.EXTRA_NAME);
		if (!TextUtils.isEmpty(title)) {
			setTitle(title);
		}
		mViewPager = (ViewPager) findViewById(R.id.pagerview);
		mMyPagerAdapter = new MyPagerAdapter(this.getSupportFragmentManager());
		mViewPager.setAdapter(mMyPagerAdapter);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ModleSettings.createActionBarMenu(menu, mBundle);
		MenuItem homeItem = menu.add(R.string.menu_manage_home, R.string.menu_manage_home, 0, R.string.menu_manage_home);
		homeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.string.menu_manage_home:
			//管理家
			break;
		default:
			boolean handle = ModleSettings.onActionBarMenuSelected(item, mContext, mBundle);
			if (! handle) {
				return super.onOptionsItemSelected(item);
			}
			break;
		}
		return false;
		
	}




	class MyPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {
		
		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		

		@Override
		public CharSequence getPageTitle(int position) {
			return getHome(position).mHomeName;
		}
		
		private HomeObject getHome(int position) {
			return HaierAccountManager.getInstance().getAccountObject().mAccountHomes.get(position);
		}



		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int arg0) {
			mHomeSelected = arg0;
		}

		@Override
		public Fragment getItem(int position) {
			HomeBaoxiuCardFragment f = new HomeBaoxiuCardFragment();
			f.setOnItemClickListener(MyChooseDevicesActivity.this);
			f.setHomeBaoxiuCard(getHome(position));
			return f;
		}

		@Override
		public int getCount() {
			return HaierAccountManager.getInstance().getAccountObject().mAccountHomeCount;
		}

	}
	
	public static void startIntent(Context context, Bundle bundle) {
		Intent intent = new Intent(context, MyChooseDevicesActivity.class);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		context.startActivity(intent);
	}
	/**
	 * 发起一个选择设备的Intent,需要发起Activity处理选择后的操作
	 * @param context
	 * @param title
	 * @param type  算泽设备的类型，比如安装、新建保修卡
	 * @return
	 */
	public static Intent createChooseDevice(Context context, Bundle bundel) {
		Intent intent = new Intent(context, MyChooseDevicesActivity.class);
		intent.setAction(ACTION_CHOOSE_DEVICE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.putExtras(bundel);
		return intent;
	}

	@Override
	public void onItemClicked(BaoxiuCardObject card) {
	    if (mIsChooseDevice) {
	    	//一些特殊的操作，可以放在这里，目前暂不需要实现
	    }
	    BaoxiuCardObject.setBaoxiuCardObject(card);
	    HomeObject.setHomeObject(mMyPagerAdapter.getHome(mHomeSelected));
    	ModleSettings.doChoose(mContext, mBundle);
		
	}
	
}
