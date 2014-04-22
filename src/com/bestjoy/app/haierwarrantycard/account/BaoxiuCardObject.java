package com.bestjoy.app.haierwarrantycard.account;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.bestjoy.app.haierwarrantycard.database.BjnoteContent;
import com.bestjoy.app.haierwarrantycard.database.HaierDBHelper;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.InfoInterfaceImpl;
/**
 * 保修卡对象
 * @author chenkai
 * 
 * "baoxiu": [
        {
            "LeiXin": "类型", 
            "PinPai": "品牌", 
            "XingHao": "型号", 
            "SHBianHao": "12344", 
            "BXPhone": "400-20098000",  //保修电话
            "FPaddr": "图片地址（注意替换为ServerIP/Fapiao/20140419/10665de14261e416423e82f725bf6689.jpg", 
            "BuyDate": "20140812", 
            "BuyPrice": "125", 
            "BuyTuJing": "苏宁", 
            "YanBaoTime": "1年", 
            "YanBaoDanWei": "苏宁", 
            "UID": 1, 
            "AID": 1, 
            "BID": 1
        }
    ]
 *
 */
public class BaoxiuCardObject extends InfoInterfaceImpl {
	public static final String JSONOBJECT_NAME = "baoxiu";
	private static final String TAG = "BaoxiuCardObject";
	public String mLeiXin;
	public String mPinPai;
	public String mXingHao;
	public String mSHBianHao;
	public String mBXPhone;
	public String mFPaddr;
	public String mBuyDate;
	public String mBuyPrice;
	public String mBuyTuJing;
	public String mYanBaoTime;
	public String mYanBaoDanWei;
	/**用户定义的保修设备名称，如客厅电视机*/
	public String mCardName;
	/**主要配件保修，浮点值*/
	public String mZhuBx;
	/**本地id*/
	public long mId = -1;
	public long mUID, mAID, mBID;
	
	public static final String[] PROJECTION = new String[]{
		HaierDBHelper.ID,
		HaierDBHelper.CARD_TYPE, 
		HaierDBHelper.CARD_PINPAI,
		HaierDBHelper.CARD_MODEL,
		HaierDBHelper.CARD_SERIAL,
		HaierDBHelper.CARD_BXPhone,
		HaierDBHelper.CARD_FPaddr,
		HaierDBHelper.CARD_BUT_DATE,
		HaierDBHelper.CARD_PRICE,
		HaierDBHelper.CARD_BUY_TUJING,
		HaierDBHelper.CARD_YANBAO_TIME,
		HaierDBHelper.CARD_YANBAO_TIME_COMPANY,
		HaierDBHelper.CARD_UID,
		HaierDBHelper.CARD_AID,
		HaierDBHelper.CARD_BID,              //14
		HaierDBHelper.CARD_NAME,
	};
	
	public static final int KEY_CARD_ID = 0;
	public static final int KEY_CARD_TYPE = 1;
	public static final int KEY_CARD_PINPAI = 2;
	public static final int KEY_CARD_MODEL = 3;
	public static final int KEY_CARD_SERIAL = 4;
	public static final int KEY_CARD_BXPhone = 5;
	public static final int KEY_CARD_FPaddr = 6;
	public static final int KEY_CARD_BUT_DATE = 7;
	public static final int KEY_CARD_CARD_PRICE = 8;
	public static final int KEY_CARD_BUY_TUJING = 9;
	public static final int KEY_CARD_YANBAO_TIME = 10;
	public static final int KEY_CARD_YANBAO_TIME_COMPANY = 11;
	public static final int KEY_CARD_UID = 12;
	public static final int KEY_CARD_AID = 13;
	public static final int KEY_CARD_BID = 14;
	public static final int KEY_CARD_NAME = 15;
	
