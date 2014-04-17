package com.bestjoy.app.haierwarrantycard.account;

import android.content.Context;

public class HaierAccountManager {
	private static final String TAG = "HaierAccountManager";
	private Context mContext;
	private static HaierAccountManager mInstance = new HaierAccountManager();
	
	private HaierAccountManager() {}
	
	public static HaierAccountManager getInstance() {
		return mInstance;
	}
	
	public void setContext(Context context) {
		mContext = context; 
	}
	

}
