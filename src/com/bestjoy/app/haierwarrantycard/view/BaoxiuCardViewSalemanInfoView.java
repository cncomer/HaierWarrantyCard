package com.bestjoy.app.haierwarrantycard.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator;
import com.bestjoy.app.haierwarrantycard.HaierServiceObject;
import com.bestjoy.app.haierwarrantycard.HaierServiceObject.HaierResultObject;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.BaoxiuCardObject;
import com.bestjoy.app.haierwarrantycard.database.BjnoteContent;
import com.bestjoy.app.haierwarrantycard.database.HaierDBHelper;
import com.bestjoy.app.haierwarrantycard.im.ConversationListActivity;
import com.bestjoy.app.haierwarrantycard.im.IMHelper;
import com.bestjoy.app.haierwarrantycard.im.RelationshipObject;
import com.bestjoy.app.haierwarrantycard.service.PhotoManagerUtilsV2;
import com.bestjoy.app.haierwarrantycard.ui.CaptureActivity;
import com.bestjoy.app.haierwarrantycard.ui.CardViewActivity;
import com.bestjoy.app.haierwarrantycard.utils.VcfAsyncDownloadUtils;
import com.bestjoy.app.haierwarrantycard.utils.VcfAsyncDownloadUtils.VcfAsyncDownloadHandler;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.shwy.bestjoy.utils.Contents;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;
import com.shwy.bestjoy.utils.NetworkUtils;
import com.shwy.bestjoy.utils.SecurityUtils;

public class BaoxiuCardViewSalemanInfoView extends RelativeLayout implements View.OnClickListener, OnLongClickListener{

	private static final String TAG = "BaoxiuCardViewSalemanInfoView";
	private View mActionsLayout;
	private ImageView mAvator;
	private TextView mName, mTitle;
	
	private ValueAnimator mAnim;
	private Handler mHandler;
	private CardViewActivity mActivity;
	
	public static final int TYPE_MM_ONE = 1;
	public static final int TYPE_MM_TWO = 2;
	
	private int mMMType = 0;
	private String mToken = TAG;
	
	private boolean mIsDownload = false;
	
	private VcfAsyncDownloadHandler mVcfAsyncDownloadAndUpdateSalesInfoHandler;
	
	private class Person {
		private String _mm;
		private RelationshipObject _relationshipObject;
		private AddressBookParsedResult _addressBookParsedResult;
		private BaoxiuCardObject _baoxiuCardObject;
	}
	
	private Person mSalesPerson;
	
	public BaoxiuCardViewSalemanInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) {
			return;
		}
		mActivity = (CardViewActivity) context;
			
		mVcfAsyncDownloadAndUpdateSalesInfoHandler = new VcfAsyncDownloadHandler() {

			@Override
			public void onDownloadStart() {
				//实现该方法忽略默认的下载中提示信息
				mIsDownload = true;
				MyApplication.getInstance().showMessage(R.string.msg_download_mm_wait);
			}

			@Override
			public void onDownloadFinished(
					AddressBookParsedResult addressBookParsedResult,
					String outMsg) {
				mIsDownload = false;
				if (addressBookParsedResult != null) {
					//update bid and aid
					mSalesPerson._addressBookParsedResult = addressBookParsedResult;
					mSalesPerson._mm = addressBookParsedResult.getBid();
		   			UpdateSalesInfoAsyncTask task = new UpdateSalesInfoAsyncTask();
		   			task.execute();
				}
			}

			@Override
			public boolean onDownloadFinishedInterrupted() {
				return true;
			}
			
		};
	}
	
	private Runnable mHideActionRunnable =  new Runnable() {

		@Override
		public void run() {
			mActionsLayout.setVisibility(View.INVISIBLE);
		}
		
	};
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (isInEditMode()) {
			return;
		}
		mActionsLayout = findViewById(R.id.actions_layout);
		mActionsLayout.setVisibility(View.INVISIBLE);
		
		mAvator = (ImageView) findViewById(R.id.avator);
		mAvator.setOnClickListener(this);
		mAvator.setOnLongClickListener(this);
		mName = (TextView) findViewById(R.id.name);
		mName.setText("");
		mTitle = (TextView) findViewById(R.id.title); 
		mHandler = new Handler();
		
		findViewById(R.id.button_call).setOnClickListener(this);
		findViewById(R.id.button_info).setOnClickListener(this);
		
		findViewById(R.id.button_sms).setOnClickListener(this);
	}
	
	public void updateView() {
		if (mSalesPerson != null && mSalesPerson._relationshipObject != null) {
			mName.setText(mSalesPerson._relationshipObject.mTargetName);
			mTitle.setText(mSalesPerson._relationshipObject.mTargetTitle);
			
			PhotoManagerUtilsV2.getInstance().loadPhotoAsync(mToken, mAvator, mSalesPerson._mm, mSalesPerson._addressBookParsedResult != null ?mSalesPerson._addressBookParsedResult.getPhoto():null, PhotoManagerUtilsV2.TaskType.PREVIEW);
		} else {
			mName.setText("");
			if (mMMType == TYPE_MM_ONE) {
				mTitle.setText(R.string.salesman_title);
			} else if (mMMType == TYPE_MM_TWO) {
				mTitle.setText(R.string.serverman_title);
			}
			
		}
	}
	
	public void setMmType(int mmType) {
		mMMType = mmType;
	}
	
	public void setSalesPersonInfo(BaoxiuCardObject baoxiuCardObjectult, String token) {
		
		mSalesPerson = new Person();
		mToken = token;
		mSalesPerson._addressBookParsedResult = null;
		mSalesPerson._baoxiuCardObject = baoxiuCardObjectult;
		if (baoxiuCardObjectult != null) {
			if (mMMType == TYPE_MM_ONE) {
				mSalesPerson._relationshipObject = RelationshipObject.getFromCursorByServiceId(mActivity.getContentResolver(), String.valueOf(baoxiuCardObjectult.mUID), baoxiuCardObjectult.mMMOne);
			} else if (mMMType == TYPE_MM_TWO) {
				mSalesPerson._relationshipObject = RelationshipObject.getFromCursorByServiceId(mActivity.getContentResolver(), String.valueOf(baoxiuCardObjectult.mUID), baoxiuCardObjectult.mMMTwo);
			}
		}
		if (mSalesPerson._relationshipObject != null) {
			mSalesPerson._mm = mSalesPerson._relationshipObject.mMM;
			updateView();
		}
		
	}
	
	public void downloadSalesPersonInfo(BaoxiuCardObject baoxiuCardObjectult, String mm, String token) {
		mToken = token;
		mSalesPerson = new Person();
		mSalesPerson._addressBookParsedResult = null;
		mSalesPerson._baoxiuCardObject = baoxiuCardObjectult;
		mSalesPerson._relationshipObject = null;
		VcfAsyncDownloadUtils.getInstance().executeTaskSimply(mm, false, mVcfAsyncDownloadAndUpdateSalesInfoHandler,  PhotoManagerUtilsV2.TaskType.PREVIEW);
	}
	
	public boolean hasMM() {
		return mSalesPerson != null && !TextUtils.isEmpty(mSalesPerson._mm);
	}
	
	public boolean hasTarget() {
		return mSalesPerson != null && mSalesPerson._relationshipObject != null && !TextUtils.isEmpty(mSalesPerson._relationshipObject.mTarget);
	}
	
	public void setTitle(int resid) {
		mTitle.setText(resid);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.avator:
			if (mIsDownload) {
				MyApplication.getInstance().showMessage(R.string.msg_download_mm_wait);
				return;
			}
			//当已经有mm号码的时候，单击头像会pop出actions菜单；否则是进入条码扫描识别联系人信息。
			if (!hasMM()) {
				Intent scanIntent = new Intent(mActivity, CaptureActivity.class);
				scanIntent.putExtra(Intents.EXTRA_SCAN_TASK, true);
				mActivity.startActivityForResult(scanIntent, mMMType);
			} else {
				//pop actions
				mActionsLayout.setVisibility(View.VISIBLE);
				mHandler.removeCallbacks(mHideActionRunnable);
				mHandler.postDelayed(mHideActionRunnable, 2000);
				
			}
			break;
		case R.id.button_call:
			if (mSalesPerson._relationshipObject != null && TextUtils.isEmpty(mSalesPerson._relationshipObject.mTargetCell)) {
				Intents.callPhone(mActivity, mSalesPerson._relationshipObject.mTargetCell);
			}
			break;
		case R.id.button_info:
			if (hasMM()) {
				Intents.openURL(mActivity, Contents.MingDang.buildDirectCloudUri(mSalesPerson._mm));
			} else {
				DebugUtils.logD(TAG, "ignore open Contact Info page due to non-MM");
			}
			break;
		case R.id.button_sms:
			if (hasTarget()) {
				ConversationListActivity.startActivity(mActivity, mSalesPerson._relationshipObject);
			}
			break;
		}
		
		
	}

	@Override
	public boolean onLongClick(View v) {
		switch(v.getId()) {
		case R.id.avator:
			if (mIsDownload) {
				MyApplication.getInstance().showMessage(R.string.msg_download_mm_wait);
				return true;
			}
			Intent scanIntent = new Intent(mActivity, CaptureActivity.class);
			scanIntent.putExtra(Intents.EXTRA_SCAN_TASK, true);
			mActivity.startActivityForResult(scanIntent, mMMType);
			return true;
		}
		return false;
	}
	
	private class UpdateSalesInfoAsyncTask extends AsyncTask<Void, Void, HaierResultObject> {

		@Override
		protected HaierResultObject doInBackground(Void... arg0) {
			
			//这里我们先尝试去下载名片信息
			HaierResultObject serviceResultObject  = new HaierResultObject();
			InputStream is = null;
			StringBuilder sb = new StringBuilder();
			sb.append(mSalesPerson._baoxiuCardObject.mBID).append("_").append(mSalesPerson._mm).append("_").append(mSalesPerson._mm);
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("BID", mSalesPerson._baoxiuCardObject.mBID);
				jsonObject.put("UID", mSalesPerson._baoxiuCardObject.mUID);
				jsonObject.put("MM", mSalesPerson._mm);
				jsonObject.put("type", mMMType);
				jsonObject.put("token", SecurityUtils.MD5.md5(sb.toString()));
				DebugUtils.logD(TAG, "UpdateSalesInfoAsyncTask jsonObject = " + jsonObject.toString());
				is = NetworkUtils.openContectionLocked(HaierServiceObject.updateBaoxiucardSalesmanInfo("para", jsonObject.toString()), MyApplication.getInstance().getSecurityKeyValuesObject());
				serviceResultObject = HaierResultObject.parse(NetworkUtils.getContentFromInput(is));
				if (serviceResultObject.isOpSuccessfully()) {
					if (serviceResultObject.mJsonData != null) {
						mSalesPerson._relationshipObject =  RelationshipObject.parse(serviceResultObject.mJsonData);
						
						ContentValues values = new ContentValues();
						if (mMMType == TYPE_MM_ONE) {
							values.put(HaierDBHelper.CARD_MM_ONE, mSalesPerson._relationshipObject.mRelationshipServiceId);
						} else if (mMMType == TYPE_MM_TWO) {
							values.put(HaierDBHelper.CARD_MM_TWO, mSalesPerson._relationshipObject.mRelationshipServiceId);
						}
						values.put(HaierDBHelper.DATE, new Date().getTime());
						int updated = BjnoteContent.update(mActivity.getContentResolver(), BjnoteContent.BaoxiuCard.CONTENT_URI, values, BjnoteContent.ID_SELECTION, new String[]{String.valueOf(mSalesPerson._baoxiuCardObject.mId)});
						DebugUtils.logD(TAG, "UpdateSalesInfoAsyncTask update BaoxiuCardObject#updated " +updated);
						if (updated > 0) {
							//保存关系数据
							mSalesPerson._relationshipObject.saveInDatebase(mActivity.getContentResolver(), null);
							//本地更新成功，我们增加这几个值
							if (mMMType == TYPE_MM_ONE) {
								mSalesPerson._baoxiuCardObject.mMMOne = mSalesPerson._relationshipObject.mRelationshipServiceId;
								mSalesPerson._mm = mSalesPerson._relationshipObject.mMM;
							} else if (mMMType == TYPE_MM_TWO) {
								mSalesPerson._baoxiuCardObject.mMMTwo = mSalesPerson._relationshipObject.mRelationshipServiceId;
								mSalesPerson._mm = mSalesPerson._relationshipObject.mMM;
							}
						}
					} else {
						serviceResultObject.mStatusCode = -1;
						serviceResultObject.mStatusMessage = mActivity.getString(R.string.msg_get_no_content_from_server);
					}
					
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				NetworkUtils.closeInputStream(is);
			}
			return serviceResultObject;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(HaierResultObject result) {
			super.onPostExecute(result);
//			if (result.isOpSuccessfully()) {
//				MyApplication.getInstance().showMessage(result.mStatusMessage);
//			} else if (result.mStatusCode == -1) {
//				//添加失败
//				MyApplication.getInstance().showMessage(result.mStatusMessage);
//			} else {
//				MyApplication.getInstance().showMessage(result.mStatusMessage);
//			}
			MyApplication.getInstance().showMessage(result.mStatusMessage);
			updateView();
		}
		
		
	}

}
