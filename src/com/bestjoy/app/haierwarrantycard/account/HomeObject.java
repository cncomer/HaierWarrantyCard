package com.bestjoy.app.haierwarrantycard.account;

import android.content.ContentResolver;
import android.database.Cursor;
import android.text.TextUtils;

import com.bestjoy.app.haierwarrantycard.database.BjnoteContent;
import com.bestjoy.app.haierwarrantycard.database.HaierDBHelper;

public class HomeObject {

	public String mHomeName;
	//住址信息
	public String mHomeProvince, mHomeCity, mHomeDis, mHomePlaceDetail;
	/**家所属账户uid*/
	public long mHomeUid;
	/**住址id,对应服务器上的数据项*/
	public long mHomeId;
	
	
	public static final String[] PROVINCE_PROJECTION = new String[]{
		HaierDBHelper.DEVICE_PRO_ID,
		HaierDBHelper.DEVICE_PRO_NAME
	};
	
	public static final String[] CITY_PROJECTION = new String[]{
		HaierDBHelper.DEVICE_CITY_ID,
		HaierDBHelper.DEVICE_CITY_NAME,
		HaierDBHelper.DEVICE_CITY_PID,
	};
	
	public static final String[] DISTRICT_PROJECTION = new String[]{
		HaierDBHelper.DEVICE_DIS_ID,
		HaierDBHelper.DEVICE_DIS_NAME,
		HaierDBHelper.DEVICE_DIS_CID,
	};
	
	public static final String SELECTION_PROVINCE_NAME = HaierDBHelper.DEVICE_PRO_NAME + "=?";
	
	//city table
	public static final String SELECTION_CITY_NAME = HaierDBHelper.DEVICE_CITY_NAME + "=?";
	
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
		return cr.query(BjnoteContent.City.CONTENT_URI, PROVINCE_PROJECTION, selection, null, null);
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
}
