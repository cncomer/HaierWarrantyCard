package com.bestjoy.app.haierwarrantycard.account;

import java.util.Date;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.bestjoy.app.haierwarrantycard.database.BjnoteContent;
import com.bestjoy.app.haierwarrantycard.database.HaierDBHelper;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.InfoInterface;

public class HomeObject implements InfoInterface{

	private static final String TAG = "HomeObject";
	public String mHomeName;
	//住址信息
	public String mHomeProvince, mHomeCity, mHomeDis, mHomePlaceDetail;
	/**家所属账户uid*/
	public long mHomeUid;
	/**住址id,对应服务器上的数据项*/
	public long mHomeAid;
	/**本地_id数据字段值*/
	public long mHomeId;
	public int mHomePosition;
	public boolean mIsDefault = false;
	
	
	public static final String[] PROVINCE_PROJECTION = new String[]{
		HaierDBHelper.DEVICE_PRO_ID,
		HaierDBHelper.DEVICE_PRO_NAME,
		"_id",
	};
	
	public static final String[] CITY_PROJECTION = new String[]{
		HaierDBHelper.DEVICE_CITY_ID,
		HaierDBHelper.DEVICE_CITY_NAME,
		HaierDBHelper.DEVICE_CITY_PID,
		"_id",
	};
	
	public static final String[] DISTRICT_PROJECTION = new String[]{
		HaierDBHelper.DEVICE_DIS_ID,
		HaierDBHelper.DEVICE_DIS_NAME,
		HaierDBHelper.DEVICE_DIS_CID,
		"_id",
	};
	
	public static final String SELECTION_PROVINCE_NAME = HaierDBHelper.DEVICE_PRO_NAME + "=?";
	
	// city table
	public static final String SELECTION_CITY_NAME = HaierDBHelper.DEVICE_CITY_NAME + "=?";
	
	// home table
	private static final String WHERE_HOME_ACCOUNTID = HaierDBHelper.REF_ACCOUNT_ID + "=?";
	private static final String WHERE_HOME_ADDRESS_ID = HaierDBHelper.HOME_ADDRESS_ID + "=?";
	private static final String WHERE_HOME_AID_ACCOUNT_UID = WHERE_HOME_ACCOUNTID + " and " + WHERE_HOME_ADDRESS_ID;
	
	public static final String[] HOME_PROJECTION = new String[]{
		HaierDBHelper.REF_ACCOUNT_ID,        //0
		HaierDBHelper.HOME_ADDRESS_ID,
		HaierDBHelper.HOME_NAME,
		HaierDBHelper.DEVICE_PRO_NAME,
		HaierDBHelper.DEVICE_CITY_NAME,
		HaierDBHelper.DEVICE_DIS_NAME,
		HaierDBHelper.HOME_DETAIL,
		HaierDBHelper.HOME_DEFAULT,
		HaierDBHelper.POSITION,
		"_id",
	};
	
	private static final int KEY_HOME_ADDRESS_ID = 1;
	
	public static long getProvinceId(ContentResolver cr, String provinceName) {
		if (TextUtils.isEmpty(provinceName)) {
			return -1;
		}
		long proId = -1;
		Cursor c = cr.query(BjnoteContent.Province.CONTENT_URI, PROVINCE_PROJECTION, SELECTION_PROVINCE_NAME, new String[]{provinceName}, null);
		if (c != null) {
			if (c.moveToNext()) {
				proId = c.getLong(0);
			}
			c.close();
		}
		return proId;
	}
	
	public static Cursor getProvincesLike(ContentResolver cr, String provinceNameLike) {
		if (TextUtils.isEmpty(provinceNameLike)) {
			return cr.query(BjnoteContent.Province.CONTENT_URI, PROVINCE_PROJECTION, null, null, null);
		}
		String selection = HaierDBHelper.DEVICE_PRO_NAME + " like '" + provinceNameLike + "%'";
		return cr.query(BjnoteContent.Province.CONTENT_URI, PROVINCE_PROJECTION, selection, null, null);
	}
	public static Cursor getCitiesLike(ContentResolver cr, long proId, String cityNameLike) {
		if (proId == -1) {
			return null;
		}
		String selection = HaierDBHelper.DEVICE_CITY_PID + "=" + proId;
		if (!TextUtils.isEmpty(cityNameLike)) {
			selection += " and " + HaierDBHelper.DEVICE_CITY_NAME + " like '" + cityNameLike + "%'";
		}
		return cr.query(BjnoteContent.City.CONTENT_URI, CITY_PROJECTION, selection, null, null);
	}
	public static long getCityId(ContentResolver cr, String cityName) {
		if (TextUtils.isEmpty(cityName)) {
			return -1;
		}
		long proId = -1;
		Cursor c = cr.query(BjnoteContent.City.CONTENT_URI, CITY_PROJECTION, SELECTION_CITY_NAME, new String[]{cityName}, null);
		if (c != null) {
			if (c.moveToNext()) {
				proId = c.getLong(0);
			}
			c.close();
		}
		return proId;
	}
	
