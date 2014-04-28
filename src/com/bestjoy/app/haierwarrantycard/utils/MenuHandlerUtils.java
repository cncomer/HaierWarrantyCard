package com.bestjoy.app.haierwarrantycard.utils;

import android.content.Context;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.ui.LoginActivity;
import com.bestjoy.app.haierwarrantycard.ui.RegisterActivity;

public class MenuHandlerUtils {
	
    public static void onCreateOptionsMenu(Menu menu) {
        SubMenu subMenu1 = menu.addSubMenu(1000, R.string.menu_more, 1000, R.string.menu_more);
        subMenu1.add(1000, R.string.menu_login, 1001, R.string.menu_login);
        subMenu1.add(1000, R.string.menu_register, 1002, R.string.menu_register);
        subMenu1.add(1000, R.string.menu_setting, 1003, R.string.menu_setting);
        subMenu1.add(1000, R.string.menu_help, 1004, R.string.menu_help);
        subMenu1.add(1000, R.string.menu_about, 1004, R.string.menu_about);

        MenuItem subMenu1Item = subMenu1.getItem();
        subMenu1Item.setIcon(R.drawable.abs__ic_menu_moreoverflow_normal_holo_dark);
        subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
    
    public static boolean onOptionsItemSelected(MenuItem item, Context context) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case R.string.menu_login:
        	LoginActivity.startIntent(context, null);
     	   break;
        case R.string.menu_register:
        	RegisterActivity.startIntent(context);
      	   break;
        case R.string.menu_setting:
      	   break;
        case R.string.menu_help:
      	   break;
        case R.string.menu_about:
      	   break;

        }
        return false;
    }
    
    public static boolean onPrepareOptionsMenu(Menu menu, Context context) {
    	//如果已经登陆了，那么我们不再显示登录菜单
//    	if (HaierAccountManager.getInstance().hasLoginned()) {
//    		menu.findItem(R.string.menu_login).setVisible(false);
//    	} else {
//    		menu.findItem(R.string.menu_login).setVisible(true);
//    	}
		return true;
	}
}
