package com.bestjoy.app.haierwarrantycard.ui;

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.ui.model.ModleSettings;
import com.bestjoy.app.haierwarrantycard.update.UpdateService;
import com.bestjoy.app.haierwarrantycard.utils.BitmapUtils;
import com.bestjoy.app.haierwarrantycard.utils.MenuHandlerUtils;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.FilesUtils;

public class MainActivity extends BaseActionbarActivity {
	private LinearLayout mDotsLayout;
	private ViewPager mAdsViewPager;
	private boolean mAdsViewPagerIsScrolling = false;
	
	private static int[] mAddsDrawableId = new int[]{
			R.drawable.ad1,
			R.drawable.ad2,
			R.drawable.ad3,
			R.drawable.ad4,
	};
	
	private Drawable[] mDotDrawableArray;
	private Bitmap[] mAdsBitmaps;
	
	private ImageView[] mDotsViews = null;
	private ImageView[] mAdsPagerViews = null;
	private int mCurrentPagerIndex = 0;
	private static final int DEFAULT_MAX_ADS_SIZE = mAddsDrawableId.length;
	
	private Handler mHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		setContentView(R.layout.activity_main);
		mDotsLayout = (LinearLayout) findViewById(R.id.dots);
		mAdsViewPager = (ViewPager) findViewById(R.id.adsViewPager);
		this.initViewPagers(DEFAULT_MAX_ADS_SIZE);
		this.initDots(DEFAULT_MAX_ADS_SIZE);
		mAdsViewPager.setAdapter(new AdsViewPagerAdapter());
		
		mAdsViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (mCurrentPagerIndex != position) {
					mDotsViews[mCurrentPagerIndex].setImageDrawable(mDotDrawableArray[0]);
					mDotsViews[position].setImageDrawable(mDotDrawableArray[1]);
					mCurrentPagerIndex = position;
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				mAdsViewPagerIsScrolling  = state == 1;
				
			}
		});
		
		ModleSettings.addModelsAdapter(this, (ListView) findViewById(R.id.listview));
		UpdateService.startUpdateServiceOnAppLaunch(mContext);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		invalidateOptionsMenu();
		changeAdsDelay();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		mHandler.removeCallbacks(mChangeAdsRunnable);
	}
	
	 @Override
     public boolean onCreateOptionsMenu(Menu menu) {
  	     boolean result = super.onCreateOptionsMenu(menu);
  	     MenuItem subMenu1Item = menu.findItem(R.string.menu_more);
  	   subMenu1Item.getSubMenu().add(1000, R.string.menu_refresh, 1005, R.string.menu_refresh);
  	     subMenu1Item.getSubMenu().add(1000, R.string.menu_exit, 1006, R.string.menu_exit);
  	     subMenu1Item.setIcon(R.drawable.abs__ic_menu_moreoverflow_normal_holo_light);
         return result;
     }
	 
	 public boolean onPrepareOptionsMenu(Menu menu) {
		 menu.findItem(R.string.menu_exit).setVisible(HaierAccountManager.getInstance().hasLoginned());
		 menu.findItem(R.string.menu_refresh).setVisible(HaierAccountManager.getInstance().hasLoginned());
	     return super.onPrepareOptionsMenu(menu);
	 }
	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem menuItem) {
		 switch(menuItem.getItemId()) {
		 case R.string.menu_exit:
			 new AlertDialog.Builder(mContext)
				.setMessage(R.string.msg_existing_system_confirm)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteAccountAsync();
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.show();
			 return true;
		 case R.string.menu_refresh:
			 if (HaierAccountManager.getInstance().hasLoginned()) {
				 //做一次登陆操作
				 //目前只删除本地的所有缓存文件
				 File dir = MyApplication.getInstance().getCachedXinghaoInternalRoot();
				 FilesUtils.deleteFile("Updating ", dir);
				 
				 dir = MyApplication.getInstance().getCachedXinghaoExternalRoot();
				 if (dir != null) {
					 FilesUtils.deleteFile("Updating ", dir);
				 }
			 }
			 break;
		 }
		 return super.onOptionsItemSelected(menuItem);
	 }
	 
	 private DeleteAccountTask mDeleteAccountTask;
	 private void deleteAccountAsync() {
		 AsyncTaskUtils.cancelTask(mDeleteAccountTask);
		 showDialog(DIALOG_PROGRESS);
		 mDeleteAccountTask = new DeleteAccountTask();
		 mDeleteAccountTask.execute();
	 }
	 private class DeleteAccountTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			HaierAccountManager.getInstance().deleteDefaultAccount();
			HaierAccountManager.getInstance().saveLastUsrTel("");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dismissDialog(DIALOG_PROGRESS);
			invalidateOptionsMenu();
			MyApplication.getInstance().showMessage(R.string.msg_op_successed);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			invalidateOptionsMenu();
			dismissDialog(DIALOG_PROGRESS);
		}
		
		
		 
	 }
	 


	private void initDots(int count){
		LayoutInflater flater = this.getLayoutInflater();
		if (mDotDrawableArray == null) {
			mDotDrawableArray = new Drawable[2];
			mDotDrawableArray[0] = this.getResources().getDrawable(R.drawable.dot);
			mDotDrawableArray[1] = this.getResources().getDrawable(R.drawable.dot_on);
		}
		mDotsViews = new ImageView[count];
		for (int j = 0; j < count; j++) {
			mDotsViews[j] = (ImageView) flater.inflate(R.layout.dot, mDotsLayout, false);
			if (mCurrentPagerIndex == j) {
				mDotsViews[j].setImageDrawable(mDotDrawableArray[1]);
			} else {
				mDotsViews[j].setImageDrawable(mDotDrawableArray[0]);
			}
			mDotsLayout.addView(mDotsViews[j]);
		}
	}
	
	private void initAdsBitmap() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		if (mAdsBitmaps == null) {
			mAdsBitmaps = new Bitmap[DEFAULT_MAX_ADS_SIZE];
		} else {
			for(Bitmap bitmap:mAdsBitmaps) {
				bitmap.recycle();
			}
		}
		int adsW = getResources().getDimensionPixelSize(R.dimen.ads_width);
		int adsH = getResources().getDimensionPixelSize(R.dimen.ads_height);
		mAdsBitmaps = BitmapUtils.getSuitedBitmaps(this, mAddsDrawableId, adsW, adsH);
	}
	
	private void initViewPagers(int count) {
		initAdsBitmap();
		mAdsPagerViews = new ImageView[count];
		LayoutInflater flater = getLayoutInflater();
		for (int j = 0; j < count; j++) {
			mAdsPagerViews[j] = (ImageView) flater.inflate(R.layout.ads, null, false);
			mAdsPagerViews[j].setImageBitmap(mAdsBitmaps[j]);
		}
	}
	
	private void initAdsPager(){
	}
	
	class AdsViewPagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return mAdsPagerViews.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		
		private View getView(ViewGroup container, int position) {
			container.addView(mAdsPagerViews[position]);
			return mAdsPagerViews[position];
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			return getView(container, position);
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mAdsPagerViews[position]);
		}
		
	}
	
	private void changeAdsDelay() {
		mHandler.postDelayed(mChangeAdsRunnable, DEFAULT_DELAY);
	}
	private static long DEFAULT_DELAY = 5000;
	private ChangeAdsRunnable mChangeAdsRunnable = new ChangeAdsRunnable();
	private class ChangeAdsRunnable implements Runnable {
		@Override
		public void run() {
			if (!mAdsViewPagerIsScrolling) {
				int pageCount = mAdsViewPager.getAdapter().getCount();
				int nextPage = mCurrentPagerIndex % pageCount + 1;
				if (nextPage >= pageCount) {
					nextPage = 0;
				}
				mAdsViewPager.setCurrentItem(nextPage);
				
			}
			mHandler.postDelayed(this, DEFAULT_DELAY);
		}
		
	}

	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}
	/**
	 * 回到主界面
	 * @param context
	 */
	public static void startActivityForTop(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}

}
