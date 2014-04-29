package com.bestjoy.app.haierwarrantycard.account;

import android.content.ContentResolver;
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
		initAccountObject();
	}
	
	public void initAccountObject() {
		if (mHaierAccount == null) {
			mHaierAccount = AccountObject.getHaierAccountFromDatabase(mContext);
			if (mHaierAccount != null) {
				mHaierAccount.mAccountHomes = HomeObject.getAllHomeObjects(mContext.getContentResolver(), mHaierAccount.mAccountUid);
				//XXX 如果保修卡数据太多，这里太耗时了，我们不做加载,在我的家的时候再做加载
//				for(HomeObject homeObject : mHaierAccount.mAccountHomes) {
//					homeObject.initBaoxiuCards(mContext.getContentResolver());
//				}
			}
		}
	}
	
	public AccountObject getAccountObject() {
		return mHaierAccount;
	}
	
	public boolean hasLoginned() {
		return mHaierAccount != null && mHaierAccount.mAccountId > 0;
	}
	/**是否有保修卡*/
	public boolean hasBaoxiuCards() {
		if (mHaierAccount != null) {
			for(HomeObject home : mHaierAccount.mAccountHomes) {
				if (home.mHomeCardCount > 0) {
					return true;
				}
			}
		}
		return false;
	}
	/**新建保修卡后都需要调用该方法来更新家*/
	public void updateHomeObject(long aid) {
		if (mHaierAccount != null) {
			for(HomeObject home : mHaierAccount.mAccountHomes) {
				if (home.mHomeAid  == aid) {
					home.initBaoxiuCards(mContext.getContentResolver());
				}
			}
		}
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
    
    public boolean saveAccountObject(ContentResolver cr, AccountObject accountObject) {
    	
    	if (mHaierAccount != accountObject) {
    		boolean success = accountObject.saveInDatebase(cr, null);
    		if (success) {
    			mHaierAccount = accountObject;
    			return true;
    		}
    	}
    	return false;
    	
    }
    /**
     * 更新账户，每当我们增删家和保修卡数据的时候，调用该方法可以同步当前账户信息.
     */
    public void updateAccount() {
    	mHaierAccount = null;
    	initAccountObject();
    }
    
}
