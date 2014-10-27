package com.bestjoy.app.haierwarrantycard.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.bestjoy.app.haierwarrantycard.account.MyAccountManager;
import com.bestjoy.app.haierwarrantycard.im.RelationshipActivity;
import com.bestjoy.app.haierwarrantycard.ui.BrowserActivity;
import com.bestjoy.app.haierwarrantycard.ui.CaptureActivity;
import com.bestjoy.app.haierwarrantycard.ui.MyChooseDevicesActivity;
import com.bestjoy.app.haierwarrantycard.ui.NewCardActivity;
import com.bestjoy.app.haierwarrantycard.ui.NewHomeActivity;
import com.bestjoy.app.haierwarrantycard.ui.YMessageListActivity;
import com.bestjoy.app.haierwarrantycard.ui.model.ModleSettings;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;

public class ModuleViewUtils {

	private static final String TAG = "ModuleViewUtils";
	private Context mContext;
	private static ModuleViewUtils INSTANCE = new ModuleViewUtils();
	
	private static final int[] MODULE_IDS = new int[] {
		R.id.model_my_card,
		R.id.model_my_messages,
		R.id.model_my_business,
		R.id.model_my_community,
		R.id.model_my_store,
		R.id.model_install,
		R.id.model_repair,
		R.id.model_feedback,
		R.id.model_my_scan,
		
	};
	
	public static ModuleViewUtils getInstance() {
		return INSTANCE;
	}
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	public void initModules(Activity activity) {
		int index = 0;
		for (int id : MODULE_IDS) {
			DebugUtils.logD(TAG, "index " + index);
			if (id == R.id.model_my_scan) {
				ModuleView.findViewById(id, activity).findViewById(R.id.button_scan).setOnClickListener(mModuleOnClickListener);
			} else {
				ModuleView.findViewById(id, activity).setOnClickListener(mModuleOnClickListener);
			}
			
			index++;
		}
	}
	
