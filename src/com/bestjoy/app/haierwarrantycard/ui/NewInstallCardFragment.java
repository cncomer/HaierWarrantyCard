package com.bestjoy.app.haierwarrantycard.ui;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.BaoxiuCardObject;
import com.bestjoy.app.haierwarrantycard.view.ProCityDisEditView;
import com.shwy.bestjoy.utils.DateUtils;
import com.shwy.bestjoy.utils.InfoInterface;

public class NewInstallCardFragment extends ModleBaseFragment implements View.OnClickListener{
	
	//按钮
	private Button mSaveBtn;
	private TextView mDatePickBtn;
	//商品信息
	private EditText mTypeInput, mPinpaiInput, mModelInput, mBianhaoInput, mBaoxiuTelInput;
	//联系人信息
	private EditText mContactNameInput, mContactTelInput;
	private ProCityDisEditView mProCityDisEditView;
	
	//预约信息
	private TextView mYuyueDate, mYuyueTime;
	private Calendar mCalendar;
	
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
		if (baoxiuCardObject == null) {
			mTypeInput.getText().clear();
			mPinpaiInput.getText().clear();
			mModelInput.getText().clear();
			mBianhaoInput.getText().clear();
			mBaoxiuTelInput.getText().clear();
			mContactNameInput.getText().clear();
			mContactTelInput.getText().clear();
		} else {
			mTypeInput.setText(baoxiuCardObject.mLeiXin);
			mPinpaiInput.setText(baoxiuCardObject.mPinPai);
			mModelInput.setText(baoxiuCardObject.mXingHao);
			mBianhaoInput.setText(baoxiuCardObject.mSHBianHao);
			mBaoxiuTelInput.setText(baoxiuCardObject.mBXPhone);
		}
		
	}
	
	public BaoxiuCardObject getmBaoxiuCardObject() {
		return null;
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
		}
		
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
		}
	}
}
