package com.bestjoy.app.haierwarrantycard.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import com.bestjoy.app.haierwarrantycard.ui.model.ModleSettings;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.DateUtils;
import com.shwy.bestjoy.utils.ImageHelper;
import com.shwy.bestjoy.utils.InfoInterface;
import com.shwy.bestjoy.utils.NetworkUtils;

public class NewWarrantyCardFragment extends ModleBaseFragment implements View.OnClickListener{
	private static final String TAG = "NewWarrantyCardFragment";
	private BaoxiuCardObject mBaoxiuCardObject;
	private HomeObject mHomeObject;
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
	private static final int DIALOG_BILL_OP_CONFIRM = 4;
	/**显示操作选项*/
	private static final int DIALOG_OPERATION = 5;
	/**显示进度对话框*/
	private static final int DIALOG_PROGRESS = 6;
	
	private int mPictureRequest = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mCalendar = Calendar.getInstance();
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
	
	private void populateBaoxiuInfoView(BaoxiuCardObject object) {
		//init layouts
		mBaoxiuCardObject = object;
		if (mBaoxiuCardObject == null) {
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
			mTypeInput.setText(mBaoxiuCardObject.mLeiXin);
			mPinpaiInput.setText(mBaoxiuCardObject.mPinPai);
			mModelInput.setText(mBaoxiuCardObject.mXingHao);
			mBianhaoInput.setText(mBaoxiuCardObject.mSHBianHao);
			
			mBaoxiuTelInput.setText(mBaoxiuCardObject.mBXPhone);
			mPriceInput.setText(mBaoxiuCardObject.mBuyPrice);
			mTujingInput.setText(mBaoxiuCardObject.mBuyTuJing);
			mYanbaoTimeInput.setText(mBaoxiuCardObject.mYanBaoTime);
			mYanbaoComponyInput.setText(mBaoxiuCardObject.mYanBaoDanWei);
			mYanbaoTelInput.setText(mBaoxiuCardObject.mYBPhone);
			mTagInput.setText(mBaoxiuCardObject.mCardName);
		}
		
	}
	
	public void setBaoxiuObjectAfterSlideMenu(InfoInterface slideManuObject) {
		if (mBaoxiuCardObject == null) {
			mBaoxiuCardObject = new BaoxiuCardObject();
		}
		if (slideManuObject instanceof BaoxiuCardObject) {
			BaoxiuCardObject object = (BaoxiuCardObject) slideManuObject;
			if (!TextUtils.isEmpty(object.mLeiXin)) {
				mBaoxiuCardObject.mLeiXin = object.mLeiXin;
				mTypeInput.setText(mBaoxiuCardObject.mLeiXin);
			}
			if (!TextUtils.isEmpty(object.mPinPai)) {
				mBaoxiuCardObject.mPinPai = object.mPinPai;
				mPinpaiInput.setText(mBaoxiuCardObject.mPinPai);
			}
			
			if (!TextUtils.isEmpty(object.mXingHao)) {
				mBaoxiuCardObject.mXingHao = object.mXingHao;
				mModelInput.setText(mBaoxiuCardObject.mXingHao);
			}
			
			if (!TextUtils.isEmpty(object.mSHBianHao)) {
				mBaoxiuCardObject.mSHBianHao = object.mSHBianHao;
				mBianhaoInput.setText(mBaoxiuCardObject.mSHBianHao);
			}
		}
	}
	
