package com.bestjoy.app.haierwarrantycard.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestjoy.app.haierwarrantycard.HaierServiceObject;
import com.bestjoy.app.haierwarrantycard.HaierServiceObject.HaierResultObject;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.AccountObject;
import com.bestjoy.app.haierwarrantycard.account.BaoxiuCardObject;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.bestjoy.app.haierwarrantycard.service.PhotoManagerUtilsV2;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.DateUtils;
import com.shwy.bestjoy.utils.ImageHelper;
import com.shwy.bestjoy.utils.InfoInterface;
import com.shwy.bestjoy.utils.NetworkUtils;

public class NewWarrantyCardFragment extends ModleBaseFragment implements View.OnClickListener{
	private static final String TAG = "NewWarrantyCardFragment";
	private static final String TOKEN = NewWarrantyCardFragment.class.getName();
	//按钮
	private Button mSaveBtn;
	private TextView mDatePickBtn;
	private ImageView mBillImageView;
	private EditText mTypeInput, mPinpaiInput, mModelInput, mBianhaoInput, mBaoxiuTelInput, mTagInput;
	private EditText mPriceInput, mTujingInput, mYanbaoTimeInput, mYanbaoComponyInput, mYanbaoTelInput;
	private Calendar mCalendar;
	//临时的拍摄照片路径
	private File mBillTempFile, mAvatorTempFile;
	/**请求商品预览图*/
	private static final int REQUEST_AVATOR = 2;
	/**请求发票预览图*/
	private static final int REQUEST_BILL = 3;
	
	/**是否要重新拍摄商品预览图*/
	private static final int DIALOG_PICTURE_AVATOR_CONFIRM = 4;
	private static final int DIALOG_BILL_OP_CONFIRM = 5;
	
	private int mPictureRequest = -1;
	private long mAid = -1;
	private long mUid = -1;
	private long mBid = -1;
	
