package com.bestjoy.app.haierwarrantycard.account;

import android.content.Context;
import android.content.SharedPreferences;

public class HaierAccountManager {
	private static final String TAG = "HaierAccountManager";
	private AccountObject mHaierAccount;
	private Context mContext;
	SharedPreferences mSharedPreferences;
	private static HaierAccountManager mInstance = new HaierAccountManager();
	
	private HaierAccountManager() {}
	
	public static HaierAccountManager getInstance() {
		return mInstance;
	}
	
	public void setContext(Context context) {
		mContext = context; 
		mHaierAccount = null;
		mSharedPreferences = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
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
	/**
	 * 返回上一次登陆时候使用的用户名
	 * @return
	 */
	public String getLastUsrTel() {
		return mSharedPreferences.getString("lastUserTel", "");
	}
	
    public void saveLastUsrTel(String userName) {
    	mSharedPreferences.edit().putString("lastUserTel", (userName == null ? "" : userName)).commit();
	}

}
