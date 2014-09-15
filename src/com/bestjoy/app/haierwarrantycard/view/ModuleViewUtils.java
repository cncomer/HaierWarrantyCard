package com.bestjoy.app.haierwarrantycard.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.MyAccountManager;
import com.bestjoy.app.haierwarrantycard.im.RelationshipActivity;
import com.bestjoy.app.haierwarrantycard.ui.YMessageListActivity;
import com.shwy.bestjoy.utils.DebugUtils;

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
		R.id.model_scan,
		
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
			ModuleView.findViewById(id, activity).setOnClickListener(mModuleOnClickListener);
			index++;
		}
	}
	
	public static int getModelIdFromBundle(Bundle bundle) {
		return R.id.model_my_business;
	}
	
	private View.OnClickListener mModuleOnClickListener  = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.model_my_business:
				if (MyAccountManager.getInstance().hasLoginned()) {
					RelationshipActivity.startActivity(mContext);
				} else {
					MyApplication.getInstance().showNeedLoginMessage();
				}
				
				break;
			case R.id.model_my_messages:
				YMessageListActivity.startActivity(mContext);
				break;
			case R.id.model_my_store:
				break;
			}
			
		}
		
	};

}