	private void getmBaoxiuCardObject() {
		if(mBaoxiuCardObject == null) {
			mBaoxiuCardObject = new BaoxiuCardObject();
		}
		mBaoxiuCardObject.mLeiXin = mTypeInput.getText().toString().trim();
		mBaoxiuCardObject.mPinPai = mPinpaiInput.getText().toString().trim();
		mBaoxiuCardObject.mXingHao = mModelInput.getText().toString().trim();
		mBaoxiuCardObject.mSHBianHao = mBianhaoInput.getText().toString().trim();
		mBaoxiuCardObject.mBXPhone = mBaoxiuTelInput.getText().toString().trim();
		
		mBaoxiuCardObject.mBuyDate = mDatePickBtn.getText().toString().trim();
		mBaoxiuCardObject.mBuyPrice = mPriceInput.getText().toString().trim();
		mBaoxiuCardObject.mBuyTuJing = mTujingInput.getText().toString().trim();
		
		mBaoxiuCardObject.mYanBaoTime = mYanbaoTimeInput.getText().toString().trim();
		mBaoxiuCardObject.mYanBaoDanWei = mYanbaoComponyInput.getText().toString().trim();
		mBaoxiuCardObject.mYBPhone = mYanbaoTelInput.getText().toString().trim();
		
		mBaoxiuCardObject.mCardName = mTagInput.getText().toString().trim();
		
		//设置所属家aid
		if (mHomeObject != null) {
			mBaoxiuCardObject.mAID = mHomeObject.mHomeAid;
		}
		//设置所属账户uid
		AccountObject accountObject = HaierAccountManager.getInstance().getAccountObject();
		if (accountObject != null) {
			mBaoxiuCardObject.mUID = accountObject.mAccountUid;
		}
		DebugUtils.logD(TAG, "getmBaoxiuCardObject() " + mBaoxiuCardObject.mBID);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_scan_bill:
//			if (mGoodsObject != null && !mGoodsObject.hasBill()) {
//				//如果没有发票，我们直接调用相机
//				mPictureRequest = REQUEST_BILL;
//				onCapturePhoto();
//			} else {
//				//如果有，我们显示操作选项，查看或是拍摄发票
//				showDialog(DIALOG_BILL_OP_CONFIRM);
//			}
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
			saveNewWarrantyCardAndSync();
			break;
		}
		
	}
	
	private void saveNewWarrantyCardAndSync() {
		if(HaierAccountManager.getInstance().hasLoginned()) {
			//如果没有注册，我们前往登陆界面
			if(checkInput()) {
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
		@Override
		protected Boolean doInBackground(String... params) {
			//更新保修卡信息
			getmBaoxiuCardObject();
			
			mError = null;
			InputStream is = null;
			final int LENGTH = 9;
			String[] urls = new String[LENGTH];
			String[] paths = new String[LENGTH];
			urls[0] = HaierServiceObject.SERVICE_URL + "AddBaoXiuData.ashx?LeiXin=";
			paths[0] = mBaoxiuCardObject.mLeiXin;
			urls[1] = "&BuyDate=";
			paths[1] = mBaoxiuCardObject.mBuyDate;
			urls[2] = "&BuyPrice=";
			paths[2] = mBaoxiuCardObject.mBuyPrice;
			urls[3] = "&BuyTuJing=";
			paths[3] = mBaoxiuCardObject.mBuyTuJing;
			urls[4] = "&BXPhone=";
			paths[4] = mBaoxiuCardObject.mBXPhone;
			urls[5] = "&PinPai=";
			paths[5] = mBaoxiuCardObject.mPinPai;
			urls[6] = "&UID=";
			paths[6] = String.valueOf(mBaoxiuCardObject.mUID);
			urls[7] = "&XingHao=";
			paths[7] = mBaoxiuCardObject.mXingHao;
			urls[8] = "&YanBaoDanWei=";
			paths[8] = mBaoxiuCardObject.mYanBaoDanWei;
			urls[9] = "&YanBaoTime=";
			paths[9] = mBaoxiuCardObject.mYanBaoTime;
			urls[10] = "&AID=";
			paths[10] = String.valueOf(mBaoxiuCardObject.mAID);
			urls[11] = "&SHBianHao=";
			paths[11] = mBaoxiuCardObject.mSHBianHao;
			urls[12] = "&Tag=";
			paths[12] = mBaoxiuCardObject.mCardName;
			urls[13] = "&YBPhone=";
			paths[13] = mBaoxiuCardObject.mYBPhone;
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
						boolean savedOk = mBaoxiuCardObject.saveInDatebase(getActivity().getContentResolver(), null);
						if (!savedOk) {
							//通常不会发生
							mError = getActivity().getString(R.string.msg_local_save_card_failed);
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
				NewWarrantyCardFragment.this.getActivity().finish();
				MyChooseDevicesActivity.startIntent(NewWarrantyCardFragment.this.getActivity(), ModleSettings.createMyCardDefaultBundle(NewWarrantyCardFragment.this.getActivity()));
			} else {
				MyApplication.getInstance().showMessage(mStatusMessage);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dissmissDialog(DIALOG_PROGRESS);
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
		/*if(TextUtils.isEmpty(mModelInput.getText().toString().trim())){
			showEmptyInputToast(R.string.product_model);
			return false;
		}*/
		if(TextUtils.isEmpty(mBianhaoInput.getText().toString().trim())){
			showEmptyInputToast(R.string.product_sn);
			return false;
		}
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
				//更新日期数据
//				mGoodsObject.mDate = mCalendar.getTimeInMillis();
				//更新UI
				mDatePickBtn.setText(DateUtils.TOPIC_DATE_TIME_FORMAT.format(new Date(mCalendar.getTimeInMillis())));
			}
				
		}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH))
		.show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (REQUEST_BILL == requestCode) {
                if (mBillTempFile.exists()) {
//                	mGoodsObject.updateBillAvatorTempLocked(mBillTempFile);
//                	mBillImageView.setImageBitmap(mGoodsObject.mBillTempBitmap);
				}
			}
		}
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
			populateBaoxiuInfoView((BaoxiuCardObject)infoInterface);
		} else if (infoInterface instanceof HomeObject) {
			mHomeObject = (HomeObject) infoInterface;
		} else if (infoInterface instanceof AccountObject) {
			//do nothing
		}
	}
	
}
