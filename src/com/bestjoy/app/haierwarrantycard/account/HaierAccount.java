package com.bestjoy.app.haierwarrantycard.account;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.bestjoy.app.haierwarrantycard.database.BjnoteContent;
import com.bestjoy.app.haierwarrantycard.database.HaierDBHelper;
import com.shwy.bestjoy.utils.DebugUtils;

public class HaierAccount {
	private static final String TAG = "HaierAccount";
	
	private static final String[] PROJECTION = new String[]{
		HaierDBHelper.ID,
		HaierDBHelper.ACCOUNT_MD,
		HaierDBHelper.ACCOUNT_NAME,
		HaierDBHelper.ACCOUNT_TEL,
		HaierDBHelper.ACCOUNT_PWD,
		HaierDBHelper.ACCOUNT_CARD_COUNT,
	};
	
	private static final int KEY_ID = 0;
	private static final int KEY_MD = 1;
	private static final int KEY_NAME = 2;
	private static final int KEY_TEL = 3;
	private static final int KEY_PWD = 4;
	
	private static final String WHERE_DEFAULT = HaierDBHelper.ACCOUNT_DEFAULT + "=1";
	
	public long mAccountId;
	public String mAccountMd;
	public String mAccountName;
	public String mAccountTel;
	public String mAccountPwd;
	
	private List<Home> mAccountHomes = new LinkedList<Home>();

	
	public static class Home {
		public String mHomeName;
		public String mHomeProvince, mHomeCity, mHomeArea, mHomePlaceDetail;
		
	}
	
	
	
	
	public static HaierAccount getHaierAccountFromDatabase(Context context) {
		HaierAccount haierAccount = null;
		Cursor c = context.getContentResolver().query(BjnoteContent.Accounts.CONTENT_URI, PROJECTION, WHERE_DEFAULT, null, null);
		if (c != null) {
			if (c.moveToNext()) {
				haierAccount = new HaierAccount();
				String idStr = c.getString(KEY_ID);
				if (TextUtils.isEmpty(idStr)) {
					DebugUtils.logD(TAG, "getHaierAccountFromDatabase accountId is " + idStr);
					return null;
				}
				haierAccount.mAccountId = Long.parseLong(idStr);
				DebugUtils.logD(TAG, "getHaierAccountFromDatabase accountId is " + haierAccount.mAccountId);
				if (haierAccount.mAccountId <= 0) {
					
				}
				haierAccount.mAccountMd = c.getString(KEY_MD);
				haierAccount.mAccountName = c.getString(KEY_NAME);
				haierAccount.mAccountTel = c.getString(KEY_TEL);
				haierAccount.mAccountPwd = c.getString(KEY_PWD);
			}
		}
		
		return haierAccount;
	}
	
}
