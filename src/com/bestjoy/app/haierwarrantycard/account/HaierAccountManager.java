package com.bestjoy.app.haierwarrantycard.account;

import android.content.Context;

public class HaierAccountManager {
	private static final String TAG = "HaierAccountManager";
	private HaierAccount mHaierAccount;
	private Context mContext;
	private static HaierAccountManager mInstance = new HaierAccountManager();
	
	private HaierAccountManager() {}
	
	public static HaierAccountManager getInstance() {
		return mInstance;
	}
	
	public void setContext(Context context) {
		mContext = context; 
		mHaierAccount = null;
	}
	
	public boolean hasLoginned() {
		return mHaierAccount != null && mHaierAccount.mAccountId > 0;
	}
	
	public boolean hasWarrantyCards() {
		return mHaierAccount != null && mHaierAccount.mAccountCardCount > 0;
	}
	
	public boolean hasHomes() {
		return mHaierAccount != null && mHaierAccount.mAccountHomeCount > 0;
	}

}
