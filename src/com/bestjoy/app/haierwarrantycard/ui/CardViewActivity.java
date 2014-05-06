package com.bestjoy.app.haierwarrantycard.ui;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.apache.http.client.ClientProtocolException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bestjoy.app.haierwarrantycard.HaierServiceObject;
import com.bestjoy.app.haierwarrantycard.HaierServiceObject.HaierResultObject;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.BaoxiuCardObject;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.bestjoy.app.haierwarrantycard.service.PhotoManagerUtilsV2;
import com.bestjoy.app.haierwarrantycard.ui.model.ModleSettings;
import com.bestjoy.app.haierwarrantycard.utils.SpeechRecognizerEngine;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.Intents;
import com.shwy.bestjoy.utils.NetworkUtils;
import com.shwy.bestjoy.utils.NotifyRegistrant;

public class CardViewActivity extends BaseActionbarActivity implements View.OnClickListener{
	private static final String TOKEN = CardViewActivity.class.getName();
	private EditText mAskInput;
	//private Handler mHandler;
	private Button mSpeakButton;
	private SpeechRecognizerEngine mSpeechRecognizerEngine;
	//按钮
	private Button mSaveBtn, mOnekeyInstallBtn, mOnekeyRepairBtn;
	//商品信息
	private TextView mNameInput, mPinpaiInput, mModelInput, mBianhaoInput, mBaoxiuTelInput;
	private TextView mDatePickBtn, mPriceInput, mTujingInput, mYanbaoTimeInput, mYanbaoComponyInput, mYanbaoTelInput;
	
	private ImageView mAvatorView, mPolicyView, mQaView, mGuideView, mBillView;
	
	private BaoxiuCardObject mBaoxiuCardObject;
	
	private HomeObject mHomeObject;
	
	private Handler mHandler;
	
	private Bundle mBundles;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFinishing()) {
			return;
		}
		mHandler = new Handler();
		BaoxiuCardObject.showBill(mContext, null);
		NotifyRegistrant.getInstance().register(mHandler);
		PhotoManagerUtilsV2.getInstance().requestToken(TOKEN);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.activity_card_view);
		
		mNameInput = (TextView) findViewById(R.id.name);
		//商品信息
		 mPinpaiInput = (TextView) findViewById(R.id.product_brand_input);
		 mModelInput = (TextView) findViewById(R.id.product_model_input);
		 mBianhaoInput = (TextView) findViewById(R.id.product_sn_input);
		 mBaoxiuTelInput = (TextView) findViewById(R.id.product_tel_input);
		 mDatePickBtn = (TextView) findViewById(R.id.product_buy_date);
		 mPriceInput = (TextView) findViewById(R.id.product_buy_cost);
		 mTujingInput = (TextView) findViewById(R.id.product_buy_entry);
		 mYanbaoTimeInput = (TextView) findViewById(R.id.product_buy_delay_time);
		 mYanbaoComponyInput = (TextView) findViewById(R.id.product_buy_delay_componey);
		 mYanbaoTelInput = (TextView) findViewById(R.id.product_buy_delay_componey_tel);
		
