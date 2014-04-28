package com.bestjoy.app.haierwarrantycard.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.bestjoy.app.haierwarrantycard.HaierServiceObject;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.AccountObject;
import com.bestjoy.app.haierwarrantycard.account.BaoxiuCardObject;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.bestjoy.app.haierwarrantycard.utils.SpeechRecognizerEngine;
import com.bestjoy.app.haierwarrantycard.view.ProCityDisEditView;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.DateUtils;
import com.shwy.bestjoy.utils.InfoInterface;
import com.shwy.bestjoy.utils.NetworkUtils;

public class NewRepairCardFragment extends ModleBaseFragment implements View.OnClickListener{
	private static final String TAG = "NewRepairCardFragment";
	//按钮
	private Button mSaveBtn;
	//商品信息
	private EditText mTypeInput, mPinpaiInput, mModelInput, mBianhaoInput, mBaoxiuTelInput;
	//联系人信息
	private EditText mContactNameInput, mContactTelInput;
	private ProCityDisEditView mProCityDisEditView;
	
	//预约信息
	private TextView mYuyueDate, mYuyueTime;
	private Calendar mCalendar;
	
	private EditText mAskInput;
	//private Handler mHandler;
	private Button mSpeakButton;
	private SpeechRecognizerEngine mSpeechRecognizerEngine;
	
