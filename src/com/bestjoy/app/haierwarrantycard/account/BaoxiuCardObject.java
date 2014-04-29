package com.bestjoy.app.haierwarrantycard.account;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import android.text.TextUtils;

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
            "YanBaoTime": "1年",    默认单位是年，使用的时候该值x365=天数
            "YanBaoDanWei": "苏宁", 
            "UID": 1, 
            "AID": 1, 
            "BID": 1,
            "ZhuBx":0.0,    部件保修天数，单位是年，计算同保修时间
            "Tag":"大厅暖气",  保修卡的标签，比如卧室电视机
            "WY": 1.0,          整机保修时长，单位是年
            "YBPhone":"400-20098005",  延保电话
            "KY":"101000003"     KY编码，用于显示产品图片
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
	/**整机保修时间，浮点型*/
	public String mWY;
	public String mYanBaoTime;
	public String mYanBaoDanWei;
	/**用户定义的保修设备名称，如客厅电视机*/
	public String mCardName;
	/**主要配件保修，浮点值*/
	public String mZhuBx;
	/**延保电话*/
	public String mYBPhone;
	public String mKY;
	/**本地id*/
	public long mId = -1;
	public long mUID, mAID, mBID;
	
	private int mZhengjiValidity = -1, mComponentValidity = -1;
	
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
		HaierDBHelper.CARD_COMPONENT_VALIDITY,
		HaierDBHelper.CARD_WY,
		HaierDBHelper.CARD_YBPhone,          //18
		HaierDBHelper.CARD_KY,               //19
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
	public static final int KEY_CARD_COMPONENT_VALIDITY = 16;
	public static final int KEY_CARD_WY = 17;
	public static final int KEY_CARD_YBPHONE = 18;
	public static final int KEY_CARD_KY = 19;
	
	public static final String WHERE_UID = HaierDBHelper.CARD_UID + "=?";
	public static final String WHERE_AID = HaierDBHelper.CARD_AID + "=?";
	public static final String WHERE_BID = HaierDBHelper.CARD_BID + "=?";
	public static final String WHERE_UID_AND_AID = WHERE_UID + " and " + WHERE_AID;
	public static final String WHERE_UID_AND_AID_AND_BID = WHERE_UID_AND_AID + " and " + WHERE_BID;
	
	/**这个值用作不同Activity之间的传递，如选择设备的时候*/
	private static BaoxiuCardObject mBaoxiuCardObject = null;
	
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
		
		cardObject.mCardName = jsonObject.getString("Tag");
		cardObject.mZhuBx = jsonObject.getString("ZhuBx");
		
		cardObject.mUID = jsonObject.getLong("UID");
		cardObject.mAID = jsonObject.getLong("AID");
		cardObject.mBID = jsonObject.getLong("BID");
		cardObject.mWY = jsonObject.getString("WY");
		cardObject.mYBPhone = jsonObject.getString("YBPhone");
		cardObject.mKY = jsonObject.getString("KY");
		return cardObject;
	}
	
	public BaoxiuCardObject clone() {
		BaoxiuCardObject newBaoxiuCardObject = new BaoxiuCardObject();
		newBaoxiuCardObject.mUID = mUID;
		newBaoxiuCardObject.mAID = mAID;
		newBaoxiuCardObject.mBID = mBID;
		newBaoxiuCardObject.mWY = mWY;
		
		newBaoxiuCardObject.mCardName = mCardName;
		newBaoxiuCardObject.mZhuBx = mZhuBx;
		
		newBaoxiuCardObject.mLeiXin = mLeiXin;
		newBaoxiuCardObject.mPinPai = mPinPai;
		newBaoxiuCardObject.mXingHao = mXingHao;
		newBaoxiuCardObject.mSHBianHao = mSHBianHao;
		
		newBaoxiuCardObject.mBXPhone = mBXPhone;
		newBaoxiuCardObject.mFPaddr = mFPaddr;
		newBaoxiuCardObject.mBuyDate = mBuyDate;
		newBaoxiuCardObject.mBuyPrice = mBuyPrice;
		newBaoxiuCardObject.mBuyTuJing = mBuyTuJing;
		newBaoxiuCardObject.mYanBaoTime = mYanBaoTime;
		newBaoxiuCardObject.mYanBaoDanWei = mYanBaoDanWei;
		newBaoxiuCardObject.mYBPhone = mYBPhone;
		newBaoxiuCardObject.mKY = mKY;
		return newBaoxiuCardObject;
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
		int deleted = cr.delete(BjnoteContent.BaoxiuCard.CONTENT_URI, WHERE_UID, new String[]{String.valueOf(uid)});
		DebugUtils.logD(TAG, "deleteAllBaoxiuCardsInDatabaseForAccount uid#" + uid + ", delete " + deleted);
		return deleted;
	}
	 public static int getAllBaoxiuCardsCount(ContentResolver cr, long uid, long aid) {
		 Cursor c = getAllBaoxiuCardsCursor(cr, uid, aid);
		 if (c != null) {
			 int count = c.getCount();
			 c.close();
			 return count;
		 }
		 return 0;
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
    
    public static BaoxiuCardObject getFromBaoxiuCardsCursor(Cursor c) {
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
    	baoxiuCardObject.mZhuBx = c.getString(KEY_CARD_COMPONENT_VALIDITY);
    	baoxiuCardObject.mWY = c.getString(KEY_CARD_WY);
    	
    	baoxiuCardObject.mYBPhone = c.getString(KEY_CARD_YBPHONE);
    	baoxiuCardObject.mKY = c.getString(KEY_CARD_KY);
    	
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
		values.put(HaierDBHelper.CARD_NAME, mCardName);
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
		
		values.put(HaierDBHelper.CARD_COMPONENT_VALIDITY, mZhuBx);
		values.put(HaierDBHelper.CARD_WY, mWY);
		values.put(HaierDBHelper.CARD_YBPhone, mYBPhone);
		values.put(HaierDBHelper.CARD_KY, mKY);
		
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
	
	/**
	 * 返回该保修对象的整机保修有效期天数，计算保修有效期公式 = 延保时间+保修天数-已买天数
	 * @return
	 */
	public int getBaoxiuValidity() {
		if (mZhengjiValidity == -1) {
			if (TextUtils.isEmpty(mZhuBx)) {
				mZhengjiValidity = 0;
			} else {
				int validity = (int) ((Float.valueOf(mWY) + Float.valueOf(mYanBaoTime)) * 365 + 0.5f);
				try {
					//转换购买日期
					Date buyDate = BUY_DATE_TIME_FORMAT.parse(mBuyDate);
					//当前日期
					Date now = new Date();
					long passedTimeLong = now.getTime() - buyDate.getTime();
					if (passedTimeLong < 0) {
						passedTimeLong = 0;
					}
					int passedDay = (int) (passedTimeLong / DAY_IN_MILLISECONDS);
					mZhengjiValidity = validity - passedDay;
				} catch (ParseException e) {
					e.printStackTrace();
					mZhengjiValidity = 0;
				}
			}
		}
		return mZhengjiValidity;
	}
	
	/**
	 * 返回该保修对象的主要部件保修有效期天数，计算保修有效期公式 = 保修天数-已买天数
	 * @return
	 */
	public int getComponentBaoxiuValidity() {
		if (mComponentValidity == -1) {
			if (TextUtils.isEmpty(mZhuBx)) {
				//可能会是空的字串，我们就当做0天
				mComponentValidity = 0;
			} else {
				int validity = (int) (Float.valueOf(mZhuBx) * 365 + 0.5f);
				try {
					//转换购买日期
					Date buyDate = BUY_DATE_TIME_FORMAT.parse(mBuyDate);
					//当前日期
					Date now = new Date();
					long passedTimeLong = now.getTime() - buyDate.getTime();
					if (passedTimeLong < 0) {
						passedTimeLong = 0;
					}
					int passedDay = (int) (passedTimeLong / DAY_IN_MILLISECONDS);
					mComponentValidity = validity - passedDay;
				} catch (ParseException e) {
					e.printStackTrace();
					mComponentValidity = 0;
				}
			}
			
		}
		return mComponentValidity;
	}
	/**
	 * 当我们设置过mBaoxiuCardObject值后，需要使用这个方法来获取，这会重置mBaoxiuCardObject对象为null.
	 * @return
	 */
	public static BaoxiuCardObject getBaoxiuCardObject() {
		BaoxiuCardObject object = null;
		if (mBaoxiuCardObject != null) {
			object = mBaoxiuCardObject;
			mBaoxiuCardObject = null;
		}
		return object;
	}
	/**
	 * 需要在Activity之间传递保修卡对象的时候，需要调用该方法来设置，之后使用getBaoxiuCardObject()来获得.
	 * @param baoxiucardObject
	 */
	public static void setBaoxiuCardObject(BaoxiuCardObject baoxiucardObject) {
		mBaoxiuCardObject = baoxiucardObject;
	}
	
	public static  DateFormat BUY_DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd");
	public static  DateFormat BUY_TIME_FORMAT = new SimpleDateFormat("HHmm");
	private static long DAY_IN_MILLISECONDS = 1000 * 60 * 60 * 24;

}
