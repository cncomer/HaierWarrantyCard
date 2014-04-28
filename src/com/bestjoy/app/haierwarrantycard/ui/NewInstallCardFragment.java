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
import com.bestjoy.app.haierwarrantycard.view.ProCityDisEditView;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.DateUtils;
import com.shwy.bestjoy.utils.InfoInterface;
import com.shwy.bestjoy.utils.NetworkUtils;

public class NewInstallCardFragment extends ModleBaseFragment implements View.OnClickListener{
	private static final String TAG = "NewInstallCardFragment";
	//按钮
	private Button mSaveBtn;
	//商品信息
	private EditText mTypeInput, mPinpaiInput, mModelInput, mBianhaoInput, mBaoxiuTelInput, mBeizhuTag;
	//联系人信息
	private EditText mContactNameInput, mContactTelInput;
	private ProCityDisEditView mProCityDisEditView;
	
	//预约信息
	private TextView mYuyueDate, mYuyueTime;
	private Calendar mCalendar;
	
	private BaoxiuCardObject mBaoxiuCardObject;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.activity_title_install);
		mCalendar = Calendar.getInstance();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 View view = inflater.inflate(R.layout.activity_install_20140418, container, false);
		 
		 mTypeInput = (EditText) view.findViewById(R.id.product_type_input);
		 mPinpaiInput = (EditText) view.findViewById(R.id.product_brand_input);
		 mModelInput = (EditText) view.findViewById(R.id.product_model_input);
		 mBianhaoInput = (EditText) view.findViewById(R.id.product_sn_input);
		 mBaoxiuTelInput = (EditText) view.findViewById(R.id.product_tel_input);
		 mBeizhuTag = (EditText) view.findViewById(R.id.product_beizhu_tag);
		 
		 //联系人
		 ((TextView) view.findViewById(R.id.people_info_title)).setTextColor(getResources().getColor(R.color.light_blue));
		 view.findViewById(R.id.people_info_divider).setBackgroundResource(R.color.light_blue);
		 mContactNameInput = (EditText) view.findViewById(R.id.contact_name_input);
		 mContactTelInput = (EditText) view.findViewById(R.id.contact_tel_input);
		 mProCityDisEditView = (ProCityDisEditView) view.findViewById(R.id.home);
		 //不要显示HomeName输入框
		 mProCityDisEditView.setHomeEditVisiable(View.GONE);
		 
		 //预约时间
		 ((TextView) view.findViewById(R.id.yuyue_info_title)).setTextColor(getResources().getColor(R.color.light_blue));
		 view.findViewById(R.id.yuyue_info_divider).setBackgroundResource(R.color.light_blue);
		 mYuyueDate = (TextView) view.findViewById(R.id.date);
		 mYuyueTime = (TextView) view.findViewById(R.id.time);
		 mYuyueDate.setOnClickListener(this);
		 mYuyueTime.setOnClickListener(this);
		 
		 mSaveBtn = (Button) view.findViewById(R.id.button_save);
		 mSaveBtn.setOnClickListener(this);
			
		view.findViewById(R.id.button_scan_qrcode).setOnClickListener(this);
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
			mContactNameInput.getText().clear();
			mContactTelInput.getText().clear();
		} else {
			mTypeInput.setText(mBaoxiuCardObject.mLeiXin);
			mPinpaiInput.setText(mBaoxiuCardObject.mPinPai);
			mModelInput.setText(mBaoxiuCardObject.mXingHao);
			mBianhaoInput.setText(mBaoxiuCardObject.mSHBianHao);
			mBaoxiuTelInput.setText(mBaoxiuCardObject.mBXPhone);
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
			showTimePickerDialog();
			break;
		case R.id.button_save:
			createNewInatallCard();
			break;
		}
		
	}
	
	private void createNewInatallCard() {
		if(checkInput()) {
			if(HaierAccountManager.getInstance().hasLoginned()) {
				updateNewInstallCardInfo();
				createNewInatallCardAsync();
			} else {
				MyApplication.getInstance().showMessage(R.string.msg_yuyue_fail);
				LoginActivity.startIntent(this.getActivity(), getArguments());
			}
		}
		
	}

	private CreateNewInatallCardAsyncTask mCreateNewInatallCardAsyncTask;
	private void createNewInatallCardAsync(String... param) {
		AsyncTaskUtils.cancelTask(mCreateNewInatallCardAsyncTask);
		showDialog(DIALOG_PROGRESS);
		mCreateNewInatallCardAsyncTask = new CreateNewInatallCardAsyncTask();
		mCreateNewInatallCardAsyncTask.execute(param);
	}

	private class CreateNewInatallCardAsyncTask extends AsyncTask<String, Void, Void> {
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
			paths[3] = mBeizhuTag.getText().toString().trim();
			urls[4] = "&AID=";
			paths[4] = String.valueOf(mBaoxiuCardObject.mAID);
			urls[5] = "&Type=";
			paths[5] = "安装";
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
				NewInstallCardFragment.this.getActivity().finish();
			} else {
				//预约失败
				new AlertDialog.Builder(NewInstallCardFragment.this.getActivity())
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
	
	private void updateNewInstallCardInfo() {
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
	
	private void showTimePickerDialog() {
        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
				mYuyueTime.setText(DateUtils.TOPIC_TIME_FORMAT.format(new Date(mCalendar.getTimeInMillis())));
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
			populateBaoxiuInfoView((BaoxiuCardObject)infoInterface);
		} else if (infoInterface instanceof HomeObject) {
			populateHomeInfoView((HomeObject)infoInterface);
		} else if (infoInterface instanceof AccountObject) {
			populateContactInfoView((AccountObject) infoInterface);
		}
	}
}
