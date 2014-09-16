package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.MyAccountManager;
import com.bestjoy.app.haierwarrantycard.utils.MenuHandlerUtils;
import com.bestjoy.app.haierwarrantycard.view.ModuleViewUtils;

public class BlueStyleMainActivity extends BaseNoActionBarActivity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setShowHomeUp(true);
		setContentView(R.layout.activity_main_blue_style);
		ModuleViewUtils.getInstance().setContext(mContext);
		ModuleViewUtils.getInstance().initModules(this);
		initActionMenu();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		onPrepareOptionsMenu(getActionMenu());
		invalidateActionMenu();
	}
	
	private void initActionMenu() {
		MenuBuilder menu = new MenuBuilder(this);
		MenuHandlerUtils.onCreateOptionsMenu(menu);
        initActionMenu(menu);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.string.menu_exit).setVisible(MyAccountManager.getInstance().hasLoginned());
		 menu.findItem(R.string.menu_refresh).setVisible(MyAccountManager.getInstance().hasLoginned());
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!MenuHandlerUtils.onOptionsItemSelected(item, mContext)) {
			super.onOptionsItemSelected(item);
		}
		return true;
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
		Intent intent = new Intent(context, BlueStyleMainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}


}
