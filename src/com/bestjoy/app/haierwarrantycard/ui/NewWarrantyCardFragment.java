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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.AccountObject;
import com.bestjoy.app.haierwarrantycard.account.BaoxiuCardObject;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.DateUtils;
import com.shwy.bestjoy.utils.ImageHelper;
import com.shwy.bestjoy.utils.InfoInterface;
import com.shwy.bestjoy.utils.NetworkUtils;

public class NewWarrantyCardFragment extends ModleBaseFragment implements View.OnClickListener{
	private static final String TAG = "NewWarrantyCardFragment";
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
	
	public boolean hasEditable() {
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
			if (hasEditable()) {
				//如果是已经创建了的，我们不允许修改时间，并且要使用保修卡的购买时间
				try {
					Date date = BaoxiuCardObject.BUY_DATE_TIME_FORMAT.parse(object.mBuyDate);
					mDatePickBtn.setText(DateUtils.TOPIC_DATE_TIME_FORMAT.format(date));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				 mDatePickBtn.setEnabled(false);
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
			saveNewWarrantyCardAndSync();
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

	private class CreateNewWarrantyCardAsyncTask extends AsyncTask<String, Void, Boolean> {
		private String mError;
		int mStatusCode = -1;
		String mStatusMessage = null;
		/*{
		    "StatusCode": "1", 
		    "StatusMessage": "成功返回数据", 
		    "Data": "Bid:4"
		}*/
		@Override
		protected Boolean doInBackground(String... params) {
			//更新保修卡信息
			BaoxiuCardObject baoxiuCardObject = getmBaoxiuCardObject();
			
			mError = null;
			InputStream is = null;
			final int LENGTH = 14;
			String[] urls = new String[LENGTH];
			String[] paths = new String[LENGTH];
			urls[0] = HaierServiceObject.SERVICE_URL + "AddBaoXiuData.ashx?LeiXin=";
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
			urls[6] = "&UID=";
			paths[6] = String.valueOf(baoxiuCardObject.mUID);
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
				try {
					JSONObject jsonObject = new JSONObject(NetworkUtils.getContentFromInput(is));
					mStatusCode = Integer.parseInt(jsonObject.getString("StatusCode"));
					mStatusMessage = jsonObject.getString("StatusMessage");
					DebugUtils.logD(TAG, "StatusCode = " + mStatusCode);
					DebugUtils.logD(TAG, "StatusMessage = " + mStatusMessage);
					if (mStatusCode == 1) {
						//在保存前，我们需要回填bid数据
						String data = jsonObject.getString("Data");
						DebugUtils.logD(TAG, "Data = " + data);
						if (data.length() > "Bid:".length()) {
							data = data.substring("Bid:".length());
							baoxiuCardObject.mBID = Long.valueOf(data);
							//如果后台返回了bid,我们根据它向服务器查询保修卡数据，并解析保存在本地。
							if (baoxiuCardObject.mBID > 0) {
								NetworkUtils.closeInputStream(is);
								StringBuilder sb = new StringBuilder(HaierServiceObject.SERVICE_URL);
								sb.append("GetBaoXiuDataByBID.ashx?BID=").append(baoxiuCardObject.mBID);
								is = NetworkUtils.openContectionLocked(sb.toString(), MyApplication.getInstance().getSecurityKeyValuesObject());
								if (is != null){
									jsonObject = new JSONObject(NetworkUtils.getContentFromInput(is));
									mStatusCode = Integer.parseInt(jsonObject.getString("StatusCode"));
									mStatusMessage = jsonObject.getString("StatusMessage");
									if (mStatusCode == 1) {
										baoxiuCardObject = BaoxiuCardObject.parseBaoxiuCards(jsonObject.getJSONObject("Data"), null);
										boolean savedOk = baoxiuCardObject.saveInDatebase(getActivity().getContentResolver(), null);
										if (!savedOk) {
											//通常不会发生
											mError = getActivity().getString(R.string.msg_local_save_card_failed);
										} else {
											HaierAccountManager.getInstance().updateHomeObject(baoxiuCardObject.mAID);
										}
									}
									
								}
							}
						}
						return true;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				mError = e.getMessage();
			} catch (IOException e) {
				e.printStackTrace();
				mError = e.getMessage();
			} finally {
				NetworkUtils.closeInputStream(is);
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mSaveBtn.setEnabled(true);
			dissmissDialog(DIALOG_PROGRESS);
			if (mError != null) {
				if (result) {
					//服务器上传信息成功，但本地保存失败，请重新登录同步数据
					new AlertDialog.Builder(getActivity())
					.setTitle(R.string.msg_tip_title)
		   			.setMessage(mError)
		   			.setCancelable(false)
		   			.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
		   				@Override
		   				public void onClick(DialogInterface dialog, int which) {
		   					LoginActivity.startIntent(getActivity(), null);
		   				}
		   			})
		   			.create()
		   			.show();
				} else {
					MyApplication.getInstance().showMessage(mError);
				}
			} else if (result) {
				//添加成功
				MyApplication.getInstance().showMessage(R.string.save_success);
				getActivity().finish();
				MyChooseDevicesActivity.startIntent(getActivity(), getArguments());
			} else {
				MyApplication.getInstance().showMessage(mStatusMessage);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dissmissDialog(DIALOG_PROGRESS);
			mSaveBtn.setEnabled(true);
		}
	}

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
                    	mBillImageView.setImageBitmap(mBaoxiuCardObject.mBillTempBitmap);
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
