package com.bestjoy.app.haierwarrantycard.im;

import java.util.Date;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.bestjoy.app.haierwarrantycard.database.BjnoteContent;
import com.bestjoy.app.haierwarrantycard.database.HaierDBHelper;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.InfoInterface;

public class ConversationItemObject implements InfoInterface{
	private static final String TAG = "ConversationItemObject";
	/**信息本地id*/
	public long mId = -1;
	/**信息服务器id*/
	public String mServiceId = "-1";
	/**信息类型*/
	public int mTargetType;
	/**信息目标*/
	public String mTarget;
	public String mUid, mPwd, mUName, mMessage;
	/**信息服务器时间*/
	public long mServiceDate;
	/**信息当前状态，如发送中0，发送成功1*/
	public int mMessageStatus = 0;
	
	public static final String[] ID_PROJECTION = new String[]{
		HaierDBHelper.ID,              //0
		HaierDBHelper.IM_SERVICE_ID,   //1
	};
	public static final String UID_AND_TARGET_SELECTION = HaierDBHelper.IM_UID + "=? and " + HaierDBHelper.IM_TARGET_TYPE + "=? and " + HaierDBHelper.IM_TARGET + "=?";
	public static final String SID_UID_AND_TARGET_SELECTION = HaierDBHelper.IM_SERVICE_ID + "=? and " + UID_AND_TARGET_SELECTION;
	public static final String ID_SELECTION = HaierDBHelper.ID + "=?";

	public boolean hasId() {
		return mId > -1;
	}
	@Override
	public boolean saveInDatebase(ContentResolver cr, ContentValues addtion) {
		ContentValues values = new ContentValues();
		values.put(HaierDBHelper.IM_UNAME, mUName);
		values.put(HaierDBHelper.DATE, new Date().getTime());
		values.put(HaierDBHelper.IM_MESSAGE_STATUS, mMessageStatus);
		values.put(HaierDBHelper.IM_SERVICE_ID, mServiceId);
		values.put(HaierDBHelper.IM_TARGET_TYPE, mTargetType);
		values.put(HaierDBHelper.IM_SERVICE_TIME, mServiceDate);
		if (addtion != null) {
			values.putAll(addtion);
		}
		String[] selectionArgs = new String[]{mServiceId, mUid, String.valueOf(mTargetType), mTarget};
		long id = isExsited(cr, selectionArgs);
		if (id != -1) {
			int updated = cr.update(BjnoteContent.IM.CONTENT_URI, values, ID_SELECTION, new String[]{String.valueOf(id)});
			if (updated > 0) {
				DebugUtils.logD(TAG, "saveInDatebase update exsited ServiceId#" + mServiceId);
			} else {
				DebugUtils.logD(TAG, "saveInDatebase failly update exsited ServiceId " + mServiceId);
			}
			return updated > 0;
		} else {
			values.put(HaierDBHelper.IM_TARGET, mTarget);
			values.put(HaierDBHelper.IM_TEXT, mMessage);
			values.put(HaierDBHelper.IM_UID, mUid);
			Uri uri = cr.insert(BjnoteContent.IM.CONTENT_URI, values);
			if (uri != null) {
				DebugUtils.logD(TAG, "saveInDatebase insert ServiceId#" + mServiceId);
				mId = ContentUris.parseId(uri);
			} else {
				DebugUtils.logD(TAG, "saveInDatebase failly insert ServiceId#" + mServiceId);
			}
			return uri != null;
		}
	}
	
	public boolean saveInDatebaseWithoutCheckExisted(ContentResolver cr, ContentValues addtion) {
		ContentValues values = new ContentValues();
		values.put(HaierDBHelper.IM_UNAME, mUName);
		values.put(HaierDBHelper.DATE, new Date().getTime());
		values.put(HaierDBHelper.IM_MESSAGE_STATUS, mMessageStatus);
		values.put(HaierDBHelper.IM_SERVICE_ID, mServiceId);
		values.put(HaierDBHelper.IM_TARGET_TYPE, mTargetType);
		values.put(HaierDBHelper.IM_SERVICE_TIME, mServiceDate);
		if (addtion != null) {
			values.putAll(addtion);
		}
		values.put(HaierDBHelper.IM_TARGET, mTarget);
		values.put(HaierDBHelper.IM_TEXT, mMessage);
		values.put(HaierDBHelper.IM_UID, mUid);
		Uri uri = cr.insert(BjnoteContent.IM.CONTENT_URI, values);
		if (uri != null) {
			DebugUtils.logD(TAG, "saveInDatebase insert ServiceId#" + mServiceId);
			mId = ContentUris.parseId(uri);
		} else {
			DebugUtils.logD(TAG, "saveInDatebase failly insert ServiceId#" + mServiceId);
		}
		return mId > -1;
	}
	
	private long isExsited(ContentResolver cr, String[] selectionArgs) {
		Cursor c = cr.query(BjnoteContent.IM.CONTENT_URI, ID_PROJECTION, SID_UID_AND_TARGET_SELECTION, selectionArgs, null);
		if (c != null) {
			if (c.moveToNext()) {
				return c.getLong(0); 
			}
			c.close();
		}
		return -1;
	}


}
