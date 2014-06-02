package com.bestjoy.app.haierwarrantycard;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;


public class HaierServiceObject {

	public static final String SERVICE_URL = "http://115.29.231.29/Haier/";
	
	public static final String PRODUCT_AVATOR_URL= "http://115.29.231.29/proimg/";
	/**发票路径的前缀*/
	public static final String FAPIAO_PREFIX = "http://115.29.231.29/Fapiao/";
	
	public static final String CARD_DELETE_URL = SERVICE_URL + "DeleteBaoXiuByBIDUID.ashx?";
	
	public static final String HOME_DELETE_URL = SERVICE_URL + "DeleteAddressByAID.ashx?";
	
	private static String mHaierPinpaiName;
	public static final String BX_PHONE_HAIER = "400699999";
	
	private static String mKasadiPinpaiName;
	public static final String BX_PHONE_KASADI = "4006399699";
	public static void setContext(Context context) {
		mHaierPinpaiName = context.getString(R.string.pinpai_haier);
		mKasadiPinpaiName = context.getString(R.string.pinpai_kasadi);
	}
	public static boolean isHaierPinpai(String pinpaiName) {
		return mHaierPinpaiName.equals(pinpaiName);
	}
	/**
	 * 是否是卡萨帝品牌
	 * @param pinpaiName
	 * @return
	 */
	public static boolean isKasadiPinpai(String pinpaiName) {
		return mKasadiPinpaiName.equals(pinpaiName);
	}

	/***
	   * 产品图片网址  http://115.29.231.29/proimg/507/5070A000A.jpg  说明5070A000A：为Key，507：key 为前三位
	   * @return
	   */
	public static String getProdcutAvatorUrl(String ky) {
		String ky3 = ky.substring(0,3);
		  StringBuilder sb = new StringBuilder(PRODUCT_AVATOR_URL);
		  sb.append(ky3).append("/").append(ky).append(".jpg");
		  return sb.toString();
	}
	
	/**
	 * http://115.29.231.29/Fapiao/20140421/01324df60b0734de0f973c7907af55fc.jpg
	 * @param ky
	 * @return
	 */
	public static String getProdcutFaPiaoUrl(String fapiao) {
		return fapiao;
	}
	/**
	 * 删除保修数据： serverIP/Haier/DeleteBaoXiuByBIDUID.ashx
	 * @param BID:保修ID
	 * @param UID:用户ID
	 * @return
	 */
	public static String getBaoxiuCardDeleteUrl(String bid, String uid) {
		StringBuilder sb = new StringBuilder(CARD_DELETE_URL);
		sb.append("BID=").append(bid)
		.append("&UID=").append(uid);
		return sb.toString();
	}
	
	
	
	public static class HaierResultObject {
		public int mStatusCode = 0;
		public String mStatusMessage;
		public JSONObject mJsonData;
		public String mStrData;
		
		public static HaierResultObject parse(String content) {
			HaierResultObject resultObject = new HaierResultObject();
			if (TextUtils.isEmpty(content)) {
				return resultObject;
			}
			try {
				JSONObject jsonObject = new JSONObject(content);
				resultObject.mStatusCode = Integer.parseInt(jsonObject.getString("StatusCode"));
				resultObject.mStatusMessage = jsonObject.getString("StatusMessage");
				DebugUtils.logD("HaierResultObject", "StatusCode = " + resultObject.mStatusCode);
				DebugUtils.logD("HaierResultObject", "StatusMessage = " +resultObject.mStatusMessage);
				try {
					resultObject.mJsonData = jsonObject.getJSONObject("Data");
				} catch (JSONException e) {
					resultObject.mStrData = jsonObject.getString("Data");
				}
			} catch (JSONException e) {
				e.printStackTrace();
				resultObject.mStatusMessage = e.getMessage();
			}
			return resultObject;
		}
		
		public boolean isOpSuccessfully() {
			return mStatusCode == 1;
		}
	}
	
	//add by chenkai, for Usage, 2014.05.31 begin
	/**www.51cck.com/PD前9位数字/PD前13位[.htm][.pdf]*/
    public static final String GOODS_INTRODUCTION_BASE = "http://www.51cck.com/";
	/***
	   * www.51cck.com/KY前9位数字/KY.pdf
	   * @return
	   */
	  public static String getProductUsageUrl(String ky9, String ky) {
		  StringBuilder sb = new StringBuilder(GOODS_INTRODUCTION_BASE);
		  sb.append(ky9).append("/").append(ky).append(".pdf");
		  return sb.toString();
	  }
	  /***
	   * www.51cck.com/KY前9位数字/KY.pdf
	   * @return
	   */
	  public static String getProductUsageUrl(String ky) {
		  String ky9 = ky.substring(0,9);
		  return getProductUsageUrl(ky9, ky);
	  }
	 //add by chenkai, for Usage, 2014.05.31 end
	  
	  /**
	   * 是否支持直接从服务器下发的验证码短信中提取验证码并回填
	   * @return
	   */
	  public static boolean isSupportReceiveYanZhengMa() {
		  return true;
	  }
}
