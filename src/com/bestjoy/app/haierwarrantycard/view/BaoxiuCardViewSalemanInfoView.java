package com.bestjoy.app.haierwarrantycard.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.service.PhotoManagerUtilsV2;
import com.bestjoy.app.haierwarrantycard.ui.CaptureActivity;
import com.bestjoy.app.haierwarrantycard.ui.CardViewActivity;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;

public class BaoxiuCardViewSalemanInfoView extends RelativeLayout implements View.OnClickListener{

	private static final String TAG = "BaoxiuCardViewSalemanInfoView";
	private View mActionsLayout;
	private ImageView mAvator;
	private TextView mName, mTitle;
	
	private ValueAnimator mAnim;
	private Handler mHandler;
	private CardViewActivity mActivity;
	
	private AddressBookParsedResult mAddressBookParsedResult;
	public BaoxiuCardViewSalemanInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) {
			return;
		}
		mActivity = (CardViewActivity) context;
	}
	
	private Runnable mHideActionRunnable =  new Runnable() {

		@Override
		public void run() {
			mActionsLayout.setVisibility(View.INVISIBLE);
		}
		
	};
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (isInEditMode()) {
			return;
		}
		mActionsLayout = findViewById(R.id.actions_layout);
		mActionsLayout.setVisibility(View.INVISIBLE);
		
		mAvator = (ImageView) findViewById(R.id.avator);
		mAvator.setOnClickListener(this);
		mName = (TextView) findViewById(R.id.name);
		mName.setText("");
		mTitle = (TextView) findViewById(R.id.title); 
		mHandler = new Handler();
		
		findViewById(R.id.button_call).setOnClickListener(this);
		findViewById(R.id.button_info).setOnClickListener(this);
	}
	
	public void setAddressBookParsedResult(AddressBookParsedResult result, String token) {
		mAddressBookParsedResult = result;
		if (mAddressBookParsedResult != null) {
			mName.setText(mAddressBookParsedResult.getFirstName());
			PhotoManagerUtilsV2.getInstance().loadPhotoAsync(token, mAvator, mAddressBookParsedResult.getBid(), null, PhotoManagerUtilsV2.TaskType.Baoxiucard_Salesman_Avator);
		}
	}
	public boolean hasMM() {
		return mAddressBookParsedResult != null && mAddressBookParsedResult.getBid() != null;
	}
	
	public void setTitle(int resid) {
		mTitle.setText(resid);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.avator:
			//当已经有mm号码的时候，单击头像会pop出actions菜单；否则是进入条码扫描识别联系人信息。
			if (!hasMM()) {
				Intent scanIntent = new Intent(mActivity, CaptureActivity.class);
				scanIntent.putExtra(Intents.EXTRA_SCAN_TASK, true);
				mActivity.startActivityForResult(scanIntent, (getId() & 0x0000ffff));
			} else {
				//pop actions
				mActionsLayout.setVisibility(View.VISIBLE);
				mHandler.removeCallbacks(mHideActionRunnable);
				mHandler.postDelayed(mHideActionRunnable, 2000);
				
			}
			break;
		case R.id.button_call:
			if (mAddressBookParsedResult.hasPhoneNumbers()) {
				Intents.callPhone(mActivity, mAddressBookParsedResult.getPhoneNumbers()[0]);
			}
			break;
		case R.id.button_info:
			if (hasMM()) {
				Intents.openURL(mActivity, "http://www.mingdown.com/" + mAddressBookParsedResult.getBid());
			} else {
				DebugUtils.logD(TAG, "ignore open Contact Info page due to non-MM");
			}
			break;
		}
		
		
	}

}
