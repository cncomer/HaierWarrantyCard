package com.bestjoy.app.haierwarrantycard.account;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;

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
	public long mUID, mAID, mBID;
	
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
		
		return sb.toString();
	}

	@Override
	public boolean saveInDatebase(ContentResolver cr, ContentValues addtion) {
		return super.saveInDatebase(cr, addtion);
	}

}
