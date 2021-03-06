package com.bestjoy.app.haierwarrantycard.im;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.bestjoy.app.haierwarrantycard.HaierServiceObject.HaierResultObject;
import com.bestjoy.app.haierwarrantycard.database.BjnoteContent;
import com.bestjoy.app.haierwarrantycard.database.HaierDBHelper;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.InfoInterface;
import com.shwy.bestjoy.utils.NetworkUtils;
import com.shwy.bestjoy.utils.PageInfo;

public class RelationshipObject implements InfoInterface, Parcelable{
	private static final String TAG = "RelationshipObject";

	public String mUID, mTargetTitle, mTargetOrg, mTargetCell, mTargetAvator, mTarget, mTargetBrief, mTargetName, mTargetWorkplace, mRelationshipServiceId, mRelationshipId, mLocalDate;
	public String mXinghao, mLeiXin, mMM;
	public int mTargetType = IMHelper.TARGET_TYPE_P2P;
	
	/**
	 * 是否有头像
	 * @return
	 */
	public boolean hasAvator() {
		return !TextUtils.isEmpty(mTargetAvator);
	}
	/**
	 *{"StatusCode":"1",
	 *"   StatusMessage":"登录成功",
	 *"   Data":{"total":2,"rows":[
	 *       {"simg":"xxxxx", "brief":"", "sname":"","scell":"", "suid":"682040","userName":"123","LeiXin":"双桶洗衣机","XingHao":"XPB80-1186BS","cell":"18611986102","BuyDate":"20140902","id":"3","uid":"607421","title":"销售人员","org":"国美青岛分部.台东商城","workaddress":"山东省-青岛市-市北区-威海路街道-道口路"},
	 *        {"simg":"xxxxx", "brief":"", "sname":"","scell":"", "suid":"682038","userName":"123","LeiXin":"手机","XingHao":"A6","cell":"18611986102","BuyDate":"20140911","id":"1","uid":"607421","title":"销售人员","org":"(青岛市内字样)济南人民大润发商业有限公司","workaddress":"山东省-青岛市-市南区-广电大厦附近"}
	 *     ]}
	 *     
	 *     
	 *     
	 *     brief是简介，包含了服务年限
	 *     simg是销售人员的头像地址，绝对路径
	 * }
	 */
	public static List<RelationshipObject> parseList(InputStream is, PageInfo pageInfo) {
		HaierResultObject serviceResultObject = HaierResultObject.parse(NetworkUtils.getContentFromInput(is));
		List<RelationshipObject> list = new ArrayList<RelationshipObject>();
		if (serviceResultObject.isOpSuccessfully()) {
			try {
				JSONObject jsonObject = serviceResultObject.mJsonData;
				pageInfo.mTotalCount = jsonObject.getInt("total");
				JSONArray rows = jsonObject.getJSONArray("rows");
				long rowsLen = rows.length();
				
				DebugUtils.logD(TAG, "parseList find rows " + rowsLen);
				for(int index = 0; index < rowsLen; index++) {
					list.add(parse(rows.getJSONObject(index)));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public static RelationshipObject parse(JSONObject row) throws JSONException {
		RelationshipObject relastionship = new RelationshipObject();
		relastionship.mUID = row.getString("uid");
		relastionship.mTarget = row.getString("suid");
		relastionship.mMM = row.getString("mm");
		relastionship.mTargetName = row.getString("sname");
		relastionship.mTargetTitle = row.getString("title");
		relastionship.mTargetOrg = row.getString("org");
		relastionship.mTargetBrief = row.getString("brief");
		relastionship.mTargetCell = row.getString("scell");
		relastionship.mTargetAvator = row.optString("simg", "");
		relastionship.mTargetWorkplace = row.getString("workaddress");
		
		relastionship.mLeiXin = row.optString("LeiXin", "");
		relastionship.mXinghao = row.getString("XingHao");
		
		relastionship.mRelationshipServiceId = row.getString("id");
		
		return relastionship;
	}
	
	public static RelationshipObject getFromCursor(Cursor cursor) {
		RelationshipObject object = new RelationshipObject();
		object.mUID = cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_UID);
		object.mTargetType = cursor.getInt(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_TARGET_TYPE);
		object.mTarget = cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_TARGET);
		object.mTargetName = cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_UNAME);
		object.mTargetTitle = cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_TITLE);
		object.mTargetOrg = cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_ORG);
		object.mTargetCell = cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_CELL);
		object.mTargetBrief = cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_BRIEF);
		object.mTargetWorkplace = cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_WORKPLACE);
		object.mTargetAvator = cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_AVATOR);
		object.mRelationshipServiceId = cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_SERVICE_ID);
		object.mRelationshipId = cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_ID);
		object.mLocalDate = cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_LOCAL_DATE);
		
		
		object.mLeiXin = cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_LEIXING);
		object.mXinghao = cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_XINGHAO);
		object.mMM = cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_MM);
		
		return object;
	}
	/***
	 * 根据保修卡id查询关系
	 * @param cr
	 * @param bid
	 * @return
	 */
	public static RelationshipObject getFromCursorByServiceId(ContentResolver cr, String uid, String serviceId) {
		RelationshipObject object = null;
		Cursor c = cr.query(BjnoteContent.RELATIONSHIP.CONTENT_URI, BjnoteContent.RELATIONSHIP.RELATIONSHIP_PROJECTION, WHERE_UID_AND_SID, new String[]{uid, serviceId}, null);
		if (c != null) {
			if (c.moveToNext()) {
				object = getFromCursor(c);
			}
			c.close();
		}
		return object;
	}
	public static final String WHERE_UID_AND_SID = HaierDBHelper.RELATIONSHIP_UID + "=? and " + HaierDBHelper.RELATIONSHIP_SERVICE_ID + "=?";

	public static final String WHERE = HaierDBHelper.RELATIONSHIP_SERVICE_ID + "=? and " + HaierDBHelper.RELATIONSHIP_UID + "=? and " + HaierDBHelper.RELATIONSHIP_TARGET + "=?";
	@Override
	public boolean saveInDatebase(ContentResolver cr, ContentValues addtion) {
		ContentValues values = new ContentValues();
		values.put(HaierDBHelper.RELATIONSHIP_UID, mUID);
		values.put(HaierDBHelper.RELATIONSHIP_NAME, mTargetName);
		values.put(HaierDBHelper.RELATIONSHIP_TYPE, mTargetType);
		values.put(HaierDBHelper.RELATIONSHIP_TARGET, mTarget);
		values.put(HaierDBHelper.RELATIONSHIP_SERVICE_ID, mRelationshipServiceId);
		values.put(HaierDBHelper.DATA1, mTargetTitle);
		values.put(HaierDBHelper.DATA2, mTargetOrg);
		values.put(HaierDBHelper.DATA3, mTargetWorkplace);
		values.put(HaierDBHelper.DATA4, mTargetBrief);
		values.put(HaierDBHelper.DATA5, mTargetCell);
		values.put(HaierDBHelper.DATA6, mTargetAvator);
		
		values.put(HaierDBHelper.DATA7, mLeiXin);
		values.put(HaierDBHelper.DATA8, mXinghao);
		values.put(HaierDBHelper.DATA9, mMM);
		
		values.put(HaierDBHelper.DATE, new Date().getTime());
		String[] selectionArgs = new String[]{mRelationshipServiceId, mUID, mTarget};
		//首先判断是不是存在数据
		long id = BjnoteContent.existed(cr, BjnoteContent.RELATIONSHIP.CONTENT_URI, WHERE, selectionArgs);
		if (id > -1) {
			//已存在，我们仅仅是更新操作
			int update = BjnoteContent.update(cr, BjnoteContent.RELATIONSHIP.CONTENT_URI, values, BjnoteContent.ID_SELECTION, new String[]{String.valueOf(id)});
			DebugUtils.logD(TAG, "saveInDatebase() update exsited serviceId# " + mRelationshipServiceId + ", name=" + mTargetName + ", updated " + update);
			return update > 0;
		} else {
			Uri uri = BjnoteContent.insert(cr, BjnoteContent.RELATIONSHIP.CONTENT_URI, values);
			DebugUtils.logD(TAG, "saveInDatebase() insert serviceId# " + mRelationshipServiceId + ", name=" + mTargetName + ", uri " + uri);
			return uri != null;
		}
	}
	
     public static final Parcelable.Creator<RelationshipObject> CREATOR  = new Parcelable.Creator<RelationshipObject>() {
	 public RelationshipObject createFromParcel(Parcel in) {
	     return new RelationshipObject(in);
	 }
	
	 public RelationshipObject[] newArray(int size) {
	     return new RelationshipObject[size];
	 }
	};
	
	public RelationshipObject(Parcel in) {
		mUID = in.readString();
		mTarget = in.readString();
		mTargetName = in.readString();
		mTargetTitle = in.readString();
		mTargetOrg = in.readString();
		mTargetWorkplace = in.readString();
		mTargetBrief = in.readString();
		mTargetCell = in.readString();
		mRelationshipServiceId = in.readString();
		mRelationshipId = in.readString();
		mLocalDate = in.readString();
		mTargetType = in.readInt();
		mTargetAvator = in.readString();
		mLeiXin = in.readString();
		mXinghao= in.readString();
		mMM= in.readString();
	}
	
	public RelationshipObject(){};


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mUID);
		dest.writeString(mTarget);
		dest.writeString(mTargetName);
		dest.writeString(mTargetTitle);
		dest.writeString(mTargetOrg);
		dest.writeString(mTargetWorkplace);
		dest.writeString(mTargetBrief);
		dest.writeString(mTargetCell);
		dest.writeString(mRelationshipServiceId);
		dest.writeString(mRelationshipId);
		dest.writeString(mLocalDate);
		dest.writeInt(mTargetType);
		dest.writeString(mTargetAvator);
		dest.writeString(mLeiXin);
		dest.writeString(mXinghao);
		dest.writeString(mMM);
	}
	
	
	
	
	
}