	private BaoxiuCardObject mBaoxiuCardObject;
	private AccountObject mAccountObject;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.activity_title_repair);
		mCalendar = Calendar.getInstance();
		mAccountObject = HaierAccountManager.getInstance().getAccountObject();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 View view = inflater.inflate(R.layout.activity_repair_20140418, container, false);
		 //条码识别
		 view.findViewById(R.id.button_scan_qrcode).setOnClickListener(this);
		 
		 //商品信息
		 mTypeInput = (EditText) view.findViewById(R.id.product_type_input);
		 mPinpaiInput = (EditText) view.findViewById(R.id.product_brand_input);
		 mModelInput = (EditText) view.findViewById(R.id.product_model_input);
		 mBianhaoInput = (EditText) view.findViewById(R.id.product_sn_input);
		 mBaoxiuTelInput = (EditText) view.findViewById(R.id.product_tel_input);
		 
		 //联系人
		 ((TextView) view.findViewById(R.id.people_info_title)).setTextColor(getResources().getColor(R.color.light_green));
		 view.findViewById(R.id.people_info_divider).setBackgroundResource(R.color.light_green);
		 mContactNameInput = (EditText) view.findViewById(R.id.contact_name_input);
		 mContactTelInput = (EditText) view.findViewById(R.id.contact_tel_input);
		 mProCityDisEditView = (ProCityDisEditView) view.findViewById(R.id.home);
		 //不要显示HomeName输入框
		 mProCityDisEditView.setHomeEditVisiable(View.GONE);
		 
		 //语音
		 mAskInput = (EditText) view.findViewById(R.id.product_ask_online_input);
		 mSpeakButton =  (Button) view.findViewById(R.id.button_speak);
		 mSpeakButton.setOnClickListener(this);
		 mSpeechRecognizerEngine = SpeechRecognizerEngine.getInstance(getActivity());
		 mSpeechRecognizerEngine.setResultText(mAskInput);
		 
		 mSaveBtn = (Button) view.findViewById(R.id.button_save);
		 mSaveBtn.setOnClickListener(this);

		 //预约时间
		 ((TextView) view.findViewById(R.id.yuyue_info_title)).setTextColor(getResources().getColor(R.color.light_green));
		 view.findViewById(R.id.yuyue_info_divider).setBackgroundResource(R.color.light_green);
		 mYuyueDate = (TextView) view.findViewById(R.id.date);
		 mYuyueTime = (TextView) view.findViewById(R.id.time);
		 mYuyueDate.setOnClickListener(this);
		 mYuyueTime.setOnClickListener(this);
		 
		 mBaoxiuCardObject = new BaoxiuCardObject();
		 
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	private void populateBaoxiuInfoView(BaoxiuCardObject baoxiuCardObject) {
		//init layouts
		mBaoxiuCardObject = baoxiuCardObject;
		if (mBaoxiuCardObject == null) {
			mTypeInput.getText().clear();
			mPinpaiInput.getText().clear();
			mModelInput.getText().clear();
			mBianhaoInput.getText().clear();
			mBaoxiuTelInput.getText().clear();
		} else {
			mTypeInput.setText(mBaoxiuCardObject.mLeiXin);
			mPinpaiInput.setText(mBaoxiuCardObject.mPinPai);
			mModelInput.setText(mBaoxiuCardObject.mXingHao);
			mBianhaoInput.setText(mBaoxiuCardObject.mSHBianHao);
			mBaoxiuTelInput.setText(mBaoxiuCardObject.mBXPhone);
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
	
	public void populateHomeInfoView(HomeObject homeObject) {
		mProCityDisEditView.setHomeObject(homeObject);
	}
	
    public void populateContactInfoView(AccountObject accountObject) {
    	if(accountObject == null) {
			mContactNameInput.getText().clear();
			mContactTelInput.getText().clear();
		} else {
			mContactNameInput.setText(accountObject.mAccountName);
			mContactTelInput.setText(accountObject.mAccountTel);
			
		}
	}
	
	public BaoxiuCardObject getBaoxiuCardObject() {
		if (mBaoxiuCardObject == null) {
			mBaoxiuCardObject = new BaoxiuCardObject();
		}
		mBaoxiuCardObject.mLeiXin = mTypeInput.getText().toString().trim();
		mBaoxiuCardObject.mPinPai = mPinpaiInput.getText().toString().trim();
		mBaoxiuCardObject.mXingHao = mModelInput.getText().toString().trim();
		mBaoxiuCardObject.mSHBianHao = mBianhaoInput.getText().toString().trim();
		mBaoxiuCardObject.mBXPhone = mBaoxiuTelInput.getText().toString().trim();
		return mBaoxiuCardObject;
	}
	
	public HomeObject getHomeObject() {
		return mProCityDisEditView.getHomeObject();
	}
	

	public AccountObject getContactInfoObject() {
		AccountObject contactInfoObject = new AccountObject();
		contactInfoObject.mAccountName = mContactNameInput.getText().toString().trim();
		contactInfoObject.mAccountTel = mContactTelInput.getText().toString().trim();
		return contactInfoObject;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_scan_qrcode:
			startScan();
			break;
		case R.id.date:
			showDatePickerDialog();
			break;
		case R.id.time:
			break;
		case R.id.button_speak:
			mSpeechRecognizerEngine.showIatDialog(getActivity());
			break;
		case R.id.button_save:
			createRepairCard();
			break;
		}
		
	}

	private void createRepairCard() {
		if(checkInput()) {
			if(HaierAccountManager.getInstance().hasLoginned()) {
				updateRepairCardInfo();
				createRepairCardAsync();
			} else {
				MyApplication.getInstance().showMessage(R.string.msg_yuyue_fail);
				LoginActivity.startIntent(this.getActivity(), getArguments());
			}
		}
		
	}

	private CreateRepairCardAsyncTask mCreateRepairCardAsyncTask;
	private void createRepairCardAsync(String... param) {
		AsyncTaskUtils.cancelTask(mCreateRepairCardAsyncTask);
		showDialog(DIALOG_PROGRESS);
		mCreateRepairCardAsyncTask = new CreateRepairCardAsyncTask();
		mCreateRepairCardAsyncTask.execute(param);
	}

	private class CreateRepairCardAsyncTask extends AsyncTask<String, Void, Void> {
		private String mError;
		int mStatusCode = -1;
		String mStatusMessage = null;
		@Override
		protected Void doInBackground(String... params) {
			mError = null;
			InputStream is = null;
			final int LENGTH = 9;
			String[] urls = new String[LENGTH];
			String[] paths = new String[LENGTH];
			urls[0] = HaierServiceObject.SERVICE_URL + "AddYuYue.ashx?Date=";
			paths[0] = mYuyueDate.getText().toString().trim();
			urls[1] = "&Time=";
			paths[1] = mYuyueTime.getText().toString().trim();
			urls[2] = "&UID=";
			paths[2] = String.valueOf(mBaoxiuCardObject.mUID);
			urls[3] = "&Note=";
			paths[3] = mAskInput.getText().toString().trim();
			urls[4] = "&AID=";
			paths[4] = String.valueOf(mBaoxiuCardObject.mAID);
			urls[5] = "&Type=";
			paths[5] = "维修";
			urls[6] = "&BID=";
			paths[6] = String.valueOf(mBaoxiuCardObject.mBID);
			urls[7] = "&UserName=";
			paths[7] = mContactNameInput.getText().toString().trim();
			urls[8] = "&CellPhone=";
			paths[8] = mContactTelInput.getText().toString().trim();
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
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			getProgressDialog().dismiss();
			if (mError != null) {
				MyApplication.getInstance().showMessage(mError);
			} else if (mStatusCode == 1) {
				//预约成功
				NewRepairCardFragment.this.getActivity().finish();
			} else {
				//预约失败
				new AlertDialog.Builder(NewRepairCardFragment.this.getActivity())
				.setTitle(R.string.msg_tip_title)
	   			.setMessage(R.string.msg_yuyue_fail)
	   			.setCancelable(false)
	   			.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
	   				@Override
	   				public void onClick(DialogInterface dialog, int which) {
	   				}
	   			})
	   			.create()
	   			.show();
			}
			MyApplication.getInstance().showMessage(mStatusMessage);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			getProgressDialog().dismiss();
		}
	}
	
	private void updateRepairCardInfo() {
		if(mBaoxiuCardObject == null) {
			mBaoxiuCardObject = new BaoxiuCardObject();
		}
		mBaoxiuCardObject.mLeiXin = mTypeInput.getText().toString().trim();
		mBaoxiuCardObject.mPinPai = mPinpaiInput.getText().toString().trim();
		mBaoxiuCardObject.mXingHao = mModelInput.getText().toString().trim();
		mBaoxiuCardObject.mSHBianHao = mBianhaoInput.getText().toString().trim();
		mBaoxiuCardObject.mBXPhone = mBaoxiuTelInput.getText().toString().trim();
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
		/*if(TextUtils.isEmpty(mBianhaoInput.getText().toString().trim())){
			showEmptyInputToast(R.string.product_sn);
			return false;
		}*/
		if(TextUtils.isEmpty(mBaoxiuTelInput.getText().toString().trim())){
			showEmptyInputToast(R.string.product_tel);
			return false;
		}
		

		if(TextUtils.isEmpty(mContactNameInput.getText().toString().trim())){
			showEmptyInputToast(R.string.name);
			return false;
		}
		if(TextUtils.isEmpty(mContactTelInput.getText().toString().trim())){
			showEmptyInputToast(R.string.usr_tel);
			return false;
		}
		
		if(TextUtils.isEmpty(mYuyueDate.getText().toString().trim())){
			showEmptyInputToast(R.string.date);
			return false;
		}
		if(TextUtils.isEmpty(mYuyueTime.getText().toString().trim())){
			showEmptyInputToast(R.string.time);
			return false;
		}
		if(TextUtils.isEmpty(mAskInput.getText().toString().trim())){
			showEmptyInputToast(R.string.error_des);
			return false;
		}
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
				mYuyueDate.setText(DateUtils.TOPIC_DATE_TIME_FORMAT.format(new Date(mCalendar.getTimeInMillis())));
			}
				
		}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH))
		.show();
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
	@Override
	public void updateInfoInterface(InfoInterface infoInterface) {
		if (infoInterface instanceof BaoxiuCardObject) {
			populateBaoxiuInfoView((BaoxiuCardObject)infoInterface);
		} else if (infoInterface instanceof HomeObject) {
			populateHomeInfoView((HomeObject)infoInterface);
		} else if (infoInterface instanceof AccountObject) {
			populateContactInfoView((AccountObject) infoInterface);
		}
	}
}