	public static Cursor getDistrictsLike(ContentResolver cr, long cityId, String districtNameLike) {
		if (cityId == -1) {
			return null;
		}
		String selection = HaierDBHelper.DEVICE_DIS_CID + "=" + cityId;
		if (!TextUtils.isEmpty(districtNameLike)) {
			selection += " and " + HaierDBHelper.DEVICE_DIS_NAME + " like '" + districtNameLike + "'%";
		}
		return cr.query(BjnoteContent.District.CONTENT_URI, DISTRICT_PROJECTION, selection, null, null);
	}

	@Override
	public boolean saveInDatebase(ContentResolver cr, ContentValues addtion) {
		ContentValues values = new ContentValues();
		if (addtion != null) {
			values.putAll(addtion);
		}
		long id = isExsited(cr,mHomeUid, mHomeAid);
		values.put(HaierDBHelper.HOME_NAME, mHomeName);
		values.put(HaierDBHelper.DEVICE_PRO_NAME, mHomeProvince);
		values.put(HaierDBHelper.DEVICE_CITY_NAME, mHomeCity);
		values.put(HaierDBHelper.DEVICE_DIS_NAME, mHomeDis);
		values.put(HaierDBHelper.HOME_DETAIL, mHomePlaceDetail);
		values.put(HaierDBHelper.CONTACT_DATE, new Date().getTime());
		values.put(HaierDBHelper.POSITION, mHomePosition);
		//对于家，只有位置是0的才是默认，其余的都不是, Home的uid和aid只有新增的时候会插入
		if (mHomePosition == 0) {
			values.put(HaierDBHelper.HOME_DEFAULT, 1);
		} else {
			values.put(HaierDBHelper.HOME_DEFAULT, 0);
		}
		if (id > 0) {
			DebugUtils.logD(TAG, "saveInDatebase update exsited aid#" + mHomeAid);
			int update = cr.update(BjnoteContent.Homes.CONTENT_URI, values,  WHERE_HOME_AID_ACCOUNT_UID, new String[]{String.valueOf(mHomeUid), String.valueOf(mHomeAid)});
			if (update > 0) {
				return true;
			} else {
				DebugUtils.logD(TAG, "saveInDatebase failly update exsited aid#" + mHomeAid);
			}
		} else {
			DebugUtils.logD(TAG, "saveInDatebase insert aid#" + mHomeAid);
			values.put(HaierDBHelper.HOME_ADDRESS_ID, mHomeAid);
			values.put(HaierDBHelper.REF_ACCOUNT_ID, mHomeUid);
			Uri uri = cr.insert(BjnoteContent.Homes.CONTENT_URI, values);
			if (uri != null) {
				mHomeId = ContentUris.parseId(uri);
				return true;
			} else {
				DebugUtils.logD(TAG, "saveInDatebase failly insert aid#" + mHomeAid);
			}
		}
		return false;
	}
	
	private long isExsited(ContentResolver cr, long uid, long aid) {
		Cursor c = cr.query(BjnoteContent.Homes.CONTENT_URI, HOME_PROJECTION, WHERE_HOME_AID_ACCOUNT_UID, new String[]{String.valueOf(uid), String.valueOf(aid)}, null);
		if (c != null) {
			if (c.moveToNext()) {
				return c.getLong(KEY_HOME_ADDRESS_ID);
			}
			c.close();
		}
		return -1;
	}
	
	/**
	 * 删除某个account的全部home
	 * @param cr
	 * @param uid
	 * @return
	 */
	public static int deleteAllHomesInDatabaseForAccount(ContentResolver cr, long uid) {
		return cr.delete(BjnoteContent.Homes.CONTENT_URI, WHERE_HOME_ACCOUNTID, new String[]{String.valueOf(uid)});
	}
	
	/**
	 * 是否有有效的地址，如果各个字段都是空的，那么我们认为丢弃该家
	 * @return
	 */
	public boolean hasValidateAddress() {
		return !TextUtils.isEmpty(mHomeName)
				|| TextUtils.isEmpty(mHomeProvince)
				|| TextUtils.isEmpty(mHomeCity)
				|| TextUtils.isEmpty(mHomeDis)
				|| TextUtils.isEmpty(mHomePlaceDetail);
	}
}
