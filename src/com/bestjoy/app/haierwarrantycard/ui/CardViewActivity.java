package com.bestjoy.app.haierwarrantycard.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.vudroid.pdfdroid.PdfViewerActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.bestjoy.app.haierwarrantycard.account.MyAccountManager;
import com.bestjoy.app.haierwarrantycard.service.PhotoManagerUtilsV2;
import com.bestjoy.app.haierwarrantycard.ui.model.ModleSettings;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.bestjoy.app.haierwarrantycard.utils.DialogUtils;
import com.bestjoy.app.haierwarrantycard.utils.FilesLengthUtils;
import com.bestjoy.app.haierwarrantycard.utils.SpeechRecognizerEngine;
import com.bestjoy.app.haierwarrantycard.utils.VcfAsyncDownloadUtils;
import com.bestjoy.app.haierwarrantycard.utils.VcfAsyncDownloadUtils.VcfAsyncDownloadHandler;
import com.bestjoy.app.haierwarrantycard.view.BaoxiuCardViewSalemanInfoView;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.Intents;
import com.shwy.bestjoy.utils.NetworkUtils;
import com.shwy.bestjoy.utils.NotifyRegistrant;
import com.shwy.bestjoy.utils.SecurityUtils;

public class CardViewActivity extends BaseActionbarActivity implements View.OnClickListener{
	public static final String TOKEN = CardViewActivity.class.getName();
	private static final String TAG = "CardViewActivity";
	private EditText mAskInput;
	//private Handler mHandler;
	private Button mSpeakButton;
	private SpeechRecognizerEngine mSpeechRecognizerEngine;
	//按钮
	private Button mSaveBtn, mOnekeyInstallBtn, mOnekeyRepairBtn, mOnekeyMaintainenceBtn;
	//商品信息
	private TextView mNameInput, mPinpaiInput, mModelInput, mBianhaoInput, mBaoxiuTelInput;
	private TextView mDatePickBtn, mPriceInput, mTujingInput, mYanbaoTimeInput, mYanbaoComponyInput, mYanbaoTelInput;
	
	private ImageView mAvatorView, mPolicyView, mQaView, mGuideView, mBillView;
	
	private BaoxiuCardObject mBaoxiuCardObject;
	
	private Handler mHandler;
	
	private Bundle mBundles;
	
	private TextView mBaoxiuStatusView;
	
	private ImageView mFapiaoDownloadView;
	
	/**是否显示销售人员信息*/
	private static final boolean SHOW_SALES_INFO = true;
	private BaoxiuCardViewSalemanInfoView mMMOne, mMMTwo;
	
