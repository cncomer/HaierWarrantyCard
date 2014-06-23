package com.bestjoy.app.haierwarrantycard.account;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.database.BjnoteContent;
import com.bestjoy.app.haierwarrantycard.database.HaierDBHelper;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.shwy.bestjoy.utils.Base64;
import com.shwy.bestjoy.utils.ImageHelper;
import com.shwy.bestjoy.utils.InfoInterfaceImpl;
import com.shwy.bestjoy.utils.SecurityUtils;
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
            "hasimg":"false"  true表示有发票
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
	/**这个变量的值为0,1，表示是否有发票*/
	public String mFPaddr = "0";
	public String mBuyDate;
	public String mBuyPrice;
	public String mBuyTuJing;
	/**整机保修时间，浮点型, 默认是1年*/
	public String mWY = "1";
	public String mYanBaoTime = "0";
	public String mYanBaoDanWei;
	/**用户定义的保修设备名称，如客厅电视机*/
	public String mCardName;
	/**主要配件保修，浮点值*/
	public String mZhuBx = "0";
	/**延保电话*/
	public String mYBPhone;
	public String mKY;
	/**本地id*/
	public long mId = -1;
	public long mUID = -1, mAID = -1, mBID = -1;
	
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
		
//		if (!TextUtils.isEmpty(cardObject.mFPaddr) && !BaoxiuCardObject.PHOTOID_PLASEHOLDER.equals(cardObject.getFapiaoPhotoId())) {
//			//如果有发票，我们需要先下载发票
//			File faPiaoFile = MyApplication.getInstance().getProductFaPiaoFile(cardObject.getFapiaoPhotoId());
//			if (faPiaoFile.exists()) {
//				DebugUtils.logD(TAG, "parseBaoxiuCards delete local existed fapiao " + faPiaoFile.getAbsolutePath());
//				faPiaoFile.delete();
//			}
//			InputStream is = null;
//			try {
//				is = NetworkUtils.openContectionLocked(cardObject.mFPaddr, MyApplication.getInstance().getSecurityKeyValuesObject());
//				if (is != null) {
//					OutputStream out = new FileOutputStream(faPiaoFile);
//					byte[] buffer = new byte[4096];
//					int size;
//					try {
//						size = is.read(buffer);
//						while (size >= 0) {
//							out.write(buffer, 0, size);
//							size = is.read(buffer);
//						}
//						out.flush();
//						out.close();
//						DebugUtils.logD(TAG, "parseBaoxiuCards download fapiao " + faPiaoFile.getAbsolutePath());
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			} catch (ClientProtocolException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				NetworkUtils.closeInputStream(is);
//			}
//		}
		
		String buyDate = jsonObject.getString("BuyDate");
		if(buyDate != null) {
			//2010-01-01
			cardObject.mBuyDate = buyDate.replaceAll("[ -]", "");
			DebugUtils.logD(TAG, "reset BuyDate from " + buyDate + " to " + cardObject.mBuyDate);
		} else {
			cardObject.mBuyDate = "";
		}
		cardObject.mBuyPrice = jsonObject.getString("BuyPrice");
		
		cardObject.mBuyTuJing = jsonObject.getString("BuyTuJing");
		cardObject.mYanBaoTime = jsonObject.getString("YanBaoTime");
		if ("null".equals(cardObject.mYanBaoTime)) {
			cardObject.mYanBaoTime = "0";
		}
		cardObject.mYanBaoDanWei = jsonObject.getString("YanBaoDanWei");
		
		cardObject.mCardName = jsonObject.getString("Tag");
		//delete by chenkai, 不要ZhuBx字段了 begin
		//cardObject.mZhuBx = jsonObject.getString("ZhuBx");
		//if ("null".equals(cardObject.mZhuBx)) {
			//cardObject.mZhuBx = "0";
		//}
		//delete by chenkai, 不要ZhuBx字段了 end
		cardObject.mUID = jsonObject.getLong("UID");
		cardObject.mAID = jsonObject.getLong("AID");
		cardObject.mBID = jsonObject.getLong("BID");
		cardObject.mWY = jsonObject.getString("WY");
		if ("null".equals(cardObject.mWY)) {
			cardObject.mWY = "0";
		}
		cardObject.mYBPhone = jsonObject.getString("YBPhone");
		cardObject.mKY = jsonObject.getString("KY");
		if ("null".equalsIgnoreCase(cardObject.mKY)) {
			DebugUtils.logE(TAG, "parseBaoxiuCards find illegal value " + cardObject.mKY + " for ky");
			cardObject.mKY = "";
		}
		//解码发票，如果有的话
		//delete by chenkai, 现在FPaddr不再返回数据了，而是使用hasimg来表示是否存在发票图片 begin
		//cardObject.mFPaddr = jsonObject.getString("FPaddr");
		//decodeFapiao(cardObject);
		boolean hasimg = jsonObject.getBoolean("hasimg");
		cardObject.mFPaddr = hasimg ? "1" : "0";
		//delete by chenkai, 现在FPaddr不再返回数据了，而是使用hasimg来表示是否存在发票图片 end
		return cardObject;
	}
	
	private static void decodeFapiao(BaoxiuCardObject cardObject) {
		//如果有发票，我们需要先下载发票
		File faPiaoFile = MyApplication.getInstance().getProductFaPiaoFile(cardObject.getFapiaoPhotoId());
		if (faPiaoFile.exists()) {
			DebugUtils.logD(TAG, "parseBaoxiuCards delete local existed fapiao " + faPiaoFile.getAbsolutePath());
			faPiaoFile.delete();
		}
		
		String base64Str = cardObject.mFPaddr;
		//首先假设没有发票
		cardObject.mFPaddr = "0";
		if (TextUtils.isEmpty(base64Str)) {
			return;
		}
		DebugUtils.logE(TAG, "base64Str.len " + base64Str.length() + " for bid " + cardObject.mBID);
		base64Str = base64Str.replaceAll(" ", "");
    	if (TextUtils.isEmpty(base64Str)) {
    		 DebugUtils.logE(TAG, "find empty original text encoded by base64, so we just skip decode bill bitmap.");
    		 return;
    	} 
    	
        try {
        	byte[] byteArray = Base64.decode(base64Str, Base64.DEFAULT);
        	 if (byteArray == null) {
        		 DebugUtils.logE(TAG, "can't decode bill byte array from original text encoded by base64");
        		 return;
			 }
        	 Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
   			 if (bitmap != null) {
   				 try {
   					 boolean saveOk = bitmap.compress(CompressFormat.PNG, 100, new FileOutputStream(faPiaoFile));
   					 DebugUtils.logE(TAG, "save bill bitmap as file " + faPiaoFile.getAbsolutePath() + " " + saveOk);
   					 bitmap.recycle();
   					 if (saveOk) {
   						 cardObject.mFPaddr = "1";
   					 }
   				 } catch (FileNotFoundException ffe) {
   					 ffe.printStackTrace();
   				 } catch(Exception e) {
   					e.printStackTrace();
   				 }
   			 } else {
   			     DebugUtils.logE(TAG, "can't decode bill bitmap from byte array");
   			 }
		 } catch (IllegalArgumentException e) {
			e.printStackTrace();
		 }
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
	
	public static int deleteBaoxiuCardInDatabaseForAccount(ContentResolver cr, long uid, long aid, long bid) {
		int deleted = cr.delete(BjnoteContent.BaoxiuCard.CONTENT_URI, WHERE_UID_AND_AID_AND_BID, new String[]{String.valueOf(uid), String.valueOf(aid), String.valueOf(bid)});
		DebugUtils.logD(TAG, "deleteBaoxiuCardInDatabaseForAccount bid#" + bid + ", delete " + deleted);
		return deleted;
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
			if (TextUtils.isEmpty(mWY)) {
				mWY = "0";
			}
			if (TextUtils.isEmpty(mYanBaoTime)) {
				mYanBaoTime = "0";
			}
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
		return mZhengjiValidity;
	}
	
	public int mZhengjiValidityWithoutYanbao = -1;
	/**返回不包含延保时间的保修期剩余天数*/
	public int getBaoxiuValidityWithoutYanbao() {
		if (mZhengjiValidityWithoutYanbao == -1) {
			if (TextUtils.isEmpty(mWY)) {
				mWY = "0";
			}
			int validity = (int) (Float.valueOf(mWY) * 365 + 0.5f);
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
				mZhengjiValidityWithoutYanbao = validity - passedDay;
			} catch (ParseException e) {
				e.printStackTrace();
				mZhengjiValidityWithoutYanbao = 0;
			}
		}
		return mZhengjiValidityWithoutYanbao;
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
	
	/**
	 * 标签的内容应该是“备注标签+类型”如“客厅空调”
	 * @param cardName   备注标签
	 * @param cardType   类型
	 * @return
	 */
	public static String getTagName(String cardName, String cardType) {
		StringBuilder sb = new StringBuilder();
		if (!TextUtils.isEmpty(cardName)) {
			sb.append(cardName);
		}
		sb.append(cardType);
		return sb.toString();
	}
	private static final int mAvatorWidth = 320, mAvatorHeight = 480;
	public static final String PHOTOID_SEPERATOR = "_";
	/**占位符号*/
	public static final String PHOTOID_PLASEHOLDER = "00_00_00";
	/**临时拍摄的照片路径，当保存成功的时候会将该文件路径重命名为mBillAvator*/
	public Bitmap mBillTempBitmap;
	/**本地发票图片路径*/
	public File mBillFile;
	/**临时拍摄的照片路径，当保存成功的时候会将该文件路径重命名为mBillAvator*/
	public File mBillTempFile;
	
	public static BaoxiuCardObject objectUseForbill = null;
	/**是否有发票,如果有发票文件或是有发票的拍摄获得的临时文件,我们认为是有发票的*/
	public boolean hasLocalBill() {
		if (mBillFile == null) {
			mBillFile = MyApplication.getInstance().getProductFaPiaoFile(getFapiaoPhotoId());
		}
		return mBillFile.exists() || mBillTempFile != null;
	}
	/**
	 * 是否有发票
	 * @return
	 */
	public boolean hasBillAvator() {
		return mFPaddr != null && mFPaddr.equals("1");
	}
	/**
	 * 添加发票时候使用，用来表示是否有临时的拍摄发票文件，有的话，我们认为是要上传的
	 * @return
	 */
	public boolean hasTempBill() {
		return mBillTempFile != null && mBillTempFile.exists() ;
	}
	
	/**
	 * http://115.29.231.29/Fapiao/20140421/01324df60b0734de0f973c7907af55fc.jpg
	 * 返回 20140421_01324df60b0734de0f973c7907af55fc
	 * @return
	 */
	public String getFapiaoPhotoId() {
//		if (!TextUtils.isEmpty(mFPaddr) && mFPaddr.startsWith(HaierServiceObject.FAPIAO_PREFIX)) {
//			String photoId = mFPaddr.substring(HaierServiceObject.FAPIAO_PREFIX.length());
//			photoId = photoId.replaceAll("/", "_");
//			return photoId;
//		}
		if (mUID > 0 && mAID > 0 && mBID > 0) {
			StringBuilder sb = new StringBuilder();
			//delete by chenkai, 发票id为md5(aid+bid) begin
			//sb.append(mUID).append(PHOTOID_SEPERATOR).append(mAID).append(PHOTOID_SEPERATOR).append(mBID);
			sb.append(SecurityUtils.MD5.md5(String.valueOf(mAID) + String.valueOf(mBID)));
			//delete by chenkai, 发票id为md5(aid+bid) begin
			return sb.toString();
		}
		return PHOTOID_PLASEHOLDER;
	}
	
	/**保存临时的发票拍摄作为该商品的使用发票预览图*/
	public boolean saveBillAvatorTempFileLocked() {
		if (mBillTempBitmap != null) {
			File newPath = MyApplication.getInstance().getProductFaPiaoFile(getFapiaoPhotoId());
			boolean result = ImageHelper.bitmapToFile(mBillTempBitmap, newPath, 100);
			if (result) {
				mBillFile = newPath;
				if (mBillTempFile != null && mBillTempFile.exists()) {
					mBillTempFile.delete();
					mBillTempFile = null;
				}
			}
			return result;
		} else {
			return false;
		}
	}
	
	 /**
     * 返回商品发票预览图的Base64编码字符串
     * @return
     */
    public String getBase64StringFromBillAvator(){
    	//默认返回""
    	String result = "";
    	//如果此时还没有临时商品预览图，我们从文件中构建
        if (mBillTempBitmap == null) {
        	if (mBillFile == null) {
    			mBillFile = MyApplication.getInstance().getProductFaPiaoFile(getFapiaoPhotoId());
    		}
        	if (mBillFile != null && mBillFile.exists()) {
        		Bitmap billTempBitmap = ImageHelper.getSmallBitmap(mBillFile.getAbsolutePath(), mAvatorWidth, mAvatorHeight);
        		if (billTempBitmap != null) {
        			result = ImageHelper.bitmapToString(billTempBitmap, 100);
        		} else{
        			new Exception("getBase64StringFromBillAvator() getSmallBitmap return null").printStackTrace();
        		}
        	}
        } else {
        	 result = ImageHelper.bitmapToString(mBillTempBitmap, 100);
        }
        
       return result == null ? "":result;
    }
    
    public void updateBillAvatorTempLocked(File file) {
    	mBillTempFile = file;
    	mBillTempBitmap = ImageHelper.getSmallBitmap(file.getAbsolutePath(), mAvatorWidth, mAvatorWidth);
//    	mBillTempBitmap = ImageHelper.rotateBitmap(mBillTempBitmap, 90);
		ImageHelper.bitmapToFile(mBillTempBitmap, mBillTempFile, 100);
    }
	
	public void clear() {
		if (mBillTempBitmap != null) {
			mBillTempBitmap.recycle();
			mBillTempBitmap = null;
		}
		if (mBillTempFile != null && mBillTempFile.exists()) {
			mBillTempFile.delete();
			mBillTempFile = null;
		}
	}
	
	public static void showBill(Context context, BaoxiuCardObject baociuCardObject) {
		objectUseForbill = baociuCardObject;
		if (baociuCardObject != null) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(BjnoteContent.BaoxiuCard.BILL_CONTENT_URI, "image/png");
			context.startActivity(intent);
		}
	}
	
	/**
	 * 当前时间精确到秒base64（20140514121212）
	 * @return
	 */
	public static String getYuyueSecurityTip(String timeStr) {
		String tip = "";
		try {
			DebugUtils.logD(TAG, "getYuyueSecurityTip getTime " + timeStr);
			tip = Base64.encodeToString(timeStr.getBytes("UTF-8"), Base64.DEFAULT);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		DebugUtils.logD(TAG, "getYuyueSecurityTip getTip " + tip);
		return tip;
	}
	/**
	 * 加密后的字符（md5(md5(cell+tip))）
	 * @return
	 */
	public static String getYuyueSecurityKey(String cell, String tip) {
		String key = SecurityUtils.MD5.md5(cell+tip);
		DebugUtils.logD(TAG, "md5(cell+tip) " + key);
		key = SecurityUtils.MD5.md5(key);
		DebugUtils.logD(TAG, "md5(md5(cell+tip) " + key);
		return key;
	}
	//add by chenkai for FaPiao end
	public static  DateFormat BUY_DATE_TIME_FORMAT_ALL = new SimpleDateFormat("yyyyMMddHHmm");
	public static  DateFormat BUY_DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd");
	public static  DateFormat BUY_TIME_FORMAT = new SimpleDateFormat("HHmm");
	public static  DateFormat BUY_DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd日");
	public static  DateFormat BUY_DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");
	public static  DateFormat BUY_DATE_FORMAT_TIME = new SimpleDateFormat("HH:mm");
	public static  DateFormat BUY_DATE_FORMAT_YUYUE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static long DAY_IN_MILLISECONDS = 1000 * 60 * 60 * 24;
	
	//用于tip
	public static  DateFormat DATE_FORMAT_YUYUE_TIME = new SimpleDateFormat("yyyyMMddHHmmss");
	

}