	public static final String WHERE_UID = HaierDBHelper.CARD_UID + "=?";
	public static final String WHERE_AID = HaierDBHelper.CARD_AID + "=?";
	public static final String WHERE_BID = HaierDBHelper.CARD_BID + "=?";
	public static final String WHERE_UID_AND_AID = WHERE_UID + " and " + WHERE_AID;
	public static final String WHERE_UID_AND_AID_AND_BID = WHERE_UID_AND_AID + " and " + WHERE_BID;
	
	public static BaoxiuCardObject parseBaoxiuCards(JSONObject jsonObject, AccountObject accountObject) throws JSONException {
		BaoxiuCardObject cardObject = new BaoxiuCardObject();
		cardObject.mLeiXin = jsonObject.getString("LeiXin");
		cardObject.mPinPai = jsonObject.getString("PinPai");
		cardObject.mXingHao = jsonObject.getString("XingHao");
		cardObject.mSHBianHao = jsonObject.getString("SHBianHao");
		
		cardObject.mBXPhone = jsonObject.getString("BXPhone");
		cardObject.mFPaddr = jsonObject.getString("FPaddr");
		cardObject.mBuyDate = jsonObject.getString("BuyDate");
		cardObject.mBuyPrice = jsonObject.getString("BuyPrice");
		
		cardObject.mBuyTuJing = jsonObject.getString("BuyTuJing");
		cardObject.mYanBaoTime = jsonObject.getString("YanBaoTime");
		cardObject.mYanBaoDanWei = jsonObject.getString("YanBaoDanWei");
		
		
		cardObject.mUID = jsonObject.getLong("UID");
		cardObject.mAID = jsonObject.getLong("AID");
		cardObject.mBID = jsonObject.getLong("BID");
		return cardObject;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Leixing:").append(mLeiXin).append(", Pinpai:").append(mPinPai)
		.append(", XingHao:").append(mXingHao).append(", BianHao:").append(mSHBianHao).append("]");
		return sb.toString();
	}
	
	/**
	 * 删除某个account的全部保修卡
	 * @param cr
	 * @param uid
	 * @return
	 */
	public static int deleteAllBaoxiuCardsInDatabaseForAccount(ContentResolver cr, long uid) {
		return cr.delete(BjnoteContent.BaoxiuCard.CONTENT_URI, WHERE_UID, new String[]{String.valueOf(uid)});
	}
	/**
	 * 获取某个账户某个家的全部保修卡数据
	 * @param cr
	 * @param uid
	 * @param aid
	 * @return
	 */
    public static Cursor getAllBaoxiuCardsCursor(ContentResolver cr, long uid, long aid) {
		return cr.query(BjnoteContent.BaoxiuCard.CONTENT_URI, PROJECTION, WHERE_UID_AND_AID, new String[]{String.valueOf(uid), String.valueOf(aid)}, null);
	}
    
    public static List<BaoxiuCardObject> getAllBaoxiuCardObjects(ContentResolver cr, long uid, long aid) {
		Cursor c = getAllBaoxiuCardsCursor(cr, uid, aid);
		List<BaoxiuCardObject> list = new ArrayList<BaoxiuCardObject>();
		if (c != null) {
			list = new ArrayList<BaoxiuCardObject>(c.getCount());
			while(c.moveToNext()) {
				list.add(getFromBaoxiuCardsCursor(c));
			}
			c.close();
		}
		return list;
	}
    