	public static AddressBookParsedResult mAddressResult;
	private VcfAsyncDownloadHandler mVcfAsyncDownloadHandler;
	/**MM联系人布局的ID,对应getId()&0x0000ffff*/
	private long mMMLayoutViewId = -1;
	/**当前的MM*/
	private String mMM = "";
	private static final int WHAT_SHOW_FAPIAO_WAIT = 12;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFinishing()) {
			return;
		}
		mAddressResult = null;
		if (savedInstanceState != null) {
			mBundles = savedInstanceState.getBundle(TAG);
			DebugUtils.logD(TAG, "onCreate() savedInstanceState != null, restore mBundle=" + mBundles);
		}
		
		mHandler = new Handler() {

			@Override
            public void handleMessage(Message msg) {
	            switch(msg.what) {
	            case NotifyRegistrant.EVENT_NOTIFY_MESSAGE_RECEIVED:
	            	Bundle bundle = (Bundle) msg.obj;
	            	boolean status = bundle.getBoolean(PhotoManagerUtilsV2.EXTRA_DOWNLOAD_STATUS);
	            	String photoid = bundle.getString(Intents.EXTRA_PHOTOID);
	            	File fapiao = MyApplication.getInstance().getProductFaPiaoFile(mBaoxiuCardObject.getFapiaoPhotoId());
	            	//modify by chenkai, 20140701, 将发票地址存进数据库（不再拼接），增加海尔奖励延保时间 begin 
	            	if (photoid.equals(mBaoxiuCardObject.getFapiaoPhotoId())) {
	            		dismissDialog(DIALOG_PROGRESS);
	            		if (status) {
	            			//下载完成
		            		DebugUtils.logD(TAG, "FapiaoTask finished for " + mBaoxiuCardObject.getFapiaoPhotoId());
		            		//modify by chenkai, 20140701, 将发票地址存进数据库（不再拼接），增加海尔奖励延保时间 end 
		            		
		        			if (fapiao.exists()) {
		        				mBillView.setEnabled(false);
		        				mHandler.sendEmptyMessageDelayed(WHAT_SHOW_FAPIAO_WAIT, 6000);
		        				DebugUtils.logD(TAG, "FapiaoTask downloaded " + fapiao.getAbsolutePath());
		        				BaoxiuCardObject.showBill(mContext, mBaoxiuCardObject);
		        			}
	            		} else {
	            			MyApplication.getInstance().showMessage(bundle.getString(PhotoManagerUtilsV2.EXTRA_DOWNLOAD_STATUS_MESSAGE));
	            		}
	            	}
	            	return;
	            case WHAT_SHOW_FAPIAO_WAIT:
	            	mBillView.setEnabled(true);
	            	break;
	            }
	            super.handleMessage(msg);
            }
			
		};
		BaoxiuCardObject.showBill(mContext, null);
		
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
		 
		 //add by chenkai, 2014.05.31，增加一键保养 begin
		 mOnekeyMaintainenceBtn = (Button) findViewById(R.id.button_onekey_maintenance);
		 mOnekeyMaintainenceBtn.setOnClickListener(this);
		 //add by chenkai, 2014.05.31，增加一键保养 end
		 //卡萨帝品牌和海尔品牌一样处理,是海尔的品牌
		 if (HaierServiceObject.isHaierPinpaiGenaral(mBaoxiuCardObject.mPinPai)) {
			 findViewById(R.id.onekey_for_haier).setVisibility(View.VISIBLE);
			 findViewById(R.id.onekey_for_other).setVisibility(View.GONE);
		 } else {
			 findViewById(R.id.onekey_for_haier).setVisibility(View.GONE);
			 findViewById(R.id.onekey_for_other).setVisibility(View.VISIBLE);
			 
			 findViewById(R.id.button_onekey_tel).setOnClickListener(this);
		 }
		 //add by chenkai, 2014.06.06, 保修期状态 begin
		 mBaoxiuStatusView = (TextView) findViewById(R.id.warranty);
		//add by chenkai, 2014.06.06, 保修期状态 end
		 populateView();
		 
		 //根据SHOW_SALES_INFO的值来决定是否要显示销售员信息布局
		 View view = findViewById(R.id.sales_layout);
		 if (view != null) {
			 view.setVisibility(SHOW_SALES_INFO?View.VISIBLE:View.GONE);
		 }
		 if (SHOW_SALES_INFO) {
			 mVcfAsyncDownloadHandler = new VcfAsyncDownloadHandler() {

					@Override
					public void onDownloadStart() {
						//实现该方法忽略默认的下载中提示信息
					}

					@Override
					public void onDownloadFinished(
							AddressBookParsedResult addressBookParsedResult,
							String outMsg) {
						super.onDownloadFinished(addressBookParsedResult, outMsg);
						dismissDialog(DIALOG_PROGRESS);
						if (addressBookParsedResult != null) {
							//update bid and aid
				   			UpdateSalesInfoAsyncTask task = new UpdateSalesInfoAsyncTask();
				   			if (mMMLayoutViewId == (mMMOne.getId() & 0x0000ffff)) {
				   				mMMOne.setAddressBookParsedResult(addressBookParsedResult, TOKEN);
				   				task.setType(1);
				   				task.setMMOne(addressBookParsedResult.getBid());
				   				if (addressBookParsedResult.hasPhoneNumbers()) {
				   					task.setMMOneTel(addressBookParsedResult.getPhoneNumbers()[0]);
				   				}
				   				if (addressBookParsedResult.getFirstName() != null) {
				   					task.setMMOneName(addressBookParsedResult.getFirstName());
				   				}
				   			} else if (mMMLayoutViewId == (mMMTwo.getId() & 0x0000ffff)) {
				   				mMMTwo.setAddressBookParsedResult(addressBookParsedResult, TOKEN);
				   				task.setType(2);
				   				task.setMMTwo(addressBookParsedResult.getBid());
				   				if (addressBookParsedResult.hasPhoneNumbers()) {
				   					task.setMMTwoTel(addressBookParsedResult.getPhoneNumbers()[0]);
				   				}
				   				if (addressBookParsedResult.getFirstName() != null) {
				   					task.setMMTwoName(addressBookParsedResult.getFirstName());
				   				}
				   			} 
				   			task.execute();
						}
						
						mMMLayoutViewId = -1;
						mMM = "";
					}

					@Override
					public boolean onDownloadFinishedInterrupted() {
						return true;
					}
					
				};
			 mMMOne = (BaoxiuCardViewSalemanInfoView) findViewById(R.id.mmone);
			//销售员
			 mMMOne.setTitle(R.string.salesman_title);
			 
			 //服务员
			 mMMTwo = (BaoxiuCardViewSalemanInfoView) findViewById(R.id.mmtwo);
			 mMMTwo.setTitle(R.string.serverman_title);
			 if (!TextUtils.isEmpty(mBaoxiuCardObject.mMMOne)) {
				 AddressBookParsedResult resultOne = new AddressBookParsedResult(new String[]{mBaoxiuCardObject.mMMOneName}, null, new String[]{mBaoxiuCardObject.mMMOneTel}, null, null, null, null, null, null, null, null, mBaoxiuCardObject.mMMOne, null, null);
				 mMMOne.setAddressBookParsedResult(resultOne, TOKEN);
			 }
			 
			 if (!TextUtils.isEmpty(mBaoxiuCardObject.mMMTwo)) {
				 AddressBookParsedResult resultTwo = new AddressBookParsedResult(new String[]{mBaoxiuCardObject.mMMTwoName}, null, new String[]{mBaoxiuCardObject.mMMTwoTel}, null, null, null, null, null, null, null, null, mBaoxiuCardObject.mMMTwo, null, null);
				 mMMTwo.setAddressBookParsedResult(resultTwo, TOKEN);
			 }
		 }
	}
	
	private void populateView() {
		 if (!TextUtils.isEmpty(mBaoxiuCardObject.mPKY) && !mBaoxiuCardObject.mPKY.equals(BaoxiuCardObject.DEFAULT_BAOXIUCARD_IMAGE_KEY)) {
			 mGuideView.setVisibility(View.VISIBLE);
			 PhotoManagerUtilsV2.getInstance().loadPhotoAsync(TOKEN, mAvatorView, mBaoxiuCardObject.mPKY, null, PhotoManagerUtilsV2.TaskType.HOME_DEVICE_AVATOR);
		 } else {
			 //设置默认的ky图片
			 mAvatorView.setImageResource(R.drawable.ky_default);
			 mGuideView.setVisibility(View.GONE);
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
		 mYanbaoTimeInput.setText(mBaoxiuCardObject.mYanBaoTime + getString(R.string.year));
		 mYanbaoComponyInput.setText(mBaoxiuCardObject.mYanBaoDanWei);
		 mYanbaoTelInput.setText(mBaoxiuCardObject.mYBPhone);
		 
		 //add by chenkai, 2014.06.06, 保修期状态 begin
		 if (mBaoxiuCardObject.getBaoxiuValidity() <= 0) {
			 //过保
			 mBaoxiuStatusView.setText(R.string.title_over_deadline);
		 } else if (mBaoxiuCardObject.getBaoxiuValidityWithoutYanbao() > 0) {
			 //保内
			 mBaoxiuStatusView.setText(R.string.title_in_deadline);
		 } else {
			 //延保
			 mBaoxiuStatusView.setText(R.string.title_in_yanbao);
		 }
		//add by chenkai, 2014.06.06, 保修期状态 end
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
			//delete by chenkai, 20140726, 允许编辑不允许删除 begin
			//add by chenkai, 锁定认证字段 20140701 begin
			 /**
			  *  2.4 凡是被认证锁定的保修卡，手机端在用户点击编辑按钮时，提醒用户 “该报修卡已经经过厂家认证，无法编辑。”，用户不能进入编辑状态。
			  * 同样，该产品的发票也不能更改。 
			  */
			 /*if (mBaoxiuCardObject.isLocked()) {
				 MyApplication.getInstance().showLockedEditMode(mContext, R.string.msg_for_card_be_locked_cant_edit, null);
				 return true;
			 }*/
			//add by chenkai, 锁定认证字段 20140701 end
			//delete by chenkai, 20140726, 允许编辑不允许删除 begin
			 NewCardActivity.startIntent(mContext, mBundles);
			 finish();
			 break;
		 case R.string.menu_delete:
			//add by chenkai, 锁定认证字段 20140701 begin
			 /**
			  *  2.5已经锁定的保修卡，不允许用户删除。提示用户
			  */
			 /*if (mBaoxiuCardObject.isLocked()) {
				 MyApplication.getInstance().showLockedEditMode(mContext, R.string.msg_for_card_be_locked_cant_delete, null);
				 return true;
			 }*/
			//add by chenkai, 锁定认证字段 20140701 end
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
				BrowserActivity.startActivity(mContext, "http://m.rrs.com/rrsm/qa/qa.html", mContext.getString(R.string.button_qa));
			}
			break;
		case R.id.button_policy:
			if (checkHaierPinpai()) {
				BrowserActivity.startActivity(mContext, "http://m.rrs.com/rrsm/policy_fee/policy_fee.html", mContext.getString(R.string.button_policy));
			}
			break;
		case R.id.button_guide:
			//add by chenkai, for Usage, 2014.05.31 begin
			if (TextUtils.isEmpty(mBaoxiuCardObject.mKY)) {
				DebugUtils.logE(TAG, "ky is null, so ignore guild button");
				return;
			}
			if (!MyApplication.getInstance().hasExternalStorage()) {
				//没有SD,我们需要提示用户
				MyApplication.getInstance().showNoSDCardMountedMessage();
				return;
			}
			File pdfFile = MyApplication.getInstance().getProductUsagePdf(mBaoxiuCardObject.mKY);
			if (pdfFile.exists()) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(MyApplication.getInstance().getProductUsagePdf(mBaoxiuCardObject.mKY)));
				intent.setClass(mContext, PdfViewerActivity.class);

				startActivity(intent);
			} else {
				//开始下载
				downloadProductUsagePdfAsync();
			}
			//add by chenkai, for Usage, 2014.05.31 end
			break;
		case R.id.button_bill:
			File fapiao = MyApplication.getInstance().getProductFaPiaoFile(mBaoxiuCardObject.getFapiaoPhotoId());
			if (fapiao.exists()) {
				mBillView.setEnabled(false);
				mHandler.sendEmptyMessageDelayed(WHAT_SHOW_FAPIAO_WAIT, 6000);
				BaoxiuCardObject.showBill(mContext, mBaoxiuCardObject);
			} else {
				//需要下载
				showDialog(DIALOG_PROGRESS);
				if (mFapiaoDownloadView == null) {
					mFapiaoDownloadView = new ImageView(mContext);
				}
				//modify by chenkai, 20140701, 将发票地址存进数据库（不再拼接），增加海尔奖励延保时间 begin 
				//为了传值給发票下载
				BaoxiuCardObject.setBaoxiuCardObject(mBaoxiuCardObject);
				PhotoManagerUtilsV2.getInstance().loadPhotoAsync(TOKEN, mFapiaoDownloadView, mBaoxiuCardObject.getFapiaoPhotoId(), null, PhotoManagerUtilsV2.TaskType.FaPiao, true);
				//modify by chenkai, 20140701, 将发票地址存进数据库（不再拼接），增加海尔奖励延保时间 end 
			}
			break;
		case R.id.button_onekey_tel:
			Intents.callPhone(mContext, mBaoxiuCardObject.mBXPhone);
			break;
		case R.id.button_onekey_install:
		case R.id.button_onekey_repair:
		//add by chenkai, 2014.05.31，增加一键保养 begin
		case R.id.button_onekey_maintenance:
			//目前只有海尔支持预约安装和预约维修，如果不是，我们需要提示用户
	    	if (HaierServiceObject.isHaierPinpaiGenaral(mBaoxiuCardObject.mPinPai)) {
    			Bundle bundle = new Bundle();
    			bundle.putAll(mBundles);
    			if (id == R.id.button_onekey_install) {
    				bundle.putAll(ModleSettings.createMyInstallDefaultBundle(mContext));
    			} else if (id == R.id.button_onekey_repair) {
    				bundle.putAll(ModleSettings.createMyRepairDefaultBundle(mContext));
    			} else if (id == R.id.button_onekey_maintenance) {
    				bundle.putAll(ModleSettings.createMyMaintenanceDefaultBundle(mContext));
    				//add by chenkai, 2014.05.31，增加一键保养 end
    			}
    			
//    			bundle.putLong("aid", mBaoxiuCardObject.mAID);
//    			bundle.putLong("bid", mBaoxiuCardObject.mBID);
//    			bundle.putLong("uid", mBaoxiuCardObject.mUID);
    			ModleSettings.doChoose(mContext, bundle);
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
		 
	 }
	 
	 @Override
	 public void onPause() {
		 super.onPause();
		 NotifyRegistrant.getInstance().unRegister(mHandler);
	 }
	 
	 @Override
	 public void onResume() {
		 super.onResume();
		 NotifyRegistrant.getInstance().register(mHandler);
		 if (mMMLayoutViewId  > 0 && !TextUtils.isEmpty(mMM)) {
			 showDialog(DIALOG_PROGRESS);
			 //扫描进来，我们需要下载MM.vcf文件
			 VcfAsyncDownloadUtils.getInstance().executeTaskSimply(mMM, false, mVcfAsyncDownloadHandler,  PhotoManagerUtilsV2.TaskType.PREVIEW);
		 }
		 
	 }

	@Override
	protected boolean checkIntent(Intent intent) {
		mBundles = intent.getExtras();
		if (mBundles == null) {
			DebugUtils.logW(TAG, "checkIntent mBundles == null");
			return false;
		}
		mBaoxiuCardObject = BaoxiuCardObject.getBaoxiuCardObject(mBundles);
		return mBaoxiuCardObject != null && mBaoxiuCardObject.mBID > 0;
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
							MyAccountManager.getInstance().updateHomeObject(mBaoxiuCardObject.mAID);
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
	
	//add by chenkai, for Usage, 2014.05.31, begin
	private DownloadProductUsagePdfTask mDownloadProductUsagePdfTask;
	private ProgressDialog mDownloadGoodsUsagePdfProgressDialog;
	private void downloadProductUsagePdfAsync() {
		showDialog(DIALOG_PROGRESS);
		AsyncTaskUtils.cancelTask(mDownloadProductUsagePdfTask);
		mDownloadProductUsagePdfTask = new DownloadProductUsagePdfTask();
		mDownloadProductUsagePdfTask.execute();
	}
	private class DownloadProductUsagePdfTask extends AsyncTask<Void, Integer, HaierResultObject> {

		public long mPdfLength;
		public String mPdfLengthStr;
		@Override
		protected HaierResultObject doInBackground(Void... arg0) {
			mDownloadGoodsUsagePdfProgressDialog = getProgressDialog();
			HaierResultObject haierResultObject =  new HaierResultObject();
			InputStream is = null;
			FileOutputStream fos = null;
			try {
				is = NetworkUtils.openContectionLocked(HaierServiceObject.getProductPdfUrlForQuery(mBaoxiuCardObject.mKY), MyApplication.getInstance().getSecurityKeyValuesObject());
				if (is != null) {
					haierResultObject = HaierResultObject.parse(NetworkUtils.getContentFromInput(is));
					if (haierResultObject.isOpSuccessfully()) {
						if (TextUtils.isEmpty(haierResultObject.mStrData)) {
							//没有说明书
							haierResultObject.mStatusCode = 0;
							haierResultObject.mStatusMessage = getString(R.string.msg_no_product_usage);
							return haierResultObject;
						}
						//成功，表示有使用说明书
						NetworkUtils.closeInputStream(is);
						HttpResponse response = NetworkUtils.openContectionLockedV2(HaierServiceObject.getProductUsageUrl(haierResultObject.mStrData), MyApplication.getInstance().getSecurityKeyValuesObject());
						int code = response.getStatusLine().getStatusCode();
						DebugUtils.logD(TAG, "DownloadProductUsagePdfTask return StatusCode is " + code);
						if (code == HttpStatus.SC_OK) {
							mPdfLength = response.getEntity().getContentLength();
							DebugUtils.logD(TAG, "DownloadProductUsagePdfTask return length of pdf file is " + mPdfLength);
							mPdfLengthStr = FilesLengthUtils.computeLengthToString(mPdfLength);
							is = response.getEntity().getContent();
							
							fos = new FileOutputStream(MyApplication.getInstance().getProductUsagePdf(mBaoxiuCardObject.mKY));
							byte[] buffer = new byte[4096];
							int read = is.read(buffer);
							long readAll  = read;
							int percent = 0;
							while(read != -1) {
								percent = Math.round((100.0f * readAll/mPdfLength)) ;
								publishProgress(percent);
								fos.write(buffer, 0, read);
								read = is.read(buffer);
								readAll += read;
							}
							fos.flush();
						} else if (code == HttpStatus.SC_NOT_FOUND) {
							haierResultObject.mStatusMessage = getString(R.string.msg_no_product_usage);
						}
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				haierResultObject.mStatusMessage = MyApplication.getInstance().getGernalNetworkError();
			} catch (IOException e) {
				e.printStackTrace();
				haierResultObject.mStatusMessage = MyApplication.getInstance().getGernalNetworkError();
			} finally {
				NetworkUtils.closeInputStream(is);
				NetworkUtils.closeOutStream(fos);
			}
			return haierResultObject;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			if (mDownloadGoodsUsagePdfProgressDialog != null) {
				mDownloadGoodsUsagePdfProgressDialog.setMessage(getString(R.string.msg_product_usage_downloading_format, values[0], mPdfLengthStr));
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dismissDialog(DIALOG_PROGRESS);
			mDownloadGoodsUsagePdfProgressDialog = null;
		}

		@Override
		protected void onPostExecute(HaierResultObject result) {
			super.onPostExecute(result);
			dismissDialog(DIALOG_PROGRESS);
			mDownloadGoodsUsagePdfProgressDialog = null;
			if (result.isOpSuccessfully()) {
				MyApplication.getInstance().showMessage(R.string.msg_product_usage_downloading_ok);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(MyApplication.getInstance().getProductUsagePdf(mBaoxiuCardObject.mKY)));
				intent.setClass(mContext, PdfViewerActivity.class);
				startActivity(intent);
			} else {
				DialogUtils.createSimpleConfirmAlertDialog(mContext, result.mStatusMessage, null);
			}
		}
	}
	//add by chenkai, for Usage, 2014.05.31, end
	
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
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBundle(TAG, mBundles);
		DebugUtils.logW(TAG, "onSaveInstanceState(), we try to save mBundles=" + mBundles);
	}
	
	@Override
   	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
   		super.onActivityResult(requestCode, resultCode, data);
   		if (resultCode == Activity.RESULT_OK) {
   			
   			if (mAddressResult != null && !TextUtils.isEmpty(mAddressResult.getBid())) {
   				mMMLayoutViewId = requestCode;
   				mMM = mAddressResult.getBid();
   	   		 } else {
   	   			DebugUtils.logE(TAG, "onActivityResult return mAddressResult=" + mAddressResult);
   	   		 }
   			mAddressResult = null;
//   			//update bid and aid
//   			UpdateSalesInfoAsyncTask task = new UpdateSalesInfoAsyncTask();
//   			if (requestCode == (mMMOne.getId() & 0x0000ffff)) {
//   				mMMOne.setAddressBookParsedResult(mAddressResult, TOKEN);
//   				task.setType(1);
//   				task.setMMOne(mAddressResult.getBid());
//   				if (mAddressResult.hasPhoneNumbers()) {
//   					task.setMMOneTel(mAddressResult.getPhoneNumbers()[0]);
//   				}
//   				if (mAddressResult.getFirstName() != null) {
//   					task.setMMOneName(mAddressResult.getFirstName());
//   				}
//   			} else if (requestCode == (mMMTwo.getId() & 0x0000ffff)) {
//   				mMMTwo.setAddressBookParsedResult(mAddressResult, TOKEN);
//   				task.setType(2);
//   				task.setMMTwo(mAddressResult.getBid());
//   				if (mAddressResult.hasPhoneNumbers()) {
//   					task.setMMTwoTel(mAddressResult.getPhoneNumbers()[0]);
//   				}
//   				if (mAddressResult.getFirstName() != null) {
//   					task.setMMTwoName(mAddressResult.getFirstName());
//   				}
//   			} 
//   			mAddressResult = null;
//   			task.execute();
   		}
   	}
	
	private class UpdateSalesInfoAsyncTask extends AsyncTask<Void, Void, Void> {

		private String _mmOne="", _mmTwo="", _mmOneTel="", _mmOneName="", _mmTwoTel="", _mmTwoName="";
		private int _type = 0;
		public void setMMOne(String mm) {
			_mmOne = mm;
		}
		public void setMMTwo(String mm) {
			_mmTwo = mm;
		}
		public void setType(int type) {
			_type = type;
		}
		public void setMMOneTel(String tel) {
			_mmOneTel = tel;
		}
        public void setMMOneName(String name) {
        	_mmOneName = name;
		}
		public void setMMTwoTel(String tel) {
			_mmTwoTel = tel;
		}
        public void setMMTwoName(String name) {
        	_mmTwoName = name;
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			
			//这里我们先尝试去下载名片信息
			InputStream is = null;
			StringBuilder sb = new StringBuilder();
			sb.append(mBaoxiuCardObject.mBID).append("_").append(_mmOne).append("_").append(_mmTwo);
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("BID", mBaoxiuCardObject.mBID);
				jsonObject.put("MMOne", _mmOne);
				jsonObject.put("MMTwo", _mmTwo);
				jsonObject.put("type", _type);
				jsonObject.put("token", SecurityUtils.MD5.md5(sb.toString()));
				DebugUtils.logD(TAG, "UpdateSalesInfoAsyncTask jsonObject = " + jsonObject.toString());
				is = NetworkUtils.openContectionLocked(HaierServiceObject.updateBaoxiucardSalesmanInfo("para", jsonObject.toString()), MyApplication.getInstance().getSecurityKeyValuesObject());
				HaierResultObject serviceResultObject = HaierResultObject.parse(NetworkUtils.getContentFromInput(is));
				if (serviceResultObject.isOpSuccessfully()) {
					mBaoxiuCardObject.mMMOne = _mmOne;
					mBaoxiuCardObject.mMMOneTel = _mmOneTel;
					mBaoxiuCardObject.mMMOneName = _mmOneName;
					mBaoxiuCardObject.mMMTwo = _mmTwo;
					mBaoxiuCardObject.mMMTwoTel = _mmTwoTel;
					mBaoxiuCardObject.mMMTwoName = _mmTwoName;
					boolean saved = mBaoxiuCardObject.saveInDatebase(getContentResolver(), null);
					DebugUtils.logD(TAG, "UpdateSalesInfoAsyncTask save BaoxiuCard " +saved);
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
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
		
		
	}

}