	private View.OnClickListener mModuleOnClickListener  = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle();
			switch(v.getId()) {
			case R.id.button_scan: //Scan
				Intent scanIntent = new Intent(mContext, CaptureActivity.class);
				mContext.startActivity(scanIntent);
				return;
			case R.id.model_my_community:
				Intents.openURL(mContext,  "http://m.haier.com/bbs/index.shtml");
				return;
			case R.id.model_my_business: //Haier Eshop
				if (true) {
					MyApplication.getInstance().showUnsupportMessage();
					return;
				}
				if (MyAccountManager.getInstance().hasLoginned()) {
					RelationshipActivity.startActivity(mContext);
				} else {
					MyApplication.getInstance().showNeedLoginMessage();
				}
				return;
			case R.id.model_my_messages:
				YMessageListActivity.startActivity(mContext);
				return;
			case R.id.model_my_store: //Haier Navigation
				Intents.openURL(mContext,  "http://m.haier.com/cn/");
				return;
			case R.id.model_feedback:
				Intents.openURL(mContext,  "http://m.rrs.com/rrsm/track/verify.html");
				return;
			case R.id.model_my_card:
				bundle = createMyCardDefaultBundle(mContext);
				break;
			case R.id.model_install:
				bundle = createMyInstallDefaultBundle(mContext);
				break;
			case R.id.model_repair:
				bundle = createMyRepairDefaultBundle(mContext);
				break;
			}
			if (MyAccountManager.getInstance().hasLoginned()) {
				//如果登陆了，我们先设置默认的家对象
				bundle.putLong("aid", MyAccountManager.getInstance().getHomeAIdAtPosition(0));
				bundle.putLong("uid", MyAccountManager.getInstance().getCurrentAccountId());
				//判断是否有家，没有的话，就要去新建一个家
				if (!MyAccountManager.getInstance().hasHomes()) {
					HomeObject.setHomeObject(new HomeObject());
					MyApplication.getInstance().showNeedHomeMessage();
					NewHomeActivity.startActivity(mContext);
					return;
				}
			}
			if (MyAccountManager.getInstance().hasBaoxiuCards()) {
			    MyChooseDevicesActivity.startIntent(mContext, bundle);
			} else {
				NewCardActivity.startIntent(mContext, bundle);
			}
			
		}
		
	};
	
	/**
	 * 处理设备选择后的回调,注意的是我的保修卡选择后要进入到保修卡详细界面
	 * @param type
	 */
	public static void doChoose(Context context, Bundle bundle) {
		int type = ModleSettings.getModelIdFromBundle(bundle);
		switch(type) {
		case R.id.model_my_card:
		case R.id.model_install:
		case R.id.model_repair:
			// add by chenkai, 增加预约保养的处理, 2014.05.31 begin
		case R.id.model_maintenance:
			// add by chenkai, 增加预约保养的处理, 2014.05.31 end
			NewCardActivity.startIntent(context, bundle);
		case R.id.model_feedback:
			break;
		}
		
	}
	
	public static boolean createActionBarMenu(Menu menu, Bundle bundle) {
		if (bundle == null || bundle.getInt(Intents.EXTRA_TYPE) == 0) {
			return false;
		}
		int type = bundle.getInt(Intents.EXTRA_TYPE);
		switch(type) {
		case R.id.model_my_card:{
			MenuItem item = menu.add(0, type, 0, R.string.menu_new_card);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			break;
		}
		case R.id.model_install:{
			MenuItem item = menu.add(0, type, 0, R.string.menu_new_install);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			break;
		}
		case R.id.model_repair:{
			MenuItem item = menu.add(0, type, 0, R.string.menu_new_repair);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			break;
		}
		}
		return true;
	}
	
	public static boolean onActionBarMenuSelected(MenuItem item, Context context, Bundle bundle) {
		switch(item.getItemId()) {
		case R.id.model_my_card:
		case R.id.model_install:
		case R.id.model_repair:
			doChoose(context, bundle);
			return true;
		}
		return false;
	}
	
	
	/**
	 * 可以使用这个来创建选择设备的Bundle数据
	 * @param context
	 * @return
	 */
	public static Bundle createMyCardDefaultBundle(Context context) {
		Bundle bundle = new Bundle();
		bundle.putInt(Intents.EXTRA_TYPE, R.id.model_my_card);
		bundle.putString(Intents.EXTRA_NAME, context.getString(R.string.activity_title_choose_device_general));
		return bundle;
	}
	
	/**
	 * 可以使用这个来创建选择安装的Bundle数据
	 * @param context
	 * @return
	 */
	public static Bundle createMyInstallDefaultBundle(Context context) {
		Bundle bundle = new Bundle();
		bundle.putInt(Intents.EXTRA_TYPE, R.id.model_install);
		bundle.putString(Intents.EXTRA_NAME, context.getString(R.string.activity_title_choose_device_general));
		return bundle;
	}
	
	/**
	 * 可以使用这个来创建维修的Bundle数据
	 * @param context
	 * @return
	 */
	public static Bundle createMyRepairDefaultBundle(Context context) {
		Bundle bundle = new Bundle();
		bundle.putInt(Intents.EXTRA_TYPE, R.id.model_repair);
		bundle.putString(Intents.EXTRA_NAME, context.getString(R.string.activity_title_choose_device_general));
		return bundle;
	}
	
	//add by chenkai, 2014.05.31, 增加一键保养 begin
	/**
	 * 可以使用这个来创建保养的Bundle数据
	 * @param context
	 * @return
	 */
	public static Bundle createMyMaintenanceDefaultBundle(Context context) {
		Bundle bundle = new Bundle();
		bundle.putInt(Intents.EXTRA_TYPE, R.id.model_maintenance);
		bundle.putString(Intents.EXTRA_NAME, context.getString(R.string.activity_title_maintenance));
		return bundle;
	}
	//add by chenkai, 2014.05.31, 增加一键保养 end

	
	public static int getModelIdFromBundle(Bundle modelBundle) {
		if (modelBundle == null) {
			return -1;
		}
		return modelBundle.getInt(Intents.EXTRA_TYPE, -1);
	}

}