//		 //语音
//		 mAskInput = (EditText) findViewById(R.id.product_ask_online_input);
//		 mSpeakButton =  (Button) findViewById(R.id.button_speak);
//		 mSpeakButton.setOnClickListener(this);
//		 mSpeechRecognizerEngine = SpeechRecognizerEngine.getInstance(mContext);
//		 mSpeechRecognizerEngine.setResultText(mAskInput);
//		 
//		 mSaveBtn = (Button) findViewById(R.id.button_save);
//		 mSaveBtn.setOnClickListener(this);
		 
		 mAvatorView = (ImageView) findViewById(R.id.avator);
		 mAvatorView.setOnClickListener(this);
		 
		 mQaView = (ImageView) findViewById(R.id.button_qa);
		 mQaView.setOnClickListener(this);
		 
		 mPolicyView = (ImageView) findViewById(R.id.button_policy);
		 mPolicyView.setOnClickListener(this);
		 
		 mGuideView = (ImageView) findViewById(R.id.button_guide);
		 mGuideView.setOnClickListener(this);
		 
		 mBillView = (ImageView) findViewById(R.id.button_bill);
		 mBillView.setOnClickListener(this);
		 
		 mOnekeyInstallBtn = (Button) findViewById(R.id.button_onekey_install);
		 mOnekeyInstallBtn.setOnClickListener(this);
		 
		 mOnekeyRepairBtn = (Button) findViewById(R.id.button_onekey_repair);
		 mOnekeyRepairBtn.setOnClickListener(this);
		 
		 populateView();
		
	}
	
	private void populateView() {
		 if (!TextUtils.isEmpty(mBaoxiuCardObject.mKY)) {
			 PhotoManagerUtilsV2.getInstance().loadPhotoAsync(TOKEN, mAvatorView, mBaoxiuCardObject.mKY, null, PhotoManagerUtilsV2.TaskType.HOME_DEVICE_AVATOR);
		 }
		 if (!mBaoxiuCardObject.hasBillAvator()) {
			 mBillView.setVisibility(View.INVISIBLE);
		 } else {
			 mBillView.setVisibility(View.VISIBLE);
		 }
		 
		 mNameInput.setText(BaoxiuCardObject.getTagName(mBaoxiuCardObject.mCardName, mBaoxiuCardObject.mLeiXin));
		 mPinpaiInput.setText(mBaoxiuCardObject.mPinPai);
		 mModelInput.setText(mBaoxiuCardObject.mXingHao);
		 mBianhaoInput.setText(mBaoxiuCardObject.mSHBianHao);
		 mBaoxiuTelInput.setText(mBaoxiuCardObject.mBXPhone);
		 try {
			mDatePickBtn.setText(BaoxiuCardObject.BUY_DATE_FORMAT.format(BaoxiuCardObject.BUY_DATE_TIME_FORMAT.parse(mBaoxiuCardObject.mBuyDate)));
		} catch (ParseException e) {
		}
		 mPriceInput.setText(mBaoxiuCardObject.mBuyPrice);
		 mTujingInput.setText(mBaoxiuCardObject.mBuyTuJing);
		 mYanbaoTimeInput.setText(mBaoxiuCardObject.mYanBaoTime);
		 mYanbaoComponyInput.setText(mBaoxiuCardObject.mYanBaoDanWei);
		 mYanbaoTelInput.setText(mBaoxiuCardObject.mYBPhone);
		 
		 mHomeObject = HomeObject.getHomeObject();
	}
	
	
	 @Override
     public boolean onCreateOptionsMenu(Menu menu) {
  	     getSupportMenuInflater().inflate(R.menu.card_view_activity_menu, menu);
         return true;
     }
	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem menuItem) {
		 switch(menuItem.getItemId()) {
		 case R.string.menu_edit:
			 //编辑卡片
			 BaoxiuCardObject.setBaoxiuCardObject(mBaoxiuCardObject);
			 NewCardActivity.startIntent(mContext, mBundles);
			 finish();
			 break;
		 case R.string.menu_delete:
			 showDeleteDialog();
			 break;
		 }
		 return super.onOptionsItemSelected(menuItem);
	 }
	 
	 private void showDeleteDialog() {
		 new AlertDialog.Builder(this)
		 	.setTitle(R.string.msg_tip_title)
	    	.setMessage(R.string.sure_delete)
	    	.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//删除卡片
					 if (ComConnectivityManager.getInstance().isConnected()) {
						 delteCardAsync();
					 } else {
						 showDialog(DIALOG_DATA_NOT_CONNECTED);
					 }
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.show();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id) {
		case R.id.button_speak:
			mSpeechRecognizerEngine.showIatDialog(mContext);
			break;
		case R.id.button_qa:
			if (checkHaierPinpai()) {
				BrowserActivity.startActivity(mContext, "http://www.haier.com/cn/services_supports/overview/cooling/daily_use/troubles/", mContext.getString(R.string.button_qa));
			}
			break;
		case R.id.button_policy:
			if (checkHaierPinpai()) {
				BrowserActivity.startActivity(mContext, "http://www.haier.com/cn/services_supports/overview/cooling/service_guarantee/warranty/", mContext.getString(R.string.button_policy));
			}
			break;
		case R.id.button_guide:
			break;
		case R.id.button_bill:
			BaoxiuCardObject.showBill(mContext, mBaoxiuCardObject);
			break;
		case R.id.button_onekey_install:
		case R.id.button_onekey_repair:
			//目前只有海尔支持预约安装和预约维修，如果不是，我们需要提示用户
	    	if (HaierServiceObject.isHaierPinpai(mBaoxiuCardObject.mPinPai)) {
	    		BaoxiuCardObject.setBaoxiuCardObject(mBaoxiuCardObject);
    			HomeObject.setHomeObject(mHomeObject);
    			ModleSettings.doChoose(mContext, mBundles);
    			finish();
	    	} else {
	    		new AlertDialog.Builder(mContext)
		    	.setMessage(R.string.must_haier_confirm_yuyue)
		    	.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!TextUtils.isEmpty(mBaoxiuCardObject.mBXPhone)) {
							Intents.callPhone(mContext, mBaoxiuCardObject.mBXPhone);
						} else {
							MyApplication.getInstance().showMessage(R.string.msg_no_bxphone);
						}
						
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.show();
	    	}
			break;
		}
		
	}
	
	private boolean checkHaierPinpai() {
		boolean result = HaierServiceObject.isHaierPinpai(mBaoxiuCardObject.mPinPai);
		if (!result) {
			//不是海尔的品牌，我们给个提示
			MyApplication.getInstance().showMessage(R.string.msg_pinpai_haier_suppot_only);
		}
		return result;
	}

	private void showEmptyInputToast(int resId) {
			String msg = getResources().getString(resId);
			MyApplication.getInstance().showMessage(getResources().getString(R.string.input_type_please_input) + msg);
		}
	 
	 @Override
	 public void onDestroy() {
		 super.onDestroy();
		 PhotoManagerUtilsV2.getInstance().releaseToken(TOKEN);
		 NotifyRegistrant.getInstance().unRegister(mHandler);
	 }

	@Override
	protected boolean checkIntent(Intent intent) {
		mBaoxiuCardObject = BaoxiuCardObject.getBaoxiuCardObject();
		mBundles = intent.getExtras();
		return mBaoxiuCardObject != null && mBaoxiuCardObject.mBID > 0 && mBundles != null;
	}
	
	private DeleteCardAsyncTask mDeleteCardAsyncTask;
	private void delteCardAsync() {
		AsyncTaskUtils.cancelTask(mDeleteCardAsyncTask);
		showDialog(DIALOG_PROGRESS);
		mDeleteCardAsyncTask = new DeleteCardAsyncTask();
		mDeleteCardAsyncTask.execute();
	}
	
	private class DeleteCardAsyncTask extends AsyncTask<Void, Void, HaierResultObject> {

		@Override
		protected HaierResultObject doInBackground(Void... params) {
			InputStream is = null;
			HaierResultObject resultObject = new HaierResultObject();
			try {
				is = NetworkUtils.openContectionLocked(HaierServiceObject.getBaoxiuCardDeleteUrl(String.valueOf(mBaoxiuCardObject.mBID), String.valueOf(mBaoxiuCardObject.mUID)), MyApplication.getInstance().getSecurityKeyValuesObject());
				if (is != null) {
					String content = NetworkUtils.getContentFromInput(is);
					resultObject = HaierResultObject.parse(content);
					if (resultObject.isOpSuccessfully()) {
						//删除服务器成功后还要删除本地的数据
						int deleted = BaoxiuCardObject.deleteBaoxiuCardInDatabaseForAccount(mContext.getContentResolver(), mBaoxiuCardObject.mUID, mBaoxiuCardObject.mAID, mBaoxiuCardObject.mBID);
						if (deleted > 0) {
							//本地删除成功后我们还要刷新对应HomeObject对象的保修卡数据
							HaierAccountManager.getInstance().updateHomeObject(mBaoxiuCardObject.mAID);
						}
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				resultObject.mStatusMessage = e.getMessage();
			} catch (IOException e) {
				resultObject.mStatusMessage = e.getMessage();
				e.printStackTrace();
			}
			return resultObject;
		}

		@Override
		protected void onPostExecute(HaierResultObject result) {
			super.onPostExecute(result);
			dismissDialog(DIALOG_PROGRESS);
			MyApplication.getInstance().showMessage(result.mStatusMessage);
			if (result.isOpSuccessfully()) {
				finish();
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dismissDialog(DIALOG_PROGRESS);
		}
		
	}
	
	/**
	 * 回到主界面
	 * @param context
	 */
	public static void startActivit(Context context, Bundle bundle) {
		Intent intent = new Intent(context, CardViewActivity.class);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		context.startActivity(intent);
	}

}
