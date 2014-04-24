package com.bestjoy.app.haierwarrantycard.ui;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.goods.GoodsObject;
import com.shwy.bestjoy.utils.DateUtils;
import com.shwy.bestjoy.utils.ImageHelper;
import com.shwy.bestjoy.utils.Intents;

public class NewWarrantyCardFragment extends BaseFragment implements View.OnClickListener{
	
	private GoodsObject mGoodsObject;
	//按钮
	private Button mSaveBtn;
	private TextView mDatePickBtn;
	private ImageView mBillImageView;
	
	/**是否保存完后打开我的家，这主要是条码识别会这样做*/
	private boolean mIsOpenHomeWhenSave = false;
	private Calendar mCalendar;
	//临时的拍摄照片路径
	private File mBillTempFile, mAvatorTempFile;
	/**请求扫描条码*/
	private static final int REQUEST_SCAN = 1;
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
	
	public void setGoodsObject(GoodsObject goodsObject) {
		mGoodsObject = goodsObject;
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 View view = inflater.inflate(R.layout.activity_new_card, container, false);
		initView(view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	private void initView(View view) {
		//init layouts
		if (mSaveBtn == null) {
		
			mBillImageView = (ImageView) view.findViewById(R.id.button_scan_bill);
			mBillImageView.setOnClickListener(this);
			
			mDatePickBtn = (TextView) view.findViewById(R.id.product_buy_date);
			mDatePickBtn.setOnClickListener(this);
			mSaveBtn = (Button) view.findViewById(R.id.button_save);
			mSaveBtn.setOnClickListener(this);
			
			view.findViewById(R.id.button_scan_qrcode).setOnClickListener(this);
			
		}
		
//		mDatePickBtn.setText(DateUtils.TOPIC_DATE_TIME_FORMAT.format(new Date(mGoodsObject.mDate)));
		
	}
	
	public Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_PROGRESS:
			ProgressDialog dialog = new ProgressDialog(getActivity());
			dialog.setMessage(getString(R.string.msg_progressdialog_wait));
			dialog.setCancelable(false);
			dialog.show();
			return dialog;
		case DIALOG_BILL_OP_CONFIRM:
			return new AlertDialog.Builder(getActivity())
			.setItems(this.getResources().getStringArray(R.array.bill_op_items), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch(which) {
					case 0:
//						GoodsManager.showBill(mContext, mGoodsObject);
						break;
					case 1:
						mPictureRequest = REQUEST_BILL;
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

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_scan_bill:
			if (mGoodsObject != null && !mGoodsObject.hasBill()) {
				//如果没有发票，我们直接调用相机
				mPictureRequest = REQUEST_BILL;
				onCapturePhoto();
			} else {
				//如果有，我们显示操作选项，查看或是拍摄发票
				showDialog(DIALOG_BILL_OP_CONFIRM);
			}
			break;
		case R.id.button_scan_qrcode:
			Intent scanIntent = new Intent(getActivity(), CaptureActivity.class);
			scanIntent.putExtra(Intents.EXTRA_SCAN_TASK, true);
			startActivityForResult(scanIntent, REQUEST_SCAN);
			break;
		case R.id.product_buy_date:
			showDatePickerDialog();
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (REQUEST_SCAN == requestCode) {
				//识别到了商品信息
//				GoodsObject scanGoodsObject = GoodsObject.extractIntent(data);
//				mGoodsObject.mKY = scanGoodsObject.mKY;
//				mGoodsObject.mWY = scanGoodsObject.mWY;
//				mGoodsObject.mProductBrand = scanGoodsObject.mProductBrand;
//				mGoodsObject.mSN = scanGoodsObject.mSN;
//				
//				mGoodsObject.mProductModel = scanGoodsObject.mProductModel;
//				mGoodsObject.mProductName = scanGoodsObject.mProductName;
//				mGoodsObject.mProductTel = scanGoodsObject.mProductTel;
				
//				mSaleGoodsInfoText.setText(scanGoodsObject.toGoodsInfoString());
//				//滚动到最低端以便显示完整的商品信息
//				mScrollView.post(new Runnable() {   
//				    public void run() {  
//				        mScrollView.scrollTo(0, 1000);  
//				    }   
//				});  
				
			} else if (REQUEST_BILL == requestCode) {
                if (mBillTempFile.exists()) {
//                	mGoodsObject.updateBillAvatorTempLocked(mBillTempFile);
//                	mBillImageView.setImageBitmap(mGoodsObject.mBillTempBitmap);
				}
			}
		}
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
	
}
