package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.bestjoy.app.haierwarrantycard.R;
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
//		DebugChooseDevicesAdapter.addAdapter(this, (ListView) findViewById(R.id.listview), getIntent().getIntExtra(Intents.EXTRA_TYPE, R.id.model_my_card));
	}
	
	
	
	public static void startIntent(Context context, Bundle bundle) {
		Intent intent = new Intent(context, MyChooseDevicesActivity.class);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		context.startActivity(intent);
	}
	
//	class MyPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
//		int mCurPos = 0;
//
//		@Override
//		public int getCount() {
//			return mTabs.size();
//		}
//
//		@Override
//		public Object instantiateItem(ViewGroup container, int position) {
//			TabInfo tab = mTabs.get(position);
//			View root = tab.build(mInflater, mContentContainer, mRootView);
//			container.addView(root);
//			return root;
//		}
//
//		@Override
//		public void destroyItem(ViewGroup container, int position, Object object) {
//			container.removeView((View)object);
//		}
//
//		@Override
//		public boolean isViewFromObject(View view, Object object) {
//			return view == object;
//		}
//
//		@Override
//		public CharSequence getPageTitle(int position) {
//			return mTabs.get(position).mLabel;
//		}
//
//		@Override
//		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//		}
//
//		@Override
//		public void onPageSelected(int position) {
//			mCurPos = position;
//			//fix bug 208507 it is not the original tab,after back from the ap detail screen on 20130829 start
//			mDefaultListType = mCurPos;
//			//fix bug 208507 it is not the original tab,after back from the ap detail screen on 20130829 end
//		}
//
//		@Override
//		public void onPageScrollStateChanged(int state) {
//			if (state == ViewPager.SCROLL_STATE_IDLE) {
//				updateCurrentTab(mCurPos);
//			}
//		}
//	}
	
}