    private static BaoxiuCardObject getFromBaoxiuCardsCursor(Cursor c) {
    	BaoxiuCardObject baoxiuCardObject = new BaoxiuCardObject();
    	baoxiuCardObject.mId = c.getLong(KEY_CARD_ID);
    	baoxiuCardObject.mUID = c.getLong(KEY_CARD_UID);
    	baoxiuCardObject.mAID = c.getLong(KEY_CARD_AID);
    	baoxiuCardObject.mBID = c.getLong(KEY_CARD_BID);
    	baoxiuCardObject.mLeiXin = c.getString(KEY_CARD_TYPE);
    	baoxiuCardObject.mPinPai = c.getString(KEY_CARD_PINPAI);
    	baoxiuCardObject.mXingHao = c.getString(KEY_CARD_MODEL);
    	baoxiuCardObject.mSHBianHao = c.getString(KEY_CARD_SERIAL);
    	baoxiuCardObject.mBXPhone = c.getString(KEY_CARD_BXPhone);
    	baoxiuCardObject.mFPaddr = c.getString(KEY_CARD_FPaddr);
    	baoxiuCardObject.mBuyDate = c.getString(KEY_CARD_BUT_DATE);
    	baoxiuCardObject.mBuyPrice = c.getString(KEY_CARD_CARD_PRICE);
    	baoxiuCardObject.mBuyTuJing = c.getString(KEY_CARD_BUY_TUJING);
    	baoxiuCardObject.mYanBaoTime = c.getString(KEY_CARD_YANBAO_TIME);
    	baoxiuCardObject.mYanBaoDanWei = c.getString(KEY_CARD_YANBAO_TIME_COMPANY);
    	baoxiuCardObject.mCardName = c.getString(KEY_CARD_NAME);
		return baoxiuCardObject;
	}

	@Override
	public boolean saveInDatebase(ContentResolver cr, ContentValues addtion) {
		ContentValues values = new ContentValues();
		if (addtion != null) {
			values.putAll(addtion);
		}
		String[] selectionArgs =  new String[]{String.valueOf(mUID), String.valueOf(mAID), String.valueOf(mBID)};
		long id = isExsited(cr,selectionArgs);
		values.put(HaierDBHelper.CARD_TYPE, mLeiXin);
		values.put(HaierDBHelper.CARD_PINPAI, mPinPai);
		values.put(HaierDBHelper.CARD_MODEL, mXingHao);
		values.put(HaierDBHelper.CARD_SERIAL, mSHBianHao);
		values.put(HaierDBHelper.CARD_BXPhone, mBXPhone);
		values.put(HaierDBHelper.CARD_FPaddr, mFPaddr);
		
		values.put(HaierDBHelper.CARD_BUT_DATE, mBuyDate);
		values.put(HaierDBHelper.CARD_PRICE, mBuyPrice);
		values.put(HaierDBHelper.CARD_BUY_TUJING, mBuyTuJing);
		
		values.put(HaierDBHelper.CARD_YANBAO_TIME, mYanBaoTime);
		values.put(HaierDBHelper.CARD_YANBAO_TIME_COMPANY, mYanBaoDanWei);
		
		values.put(HaierDBHelper.DATE, new Date().getTime());
		
		if (id > 0) {
			int update = cr.update(BjnoteContent.BaoxiuCard.CONTENT_URI, values,  WHERE_UID_AND_AID_AND_BID, selectionArgs);
			if (update > 0) {
				DebugUtils.logD(TAG, "saveInDatebase update exsited bid#" + mBID);
				return true;
			} else {
				DebugUtils.logD(TAG, "saveInDatebase failly update exsited bid#" + mBID);
			}
		} else {
			//如果不存在，新增的时候需要增加uid aid bid值
			values.put(HaierDBHelper.CARD_UID, mUID);
			values.put(HaierDBHelper.CARD_AID, mAID);
			values.put(HaierDBHelper.CARD_BID, mBID);
			Uri uri = cr.insert(BjnoteContent.BaoxiuCard.CONTENT_URI, values);
			if (uri != null) {
				DebugUtils.logD(TAG, "saveInDatebase insert bid#" + mBID);
				mId = ContentUris.parseId(uri);
				return true;
			} else {
				DebugUtils.logD(TAG, "saveInDatebase failly insert bid#" + mBID);
			}
		}
		return false;
	}
	
	private long isExsited(ContentResolver cr, String[] selectionArgs) {
		long id = -1;
		Cursor c = cr.query(BjnoteContent.BaoxiuCard.CONTENT_URI, PROJECTION, WHERE_UID_AND_AID_AND_BID, selectionArgs, null);
		if (c != null) {
			if (c.moveToNext()) {
				id = c.getLong(KEY_CARD_BID);
			}
			c.close();
		}
		return id;
	}

}
