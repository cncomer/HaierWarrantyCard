package com.bestjoy.app.haierwarrantycard.ui;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class NewCardActivity extends BaseSlidingFragmentActivity implements View.OnClickListener, 
	SlidingMenu.OnOpenedListener, SlidingMenu.OnClosedListener{
	private static final String TAG = "NewCardActivity";
	private NewWarrantyCardFragment mContent;
	private ChooseDevicesFragment mMenu;
	//临时的拍摄照片路径
	private File mBillTempFile, mAvatorTempFile;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugUtils.logD(TAG, "onCreate()");
		if (isFinishing()) {
			return ;
		}
		
		
		if (savedInstanceState != null) {
			mContent = (NewWarrantyCardFragment) getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
			mMenu = (ChooseDevicesFragment) getSupportFragmentManager().getFragment(savedInstanceState, "mMenu");
		}
		
		if (mContent == null) {
			mContent = new NewWarrantyCardFragment();
		}
		if (mMenu == null) {
			mMenu = new ChooseDevicesFragment();
		}
		
		// set the Above View
		setContentView(R.layout.content_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, mContent)
		.commit();
		
		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame, mMenu)
		.commit();
		
		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
//		sm.setBehindOffsetRes(R.dimen.choose_device_slidingmenu_offset);
//        sm.setAboveOffsetRes(R.dimen.choose_device_slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);
		sm.setMode(SlidingMenu.RIGHT);
		sm.setTouchModeAbove(SlidingMenu.RIGHT);
		sm.setBehindOffsetRes(R.dimen.choose_device_choose_slidingmenu_offset);
		sm.setOnOpenedListener(this);
		sm.setOnClosedListener(this);
		
		setSlidingActionBarEnabled(false);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		
		initTempFile();
		
	}
	
	private void initTempFile() {
		File tempRootDir = Environment.getExternalStorageDirectory();
		mBillTempFile = new File(tempRootDir, ".billTemp");
		mAvatorTempFile = new File(tempRootDir, ".avatorTemp");
	}
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_save:
			LoginActivity.startIntent(this);
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.new_card_activity_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (getSlidingMenu().isMenuShowing()) {
			menu.findItem(R.string.menu_choose).setVisible(false);
			menu.findItem(R.string.menu_search).setVisible(true);
		} else {
			menu.findItem(R.string.menu_choose).setVisible(true);
			menu.findItem(R.string.menu_search).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.string.menu_search:
			break;
		case R.string.menu_choose:
			getSlidingMenu().showMenu(true);
			break;
			 // Respond to the action bar's Up/Home button
        case android.R.id.home:
     	   Intent upIntent = NavUtils.getParentActivityIntent(this);
     	   if (upIntent == null) {
     		   // If we has configurated parent Activity in AndroidManifest.xml, we just finish current Activity.
     		   finish();
     		   return true;
     	   }
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                // This activity is NOT part of this app's task, so create a new task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(this)
                        // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent)
                        // Navigate up to the closest parent
                        .startActivities();
            } else {
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                NavUtils.navigateUpTo(this, upIntent);
            }
            return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onOpened() {
		//当SlidingMenu打开后，我们需要隐藏掉手动打开SlidinMenu按钮
		this.invalidateOptionsMenu();
	}


	@Override
	public void onClosed() {
		//当SlidingMenu关闭后，我们需要重新显示手动打开SlidinMenu按钮
		this.invalidateOptionsMenu();
		
	}
	
	public static void startIntent(Context context, Bundle bundle) {
		Intent intent = new Intent(context, NewCardActivity.class);
		if (bundle != null) intent.putExtras(bundle);
		context.startActivity(intent);
	}

	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}
	
}
