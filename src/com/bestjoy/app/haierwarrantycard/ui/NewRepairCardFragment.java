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
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;

import com.bestjoy.app.haierwarrantycard.HaierServiceObject;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.AccountObject;
import com.bestjoy.app.haierwarrantycard.account.BaoxiuCardObject;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.bestjoy.app.haierwarrantycard.utils.SpeechRecognizerEngine;
import com.bestjoy.app.haierwarrantycard.view.ProCityDisEditPopView;
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
	private EditText mTypeInput, mPinpaiInput, mModelInput, mBianhaoInput, mBaoxiuTelInput, mTagInput;
	//联系人信息
	private EditText mContactNameInput, mContactTelInput;
	//private ProCityDisEditView mProCityDisEditView;
	private ProCityDisEditPopView mProCityDisEditPopView;
	
	//预约信息
	private TextView mYuyueDate, mYuyueTime;
	private Calendar mCalendar;
	
	private EditText mAskInput;
	//private Handler mHandler;
	private Button mSpeakButton;
	private SpeechRecognizerEngine mSpeechRecognizerEngine;
	
	private long mAid = -1;
	private long mUid = -1;
	private long mBid = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.activity_title_repair);
		mCalendar = Calendar.getInstance();
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
		 mBianhaoInput.setHint(R.string.hint_optional);
		 mBaoxiuTelInput = (EditText) view.findViewById(R.id.product_tel_input);
		 mTagInput = (EditText) view.findViewById(R.id.product_beizhu_tag);
		 
		 
		 //联系人
		 ((TextView) view.findViewById(R.id.people_info_title)).setTextColor(getResources().getColor(R.color.light_green));
		 view.findViewById(R.id.people_info_divider).setBackgroundResource(R.color.light_green);
		 mContactNameInput = (EditText) view.findViewById(R.id.contact_name_input);
		 mContactTelInput = (EditText) view.findViewById(R.id.contact_tel_input);
		 /*mProCityDisEditView = (ProCityDisEditView) view.findViewById(R.id.home);
		 //不要显示HomeName输入框
		 mProCityDisEditView.setHomeEditVisiable(View.GONE);*/
		 mProCityDisEditPopView = new ProCityDisEditPopView(this.getActivity(), view);
		 
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
		 mYuyueDate.setText(DateUtils.TOPIC_DATE_TIME_FORMAT.format(mCalendar.getTime()));
		 mYuyueTime.setText(DateUtils.TOPIC_TIME_FORMAT.format(mCalendar.getTime()));
		 mYuyueDate.setOnClickListener(this);
		 mYuyueTime.setOnClickListener(this);
		 
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	private void populateBaoxiuInfoView(BaoxiuCardObject baoxiuCardObject) {
		//init layouts
		if (baoxiuCardObject == null) {
			mTypeInput.getText().clear();
			mPinpaiInput.getText().clear();
			mModelInput.getText().clear();
			mBianhaoInput.getText().clear();
			mBaoxiuTelInput.getText().clear();
			mTagInput.getText().clear();
		} else {
			mTypeInput.setText(baoxiuCardObject.mLeiXin);
			mPinpaiInput.setText(baoxiuCardObject.mPinPai);
			mModelInput.setText(baoxiuCardObject.mXingHao);
			mBianhaoInput.setText(baoxiuCardObject.mSHBianHao);
			mBaoxiuTelInput.setText(baoxiuCardObject.mBXPhone);
			mTagInput.setText(baoxiuCardObject.mCardName);
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
		}
	}
	
	public void populateHomeInfoView(HomeObject homeObject) {
		mProCityDisEditPopView.setHomeObject(homeObject.clone());
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
		BaoxiuCardObject baoxiuCardObject = new BaoxiuCardObject();
		baoxiuCardObject.mLeiXin = mTypeInput.getText().toString().trim();
		baoxiuCardObject.mPinPai = mPinpaiInput.getText().toString().trim();
		baoxiuCardObject.mXingHao = mModelInput.getText().toString().trim();
		baoxiuCardObject.mSHBianHao = mBianhaoInput.getText().toString().trim();
		baoxiuCardObject.mBXPhone = mBaoxiuTelInput.getText().toString().trim();
		baoxiuCardObject.mCardName = mTagInput.getText().toString().trim();
		baoxiuCardObject.mAID = mAid;
		baoxiuCardObject.mUID = mUid;
		baoxiuCardObject.mBID = mBid;
		
		return baoxiuCardObject;
	}
	
	public HomeObject getHomeObject() {
		return mProCityDisEditPopView.getHomeObject();
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
			showTimePickerDialog();
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
		if(HaierAccountManager.getInstance().hasLoginned()) {
			//如果没有注册，我们前往登陆界面
			if(checkInput()) {
				createRepairCardAsync();
			}
		} else {
			//如果没有注册，我们前往登陆/注册界面，这里传递ModelBundle对象过去，以便做合适的跳转
			MyApplication.getInstance().showMessage(R.string.login_tip);
			LoginActivity.startIntent(this.getActivity(), getArguments());
		}
		
	}

	private CreateRepairCardAsyncTask mCreateRepairCardAsyncTask;
	private void createRepairCardAsync(String... param) {
		AsyncTaskUtils.cancelTask(mCreateRepairCardAsyncTask);
		showDialog(DIALOG_PROGRESS);
		mCreateRepairCardAsyncTask = new CreateRepairCardAsyncTask();
		mCreateRepairCardAsyncTask.execute(param);
	}

	private class CreateRepairCardAsyncTask extends AsyncTask<String, Void, Boolean> {
		private String mError;
		int mStatusCode = -1;
		String mStatusMessage = null;
		@Override
		protected Boolean doInBackground(String... params) {
			BaoxiuCardObject baoxiuCardObject = getBaoxiuCardObject();
			mError = null;
			InputStream is = null;
			final int LENGTH = 9;
			String[] urls = new String[LENGTH];
			String[] paths = new String[LENGTH];
			urls[0] = HaierServiceObject.SERVICE_URL + "AddYuYue.ashx?Date=";
			paths[0] = BaoxiuCardObject.BUY_DATE_TIME_FORMAT.format(mCalendar.getTime());
			urls[1] = "&Time=";
			paths[1] = BaoxiuCardObject.BUY_TIME_FORMAT.format(mCalendar.getTime());
			urls[2] = "&UID=";
			paths[2] = String.valueOf(baoxiuCardObject.mUID);
			urls[3] = "&Note=";
			paths[3] = mAskInput.getText().toString().trim();
			urls[4] = "&AID=";
			paths[4] = String.valueOf(baoxiuCardObject.mAID);
			urls[5] = "&Type=";
			paths[5] = getActivity().getString(R.string.type_repair);
			urls[6] = "&BID=";
			paths[6] = String.valueOf(baoxiuCardObject.mBID);
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
					if (mStatusCode == 1) {
						//操作成功
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
			} else if (true) {
				//预约成功
				getActivity().finish();
				MyApplication.getInstance().showMessage(R.string.msg_yuyue_sucess);
				MyChooseDevicesActivity.startIntent(getActivity(), getArguments());
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
				mYuyueDate.setText(DateUtils.TOPIC_DATE_TIME_FORMAT.format(mCalendar.getTime()));
			}
				
		}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH))
		.show();
	}
	
	private void showTimePickerDialog() {
        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				mCalendar.set(Calendar.MINUTE, minute);
				mYuyueTime.setText(DateUtils.TOPIC_TIME_FORMAT.format(mCalendar.getTime()));
			}
        	
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true)
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
			populateHomeInfoView((HomeObject)infoInterface);
		} else if (infoInterface instanceof AccountObject) {
			if (infoInterface != null) {
				long uid = ((AccountObject)infoInterface).mAccountUid;
				if (uid > 0) {
					mUid = uid;
				}
			}
			populateContactInfoView((AccountObject) infoInterface);
		}
	}
}
