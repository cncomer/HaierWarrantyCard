package com.bestjoy.app.haierwarrantycard.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;

public class DashboardLayout extends LinearLayout implements View.OnClickListener{
	private static final String TAG = "DashboardLayout";
	private ImageView mNewBtn, mCardBtn, mRepairBtn;
	private Context mContext;

	public DashboardLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	public DashboardLayout(Context context) {
		super(context);
		mContext = context;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mNewBtn = (ImageView) findViewById(R.id.button_new_card);
		mCardBtn = (ImageView) findViewById(R.id.button_my_card);
		mRepairBtn = (ImageView) findViewById(R.id.button_repair);
		
		mNewBtn.setOnClickListener(this);
		mCardBtn.setOnClickListener(this);
//		mRepairBtn.setOnClickListener(this);
		
		
	}

	public static DashboardLayout addDashboardLayout(Context context, ViewGroup container, boolean attachToRoot) {
		LayoutInflater flater = LayoutInflater.from(context);
		DashboardLayout view = (DashboardLayout) flater.inflate(R.layout.dashboard_layout, container, attachToRoot);
		return view;
	}
	
	public static DashboardLayout findDashboardLayout(Activity activity) {
		View view = activity.findViewById(R.id.listview);
		if (view != null) {
			return (DashboardLayout) view;
		}
		DebugUtils.logD(TAG, "current Activity not contains DashboardLayout with id dashboard");
		return null;
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_my_card:
			MyCardActivity.startIntent(mContext);
			break;
		case R.id.button_new_card:
			NewCardActivity.startIntent(mContext);
			break;
		case R.id.button_repair:
			MyCardActivity.startIntent(mContext);
			break;
		}
	}

}