	private BaoxiuCardObject mBaoxiuCardObject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PhotoManagerUtilsV2.getInstance().requestToken(TOKEN);
		setHasOptionsMenu(true);
		mCalendar = Calendar.getInstance();
		mBaoxiuCardObject = new BaoxiuCardObject();
		initTempFile();
	}
	
	private void initTempFile() {
		File tempRootDir = Environment.getExternalStorageDirectory();
		mBillTempFile = new File(tempRootDir, ".billTemp");
		mAvatorTempFile = new File(tempRootDir, ".avatorTemp");
		
		
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 View view = inflater.inflate(R.layout.activity_new_card, container, false);
		 mBillImageView = (ImageView) view.findViewById(R.id.button_scan_bill);
		 mBillImageView.setOnClickListener(this);
		 
		 mTypeInput = (EditText) view.findViewById(R.id.product_type_input);
		 mPinpaiInput = (EditText) view.findViewById(R.id.product_brand_input);
		 mModelInput = (EditText) view.findViewById(R.id.product_model_input);
		 mBianhaoInput = (EditText) view.findViewById(R.id.product_sn_input);
		 mBaoxiuTelInput = (EditText) view.findViewById(R.id.product_tel_input);
		 //购买日期
		 mDatePickBtn = (TextView) view.findViewById(R.id.product_buy_date);
		 mDatePickBtn.setOnClickListener(this);
		 
		 mDatePickBtn.setText(DateUtils.TOPIC_DATE_TIME_FORMAT.format(mCalendar.getTime()));
		 
		 mPriceInput = (EditText) view.findViewById(R.id.product_buy_cost);
		 mTujingInput = (EditText) view.findViewById(R.id.product_buy_entry);
		 mYanbaoTimeInput = (EditText) view.findViewById(R.id.product_buy_delay_time);
		 mYanbaoComponyInput = (EditText) view.findViewById(R.id.product_buy_delay_componey);
		 mYanbaoTelInput = (EditText) view.findViewById(R.id.product_buy_delay_componey_tel);
		 //增加标签
		 mTagInput = (EditText) view.findViewById(R.id.product_beizhu_tag);
		 
		 mSaveBtn = (Button) view.findViewById(R.id.button_save);
		 mSaveBtn.setOnClickListener(this);
			
		view.findViewById(R.id.button_scan_qrcode).setOnClickListener(this);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		PhotoManagerUtilsV2.getInstance().releaseToken(TOKEN);
		BaoxiuCardObject.showBill(getActivity(), null);
	}

	@Override
	public Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_BILL_OP_CONFIRM:
			return new AlertDialog.Builder(getActivity())
			.setItems(this.getResources().getStringArray(R.array.bill_op_items), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch(which) {
					case 0:
						BaoxiuCardObject.showBill(getActivity(), mBaoxiuCardObject);
						break;
					case 1:
						onCapturePhoto();
						break;
					}
					
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.create();
		}
		
		return super.onCreateDialog(id);
	}
	
	public boolean isEditable() {
		return mBid > 0;
	}
	
	private void populateBaoxiuInfoView(BaoxiuCardObject object) {
		//init layouts
		if (object == null) {
			mTypeInput.getText().clear();
			mPinpaiInput.getText().clear();
			mModelInput.getText().clear();
			mBianhaoInput.getText().clear();
			mBaoxiuTelInput.getText().clear();
			mPriceInput.getText().clear();
			mTujingInput.getText().clear();
			mYanbaoTimeInput.getText().clear();
			mYanbaoComponyInput.getText().clear();
			mYanbaoTelInput.getText().clear();
			mTagInput.getText().clear();
			//传递进来的，我们还需要清空发票数据
			mBaoxiuCardObject.mFPaddr = null;
		} else {
			mTypeInput.setText(object.mLeiXin);
			mPinpaiInput.setText(object.mPinPai);
			mModelInput.setText(object.mXingHao);
			mBianhaoInput.setText(object.mSHBianHao);
			
			mBaoxiuTelInput.setText(object.mBXPhone);
			mPriceInput.setText(object.mBuyPrice);
			mTujingInput.setText(object.mBuyTuJing);
			mYanbaoTimeInput.setText(object.mYanBaoTime);
			mYanbaoComponyInput.setText(object.mYanBaoDanWei);
			mYanbaoTelInput.setText(object.mYBPhone);
			mTagInput.setText(object.mCardName);
			//传递进来的，我们还需要读取发票数据
			mBaoxiuCardObject.mFPaddr = object.mFPaddr;
			if (isEditable()) {
				//如果是已经创建了的，我们不允许修改时间，并且要使用保修卡的购买时间
				try {
					Date date = BaoxiuCardObject.BUY_DATE_TIME_FORMAT.parse(object.mBuyDate);
					mDatePickBtn.setText(DateUtils.TOPIC_DATE_TIME_FORMAT.format(date));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				 mDatePickBtn.setEnabled(false);
				 
				//如果有发票，我们显示出来
				if (mBaoxiuCardObject.hasLocalBill()) {
					PhotoManagerUtilsV2.getInstance().loadLocalPhotoAsync(TOKEN, mBillImageView, mBaoxiuCardObject.getFapiaoPhotoId(), null, PhotoManagerUtilsV2.TaskType.FaPiao);
				}
				
				//设置标题为编辑保修卡
				getActivity().setTitle(R.string.button_edit_card);
				mSaveBtn.setText(R.string.button_update);
			}
		}
		
	}
	
	public void setBaoxiuObjectAfterSlideMenu(InfoInterface slideManuObject) {
		if (slideManuObject instanceof BaoxiuCardObject) {
			BaoxiuCardObject object = (BaoxiuCardObject) slideManuObject;
			if (!TextUtils.isEmpty(object.mLeiXin)) {
				mTypeInput.setText(object.mLeiXin);
			}
			if (!TextUtils.isEmpty(object.mPinPai)) {
				mPinpaiInput.setText(object.mPinPai);
			}
			
			if (!TextUtils.isEmpty(object.mXingHao)) {
				mModelInput.setText(object.mXingHao);
			}
			
			if (!TextUtils.isEmpty(object.mSHBianHao)) {
				mBianhaoInput.setText(object.mSHBianHao);
			}
			
			if (!TextUtils.isEmpty(object.mBXPhone)) {
				mBaoxiuTelInput.setText(object.mBXPhone);
			}
		}
	}
	
	private BaoxiuCardObject getmBaoxiuCardObject() {
		mBaoxiuCardObject.mLeiXin = mTypeInput.getText().toString().trim();
		mBaoxiuCardObject.mPinPai = mPinpaiInput.getText().toString().trim();
		mBaoxiuCardObject.mXingHao = mModelInput.getText().toString().trim();
		mBaoxiuCardObject.mSHBianHao = mBianhaoInput.getText().toString().trim();
		mBaoxiuCardObject.mBXPhone = mBaoxiuTelInput.getText().toString().trim();
		
		mBaoxiuCardObject.mBuyDate = BaoxiuCardObject.BUY_DATE_TIME_FORMAT.format(mCalendar.getTime());
		mBaoxiuCardObject.mBuyPrice = mPriceInput.getText().toString().trim();
		mBaoxiuCardObject.mBuyTuJing = mTujingInput.getText().toString().trim();
		
		mBaoxiuCardObject.mYanBaoTime = mYanbaoTimeInput.getText().toString().trim();
		mBaoxiuCardObject.mYanBaoDanWei = mYanbaoComponyInput.getText().toString().trim();
		mBaoxiuCardObject.mYBPhone = mYanbaoTelInput.getText().toString().trim();
		
		mBaoxiuCardObject.mCardName = mTagInput.getText().toString().trim();
		
		mBaoxiuCardObject.mAID = mAid;
		mBaoxiuCardObject.mUID = mUid;
		mBaoxiuCardObject.mBID = mBid;
		
		return mBaoxiuCardObject;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_scan_bill:
			if (mBaoxiuCardObject != null && !mBaoxiuCardObject.hasLocalBill()) {
				//如果没有发票，我们直接调用相机
				mPictureRequest = REQUEST_BILL;
				onCapturePhoto();
			} else {
				//如果有，我们显示操作选项，查看或是拍摄发票
				showDialog(DIALOG_BILL_OP_CONFIRM);
			}
			break;
		case R.id.button_scan_qrcode:
//			Intent scanIntent = new Intent(getActivity(), CaptureActivity.class);
//			scanIntent.putExtra(Intents.EXTRA_SCAN_TASK, true);
//			startActivityForResult(scanIntent, REQUEST_SCAN);
			startScan();
			break;
		case R.id.product_buy_date:
			showDatePickerDialog();
			break;
		case R.id.button_save:
//			if (mBaoxiuCardObject.hasBill()) {
//				saveNewWarrantyCardAndSync();
//			} else {
//				MyApplication.getInstance().showMessage(R.string.msg_cant_show_bill);
//			}
			 if (ComConnectivityManager.getInstance().isConnected()) {
				 if (isEditable()) {
						updateWarrantyCardAsync();
					} else {
						saveNewWarrantyCardAndSync();
					}
			 } else {
				 showDialog(DIALOG_DATA_NOT_CONNECTED);
			 }
			break;
		}
		
	}
	
	private void saveNewWarrantyCardAndSync() {
		if(HaierAccountManager.getInstance().hasLoginned()) {
			//如果没有注册，我们前往登陆界面
			if(checkInput()) {
				mSaveBtn.setEnabled(false);
				requestNewWarrantyCardAndSync();
			}
		} else {
			//如果没有注册，我们前往登陆/注册界面，这里传递ModelBundle对象过去，以便做合适的跳转
			MyApplication.getInstance().showMessage(R.string.login_tip);
			LoginActivity.startIntent(this.getActivity(), getArguments());
		}
	}
	

	private CreateNewWarrantyCardAsyncTask mCreateNewWarrantyCardAsyncTask;
	private void requestNewWarrantyCardAndSync(String... param) {
		AsyncTaskUtils.cancelTask(mCreateNewWarrantyCardAsyncTask);
		showDialog(DIALOG_PROGRESS);
		mCreateNewWarrantyCardAsyncTask = new CreateNewWarrantyCardAsyncTask();
		mCreateNewWarrantyCardAsyncTask.execute(param);
	}

	private class CreateNewWarrantyCardAsyncTask extends AsyncTask<String, Void, HaierResultObject> {
		/*{
		    "StatusCode": "1", 
		    "StatusMessage": "成功返回数据", 
		    "Data": "Bid:4"
		}*/
		@Override
		protected HaierResultObject doInBackground(String... params) {
			//更新保修卡信息
			BaoxiuCardObject baoxiuCardObject = getmBaoxiuCardObject();
			DebugUtils.logD(TAG, "CreateNewWarrantyCardAsyncTask for AID " + baoxiuCardObject.mAID);
			HaierResultObject haierResultObject = new HaierResultObject();
			InputStream is = null;
//			final int LENGTH = 15;
//			String[] keys = new String[LENGTH];
//			String[] values = new String[LENGTH];
//			keys[0] = "LeiXin=";
//			values[0] = baoxiuCardObject.mLeiXin;
//			keys[1] = "&BuyDate=";
//			values[1] = baoxiuCardObject.mBuyDate;
//			keys[2] = "&BuyPrice=";
//			values[2] = baoxiuCardObject.mBuyPrice;
//			keys[3] = "&BuyTuJing=";
//			values[3] = baoxiuCardObject.mBuyTuJing;
//			keys[4] = "&BXPhone=";
//			values[4] = baoxiuCardObject.mBXPhone;
//			keys[5] = "&PinPai=";
//			values[5] = baoxiuCardObject.mPinPai;
//			keys[6] = "&UID=";
//			values[6] = String.valueOf(baoxiuCardObject.mUID);
//			keys[7] = "&XingHao=";
//			values[7] = baoxiuCardObject.mXingHao;
//			keys[8] = "&YanBaoDanWei=";
//			values[8] = baoxiuCardObject.mYanBaoDanWei;
//			keys[9] = "&YanBaoTime=";
//			values[9] = baoxiuCardObject.mYanBaoTime;
//			keys[10] = "&AID=";
//			values[10] = String.valueOf(baoxiuCardObject.mAID);
//			keys[11] = "&SHBianHao=";
//			values[11] = baoxiuCardObject.mSHBianHao;
//			keys[12] = "&Tag=";
//			values[12] = baoxiuCardObject.mCardName;
//			keys[13] = "&YBPhone=";
//			values[13] = baoxiuCardObject.mYBPhone;
//			keys[14] = "&imgstr=";
//			values[14] = baoxiuCardObject.getBase64StringFromBillAvator().replaceAll("\\+", "*");
//			DebugUtils.logD(TAG, "urls = " + Arrays.toString(keys));
//			DebugUtils.logD(TAG, "paths = " + Arrays.toString(values));
			StringBuilder paramValue = new StringBuilder();
//			paramValue.append("LeiXin=").append(baoxiuCardObject.mLeiXin)
//			.append("|BuyDate=").append(baoxiuCardObject.mBuyDate)
//			.append("|BuyPrice=").append(baoxiuCardObject.mBuyPrice)
//			.append("|BuyTuJing=").append(baoxiuCardObject.mBuyTuJing)
//			.append("|BXPhone=").append(baoxiuCardObject.mBXPhone)
//			.append("|PinPai=").append(baoxiuCardObject.mPinPai)
//			.append("|UID=").append(String.valueOf(baoxiuCardObject.mUID))
//			.append("|XingHao=").append(baoxiuCardObject.mXingHao)
//			.append("|YanBaoDanWei=").append(baoxiuCardObject.mYanBaoDanWei)
//			.append("|YanBaoTime=").append(baoxiuCardObject.mYanBaoTime)
//			.append("|AID=").append(String.valueOf(baoxiuCardObject.mAID))	
//			.append("|SHBianHao=").append(baoxiuCardObject.mSHBianHao)
//			.append("|Tag=").append(baoxiuCardObject.mCardName)
//			.append("|YBPhone=").append(baoxiuCardObject.mYBPhone);
//			paramValue.append("|imgstr=").append(baoxiuCardObject.getBase64StringFromBillAvator());
			paramValue.append(baoxiuCardObject.mLeiXin)
			.append("|").append(baoxiuCardObject.mBuyDate)
			.append("|").append(baoxiuCardObject.mBuyPrice)
			.append("|").append(baoxiuCardObject.mBuyTuJing)
			.append("|").append(baoxiuCardObject.mBXPhone)
			.append("|").append(baoxiuCardObject.mPinPai)
			.append("|").append(String.valueOf(baoxiuCardObject.mUID))
			.append("|").append(baoxiuCardObject.mXingHao)
			.append("|").append(baoxiuCardObject.mYanBaoDanWei)
			.append("|").append(baoxiuCardObject.mYanBaoTime)
			.append("|").append(String.valueOf(baoxiuCardObject.mAID))	
			.append("|").append(baoxiuCardObject.mSHBianHao)
			.append("|").append(baoxiuCardObject.mCardName)
			.append("|").append(baoxiuCardObject.mYBPhone);
			paramValue.append("|").append(baoxiuCardObject.getBase64StringFromBillAvator());
			DebugUtils.logD(TAG, "param " + paramValue.toString());
			try {
//				StringBuilder paramValue = new StringBuilder(/*HaierServiceObject.SERVICE_URL + "AddBaoXiu.ashx?"*/"http://115.29.231.29/UploadBaoXiu.asmx/UpLoadData?");
//				paramValue.append(NetworkUtils.getUrlEncodedString(keys, values));
//				if (baoxiuCardObject.hasTempBill()) {
//					paramValue.append("&imgstr=\"").append(baoxiuCardObject.getBase64StringFromBillAvator().replaceAll("\\+", "*")).append("\"");
//				}
//				is = NetworkUtils.openContectionLocked(paramValue.toString(), MyApplication.getInstance().getSecurityKeyValuesObject());
				is = NetworkUtils.openPostContectionLocked("http://115.29.231.29/UploadBaoXiu.asmx/UpdaLoad", "Para", paramValue.toString(), MyApplication.getInstance().getSecurityKeyValuesObject());
				try {
					haierResultObject = HaierResultObject.parse(NetworkUtils.getContentFromInput(is));
					DebugUtils.logD(TAG, "StatusCode = " + haierResultObject.mStatusCode);
					DebugUtils.logD(TAG, "StatusMessage = " + haierResultObject.mStatusMessage);
					if (haierResultObject.isOpSuccessfully()) {
						//在保存前，我们需要回填bid数据
						String data = haierResultObject.mStrData;
						DebugUtils.logD(TAG, "Data = " + data);
						if (data.length() > "Bid:".length()) {
							data = data.substring("Bid:".length());
							baoxiuCardObject.mBID = Long.valueOf(data);
							//如果后台返回了bid,我们根据它向服务器查询保修卡数据，并解析保存在本地。
							if (baoxiuCardObject.mBID > 0) {
								baoxiuCardObject.clear();
								NetworkUtils.closeInputStream(is);
								StringBuilder sb = new StringBuilder(HaierServiceObject.SERVICE_URL);
								sb.append("GetBaoXiuDataByBID.ashx?BID=").append(baoxiuCardObject.mBID);
								is = NetworkUtils.openContectionLocked(sb.toString(), MyApplication.getInstance().getSecurityKeyValuesObject());
								if (is != null){
									haierResultObject = HaierResultObject.parse(NetworkUtils.getContentFromInput(is));
									if (haierResultObject.isOpSuccessfully()) {
										baoxiuCardObject = BaoxiuCardObject.parseBaoxiuCards(haierResultObject.mJsonData, null);
										boolean savedOk = baoxiuCardObject.saveInDatebase(getActivity().getContentResolver(), null);
										if (!savedOk) {
											//通常不会发生
											haierResultObject.mStatusMessage = getActivity().getString(R.string.msg_local_save_card_failed);
										} else {
											HaierAccountManager.getInstance().updateHomeObject(baoxiuCardObject.mAID);
										}
									} 
									
								}
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				haierResultObject.mStatusMessage = e.getMessage();
			} catch (IOException e) {
				e.printStackTrace();
				haierResultObject.mStatusMessage = e.getMessage();
			} finally {
				NetworkUtils.closeInputStream(is);
			}
			return haierResultObject;
		}

		@Override
		protected void onPostExecute(HaierResultObject result) {
			super.onPostExecute(result);
			mSaveBtn.setEnabled(true);
			dissmissDialog(DIALOG_PROGRESS);
			if (result.isOpSuccessfully()) {
				//添加成功
				MyApplication.getInstance().showMessage(R.string.save_success);
				getActivity().finish();
				MyChooseDevicesActivity.startIntent(getActivity(), getArguments());
			} else {
				MyApplication.getInstance().showMessage(result.mStatusMessage);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dissmissDialog(DIALOG_PROGRESS);
			mSaveBtn.setEnabled(true);
		}
	}
	
	//########################更新操作 开始################################################
	/*
	 ServerIP/Haier/UpdateBaoXiu.ashx
	参数：
	LeiXin 
	XingHao 
	SHBianHao 
	BXPhone 
	BuyTuJing 
	YanBaoDanWei 
	Tag 
	YanBaoTime 
	YBPhone 
	BID 
	BuyDate 
	BuyPrice 
	FPaddr 
	AID 
	说明
	跟添加保修数据的参数唯一不同的事一个有UID(用户ID)没有BID 
	这个有BID(保修ID) 没有用户ID 

	 */
	private UpdateWarrantyCardAsyncTask mUpdateWarrantyCardAsyncTask;
	private void updateWarrantyCardAsync() {
		AsyncTaskUtils.cancelTask(mUpdateWarrantyCardAsyncTask);
		showDialog(DIALOG_PROGRESS);
		mUpdateWarrantyCardAsyncTask = new UpdateWarrantyCardAsyncTask();
		mUpdateWarrantyCardAsyncTask.execute();
	}

	private class UpdateWarrantyCardAsyncTask extends AsyncTask<Void, Void, HaierResultObject> {
		/*{
		    "StatusCode": "1", 
		    "StatusMessage": "成功返回数据", 
		    "Data": "Bid:4"
		}*/
		@Override
		protected HaierResultObject doInBackground(Void... params) {
			//更新保修卡信息
			BaoxiuCardObject baoxiuCardObject = getmBaoxiuCardObject();
			DebugUtils.logD(TAG, "UpdateWarrantyCardAsyncTask BID " + baoxiuCardObject.mBID);
			HaierResultObject haierResultObject = new HaierResultObject();
			InputStream is = null;
			final int LENGTH = 14;
			String[] urls = new String[LENGTH];
			String[] paths = new String[LENGTH];
			urls[0] = HaierServiceObject.SERVICE_URL + "UpdateBaoXiu.ashx?LeiXin=";
			paths[0] = baoxiuCardObject.mLeiXin;
			urls[1] = "&BuyDate=";
			paths[1] = baoxiuCardObject.mBuyDate;
			urls[2] = "&BuyPrice=";
			paths[2] = baoxiuCardObject.mBuyPrice;
			urls[3] = "&BuyTuJing=";
			paths[3] = baoxiuCardObject.mBuyTuJing;
			urls[4] = "&BXPhone=";
			paths[4] = baoxiuCardObject.mBXPhone;
			urls[5] = "&PinPai=";
			paths[5] = baoxiuCardObject.mPinPai;
			urls[6] = "&BID=";
			paths[6] = String.valueOf(baoxiuCardObject.mBID);
			urls[7] = "&XingHao=";
			paths[7] = baoxiuCardObject.mXingHao;
			urls[8] = "&YanBaoDanWei=";
			paths[8] = baoxiuCardObject.mYanBaoDanWei;
			urls[9] = "&YanBaoTime=";
			paths[9] = baoxiuCardObject.mYanBaoTime;
			urls[10] = "&AID=";
			paths[10] = String.valueOf(baoxiuCardObject.mAID);
			urls[11] = "&SHBianHao=";
			paths[11] = baoxiuCardObject.mSHBianHao;
			urls[12] = "&Tag=";
			paths[12] = baoxiuCardObject.mCardName;
			urls[13] = "&YBPhone=";
			paths[13] = baoxiuCardObject.mYBPhone;
			DebugUtils.logD(TAG, "urls = " + Arrays.toString(urls));
			DebugUtils.logD(TAG, "paths = " + Arrays.toString(paths));
			try {
				is = NetworkUtils.openContectionLocked(urls, paths, MyApplication.getInstance().getSecurityKeyValuesObject());
				haierResultObject = HaierResultObject.parse(NetworkUtils.getContentFromInput(is));
				if (haierResultObject.isOpSuccessfully()) {
					//成功删除了服务器上的数据，我们还需要同步删除本地的数据
					boolean updated = baoxiuCardObject.saveInDatebase(getActivity().getContentResolver(), null);
					if (!updated) {
						//通常不会发生
						DebugUtils.logD(TAG, "UpdateWarrantyCardAsyncTask " + getActivity().getString(R.string.msg_local_save_card_failed));
					}
				}
						
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				haierResultObject.mStatusMessage = e.getMessage();
			} catch (IOException e) {
				e.printStackTrace();
				haierResultObject.mStatusMessage = e.getMessage();
			} finally {
				NetworkUtils.closeInputStream(is);
			}
			return haierResultObject;
		}

		@Override
		protected void onPostExecute(HaierResultObject result) {
			super.onPostExecute(result);
			mSaveBtn.setEnabled(true);
			dissmissDialog(DIALOG_PROGRESS);
			if (result.isOpSuccessfully()) {
				MyApplication.getInstance().showMessage(R.string.update_success);
				getActivity().finish();
				MyChooseDevicesActivity.startIntent(getActivity(), getArguments());
			} else {
				MyApplication.getInstance().showMessage(result.mStatusMessage);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dissmissDialog(DIALOG_PROGRESS);
			mSaveBtn.setEnabled(true);
		}
	}
	
	
	//########################更新操作 结束#################################################

	private boolean checkInput() {
		if(TextUtils.isEmpty(mTypeInput.getText().toString().trim())){
			showEmptyInputToast(R.string.product_type);
			return false;
		}
		if(TextUtils.isEmpty(mPinpaiInput.getText().toString().trim())){
			showEmptyInputToast(R.string.product_brand);
			return false;
		}
		if(TextUtils.isEmpty(mModelInput.getText().toString().trim())){
			showEmptyInputToast(R.string.product_model);
			return false;
		}
//		if(TextUtils.isEmpty(mBianhaoInput.getText().toString().trim())){
//			showEmptyInputToast(R.string.product_sn);
//			return false;
//		}
		if(TextUtils.isEmpty(mBaoxiuTelInput.getText().toString().trim())){
			showEmptyInputToast(R.string.product_tel);
			return false;
		}
		/*if(TextUtils.isEmpty(mDatePickBtn.getText().toString().trim())){
			showEmptyInputToast(R.string.product_buy_date);
			return false;
		}
		if(TextUtils.isEmpty(mPriceInput.getText().toString().trim())){
			showEmptyInputToast(R.string.product_buy_cost);
			return false;
		}
		if(TextUtils.isEmpty(mTujingInput.getText().toString().trim())){
			showEmptyInputToast(R.string.product_buy_entry);
			return false;
		}
		if(TextUtils.isEmpty(mYanbaoTimeInput.getText().toString().trim())){
			showEmptyInputToast(R.string.product_buy_delay_time);
			return false;
		}
		if(TextUtils.isEmpty(mYanbaoComponyInput.getText().toString().trim())){
			showEmptyInputToast(R.string.product_buy_delay_componey);
			return false;
		}
		if(TextUtils.isEmpty(mYanbaoTelInput.getText().toString().trim())){
			showEmptyInputToast(R.string.product_buy_delay_componey_tel);
			return false;
		}*/
		return true;
	}

	private void showEmptyInputToast(int resId) {
		String msg = getResources().getString(resId);
		MyApplication.getInstance().showMessage(getResources().getString(R.string.input_type_please_input) + msg);
	}

	private void showDatePickerDialog() {
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mCalendar.set(year, monthOfYear, dayOfMonth);
				//更新UI
				mDatePickBtn.setText(DateUtils.TOPIC_DATE_TIME_FORMAT.format(mCalendar.getTime()));
			}
				
		}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH))
		.show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (REQUEST_BILL == requestCode) {
                if (mBillTempFile.exists()) {
                	if (mBillTempFile.exists()) {
                    	mBaoxiuCardObject.updateBillAvatorTempLocked(mBillTempFile);
                    	Drawable drawable = mBillImageView.getDrawable();
                    	mBillImageView.setImageBitmap(mBaoxiuCardObject.mBillTempBitmap);
                    	if (drawable instanceof BitmapDrawable) {
                    		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    		if (bitmap != null) {
                    			bitmap.recycle();
                    		}
                    	}
    				}
				}
                return;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
    public void setScanObjectAfterScan(InfoInterface barCodeObject) {
		 BaoxiuCardObject object = (BaoxiuCardObject) barCodeObject;
		//这里一般我们只设置品牌、型号、编号和名称
		if (!TextUtils.isEmpty(object.mLeiXin)) {
			mTypeInput.setText(object.mLeiXin);
		}
		if (!TextUtils.isEmpty(object.mPinPai)) {
			mPinpaiInput.setText(object.mPinPai);
		}
		if (!TextUtils.isEmpty(object.mSHBianHao)) {
			mBianhaoInput.setText(object.mSHBianHao);
		}
		if (!TextUtils.isEmpty(object.mXingHao)) {
			mModelInput.setText(object.mXingHao);
		}
		if (!TextUtils.isEmpty(object.mBXPhone)) {
			mBaoxiuTelInput.setText(object.mBXPhone);
		}
	}
	
	@Override
	public InfoInterface getScanObjectAfterScan() {
		return BaoxiuCardObject.getBaoxiuCardObject();
	}
	
	/**
	 * 调用相机拍摄图片
	 */
	private void onCapturePhoto() {
		if (!MyApplication.getInstance().hasExternalStorage()) {
			showDialog(DIALOG_MEDIA_UNMOUNTED);
			return;
		}
		Intent intent = null;
		if (mPictureRequest == REQUEST_AVATOR) {
			intent = ImageHelper.createCaptureIntent(Uri.fromFile(mAvatorTempFile));
		} else if (mPictureRequest == REQUEST_BILL) {
			intent = ImageHelper.createCaptureIntent(Uri.fromFile(mBillTempFile));
		}
		startActivityForResult(intent, mPictureRequest);
	}
	
	@Override
	public void updateInfoInterface(InfoInterface infoInterface) {
		if (infoInterface instanceof BaoxiuCardObject) {
			if (infoInterface != null) {
				mBid = ((BaoxiuCardObject)infoInterface).mBID;
				mAid = ((BaoxiuCardObject)infoInterface).mAID;
				mUid = ((BaoxiuCardObject)infoInterface).mUID;
			}
			populateBaoxiuInfoView((BaoxiuCardObject)infoInterface);
		} else if (infoInterface instanceof HomeObject) {
			if (infoInterface != null) {
				long aid = ((HomeObject)infoInterface).mHomeAid;
				if (aid > 0) {
					mAid = aid;
				}
			}
		} else if (infoInterface instanceof AccountObject) {
			if (infoInterface != null) {
				long uid = ((AccountObject)infoInterface).mAccountUid;
				if (uid > 0) {
					mUid = uid;
				}
			}
		}
	}
	
}
